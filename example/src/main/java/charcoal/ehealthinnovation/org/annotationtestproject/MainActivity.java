package charcoal.ehealthinnovation.org.annotationtestproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;

import charcoal.ehealthinnovation.org.charcoaltextview.CharcoalBinder;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Essence;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.PreferenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

@Essence(asset = "essence.xml")
public class MainActivity extends AppCompatActivity {

    @Charcoal(property = "blood_glucose", defaultUnit = "mg/dL")
    CharcoalTextView mCharcoalViewMGDL;

    @Charcoal(property = "blood_glucose", defaultUnit = "m[mol]/L")
    CharcoalTextView mCharcoalViewMMOL;

    @Charcoal(property = "blood_glucose", defaultUnit = "mg/dL", format = "%2$s")
    CharcoalTextView mUnitOnlyCharcoalViewMMOL;

    SwitchCompat mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCharcoalViewMMOL = findViewById(R.id.unit_field_mmoll);
        mCharcoalViewMMOL.setObservationDSTU3(generateBloodGlucoseReadingMmol());

        mCharcoalViewMGDL = findViewById(R.id.unit_field_mgdl);
        mCharcoalViewMGDL.setObservationDSTU3(generateBloodGlucoseReadingMgdl());

        mUnitOnlyCharcoalViewMMOL = findViewById(R.id.unit_only_field_mgdl);
        mUnitOnlyCharcoalViewMMOL.setObservationDSTU3(generateBloodGlucoseReadingMmol());

        mSwitch = findViewById(R.id.pref_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceController.setUnitForProperty(getApplicationContext(), "blood_glucose", isChecked ? "mg/dL" : "m[mol]/L");
            }
        });

        PreferenceController.setUnitForProperty(this,"blood_glucose", "m[mol]/L");

        CharcoalBinder.burn(this);
    }

    public Observation generateBloodGlucoseReadingMmol() {
        Quantity quantity = new Quantity().setUnit("m[mol]/l")
                .setValue(3.9);

        Observation observation = new Observation()
                .setValue(quantity);

        return observation;
    }

    public Observation generateBloodGlucoseReadingMgdl() {
        Quantity quantity = new Quantity().setUnit("mg/dL")
                .setValue(70);

        Observation observation = new Observation()
                .setValue(quantity);

        return observation;
    }
}
