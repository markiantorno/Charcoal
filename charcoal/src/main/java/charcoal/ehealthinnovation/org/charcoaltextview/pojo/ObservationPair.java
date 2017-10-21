package charcoal.ehealthinnovation.org.charcoaltextview.pojo;

import android.support.annotation.NonNull;
import android.util.Log;

import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.exceptions.FHIRException;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;

/**
 * A representation of {@link ca.uhn.fhir.model.dstu2.resource.Observation} or {@link org.hl7.fhir.dstu3.model.Observation}
 * for the purpose of displaying a {@link Double} with corresponding {@link String} unit.
 * <p>
 * Created by mark on 2017-10-21.
 */
public class ObservationPair {

    public static final String TAG = ObservationPair.class.getSimpleName();

    private Double mValue;
    private String mUnit;

    public ObservationPair(@NonNull Observation observationDTSU2) {
        setObservation(observationDTSU2);
    }

    public void setObservation(@NonNull Observation observationDSTU2) {
        //Check value of passed in observation
        QuantityDt quatityDt = (QuantityDt) observationDSTU2.getValue();

        if (quatityDt != null) {
            if ((quatityDt.getValueElement() != null) && (quatityDt.getValueElement().getValueAsNumber() != null)) {
                mValue = quatityDt.getValueElement().getValueAsNumber().doubleValue();
            } else {
                mValue = null;
                Log.e(TAG, "No value set for observation with id :: " + observationDSTU2.getId());
            }
            if (quatityDt.getUnit() != null) {
                mUnit = quatityDt.getUnit();
            } else {
                mUnit = null;
                Log.e(TAG, "No unit set for observation with id :: " + observationDSTU2.getId());
            }
        } else {
            Log.e(TAG, "No QuantityDt set for observation with id :: " + observationDSTU2.getId());
        }
    }

    public ObservationPair(@NonNull org.hl7.fhir.dstu3.model.Observation observationDTSU3) {
        setObservation(observationDTSU3);
    }

    public void setObservation(@NonNull org.hl7.fhir.dstu3.model.Observation observationDTSU3) {
        //Check value of passed in observation
        Quantity quatityDt = getValueQuantityDt(observationDTSU3);

        if (quatityDt != null) {
            if ((quatityDt.getValueElement() != null) && (quatityDt.getValueElement().getValueAsNumber() != null)) {
                mValue = quatityDt.getValueElement().getValueAsNumber().doubleValue();
            } else {
                mValue = null;
                Log.e(TAG, "No value set for observation with id :: " + observationDTSU3.getId());
            }
            if (quatityDt.getUnit() != null) {
                mUnit = quatityDt.getUnit();
            } else {
                mUnit = null;
                Log.e(TAG, "No unit set for observation with id :: " + observationDTSU3.getId());
            }
        } else {
            Log.e(TAG, "No QuantityDt set for observation with id :: " + observationDTSU3.getId());
        }
    }

    public Double getValue() {
        return mValue;
    }

    public String getUnit() {
        return mUnit;
    }

    /**
     * Returns the {@link Quantity} for the passed in fhir {@link org.hl7.fhir.dstu3.model.Observation}, or null, if no
     * quantity is set.
     *
     * @param observation {@link org.hl7.fhir.dstu3.model.Observation} to extract {@link Quantity} from.
     * @return {@link Quantity} for given {@link org.hl7.fhir.dstu3.model.Observation}
     */
    protected static Quantity getValueQuantityDt(@NonNull org.hl7.fhir.dstu3.model.Observation observation) {
        Quantity quatityDt = null;
        try {
            quatityDt = observation.getValueQuantity();
        } catch (FHIRException e) {
            Log.e(TAG, "Passed in observation with id: " + observation.getId() + ", has no set quantityDt!");
        }

        return quatityDt;
    }

    /**
     * Returns true if both value and unit are set.
     * @return {@link Boolean#TRUE} if both {@link ObservationPair#mValue} and {@link ObservationPair#mUnit}
     * conatain non null values.
     */
    public boolean isValid() {
        return ((mValue != null) && (mUnit != null));
    }

}
