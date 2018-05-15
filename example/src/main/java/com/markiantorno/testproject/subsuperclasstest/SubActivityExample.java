package com.markiantorno.testproject.subsuperclasstest;

import android.os.Bundle;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;

/**
 * Created by mabushawish on 2018-01-18.
 */

public class SubActivityExample extends SuperActivityExample{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCharcoalViewMGDL.setObservationDSTU3(generateBloodGlucoseReadingMgdl());
        mCharcoalViewMMOL.setObservationDSTU3(generateBloodGlucoseReadingMmol());
        mUnitOnlyCharcoalViewMMOL.setObservationDSTU3(generateBloodGlucoseReadingMmol());
    }

    public Observation generateBloodGlucoseReadingMmol() {
        Quantity quantity = new Quantity().setCode("m[mol]/l")
                .setValue(3.9);

        Observation observation = new Observation()
                .setValue(quantity);

        return observation;
    }

    public Observation generateBloodGlucoseReadingMgdl() {
        Quantity quantity = new Quantity().setCode("mg/dL")
                .setValue(70);

        Observation observation = new Observation()
                .setValue(quantity);

        return observation;
    }
}
