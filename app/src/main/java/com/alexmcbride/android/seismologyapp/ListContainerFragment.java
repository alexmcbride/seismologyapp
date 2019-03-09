package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListContainerFragment extends Fragment {
    private EarthquakeDetailFragment mEarthquakeDetailFragment;
    private OnFragmentInteractionListener mListener;

    public ListContainerFragment() {
        // Required
    }

    public static ListContainerFragment newInstance() {
        return new ListContainerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final boolean hasDetailContainer = view.findViewById(R.id.detailContainer) != null;
        final FragmentManager fm = getChildFragmentManager();

        EarthquakeListFragment earthquakeListFragment = EarthquakeListFragment.newInstance();
        earthquakeListFragment.setListener(new EarthquakeListFragment.OnFragmentInteractionListener() {
            @Override
            public void onEarthquakeSelected(long id) {
                if (hasDetailContainer) {
                    mEarthquakeDetailFragment.updateEarthquake(id);
                } else {
                    mListener.onEarthquakeSelected(id);
                }
            }
        });
        fm.beginTransaction().replace(R.id.masterContainer, earthquakeListFragment).commitNow();

        if (hasDetailContainer) {
            mEarthquakeDetailFragment = EarthquakeDetailFragment.newInstance(0);
            fm.beginTransaction().replace(R.id.detailContainer, mEarthquakeDetailFragment).commitNow();
        }
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
