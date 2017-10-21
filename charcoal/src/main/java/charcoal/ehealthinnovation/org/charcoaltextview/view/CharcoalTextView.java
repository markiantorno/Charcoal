package charcoal.ehealthinnovation.org.charcoaltextview.view;

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

import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.EssenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.PreferenceController;

/**
 * Extended {@link android.widget.TextView} used to display {@link Observation} with the correct units.
 * <p>
 * Created by miantorno on 2017-10-13.
 */
public class CharcoalTextView extends AppCompatTextView implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String TAG = CharcoalTextView.class.getSimpleName();

    // Display formatting
    private String mUnitString;
    private int mAccuracy;
    private String mProperty;
    private String mFormat;

    // Observation to display
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

    public String getUnitString() {
        return PreferenceController.getUnitForProperty(this.getContext(), getProperty(), mUnitString);
    }

    public void setUnitString(String unit) {
        this.mUnitString = unit;
    }

    public String getProperty() {
        return mProperty;
    }

    public void setProperty(String property) {
        this.mProperty = property;
    }

    public int getAccuracy() {
        return PreferenceController.getAccuracyForUnit(this.getContext(), getUnitString(), mAccuracy);
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
     * Resets the view. Triggers a recalculation of unit, and conversion type.
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
        Quantity quatityDt = getValueQuantityDt(obs);

        if (quatityDt != null) {
            if (unitAndValueSet(quatityDt)) {
                setAndFormatText(quatityDt.getValueElement().getValueAsNumber().doubleValue(),
                        quatityDt.getUnit());
            }
        }
    }

    /**
     * Returns the {@link Quantity} for the passed in fhir {@link Observation}, or null, if no
     * quantity is set.
     *
     * @param observation {@link Observation} to extract {@link Quantity} from.
     * @return {@link Quantity} for given {@link Observation}
     */
    protected Quantity getValueQuantityDt(@NonNull Observation observation) {
        Quantity quatityDt = null;

        try {
            quatityDt = observation.getValueQuantity();
        } catch (FHIRException e) {
            Log.e(TAG, "Passed in observation with id: " + observation.getId() + ", has no set quantityDt!");
        }

        return quatityDt;
    }

    /**
     * Checks to see if the given {@link Quantity} has both a set unit and value.
     *
     * @param quantity {@link Quantity} to display.
     * @return {@link Boolean#TRUE} if both are set.
     */
    protected boolean unitAndValueSet(Quantity quantity) {
        return ((quantity.getUnit() != null)
                && (quantity.getValueElement() != null));
    }

    //TODO two methods here, one for getValue dstu2, and one for getValue dstu3

    private void setAndFormatText(Double observationValue, String observationUnit) {

        Log.d(TAG, "Attempting to display value: " + observationValue + ", with corresponding " +
                "unit: " + observationUnit);
        UcumEssenceService ucumService = EssenceController.getUcumService();

        if (charcoalTextViewInitialized()
                && (ucumService != null)) {

            String destinationUnit = getUnitString();

            if (!observationUnit.equals(destinationUnit)) {
                Log.d(TAG, "Unit for property does not equal current unit. Need to convert.");
                try {
                    Log.d(TAG, "Value passed in -> " + observationValue);
                    Decimal sourceValue = new Decimal(String.valueOf(observationValue));
                    Decimal convertedValue = ucumService.convert(sourceValue,
                            observationUnit,
                            destinationUnit);

                    Log.d(TAG, "Adjusted value for new unit -> " + convertedValue);
                    displayObservationValue(asPrecisionDecimalString(convertedValue, getAccuracy()),
                            destinationUnit);

                } catch (UcumException e) {
                    Log.d(TAG, "Defaulting to passed in unit... \n" + e.getMessage());
                    displayObservationValue(asPrecisionDecimalString(String.valueOf(observationValue),
                            getAccuracy()), destinationUnit);
                }
            } else {
                Log.d(TAG, "Current unit matches desired unit.");
                displayObservationValue(asPrecisionDecimalString(String.valueOf(observationValue),
                        getAccuracy()), destinationUnit);
            }

        } else {
            Log.e(TAG, "CharcoalTextView not initialized. Displaying as plain number...");
            setText(String.valueOf(observationValue));
        }
    }

    /**
     * Displays the passed in value along with given unit, as per the current {@link CharcoalTextView#mFormat}.
     *
     * @param value Measurement value to display, as a {@link String}.
     * @param unit  UCUM unit {@link String} to use with value.
     */
    private void displayObservationValue(String value, String unit) {
        UcumEssenceService ucumService = EssenceController.getUcumService();
        setText(String.format(getFormat(), value, ucumService.getCommonDisplay(unit)));
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
     * For proper display of {@link Observation} values, all {@link Charcoal} annotation values
     * must be initialized.
     *
     * @return {@link Boolean#TRUE} if view has been initialized properly.
     */
    protected boolean charcoalTextViewInitialized() {
        return ((mAccuracy >= 0)
                && (mFormat != null)
                && (mProperty != null)
                && (mUnitString != null));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getProperty()) || key.equals(mUnitString)) {
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
    public static String asPrecisionDecimalString(Decimal decimal, int accuracy) {
        return asPrecisionDecimalString(decimal.asDecimal(), accuracy);
    }

    /**
     * The {@link Decimal#precision} variable doesn't do anything for rendering as far as I can tell.
     * This method takes the {@link String} representation of Decimal value and provides a printable
     * {@link String} with correct precision.
     *
     * @param decimalString {@link String} representation of Decimal value.
     * @return The precision correct {@link String}.
     */
    protected static String asPrecisionDecimalString(String decimalString, int accuracy) {
        if ((accuracy < 0) || (!decimalString.contains("."))) {
            Log.e(TAG, "Cannot set accuracy to a negative value, or value with no decimal places." +
                    " No adjustment done.");
            return decimalString;
        } else {
            String result = decimalString;
            int digitsAfterDecimal = ((result.length() - 1) - result.indexOf("."));
            if (accuracy > digitsAfterDecimal) {
                accuracy = digitsAfterDecimal;
            }
            result = result.substring(0, result.indexOf(".") + (accuracy) + 1);
            return result;
        }
    }
}
