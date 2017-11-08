package charcoal.ehealthinnovation.org.charcoaltextview.view;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;

import java.lang.ref.WeakReference;

import charcoal.ehealthinnovation.org.charcoaltextview.controller.EssenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.pojo.ObservationPair;

/**
 * Created by miantorno on 2017-11-08.
 */

public class ConvertAndPopulateViewTask extends AsyncTask<ObservationPair, Void, String> {

    public final static String TAG = ConvertAndPopulateViewTask.class.getSimpleName();

    protected WeakReference<CharcoalTextView> mWeakReference;
    protected String mDesiredUnit;
    protected int mDesiredAccuracy;
    protected String mFormat;

    public ConvertAndPopulateViewTask(@NonNull CharcoalTextView textView,
                                      @NonNull String desiredUnit,
                                      @NonNull int desiredAccuracy,
                                      @NonNull String format) {
        mWeakReference = new WeakReference<>(textView);
        mDesiredUnit = desiredUnit;
        mDesiredAccuracy = desiredAccuracy;
        mFormat = format;
    }

    @Override
    protected String doInBackground(@NonNull ObservationPair... observationPairs) {
        UcumEssenceService ucumService = EssenceController.getUcumService();
        ObservationPair myPair = observationPairs[0];
        String parsedStringValue;

        if ((myPair != null) && (ucumService != null)) {

            if (!myPair.getUnit().equals(mDesiredUnit)) {
                Log.d(TAG, "Unit for property does not equal current unit. Need to convert.");
                try {
                    Log.d(TAG, "Value passed in -> " + myPair.getValue());
                    Decimal sourceValue = new Decimal(String.valueOf(myPair.getValue()));
                    Decimal convertedValue = ucumService.convert(sourceValue,
                            myPair.getUnit(),
                            mDesiredUnit);

                    Log.d(TAG, "Adjusted value for new unit -> " + convertedValue);
                    parsedStringValue = asPrecisionDecimalString(convertedValue, mDesiredAccuracy);

                } catch (UcumException e) {
                    Log.d(TAG, "Defaulting to passed in unit... \n" + e.getMessage());
                    parsedStringValue = asPrecisionDecimalString(String.valueOf(myPair.getValue()),
                            mDesiredAccuracy);
                }
            } else {
                Log.d(TAG, "Current unit matches desired unit.");
                parsedStringValue = asPrecisionDecimalString(String.valueOf(myPair.getValue()),
                        mDesiredAccuracy);
            }

        } else {
            Log.e(TAG, "CharcoalTextView not initialized. Displaying as plain number...");
            parsedStringValue = String.valueOf(myPair.getValue());
        }

        return String.format(mFormat, parsedStringValue, getHumanReadableUnitString(mDesiredUnit));
    }

    @Override
    protected void onPostExecute(String stringToDisplay) {
        if (mWeakReference != null && stringToDisplay != null) {
            final CharcoalTextView textView = mWeakReference.get();
            if (textView != null) {
                textView.setText(stringToDisplay);
            }
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

        if ((ucumService.getModel() != null) && (ucumService.getModel().getUnit(unit) != null)) {
            humanReadableUnitString = ucumService.getModel().getUnit(unit).getPrintSymbol();
        } else {
            humanReadableUnitString = ucumService.getCommonDisplay(unit);
        }
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
