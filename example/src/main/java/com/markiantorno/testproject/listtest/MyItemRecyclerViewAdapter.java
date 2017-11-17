package com.markiantorno.testproject.listtest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hl7.fhir.dstu3.model.Observation;

import com.markiantorno.testproject.R;
import com.markiantorno.testproject.listtest.ItemFragment.OnListFragmentInteractionListener;
import com.markiantorno.charcoal.CharcoalBinder;
import com.markiantorno.charcoal.annotation.Charcoal;
import com.markiantorno.charcoal.view.CharcoalTextView;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ca.uhn.fhir.model.dstu2.resource.Observation} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Observation> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<Observation> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCharcoalView.setObservationDSTU3(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @Charcoal(property = "blood_glucose", defaultUnit = "mg/dL", accuracy = 0, format = "%1$s %2$s")
        public final CharcoalTextView mCharcoalView;

        public Observation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCharcoalView = view.findViewById(R.id.charcoal_test_view);
            CharcoalBinder.burn(this, view);
        }
    }
}
