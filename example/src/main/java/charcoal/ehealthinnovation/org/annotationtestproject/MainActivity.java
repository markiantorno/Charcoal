package charcoal.ehealthinnovation.org.annotationtestproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;

import charcoal.ehealthinnovation.org.charcoaltextview.CharcoalBinder;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.CharcoalWriter;
import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

@Charcoal(asset = "essence.xml")
public class MainActivity extends AppCompatActivity {

    @CharcoalWriter(property = "blood_glucose", defaultUnit = "[mgdl]")
    CharcoalTextView mCharcoalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCharcoalView = findViewById(R.id.test_text_view);
        mCharcoalView.setObservation(generateBloodGlucoseReadingMmol());

        CharcoalBinder.burn(this);
    }

    public Observation generateBloodGlucoseReadingMmol() {
        Quantity quantity = new Quantity().setUnit("mmol/L")
                .setValue(3.7);

        Observation observation = new Observation()
                .setValue(quantity);

        return observation;
    }
}
