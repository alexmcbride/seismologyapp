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
    private ChildFragmentManager mChildFragmentManager = new ChildFragmentManager();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mChildFragmentManager.saveState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchEarthquakesFragment = SearchEarthquakesFragment.newInstance();
        searchResultsFragment = SearchResultsFragment.newInstance();

        mChildFragmentManager.add(searchEarthquakesFragment);
        mChildFragmentManager.add(searchResultsFragment);
        mChildFragmentManager.loadState(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_container, container, false);

        final boolean hasDetailContainer = view.findViewById(R.id.detailContainer) != null;
        final FragmentManager fm = getChildFragmentManager();

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
        fm.beginTransaction().replace(R.id.masterContainer, searchEarthquakesFragment).commitNow();

        if (hasDetailContainer) {
            fm.beginTransaction().replace(R.id.detailContainer, searchResultsFragment).commitNow();
        }

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
        void onSearchEarthquakes(Date start, Date end);
    }
}
