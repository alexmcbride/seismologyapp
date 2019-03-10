package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.alexmcbride.android.seismologyapp.models.Earthquake;

import java.util.List;

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

    @Override
    protected Fragment getDetailsFragment() {
        if (mEarthquakeDetailFragment == null) {
            mEarthquakeDetailFragment = EarthquakeDetailFragment.newInstance();
        }
        return mEarthquakeDetailFragment;
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
            mEarthquakeDetailFragment.updateEarthquake(id);
        } else {
            mListener.onEarthquakeSelected(id);
        }
    }

    void updateEarthquakes(List<Earthquake> earthquakes) {
        mEarthquakeListFragment.updateEarthquakes(earthquakes);
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
