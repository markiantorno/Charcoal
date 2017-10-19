package charcoal.ehealthinnovation.org.charcoaltextview.view;

import android.app.Activity;

import junit.framework.Assert;

import org.fhir.ucum.Decimal;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.CharcoalWriter;

import static org.junit.Assert.*;

/**
 * Created by miantorno on 2017-10-19.
 */
@RunWith(RobolectricTestRunner.class)
public class CharcoalTextViewTest {

    private final String MMOLL_UNIT = "mmol/L";
    private final double MMOLL_MEASURE = 3.7;

    private final String WAY_TOO_ACCURATE = "3.14159";
    private final String THAT_IS_BETTTER = "3.14";
    private final String NO_DECIMAL = "314";
    private final int DESIRED_ACCURACY = 2;
    private final int WAY_TOO_MUCH_ACCURACY = 200;
    private final int IMPOSSIBLE_ACCURACY = -2;

    private Observation myDstu3ObsGood;
    private Observation myDstu3ObsBad;

    private Decimal myDecimal;

    private Activity myActivity;

    private CharcoalTextView myTextView;

    @Before
    public void setUp() throws Exception {
        myActivity = Robolectric.setupActivity(Activity.class);

        Quantity goodQuantity = new Quantity().setUnit(MMOLL_UNIT)
                .setValue(MMOLL_MEASURE);
        myDstu3ObsGood = new Observation().setValue(goodQuantity);

        Quantity badQuantity = new Quantity().setValue(MMOLL_MEASURE);
        myDstu3ObsBad = new Observation().setValue(badQuantity);

        myDecimal = new Decimal(WAY_TOO_ACCURATE);

        myTextView = new CharcoalTextView(myActivity);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetQuantityDSTU3() throws Exception {
        Quantity valueQuantityDt = myTextView.getValueQuantityDt(myDstu3ObsGood);
        Assert.assertEquals(MMOLL_UNIT, valueQuantityDt.getUnit());
        Assert.assertEquals(MMOLL_MEASURE, valueQuantityDt.getValue().doubleValue());
    }

    @Test
    public void testUnitAndValueSet() throws Exception {
        Assert.assertTrue(myTextView.unitAndValueSet(myTextView.getValueQuantityDt(myDstu3ObsGood)));
        Assert.assertFalse(myTextView.unitAndValueSet(myTextView.getValueQuantityDt(myDstu3ObsBad)));
    }

    @Test
    public void testAsPrecisionDecimalString() throws Exception {
        Assert.assertEquals(NO_DECIMAL, CharcoalTextView.asPrecisionDecimalString(new Decimal(NO_DECIMAL), DESIRED_ACCURACY));
        Assert.assertEquals(THAT_IS_BETTTER, CharcoalTextView.asPrecisionDecimalString(myDecimal, DESIRED_ACCURACY));
        Assert.assertEquals(WAY_TOO_ACCURATE, CharcoalTextView.asPrecisionDecimalString(myDecimal, WAY_TOO_MUCH_ACCURACY));
        Assert.assertEquals(WAY_TOO_ACCURATE, CharcoalTextView.asPrecisionDecimalString(myDecimal, IMPOSSIBLE_ACCURACY));
    }

    @Test
    public void testCharcoalViewInitialized() throws Exception {
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setUnitCodeUC("unit");
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setProperty("property");
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setAccuracy(2);
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setFormat("format");
        Assert.assertTrue(myTextView.charcoalTextViewInitialized());
    }
}