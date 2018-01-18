package com.markiantorno.testproject.basetest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.markiantorno.charcoal.CharcoalBinder;
import com.markiantorno.charcoal.annotation.Charcoal;
import com.markiantorno.charcoal.controller.PreferenceController;
import com.markiantorno.charcoal.view.CharcoalTextView;
import com.markiantorno.testproject.R;

import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseExample.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseExample#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseExample extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String BLOOD_GLUCOSE_PROPERTY = "blood_glucose";
    private static final String UNIT_MMOLL = "m[mol]/L";
    private static final String UNIT_MGDL = "mg/dL";

    @Charcoal(property = BLOOD_GLUCOSE_PROPERTY, defaultUnit = UNIT_MGDL)
    protected CharcoalTextView mCharcoalViewMGDL;

    @Charcoal(property = BLOOD_GLUCOSE_PROPERTY, defaultUnit = UNIT_MMOLL)
    protected CharcoalTextView mCharcoalViewMMOL;

    @Charcoal(property = BLOOD_GLUCOSE_PROPERTY, defaultUnit = UNIT_MGDL, format = "%2$s")
    protected CharcoalTextView mUnitOnlyCharcoalViewMMOL;

    private SwitchCompat mSwitch;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BaseExample() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseExample.
     */
    public static BaseExample newInstance(String param1, String param2) {
        BaseExample fragment = new BaseExample();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.single_unit_test, container, false);
        mCharcoalViewMMOL = view.findViewById(R.id.unit_field_mmoll);
        mCharcoalViewMGDL = view.findViewById(R.id.unit_field_mgdl);
        mUnitOnlyCharcoalViewMMOL = view.findViewById(R.id.unit_only_field_mgdl);

        CharcoalBinder.burn(this, view);

        PreferenceController.setUnitForProperty(getActivity(),BLOOD_GLUCOSE_PROPERTY, UNIT_MMOLL);
        PreferenceController.setAccuracyForUnit(getActivity(), UNIT_MMOLL, 1);
        PreferenceController.setAccuracyForUnit(getActivity(), UNIT_MGDL, 0);

        mCharcoalViewMMOL.setObservationDSTU3(generateBloodGlucoseReadingMmol());
        mCharcoalViewMGDL.setObservationDSTU3(generateBloodGlucoseReadingMgdl());
        mUnitOnlyCharcoalViewMMOL.setObservationDSTU3(generateBloodGlucoseReadingMmol());

        mSwitch = view.findViewById(R.id.pref_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceController.setUnitForProperty(getContext(), BLOOD_GLUCOSE_PROPERTY, isChecked ? UNIT_MGDL : UNIT_MMOLL);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
