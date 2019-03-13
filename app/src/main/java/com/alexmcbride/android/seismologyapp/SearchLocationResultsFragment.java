package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchLocationResultsFragment extends ChildFragment {
    private static final String ARG_LOCATION = "param1";

    private String mLocation;

    public SearchLocationResultsFragment() {
        // Required empty public constructor
    }

    public static SearchLocationResultsFragment newInstance(String location) {
        SearchLocationResultsFragment fragment = new SearchLocationResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mLocation = arguments.getString(ARG_LOCATION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_search_results, container, false);
    }

    @Override
    public Bundle getSavedState() {
        return null;
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }
}
