package charcoal.ehealthinnovation.org.annotationtestproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;

import charcoal.ehealthinnovation.org.charcoaltextview.CharcoalBinder;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.annotation.CharcoalWriter;
import charcoal.ehealthinnovation.org.charcoaltextview.preferences.PreferenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.view.CharcoalTextView;

@Charcoal(asset = "essence.xml")
public class MainActivity extends AppCompatActivity {

    @CharcoalWriter(property = "blood_glucose", defaultUnit = "[MGDL]")
    CharcoalTextView mCharcoalViewMGDL;

    @CharcoalWriter(property = "blood_glucose", defaultUnit = "MMOLL")
    CharcoalTextView mCharcoalViewMMOL;

    SwitchCompat mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCharcoalViewMMOL = findViewById(R.id.unit_field_mmoll);
        mCharcoalViewMMOL.setObservation(generateBloodGlucoseReadingMmol());

        mCharcoalViewMGDL = findViewById(R.id.unit_field_mgdl);
        mCharcoalViewMGDL.setObservation(generateBloodGlucoseReadingMgdl());

        mSwitch = findViewById(R.id.pref_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceController.setUnitForPropety(getApplicationContext(), "blood_glucose", isChecked ? "[mgdl]" : "mmol/L");
            }
        });

        PreferenceController.setUnitForPropety(this,"blood_glucose", "mmol/L");

        CharcoalBinder.burn(this);
    }

    public Observation generateBloodGlucoseReadingMmol() {
        Quantity quantity = new Quantity().setUnit("mmol/l")
                .setValue(4.7);

        Observation observation = new Observation()
                .setValue(quantity);

        return observation;
    }

    public Observation generateBloodGlucoseReadingMgdl() {
        Quantity quantity = new Quantity().setUnit("[mgdl]")
                .setValue(70);

        Observation observation = new Observation()
                .setValue(quantity);

        return observation;
    }
}
