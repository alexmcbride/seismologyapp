package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.support.v4.app.Fragment;

/*
 * Fragment to represent the earthquake list/detail view. In portrait only list is shown, in landscape
 * both list and detail are shown. In portrait selected event is passed back to main activity.
 */
public class ListMasterDetailFragment extends MasterDetailFragment implements EarthquakeListFragment.OnFragmentInteractionListener {
    private EarthquakeListFragment mEarthquakeListFragment;
    private EarthquakeDetailFragment mEarthquakeDetailFragment;
    private OnFragmentInteractionListener mListener;

    @Override
    protected Fragment getMasterFragment() {
        if (mEarthquakeListFragment == null) {
            mEarthquakeListFragment = EarthquakeListFragment.newInstance();
            mEarthquakeListFragment.setListener(this);
        }
        return mEarthquakeListFragment;
    }

    public static ListMasterDetailFragment newInstance() {
        return new ListMasterDetailFragment();
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
        if (hasDetailsContainer()) {
            Fragment fragment = EarthquakeDetailFragment.newInstance(id);
            updateDetailsContainer(fragment);
        } else {
            // Tell main activity to start a new details activity.
            mListener.onEarthquakeSelected(id);
        }
    }

    void earthquakesUpdated() {
        mEarthquakeListFragment.earthquakesUpdated();
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
