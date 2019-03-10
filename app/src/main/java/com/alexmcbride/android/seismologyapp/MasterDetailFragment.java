package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class MasterDetailFragment extends Fragment {
    private boolean mHasDetailsContainer;

    public MasterDetailFragment() {
        // Required empty public constructor
    }

    protected abstract Fragment getMasterFragment();
    protected abstract Fragment getDetailsFragment();

    boolean hasDetailsContainer() {
        return mHasDetailsContainer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_detail, container, false);

        Fragment masterFragment = getMasterFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerMaster, masterFragment).commitNow();
        if (savedInstanceState != null) {
            ((ChildFragment) masterFragment).setSavedState(savedInstanceState);
        }

        if (view.findViewById(R.id.containerDetail) != null) {
            mHasDetailsContainer = true;
            Fragment detailFragment = getDetailsFragment();
            fragmentManager.beginTransaction().replace(R.id.containerDetail, detailFragment).commitNow();
            if (savedInstanceState != null) {
                ((ChildFragment) detailFragment).setSavedState(savedInstanceState);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle masterState = ((ChildFragment) getMasterFragment()).getSavedState();
        outState.putAll(masterState);

        if (mHasDetailsContainer) {
            Bundle detailState = ((ChildFragment) getDetailsFragment()).getSavedState();
            outState.putAll(detailState);
        }
    }
}
