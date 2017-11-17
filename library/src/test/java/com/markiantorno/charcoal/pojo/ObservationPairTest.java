package com.markiantorno.charcoal.pojo;

import android.app.Activity;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;

/**
 * Created by mark on 2017-10-21.
 */
@RunWith(RobolectricTestRunner.class)
public class ObservationPairTest {

    private final String MGDL_UNIT = "mg/dL";
    private final double MGDL_MEASURE = 75.0;

    private final String MMOLL_UNIT = "mmol/L";
    private final double MMOLL_MEASURE = 3.7;

    private Activity myActivity;

    private ObservationPair mPairDSTU2;
    private ObservationPair mPairDSTU3;

    private Observation myDstu3ObsGood;
    private Observation myDstu3ObsBad;

    private ca.uhn.fhir.model.dstu2.resource.Observation myDstu2ObsGood;
    private ca.uhn.fhir.model.dstu2.resource.Observation myDstu2ObsBad;

    @Before
    public void setUp() throws Exception {

        myActivity = Robolectric.setupActivity(Activity.class);

        Quantity goodQuantity = new Quantity().setUnit(MMOLL_UNIT)
                .setValue(MMOLL_MEASURE);
        myDstu3ObsGood = new Observation().setValue(goodQuantity);

        Quantity badQuantity = new Quantity().setValue(MMOLL_MEASURE);
        myDstu3ObsBad = new Observation().setValue(badQuantity);

        QuantityDt goodQuantityDt = new QuantityDt().setUnit(MMOLL_UNIT)
                .setValue(MMOLL_MEASURE);
        myDstu2ObsGood = new ca.uhn.fhir.model.dstu2.resource.Observation().setValue(goodQuantityDt);

        QuantityDt badQuantityDt = new QuantityDt().setValue(MMOLL_MEASURE);
        myDstu2ObsBad = new ca.uhn.fhir.model.dstu2.resource.Observation().setValue(badQuantityDt);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setObservation() throws Exception {
        mPairDSTU2 = new ObservationPair(myDstu2ObsGood);
        Assert.assertEquals(MMOLL_MEASURE, mPairDSTU2.getValue().doubleValue(), 0);
        Assert.assertEquals(MMOLL_UNIT, mPairDSTU2.getUnit());

        mPairDSTU3 = new ObservationPair(myDstu3ObsGood);
        Assert.assertEquals(MMOLL_MEASURE, mPairDSTU3.getValue().doubleValue(), 0);
        Assert.assertEquals(MMOLL_UNIT, mPairDSTU3.getUnit());
    }

    @Test
    public void getValueQuantityDt() throws Exception {
        Quantity quantity = ObservationPair.getValueQuantityDt(myDstu3ObsGood);
        Assert.assertNotNull(quantity);
        Assert.assertEquals(MMOLL_MEASURE, quantity.getValueElement().getValue().doubleValue(), 0);
        Assert.assertEquals(MMOLL_UNIT, quantity.getUnit());
    }

    @Test
    public void isValid() throws Exception {
        mPairDSTU2 = new ObservationPair(myDstu2ObsBad);
        Assert.assertFalse(mPairDSTU2.isValid());
        mPairDSTU2 = new ObservationPair(myDstu2ObsGood);
        Assert.assertTrue(mPairDSTU2.isValid());

        mPairDSTU3 = new ObservationPair(myDstu3ObsBad);
        Assert.assertFalse(mPairDSTU3.isValid());
        mPairDSTU3 = new ObservationPair(myDstu3ObsGood);
        Assert.assertTrue(mPairDSTU3.isValid());
    }

}