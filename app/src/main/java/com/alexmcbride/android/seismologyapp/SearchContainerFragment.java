package com.alexmcbride.android.seismologyapp;

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

        FragmentManager fm = getChildFragmentManager();

        searchEarthquakesFragment = SearchEarthquakesFragment.newInstance();
        fm.beginTransaction().add(R.id.masterContainer, searchEarthquakesFragment).commitNow();

        if (view.findViewById(R.id.detailContainer) != null) {
            searchResultsFragment = SearchResultsFragment.newInstance();
            fm.beginTransaction().add(R.id.detailContainer, searchResultsFragment).commitNow();
        }
    }

    void searchEarthquakes(Date start, Date end) {
        if (searchResultsFragment !=null) {
            searchResultsFragment.updateSearchResults(start, end);
        }
    }
}
