package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class EarthquakeListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public EarthquakeListFragment() {
        // Required empty public constructor
    }

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        Button buttonEarthquakeSelected = view.findViewById(R.id.buttonEarthquakeSelected);
        buttonEarthquakeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEarthquakeSelected(1);
            }
        });

        return view;
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
