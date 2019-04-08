/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Fragment to represent the earthquake list/detail view. In portrait only list is shown, in landscape
 * both list and detail are shown. In portrait selected event is passed back to main activity.
 */
public class ListMasterDetailFragment extends MasterDetailFragment implements EarthquakeListFragment.OnFragmentInteractionListener {
    private EarthquakeListFragment mEarthquakeListFragment;
    private OnFragmentInteractionListener mListener;

    public static ListMasterDetailFragment newInstance() {
        return new ListMasterDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (hasDetailsContainer()) {
            updateDetailsContainer(EarthquakeDetailFragment.newInstance());
        }

        return view;
    }

    @Override
    protected ChildFragment getMasterFragment() {
        if (mEarthquakeListFragment == null) {
            mEarthquakeListFragment = EarthquakeListFragment.newInstance();
            mEarthquakeListFragment.setListener(this);
        }
        return mEarthquakeListFragment;
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

    @Override
    public void onEarthquakeSelected(long id) {
        // If details container present then update that. Otherwise tell the MainActivity to deal
        // with request.
        if (hasDetailsContainer()) {
            EarthquakeDetailFragment fragment = EarthquakeDetailFragment.newInstance(id);
            updateDetailsContainer(fragment);
        } else {
            // Tell main activity to start a new details activity.
            mListener.onEarthquakeSelected(id);
        }
    }

    /*
     * Called when the earthquakes in the DB have changed, tell child fragment to update.
     */
    void earthquakesUpdated() {
        mEarthquakeListFragment.earthquakesUpdated();
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
