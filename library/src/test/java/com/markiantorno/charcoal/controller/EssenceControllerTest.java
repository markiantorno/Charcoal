package com.markiantorno.charcoal.controller;

import android.app.Activity;

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
public class EssenceControllerTest {

    public static final String FILENAME = "test_essence_file.xml";
    public static final String MY_OTHER_FILENAME = "random_file.xml";

    private Activity myActivity;

    @Before
    public void setUp() throws Exception {
        myActivity = Robolectric.setupActivity(Activity.class);
    }

    @Test
    public void setEssenceFile() throws Exception {
        Assert.assertNull(EssenceController.getUcumService());
        EssenceController.setEssenceFile(FILENAME, myActivity);
        Assert.assertNotNull(EssenceController.getUcumService());
        EssenceController.clearModel();
    }

    @Test
    public void shouldLoadModel() throws Exception {
        Assert.assertTrue(EssenceController.shouldLoadModel(FILENAME));
        EssenceController.setEssenceFile(FILENAME, myActivity);
        Assert.assertFalse(EssenceController.shouldLoadModel(FILENAME));
        Assert.assertTrue(EssenceController.shouldLoadModel(MY_OTHER_FILENAME));
        EssenceController.clearModel();
    }

    @Test
    public void clearModel() throws Exception {
        Assert.assertTrue(EssenceController.shouldLoadModel(FILENAME));
        EssenceController.setEssenceFile(FILENAME, myActivity);
        Assert.assertFalse(EssenceController.shouldLoadModel(FILENAME));
        EssenceController.clearModel();
        Assert.assertNull(EssenceController.getUcumService());
        Assert.assertTrue(EssenceController.shouldLoadModel(FILENAME));
        EssenceController.clearModel();
    }

    @Test
    public void getUcumService() throws Exception {
        Assert.assertNull(EssenceController.getUcumService());
        EssenceController.setEssenceFile(FILENAME, myActivity);
        Assert.assertNotNull(EssenceController.getUcumService());
        EssenceController.clearModel();
    }

}