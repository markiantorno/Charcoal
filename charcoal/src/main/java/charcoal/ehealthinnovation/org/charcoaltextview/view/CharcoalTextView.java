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

import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.EssenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.PreferenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.pojo.ObservationPair;

/**
 * Extended {@link android.widget.TextView} used to display {@link Observation} with the correct units.
 * <p>
 * Created by miantorno on 2017-10-13.
 */
public class CharcoalTextView extends AppCompatTextView implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected final static String TAG = CharcoalTextView.class.getSimpleName();

    // Display formatting
    protected String mUnitString;
    protected int mAccuracy;
    protected String mProperty;
    protected String mFormat;

    // Observation to display
    protected ObservationPair mCurrentObservation;

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
        if ((mCurrentObservation != null) && (mCurrentObservation.isValid())) {
            setAndFormatText(mCurrentObservation);
        }
    }

    /**
     * Sets the view to display the value of the passed in {@link Observation}.
     *
     * @param obs {@link Observation} to display value of.
     */
    public void setObservationDSTU3(@NonNull Observation obs) {
        mCurrentObservation = new ObservationPair(obs);
    }

    /**
     * Sets the view to display the value of the passed in {@link ca.uhn.fhir.model.dstu2.resource.Observation}.
     *
     * @param obs {@link ca.uhn.fhir.model.dstu2.resource.Observation} to display value of.
     */
    public void setObservationDSTU2(@NonNull ca.uhn.fhir.model.dstu2.resource.Observation obs) {
        mCurrentObservation = new ObservationPair(obs);
        relight();
    }

    private void setAndFormatText(@NonNull ObservationPair observationPair) {

        Log.d(TAG, "Attempting to display value: " + observationPair.getValue() + ", with corresponding " +
                "unit: " + observationPair.getUnit());
        UcumEssenceService ucumService = EssenceController.getUcumService();

        if (charcoalTextViewInitialized()
                && (ucumService != null)) {

            String destinationUnit = getUnitString();

            if (!observationPair.getUnit().equals(destinationUnit)) {
                Log.d(TAG, "Unit for property does not equal current unit. Need to convert.");
                try {
                    Log.d(TAG, "Value passed in -> " + observationPair.getValue());
                    Decimal sourceValue = new Decimal(String.valueOf(observationPair.getValue()));
                    Decimal convertedValue = ucumService.convert(sourceValue,
                            observationPair.getUnit(),
                            destinationUnit);

                    Log.d(TAG, "Adjusted value for new unit -> " + convertedValue);
                    displayObservationValue(asPrecisionDecimalString(convertedValue, getAccuracy()),
                            destinationUnit);

                } catch (UcumException e) {
                    Log.d(TAG, "Defaulting to passed in unit... \n" + e.getMessage());
                    displayObservationValue(asPrecisionDecimalString(String.valueOf(observationPair.getValue()),
                            getAccuracy()), destinationUnit);
                }
            } else {
                Log.d(TAG, "Current unit matches desired unit.");
                displayObservationValue(asPrecisionDecimalString(String.valueOf(observationPair.getValue()),
                        getAccuracy()), destinationUnit);
            }

        } else {
            Log.e(TAG, "CharcoalTextView not initialized. Displaying as plain number...");
            setText(String.valueOf(observationPair.getValue()));
        }
    }

    /**
     * Displays the passed in value along with given unit, as per the current {@link CharcoalTextView#mFormat}.
     *
     * @param value Measurement value to display, as a {@link String}.
     * @param unit  UCUM unit {@link String} to use with value.
     */
    private void displayObservationValue(String value, String unit) {
        String humanReadableUnitString;
        UcumEssenceService ucumService = EssenceController.getUcumService();

        if ((ucumService.getModel() != null) && (ucumService.getModel().getUnit(unit) != null)) {
            humanReadableUnitString = ucumService.getModel().getUnit(unit).getPrintSymbol();
        } else {
            humanReadableUnitString = ucumService.getCommonDisplay(unit);
        }

        setText(String.format(getFormat(), value, humanReadableUnitString));
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
            if (accuracy == 0) {
                result = result.substring(0, result.indexOf("."));
            } else {
                int digitsAfterDecimal = ((result.length() - 1) - result.indexOf("."));
                if (accuracy > digitsAfterDecimal) {
                    accuracy = digitsAfterDecimal;
                }
                result = result.substring(0, result.indexOf(".") + (accuracy) + 1);
            }
            return result;
        }
    }
}
