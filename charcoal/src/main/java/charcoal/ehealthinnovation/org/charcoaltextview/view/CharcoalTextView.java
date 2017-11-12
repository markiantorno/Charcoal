package charcoal.ehealthinnovation.org.charcoaltextview.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import org.hl7.fhir.dstu3.model.Observation;

import charcoal.ehealthinnovation.org.charcoaltextview.annotation.Charcoal;
import charcoal.ehealthinnovation.org.charcoaltextview.controller.PreferenceController;
import charcoal.ehealthinnovation.org.charcoaltextview.loader.ConvertUnitTask;
import charcoal.ehealthinnovation.org.charcoaltextview.loader.ConvertUnitThread;
import charcoal.ehealthinnovation.org.charcoaltextview.loader.CustomThreadPoolManager;
import charcoal.ehealthinnovation.org.charcoaltextview.pojo.ObservationPair;

/**
 * Extended {@link android.widget.TextView} used to display {@link Observation} with the correct units.
 * <p>
 * Created by miantorno on 2017-10-13.
 */
public class CharcoalTextView extends AppCompatTextView implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected final static String TAG = CharcoalTextView.class.getSimpleName();

    // Display formatting
    protected String mUnitString;
    protected int mAccuracy;
    protected String mProperty;
    protected String mFormat;

    // Observation to display
    protected ObservationPair mCurrentObservation;
//    protected ConvertUnitTask mLoadTask;
    protected ConvertUnitThread mThread;

    // Constructors

    public CharcoalTextView(Context context) {
        super(context);
    }

    public CharcoalTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CharcoalTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Getters and Setters

    public String getUnitString() {
        return PreferenceController.getUnitForProperty(this.getContext(), getProperty(), mUnitString);
    }

    public void setUnitString(String unit) {
        this.mUnitString = unit;
    }

    public String getProperty() {
        return mProperty;
    }

    public void setProperty(String property) {
        this.mProperty = property;
    }

    public int getAccuracy() {
        return PreferenceController.getAccuracyForUnit(this.getContext(), getUnitString(), mAccuracy);
    }

    public void setAccuracy(int accuracy) {
        this.mAccuracy = accuracy;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        this.mFormat = format;
    }

    /**
     * Resets the view. Triggers a recalculation of unit, and conversion type.
     */
    public void relight() {
        if ((mCurrentObservation != null) && (mCurrentObservation.isValid())) {
            setAndFormatText(mCurrentObservation);
        }
    }

    /**
     * Sets the view to display the value of the passed in {@link Observation}.
     *
     * @param obs {@link Observation} to display value of.
     */
    public void setObservationDSTU3(@NonNull Observation obs) {
        mCurrentObservation = new ObservationPair(obs);
        relight();
    }

    /**
     * Sets the view to display the value of the passed in {@link ca.uhn.fhir.model.dstu2.resource.Observation}.
     *
     * @param obs {@link ca.uhn.fhir.model.dstu2.resource.Observation} to display value of.
     */
    public void setObservationDSTU2(@NonNull ca.uhn.fhir.model.dstu2.resource.Observation obs) {
        mCurrentObservation = new ObservationPair(obs);
        relight();
    }

    private void setAndFormatText(@NonNull ObservationPair observationPair) {

        Log.d(TAG, "Attempting to display value: " + observationPair.getValue() + ", with corresponding " +
                "unit: " + observationPair.getUnit());

        if (charcoalTextViewInitialized()) {
//            /*
//             * We save the instance of the running load task, for android use cases like Recycler
//             * views, where the same view could get used over and over, or in cases where data may
//             * change rapidly and need to be reloaded.
//             */
//            if (mThread != null) {
//                mThread.stop();
//            }
//            mThread = new ConvertUnitThread(this, getUnitString(), getAccuracy(), getFormat(), observationPair);
//            mThread.run();
            CustomThreadPoolManager.getsInstance().addCallable(new ConvertUnitThread(this, getUnitString(), getAccuracy(), getFormat(), observationPair));
        } else {
            Log.e(TAG, "CharcoalTextView not initialized. Displaying as plain number...");
            setText(String.valueOf(observationPair.getValue()));
        }
    }

    /**
     * Displays the passed in value along with given unit, as per the current {@link CharcoalTextView#mFormat}.
     *
     * @param value Measurement value to display, as a {@link String}.
     * @param unit  UCUM unit {@link String} to use with value.
     */
    protected void displayObservationValue(String value, String unit) {
        setText(String.format(getFormat(), value, unit));
    }

    /**
     * For proper display of {@link Observation} values, all {@link Charcoal} annotation values
     * must be initialized.
     *
     * @return {@link Boolean#TRUE} if view has been initialized properly.
     */
    protected boolean charcoalTextViewInitialized() {
        Log.d(TAG, "Initialization check -> " +
                "\naccuracy :: " + mAccuracy +
                "\naccuracy :: " + mFormat +
                "\naccuracy :: " + mProperty +
                "\naccuracy :: " + mUnitString
        );
        return ((mAccuracy >= 0)
                && (mFormat != null)
                && (mProperty != null)
                && (mUnitString != null));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getProperty()) || key.equals(mUnitString)) {
            Log.d(TAG, "Property change triggered for property: " + key);
            relight();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PreferenceController.registerListener(getContext(), this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PreferenceController.unregisterListener(getContext(), this);
    }
}
