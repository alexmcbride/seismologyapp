/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp;

import android.content.Context;

import java.util.Date;

/*
 * Fragment to represent the search/results view. In portrait only search is shown, in landscape
 * both search and results are shown. In portrait search event is passed back to main activity.
 */
public class SearchMasterDetailFragment extends MasterDetailFragment implements SearchEarthquakesFragment.OnFragmentInteractionListener {
    private SearchEarthquakesFragment mSearchEarthquakesFragment;
    private OnFragmentInteractionListener mListener;

    public SearchMasterDetailFragment() {
        super();
    }

    @Override
    protected ChildFragment getMasterFragment() {
        if (mSearchEarthquakesFragment == null) {
            mSearchEarthquakesFragment = SearchEarthquakesFragment.newInstance();
            mSearchEarthquakesFragment.setListener(this);
        }
        return mSearchEarthquakesFragment;
    }

    public static SearchMasterDetailFragment newInstance() {
        return new SearchMasterDetailFragment();
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
    public void onSearchEarthquakes(Date start, Date end) {
        if (hasDetailsContainer()) {
            SearchDateResultsFragment fragment = SearchDateResultsFragment.newInstance(start, end);
            fragment.setListener(new SearchDateResultsFragment.OnFragmentInteractionListener() {
                @Override
                public void onEarthquakeSelected(long id) {
                    mListener.onEarthquakeSelected(id);
                }
            });
            updateDetailsContainer(fragment);
        } else {
            mListener.onSearchEarthquakes(start, end);
        }
    }

    @Override
    public void onSearchEarthquakes(String location) {
        if (hasDetailsContainer()) {
            SearchLocationResultsFragment fragment = SearchLocationResultsFragment.newInstance(location);
            fragment.setListener(new SearchLocationResultsFragment.OnFragmentInteractionListener() {
                @Override
                public void onEarthquakeSelected(long id) {
                    mListener.onEarthquakeSelected(id);
                }
            });
            updateDetailsContainer(fragment);
        } else {
            mListener.onSearchEarthquakes(location);
        }
    }

    public interface OnFragmentInteractionListener {
        void onSearchEarthquakes(Date start, Date end);
        void onSearchEarthquakes(String location);
        void onEarthquakeSelected(long id);
    }
}
