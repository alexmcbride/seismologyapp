package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.Date;

/*
 * Fragment to represent the search/results view. In portrait only search is shown, in landscape
 * both search and results are shown. In portrait search event is passed back to main activity.
 */
public class SearchMasterDetailFragment extends MasterDetailFragment implements SearchEarthquakesFragment.OnFragmentInteractionListener {
    private SearchEarthquakesFragment mSearchEarthquakesFragment;
    private SearchResultsFragment mSearchResultsFragment;
    private OnFragmentInteractionListener mListener;

    public SearchMasterDetailFragment() {
        super();
    }

    @Override
    protected Fragment getMasterFragment() {
        if (mSearchEarthquakesFragment == null) {
            mSearchEarthquakesFragment = SearchEarthquakesFragment.newInstance();
            mSearchEarthquakesFragment.setListener(this);
        }
        return mSearchEarthquakesFragment;
    }

    @Override
    protected Fragment getDetailsFragment() {
        if (mSearchResultsFragment == null) {
            mSearchResultsFragment = SearchResultsFragment.newInstance();
        }
        return mSearchResultsFragment;
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
            mSearchResultsFragment.updateSearchResults(start, end);
        } else {
            mListener.onSearchEarthquakes(start, end);
        }
    }

    public interface OnFragmentInteractionListener {
        void onSearchEarthquakes(Date start, Date end);
    }
}