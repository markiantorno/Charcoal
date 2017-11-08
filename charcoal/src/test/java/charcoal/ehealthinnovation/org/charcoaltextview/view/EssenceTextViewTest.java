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

/**
 * Created by miantorno on 2017-10-19.
 */
@RunWith(RobolectricTestRunner.class)
public class EssenceTextViewTest {

    private final String WAY_TOO_ACCURATE = "3.14159";
    private final String THAT_IS_BETTTER = "3.14";
    private final String NO_DECIMAL = "314";
    private final int DESIRED_ACCURACY = 2;
    private final int WAY_TOO_MUCH_ACCURACY = 200;
    private final int IMPOSSIBLE_ACCURACY = -2;

    private Decimal myDecimal;
    private Activity myActivity;
    private CharcoalTextView myTextView;

    @Before
    public void setUp() throws Exception {
        myActivity = Robolectric.setupActivity(Activity.class);
        myDecimal = new Decimal(WAY_TOO_ACCURATE);
        myTextView = new CharcoalTextView(myActivity);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAsPrecisionDecimalString() throws Exception {
        Assert.assertEquals(NO_DECIMAL, ConvertAndPopulateViewTask.asPrecisionDecimalString(new Decimal(NO_DECIMAL), DESIRED_ACCURACY));
        Assert.assertEquals(THAT_IS_BETTTER, ConvertAndPopulateViewTask.asPrecisionDecimalString(myDecimal, DESIRED_ACCURACY));
        Assert.assertEquals(WAY_TOO_ACCURATE, ConvertAndPopulateViewTask.asPrecisionDecimalString(myDecimal, WAY_TOO_MUCH_ACCURACY));
        Assert.assertEquals(WAY_TOO_ACCURATE, ConvertAndPopulateViewTask.asPrecisionDecimalString(myDecimal, IMPOSSIBLE_ACCURACY));
    }

    @Test
    public void testCharcoalViewInitialized() throws Exception {
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setUnitString("unit");
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setProperty("property");
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setAccuracy(2);
        Assert.assertFalse(myTextView.charcoalTextViewInitialized());
        myTextView.setFormat("format");
        Assert.assertTrue(myTextView.charcoalTextViewInitialized());
    }
}