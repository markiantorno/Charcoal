package com.markiantorno.charcoal.loader;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;

import java.lang.ref.WeakReference;

import com.markiantorno.charcoal.controller.EssenceController;
import com.markiantorno.charcoal.pojo.ObservationPair;
import com.markiantorno.charcoal.view.CharcoalTextView;

/**
 * Created by miantorno on 2017-11-08.
 */
public class ConvertUnitThread implements Runnable {

    public final static String TAG = ConvertUnitThread.class.getSimpleName();

    protected WeakReference<CharcoalTextView> mWeakReference;
    protected String mDesiredUnit;
    protected int mDesiredAccuracy;
    protected String mFormat;
    protected ObservationPair observationPair;
    protected Handler mHandler = new Handler();

    public ConvertUnitThread(@NonNull CharcoalTextView textView,
                             @NonNull String desiredUnit,
                             int desiredAccuracy,
                             @NonNull String format,
                            ObservationPair obs) {
        mWeakReference = new WeakReference<>(textView);
        mDesiredUnit = desiredUnit;
        mDesiredAccuracy = desiredAccuracy;
        mFormat = format;
        observationPair = obs;
        textView.setText("");
    }

    public WeakReference<CharcoalTextView> getmWeakReference() {
        return mWeakReference;
    }

    public ConvertUnitThread setWeakReference(WeakReference<CharcoalTextView> mWeakReference) {
        this.mWeakReference = mWeakReference;
        return this;
    }

    public String getmDesiredUnit() {
        return mDesiredUnit;
    }

    public ConvertUnitThread setDesiredUnit(String mDesiredUnit) {
        this.mDesiredUnit = mDesiredUnit;
        return this;
    }

    public int getmDesiredAccuracy() {
        return mDesiredAccuracy;
    }

    public ConvertUnitThread setDesiredAccuracy(int mDesiredAccuracy) {
        this.mDesiredAccuracy = mDesiredAccuracy;
        return this;
    }

    public String getmFormat() {
        return mFormat;
    }

    public ConvertUnitThread setFormat(String mFormat) {
        this.mFormat = mFormat;
        return this;
    }

    public ObservationPair getObservationPair() {
        return observationPair;
    }

    public ConvertUnitThread setObservationPair(ObservationPair observationPair) {
        this.observationPair = observationPair;
        return this;
    }

    @Override
    public void run() {
        UcumEssenceService ucumService = EssenceController.getUcumService();
        String parsedStringValue;

        if ((observationPair != null) && (ucumService != null)) {

            if (!observationPair.getUnit().equals(mDesiredUnit)) {
                Log.d(TAG, "Unit for property does not equal current unit. Need to convert.");
                try {
                    Log.d(TAG, "Value passed in -> " + observationPair.getValue());
                    Decimal sourceValue = new Decimal(String.valueOf(observationPair.getValue()));
                    Decimal convertedValue = ucumService.convert(sourceValue,
                            observationPair.getUnit(),
                            mDesiredUnit);

                    Log.d(TAG, "Adjusted value for new unit -> " + convertedValue);
                    parsedStringValue = asPrecisionDecimalString(convertedValue, mDesiredAccuracy);

                } catch (UcumException e) {
                    Log.d(TAG, "Defaulting to passed in unit... \n" + e.getMessage());
                    parsedStringValue = asPrecisionDecimalString(String.valueOf(observationPair.getValue()),
                            mDesiredAccuracy);
                }
            } else {
                Log.d(TAG, "Current unit matches desired unit.");
                parsedStringValue = asPrecisionDecimalString(String.valueOf(observationPair.getValue()),
                        mDesiredAccuracy);
            }

        } else {
            Log.e(TAG, "CharcoalTextView not initialized. Displaying as plain number...");
            parsedStringValue = String.valueOf(observationPair.getValue());
        }

        final String convertedUnitString = String.format(mFormat, parsedStringValue, getHumanReadableUnitString(mDesiredUnit));

        mHandler.post(() -> {
                Log.d(TAG, "Populating view with string: " + convertedUnitString);
                if (mWeakReference != null && convertedUnitString != null) {
                    final CharcoalTextView textView = mWeakReference.get();
                    if (textView != null) {
                        Log.d(TAG, "Reference to view and String good, proceeding");
                        textView.setText(convertedUnitString);
                        Log.d(TAG, "Text set to " + convertedUnitString);
                    } else {
                        Log.d(TAG, "No reference to text view found...");
                    }
                }
        });
    }

    public void stop() {
        Thread current = Thread.currentThread();
        if (current != null) {
            current.interrupt();
        }
    }

    /**
     * Takes the passed in unit and returns the human readable version of the String.
     *
     * @param unit UCUM notation unit.
     * @return {@link String} Human readable String for display.
     */
    private String getHumanReadableUnitString(@NonNull String unit) {
        String humanReadableUnitString;
        UcumEssenceService ucumService = EssenceController.getUcumService();

        Log.d(TAG, "Unit String to generate human readable for -> " + unit);
        if ((ucumService.getModel() != null) && (ucumService.getModel().getUnit(unit) != null)) {
            Log.d(TAG, "Unit is base unit, this is easy");
            humanReadableUnitString = ucumService.getModel().getUnit(unit).getPrintSymbol();
        } else {
            Log.d(TAG, "Unit is not base unit, defaulting to common display.");
            humanReadableUnitString = ucumService.getCommonDisplay(unit);
        }
        Log.d(TAG, "Human readable String -> " + humanReadableUnitString);
        return humanReadableUnitString;
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
