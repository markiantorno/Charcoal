
package charcoal.ehealthinnovation.org.charcoaltextview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.exceptions.FHIRException;

import charcoal.ehealthinnovation.org.charcoaltextview.annotation.CharcoalWriter;
import charcoal.ehealthinnovation.org.charcoaltextview.preferences.EssenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.preferences.PreferenceController;

/**
 * Created by miantorno on 2017-10-13.
 */
public class CharcoalTextView extends AppCompatTextView implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String TAG = CharcoalTextView.class.getSimpleName();

    /*
     * Display formatting...
     */
    private String mUnitCodeUC;
    private int mAccuracy;
    private String mProperty;
    private String mFormat;

    /*
     * Observation to display
     */
    private Observation mCurrentObservation;

    // Constructors

    public CharcoalTextView(Context context) {
        super(context);
    }

    public CharcoalTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CharcoalTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Getters and Setters

    public String getUnitCodeUC() {
        return mUnitCodeUC;
    }

    public void setUnitCodeUC(String unit) {
        this.mUnitCodeUC = unit;
    }

    public String getProperty() {
        return mProperty;
    }

    public void setProperty(String property) {
        this.mProperty = property;
    }

    public int getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(int accuracy) {
        this.mAccuracy = accuracy;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        this.mFormat = format;
    }

    /**
     *
     */
    public void relight() {
        if (mCurrentObservation != null) {
            setObservation(mCurrentObservation);
        }
    }

    /**
     * Sets the view to display the value of the passed in {@link Observation}.
     *
     * @param obs {@link Observation} to display value of.
     */
    public void setObservation(@NonNull Observation obs) {

        mCurrentObservation = obs;

        //Check value of passed in observation
        Quantity quatityDt = null;

        try {
            quatityDt = obs.getValueQuantity();
        } catch (FHIRException e) {
            e.printStackTrace();
        }
        if (quatityDt == null) {
            throw new IllegalArgumentException("Passed in Observation has no set quatityDt...");
        }

        //Check unit of passed in observation
        String unit = quatityDt.getUnit();

        if (unit == null) {
            Log.e(TAG, "Passed in Observation has no set unit...");
            unit = "";
        }

        Double value = quatityDt.getValueElement().getValueAsNumber().doubleValue();

        displayValue(value, unit);
    }

    //TODO two methods here, one for getValue dstu2, and one for getValue dstu3

    @SuppressLint("StringFormatMatches")
    private void displayValue(Double value, String unitString) {

        Log.d(TAG, "Attempting to display value: " + value + ", with corresponding unit: " + unitString);

        UcumEssenceService ucumService = EssenceController.getUcumService();

        if (charcoalTextViewInitialized() && (ucumService != null)) {

            String unitForPropety = PreferenceController.getUnitForPropety(getContext(), getProperty(), getUnitCodeUC());

            Log.d(TAG, "Current unit for property: " + getProperty() + " -> " + getUnitCodeUC());

            Decimal accuracyAdjustedDecimal = getFormattedDecimal(value, getAccuracy());

            if ((!unitForPropety.equals(unitString)) && (accuracyAdjustedDecimal != null)) {

                Log.d(TAG, "Unit for property does not equal current unit. Need to convert.");

                try {

                    Log.d(TAG, "Decimal value passed in -> " + accuracyAdjustedDecimal);
                    Decimal valueConvertedToNewUnit = ucumService.convert(accuracyAdjustedDecimal,
                            unitString,
                            unitForPropety);

                    Log.d(TAG, "Adjusted value for new unit -> " + valueConvertedToNewUnit);
                    displayObservationValue(asPrecisionDecimal(valueConvertedToNewUnit),
                            unitForPropety);

                } catch (UcumException e) {
                    Log.d(TAG, "Defaulting to passed in unit... \n" + e.getMessage());

                    displayObservationValue(asPrecisionDecimal(accuracyAdjustedDecimal), unitString);
                }

            } else {
                Log.d(TAG, "Current unit matches desired unit.");
                displayObservationValue(accuracyAdjustedDecimal == null ? asPrecisionDecimal(accuracyAdjustedDecimal) : "N/A",
                        unitString);
            }

        } else {
            Log.e(TAG, "CharcoalTextView not initialized. Displaying as plain number...");
            setText(String.valueOf(value));
        }
    }

    /**
     * Displays the passed in value along with given unit, as per the current {@link CharcoalTextView#mFormat}.
     *
     * @param value Measurement value to display, as a {@link String}.
     * @param unit  {@link String} unit to use with value.
     */
    private void displayObservationValue(String value, String unit) {
        UcumEssenceService ucumService = EssenceController.getUcumService();
        if (ucumService != null) {
            unit = ucumService.getModel().getUnit(unit).getPrintSymbol();
        }
        setText(String.format(getFormat(), value, unit));
    }

    /**
     * Returns the UCUM code for the given code.
     *
     * @param printString {@link String} of unit to get the code for.
     */
    private String getUnitCode(String printString) {
        UcumEssenceService ucumService = EssenceController.getUcumService();
        if (ucumService == null) {
            Log.e(TAG, "getUnitPrintSymbol -> No UCUMEssenceService could be found. Returning blank unit String...");
            return "";
        } else if (ucumService.getModel().getUnit(printString) == null) {
            Log.e(TAG, "getUnitPrintSymbol -> .getUnit(" + printString + ") returns null. Returning blank unit String...");
            return "";
        } else {
            return ucumService.getModel().getUnit(printString).getCode();
        }
    }

    /**
     * Returns the printable symbol for the given code.
     *
     * @param unitCode {@link String} code of unit to get the printable String for.
     */
    private String getUnitPrintSymbol(String unitCode) {
        UcumEssenceService ucumService = EssenceController.getUcumService();
        if (ucumService != null) {
            return ucumService.getModel().getUnit(unitCode).getPrintSymbol();
        } else {
            Log.e(TAG, "getUnitPrintSymbol -> No UCUMEssenceService could be found. Returning blank unit String...");
            return "";
        }
    }

    /**
     * Takes the passed in {@link Double}, and converts to a UCUM type {@link Decimal}, with the
     * passed int {@param accuracy}.
     *
     * @param value    {@link Double} value to display.
     * @param accuracy The decimal place accuracy of a number is the {@link Integer} number of
     *                 digits to the right of the decimal point.
     * @return {@link Decimal} with correct accuracy.
     */
    private Decimal getFormattedDecimal(Double value, int accuracy) {
        Decimal accuracyAdjustedDecimal = null;
        try {
            accuracyAdjustedDecimal = new Decimal(value.toString(), accuracy);
        } catch (UcumException e) {
            Log.e(TAG, "Error displaying value -> " + value + ", ", e);
        }
        return accuracyAdjustedDecimal;
    }

    /**
     * For proper display of {@link Observation} values, all {@link CharcoalWriter} annotation values
     * must be initialized.
     *
     * @return {@link Boolean#TRUE} if view has been initialized properly.
     */
    private boolean charcoalTextViewInitialized() {
        return ((mAccuracy >= 0)
                && (mFormat != null)
                && (mProperty != null)
                && (mUnitCodeUC != null));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(mProperty)) {
            Log.d(TAG, "Property change triggered for property: " + key);
            relight();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PreferenceController.registerListener(getContext(), this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PreferenceController.unregisterListener(getContext(), this);
    }

    /**
     * The {@link Decimal#precision} variable doesn't do anything for rendering as far as I can tell.
     * This method takes the {@link Decimal} and provides a printable {@link String} with correct precision.
     *
     * @param decimal {@link Decimal} to generate {@link String} for.
     * @return The precision correct {@link String}.
     */
    public String asPrecisionDecimal(Decimal decimal) {
        String result = decimal.asDecimal();
        result = result.substring(0, result.indexOf(".") + (getAccuracy() + 1));
        return result;
    }
}
