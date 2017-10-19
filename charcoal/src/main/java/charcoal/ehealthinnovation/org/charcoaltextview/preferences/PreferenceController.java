package charcoal.ehealthinnovation.org.charcoaltextview.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

/**
 * Controller to provide read/write access to the {@link SharedPreferences} storage for displaying
 * units in the {@link CharcoalTextView}
 * <p>
 * Created by miantorno on 2017-10-13.
 */
public class PreferenceController {

    private final static String TAG = PreferenceController.class.getSimpleName();

    protected final static String PREF_FILE_KEY = "charcoal.preferences";

    /**
     * Get the instance of the shared preferences used for storing property/unit pairs.
     *
     * @param ctx {@link Context}
     * @return {@link SharedPreferences}
     */
    private static SharedPreferences getCharcoalPreferences(Context ctx) {
        return ctx.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
    }

    /**
     * Sets the {@link String} unit for the given property.
     * <p>
     * Example: setUnitForProperty(myContext, "blood_glucose", "mmol/L");
     * </p>
     *
     * @param ctx      {@link Context}
     * @param property {@link String} property to set default display unit for.
     * @param unit     {@linkl String} unit to us to display readings of the given property type.
     */
    public static void setUnitForPropety(@NonNull Context ctx, @NonNull String property, @NonNull String unit) {
        Log.d(TAG, "Setting unit " + unit + " for property " + property);

        SharedPreferences sharedPref = getCharcoalPreferences(ctx);

        if (sharedPref.contains(property)) {
            Log.d(TAG, "Shared preferences contains existing unit for property " + property + ". Overwriting...");
        } else {
            Log.d(TAG, "No entry for property " + property + "exists. Creating...");
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(property, unit);
        editor.apply();
    }

    /**
     * Fetches the set {@link String} unit for the given {@link String} property.
     *
     * @param ctx      {@link Context}
     * @param property Data property to fetch unit for. (Ex: blood_glucose, heart_rate...)
     * @return The given {@link String} unit to use for the given property. Will return an empty
     * {@link String} if no such property entry exists.
     */
    public static String getUnitForPropety(@NonNull Context ctx, @NonNull String property) {
        return getUnitForPropety(ctx, property, "");
    }

    /**
     * Fetched the set {@link String} unit for the given {@link String} property.
     *
     * @param ctx         {@link Context}
     * @param property    Data property to fetch unit for. (Ex: blood_glucose, heart_rate...)
     * @param defaultUnit Default {@link String} unit to return if no such property entry exists.
     * @return The given {@link String} unit to use for the given property. Will return {@param defaultUnit}
     * if no such property entry exists.
     */
    public static String getUnitForPropety(@NonNull Context ctx, @NonNull String property, @NonNull String defaultUnit) {
        Log.d(TAG, "Getting unit for property " + property);

        SharedPreferences sharedPref = getCharcoalPreferences(ctx);

        if (!sharedPref.contains(property)) {
            Log.d(TAG, "No entry for property " + property + "exists. Cannot return value...");
        }

        return sharedPref.getString(property, defaultUnit);
    }

    /**
     * Clears all set unit preferences from storage.
     *
     * @param ctx {@link Context}
     * @return {@link Boolean#TRUE} if successful.
     */
    public static void clearAllSetUnits(Context ctx) {
        SharedPreferences sharedPref = getCharcoalPreferences(ctx);
        sharedPref.edit().clear().apply();
    }

    /**
     * Registers a {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener} to the
     * Charcoal {@link SharedPreferences}.
     *
     * @param ctx      {@link Context}
     * @param listener {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener}
     */
    public static void registerListener(Context ctx, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (listener != null) {
            getCharcoalPreferences(ctx).registerOnSharedPreferenceChangeListener(listener);
        } else {
            Log.e(TAG, "Null OnSharedPreferenceChangeListener passed into registerListener.");
        }
    }

    /**
     * Unregisters a {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener} from the
     * Charcoal {@link SharedPreferences}.
     *
     * @param ctx      {@link Context}
     * @param listener {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener}
     */
    public static void unregisterListener(Context ctx, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (listener != null) {
            getCharcoalPreferences(ctx).unregisterOnSharedPreferenceChangeListener(listener);
        } else {
            Log.e(TAG, "Null OnSharedPreferenceChangeListener passed into unregisterListener.");
        }
    }
}