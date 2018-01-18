package com.markiantorno.testproject.subsuperclasstest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;

import com.markiantorno.charcoal.CharcoalBinder;
import com.markiantorno.charcoal.annotation.Charcoal;
import com.markiantorno.charcoal.controller.PreferenceController;
import com.markiantorno.charcoal.view.CharcoalTextView;
import com.markiantorno.testproject.R;

public abstract class SuperActivityExample extends AppCompatActivity {

    public static final String BLOOD_GLUCOSE_PROPERTY = "blood_glucose";
    public static final String UNIT_MMOLL = "m[mol]/L";
    public static final String UNIT_MGDL = "mg/dL";

    @Charcoal(property = BLOOD_GLUCOSE_PROPERTY, defaultUnit = UNIT_MGDL, accuracy = 1)
    protected CharcoalTextView mCharcoalViewMGDL;

    @Charcoal(property = BLOOD_GLUCOSE_PROPERTY, defaultUnit = UNIT_MMOLL, accuracy = 1)
    protected CharcoalTextView mCharcoalViewMMOL;

    @Charcoal(property = BLOOD_GLUCOSE_PROPERTY, defaultUnit = UNIT_MGDL, format = "%2$s")
    protected CharcoalTextView mUnitOnlyCharcoalViewMMOL;

    private SwitchCompat mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_unit_test);

        mCharcoalViewMMOL = findViewById(R.id.unit_field_mmoll);
        mCharcoalViewMGDL = findViewById(R.id.unit_field_mgdl);
        mUnitOnlyCharcoalViewMMOL = findViewById(R.id.unit_only_field_mgdl);

        CharcoalBinder.burn(this);

        PreferenceController.setUnitForProperty(this, BLOOD_GLUCOSE_PROPERTY, UNIT_MMOLL);
        PreferenceController.setAccuracyForUnit(this, UNIT_MMOLL, 1);
        PreferenceController.setAccuracyForUnit(this, UNIT_MGDL, 0);

        mSwitch = findViewById(R.id.pref_switch);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                PreferenceController.setUnitForProperty(SuperActivityExample.this, BLOOD_GLUCOSE_PROPERTY, isChecked ? UNIT_MGDL : UNIT_MMOLL));
    }
}
