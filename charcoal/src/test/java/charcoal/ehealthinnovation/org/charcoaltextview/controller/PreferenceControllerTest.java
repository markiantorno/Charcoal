package charcoal.ehealthinnovation.org.charcoaltextview.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by miantorno on 2017-10-19.
 */
@RunWith(RobolectricTestRunner.class)
public class PreferenceControllerTest {

    private final static String NO_UNIT = "";

    private final static String BLOOD_PRESSURE_PROPERTY = "blood_pressure";
    private final static String BLOOD_PRESSURE_UNIT = "mm[Hg]";

    private final static String GLUCOSE_PROPERTY = "blood_glucose";
    private final static String GLUCOSE_UNIT = "mmol/l";

    private final static int GLUCOSE_ACCURACY = 20;

    private Activity myActivity;

    @Before
    public void setUp() throws Exception {
        myActivity = Robolectric.setupActivity(Activity.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setUnitForProperty() throws Exception {
        PreferenceController.setUnitForProperty(myActivity, GLUCOSE_PROPERTY, GLUCOSE_UNIT);
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        Assert.assertEquals(GLUCOSE_UNIT, sharedPreferences.getString(GLUCOSE_PROPERTY, null));
        Assert.assertFalse(sharedPreferences.contains(BLOOD_PRESSURE_PROPERTY));
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void setAccuracyForUnit() throws Exception {
        PreferenceController.setAccuracyForUnit(myActivity, GLUCOSE_UNIT, GLUCOSE_ACCURACY);
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        Assert.assertEquals(GLUCOSE_ACCURACY, sharedPreferences.getInt(GLUCOSE_UNIT, 0));
        Assert.assertEquals(PreferenceController.NO_SUCH_ACCURACY, PreferenceController.getAccuracyForUnit(myActivity, BLOOD_PRESSURE_UNIT));
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void getUnitForProperty() throws Exception {
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(GLUCOSE_PROPERTY, GLUCOSE_UNIT).apply();
        Assert.assertEquals(GLUCOSE_UNIT, PreferenceController.getUnitForProperty(myActivity, GLUCOSE_PROPERTY));
        Assert.assertEquals(GLUCOSE_UNIT, PreferenceController.getUnitForProperty(myActivity, GLUCOSE_PROPERTY, null));
        Assert.assertEquals(GLUCOSE_UNIT, PreferenceController.getUnitForProperty(myActivity, GLUCOSE_PROPERTY, BLOOD_PRESSURE_PROPERTY));
        Assert.assertEquals(NO_UNIT, PreferenceController.getUnitForProperty(myActivity, BLOOD_PRESSURE_PROPERTY));
        Assert.assertEquals(BLOOD_PRESSURE_UNIT, PreferenceController.getUnitForProperty(myActivity, BLOOD_PRESSURE_PROPERTY, BLOOD_PRESSURE_UNIT));
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void getAccuracyForUnit() throws Exception {
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(GLUCOSE_UNIT, GLUCOSE_ACCURACY).apply();
        Assert.assertEquals(GLUCOSE_ACCURACY, PreferenceController.getAccuracyForUnit(myActivity, GLUCOSE_UNIT));
        Assert.assertEquals(GLUCOSE_ACCURACY, PreferenceController.getAccuracyForUnit(myActivity, GLUCOSE_UNIT));
        Assert.assertEquals(PreferenceController.NO_SUCH_ACCURACY, PreferenceController.getAccuracyForUnit(myActivity, BLOOD_PRESSURE_UNIT));
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void clearAllTest() throws Exception {
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(GLUCOSE_PROPERTY, GLUCOSE_UNIT).apply();
        sharedPreferences.edit().putString(BLOOD_PRESSURE_PROPERTY, BLOOD_PRESSURE_UNIT).apply();
        Assert.assertEquals(2, myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE).getAll().size());
        PreferenceController.clearAllPreferences(myActivity);
        Assert.assertEquals(0, myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE).getAll().size());
    }

    @Test
    public void unitSetForProperty() throws Exception {
        Assert.assertFalse(PreferenceController.unitSetForProperty(myActivity, GLUCOSE_PROPERTY));
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(GLUCOSE_PROPERTY, GLUCOSE_UNIT).apply();
        Assert.assertTrue(PreferenceController.unitSetForProperty(myActivity, GLUCOSE_PROPERTY));
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void accuracySetForUnit() throws Exception {
        Assert.assertFalse(PreferenceController.accuracySetForUnit(myActivity, GLUCOSE_UNIT));
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(GLUCOSE_UNIT, GLUCOSE_ACCURACY).apply();
        Assert.assertTrue(PreferenceController.accuracySetForUnit(myActivity, GLUCOSE_UNIT));
        sharedPreferences.edit().clear().apply();
    }
}