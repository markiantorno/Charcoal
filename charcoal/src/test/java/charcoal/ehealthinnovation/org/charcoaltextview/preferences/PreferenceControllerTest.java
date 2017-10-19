package charcoal.ehealthinnovation.org.charcoaltextview.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import junit.framework.Assert;

import org.junit.After;
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
    private final static String BLOOD_PRESSURE_UNIT = "mmHg";

    private final static String GLUCOSE_PROPERTY = "blood_glucose";
    private final static String GLUCOSE_UNIT = "mmol/l";

    private Activity myActivity;

    @Before
    public void setUp() throws Exception {
        myActivity = Robolectric.setupActivity(Activity.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setUnitForPropety() throws Exception {
        PreferenceController.setUnitForPropety(myActivity, GLUCOSE_PROPERTY, GLUCOSE_UNIT);
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        Assert.assertEquals(GLUCOSE_UNIT, sharedPreferences.getString(GLUCOSE_PROPERTY, null));
        Assert.assertFalse(sharedPreferences.contains(BLOOD_PRESSURE_PROPERTY));
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void getUnitForPropety() throws Exception {
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(GLUCOSE_PROPERTY, GLUCOSE_UNIT).apply();
        Assert.assertEquals(GLUCOSE_UNIT, PreferenceController.getUnitForPropety(myActivity, GLUCOSE_PROPERTY));
        Assert.assertEquals(GLUCOSE_UNIT, PreferenceController.getUnitForPropety(myActivity, GLUCOSE_PROPERTY, null));
        Assert.assertEquals(GLUCOSE_UNIT, PreferenceController.getUnitForPropety(myActivity, GLUCOSE_PROPERTY, BLOOD_PRESSURE_PROPERTY));
        Assert.assertEquals(NO_UNIT, PreferenceController.getUnitForPropety(myActivity, BLOOD_PRESSURE_PROPERTY));
        Assert.assertEquals(BLOOD_PRESSURE_UNIT, PreferenceController.getUnitForPropety(myActivity, BLOOD_PRESSURE_PROPERTY, BLOOD_PRESSURE_UNIT));
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void clearAllSetUnits() throws Exception {
        SharedPreferences sharedPreferences = myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(GLUCOSE_PROPERTY, GLUCOSE_UNIT).apply();
        sharedPreferences.edit().putString(BLOOD_PRESSURE_PROPERTY, BLOOD_PRESSURE_UNIT).apply();
        Assert.assertEquals(2, myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE).getAll().size());
        PreferenceController.clearAllSetUnits(myActivity);
        Assert.assertEquals(0, myActivity.getSharedPreferences(PreferenceController.PREF_FILE_KEY, Context.MODE_PRIVATE).getAll().size());
    }
}