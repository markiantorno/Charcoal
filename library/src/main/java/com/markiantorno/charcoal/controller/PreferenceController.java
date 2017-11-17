package com.markiantorno.charcoal.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.markiantorno.charcoal.view.CharcoalTextView;

/**
 * Controller to provide read/write access to the {@link SharedPreferences} storage for displaying
 * units in the {@link CharcoalTextView}
 * <p>
 * Created by miantorno on 2017-10-13.
 */
public class PreferenceController {

    private final static String TAG = PreferenceController.class.getSimpleName();

    protected final static String PREF_FILE_KEY = "charcoal.preferences";

    public final static int NO_SUCH_ACCURACY = -1;

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
     * @param unit     {@link String} unit to us to display readings of the given property type.
     */
    public static void setUnitForProperty(@NonNull Context ctx, @NonNull String property, @NonNull String unit) {
        Log.d(TAG, "Setting unit " + unit + " for property " + property);

        SharedPreferences sharedPref = getCharcoalPreferences(ctx);

        if (sharedPref.contains(property)) {
            Log.d(TAG, "Shared preferences contains existing unit for property " + property + ". Overwriting...");
        } else {
            Log.d(TAG, "No entry for property " + property + " exists. Creating...");
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
    public static String getUnitForProperty(@NonNull Context ctx, @NonNull String property) {
        return getUnitForProperty(ctx, property, "");
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
    public static String getUnitForProperty(@NonNull Context ctx, @NonNull String property, @NonNull String defaultUnit) {
        Log.d(TAG, "Getting unit for property " + property);

        SharedPreferences sharedPref = getCharcoalPreferences(ctx);

        if (!sharedPref.contains(property)) {
            Log.d(TAG, "No entry for property " + property + " exists. Cannot return value...");
        }

        return sharedPref.getString(property, defaultUnit);
    }

    /**
     * Returns true if a unit has already been set in the preferences for the given property.
     * @param ctx         {@link Context}
     * @param property    Data property to fetch unit for. (Ex: blood_glucose, heart_rate...)
     * @return {@link Boolean#TRUE}, if a unit has already been set for the given property.
     */
    public static boolean unitSetForProperty(@NonNull Context ctx, @NonNull String property) {
        Log.d(TAG, "Checking if unit set for property " + property);
        SharedPreferences sharedPref = getCharcoalPreferences(ctx);
        return sharedPref.contains(property);
    }

    /**
     * Sets the {@link Integer} decimal place accuracy for the given unit.
     * <p>
     * Example: setAccuracyForUnit(myContext, "mmol/L", 3);
     * </p>
     *
     * @param ctx      {@link Context}
     * @param unit     {@link String} unit to set default accuracy for.
     * @param accuracy {@link Integer} The decimal place accuracy of a number is the number of digits to the right of the decimal point.
     */
    public static void setAccuracyForUnit(@NonNull Context ctx, @NonNull String unit, @NonNull int accuracy) {
        Log.d(TAG, "Setting accuracy " + accuracy + " for unit " + unit);

        SharedPreferences sharedPref = getCharcoalPreferences(ctx);

        if (sharedPref.contains(unit)) {
            Log.d(TAG, "Shared preferences contains existing accuracy for unit " + unit + ". Overwriting...");
        } else {
            Log.d(TAG, "No accuracy for unit " + unit + " exists. Creating...");
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(unit, accuracy);
        editor.apply();
    }

    /**
     * Fetches the set {@link Integer} decimal place accuracy for the given {@link String} unit.
     *
     * @param ctx      {@link Context}
     * @param unit     {@link String} unit to get accuracy for.
     * @return The accuracy for the given {@link String} unit. Will return {@link PreferenceController#NO_SUCH_ACCURACY}
     * if no such property entry exists.
     */
    public static int getAccuracyForUnit(@NonNull Context ctx, @NonNull String unit) {
        return getAccuracyForUnit(ctx, unit, NO_SUCH_ACCURACY);
    }

    /**
     * Fetches the set {@link Integer} decimal place accuracy for the given {@link String} unit.
     *
     * @param ctx      {@link Context}
     * @param unit     {@link String} unit to get accuracy for.
     * @param defaultAccuracy Default {@link String} unit to return if no such property entry exists.
     * @return The accuracy for the given {@link String} unit. Will return {@link PreferenceController#NO_SUCH_ACCURACY}
     * if no such property entry exists.
     */
    public static int getAccuracyForUnit(@NonNull Context ctx, @NonNull String unit, @NonNull int defaultAccuracy) {
        Log.d(TAG, "Getting accuracy for unit " + unit);

        SharedPreferences sharedPref = getCharcoalPreferences(ctx);

        if (!sharedPref.contains(unit)) {
            Log.d(TAG, "No accuracy for unit " + unit + " exists. Cannot return value...");
        }

        return sharedPref.getInt(unit, defaultAccuracy);
    }

    /**
     * Returns true if an accuracy has already been set in the preferences for the given unit.
     * @param ctx      {@link Context}
     * @param unit     {@link String} unit to get accuracy for.
     * @return {@link Boolean#TRUE}, if a unit has already been set for the given property.
     */
    public static boolean accuracySetForUnit(@NonNull Context ctx, @NonNull String unit) {
        Log.d(TAG, "Checking if unit set for unit " + unit);
        SharedPreferences sharedPref = getCharcoalPreferences(ctx);
        return sharedPref.contains(unit);
    }

    /**
     * Clears all set unit preferences from storage.
     *
     * @param ctx {@link Context}
     */
    public static void clearAllPreferences(Context ctx) {
        SharedPreferences sharedPref = getCharcoalPreferences(ctx);
        sharedPref.edit().clear().apply();
    }

    /**
     * Registers a {@link android.content.SharedPreferences.OnSharedPreferenceChangeListener} to the
     * Essence {@link SharedPreferences}.
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
     * Essence {@link SharedPreferences}.
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