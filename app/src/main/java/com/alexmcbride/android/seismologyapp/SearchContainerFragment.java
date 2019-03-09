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

import java.util.Date;

public class SearchContainerFragment extends Fragment {
    private SearchEarthquakesFragment searchEarthquakesFragment;
    private SearchResultsFragment searchResultsFragment;
    private OnFragmentInteractionListener mListener;

    public SearchContainerFragment() {
        // Required
    }

    public static SearchContainerFragment newInstance() {
        return new SearchContainerFragment();
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

        searchEarthquakesFragment = SearchEarthquakesFragment.newInstance();
        searchEarthquakesFragment.setListener(new SearchEarthquakesFragment.OnFragmentInteractionListener() {
            @Override
            public void onSearchEarthquakes(Date start, Date end) {
                if (hasDetailContainer) {
                    searchResultsFragment.updateSearchResults(start, end);
                } else {
                    mListener.onSearchEarthquakes(start, end);
                }
            }
        });
        fm.beginTransaction().add(R.id.masterContainer, searchEarthquakesFragment).commitNow();

        if (hasDetailContainer) {
            searchResultsFragment = SearchResultsFragment.newInstance();
            fm.beginTransaction().add(R.id.detailContainer, searchResultsFragment).commitNow();
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
        void onSearchEarthquakes(Date start, Date end);
    }
}
