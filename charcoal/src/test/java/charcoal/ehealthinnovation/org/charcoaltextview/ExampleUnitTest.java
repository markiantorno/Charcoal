package charcoal.ehealthinnovation.org.charcoaltextview;

import android.app.Activity;

import org.fhir.ucum.UcumEssenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        Activity activity = Robolectric.setupActivity(Activity.class);

        UcumEssenceService mUcumService = new UcumEssenceService(activity.getAssets().open("test_essence_file.xml"));
    }
}