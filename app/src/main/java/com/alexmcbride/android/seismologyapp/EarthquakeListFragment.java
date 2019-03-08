package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Objects;

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

        final FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        final FrameLayout earthquakeDetailsContainer = view.findViewById(R.id.earthquakeDetailsContainer);

        Button buttonEarthquakeSelected = view.findViewById(R.id.buttonEarthquakeSelected);
        buttonEarthquakeSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (earthquakeDetailsContainer == null) {
                    mListener.onEarthquakeSelected(1);
                } else {
                    Fragment fragment = EarthquakeDetailFragment.newInstance(0);
                    fm.beginTransaction().replace(R.id.earthquakeDetailsContainer, fragment).commitNow();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
