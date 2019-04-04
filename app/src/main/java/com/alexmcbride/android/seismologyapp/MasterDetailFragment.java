/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Abstract fragment to manage master/detail relationship between child fragments. When in portrait
 * mode it shows only the master fragment. Inn landscape it shows with master and detail. Child
 * classes implement their own functionality and call updateDetailsContainer() to update the
 * current details fragment.
 */
public abstract class MasterDetailFragment extends Fragment {
    private boolean mHasDetailsContainer;
    private ChildFragment mDetailsFragment;

    public MasterDetailFragment() {
        // Required empty public constructor
    }

    /*
     * Overridden in child to provide the master fragment, which is always shown.
     */
    protected abstract ChildFragment getMasterFragment();

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

        // Load master fragment into container.
        ChildFragment masterFragment = getMasterFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerMaster, masterFragment).commitNow();

        // Tell master fragment to load its state.
        // todo: need to figure out way to tell detail to load state, but not needed right now.
        if (savedInstanceState != null) {
            masterFragment.loadSavedState(savedInstanceState);
        }

        // Details container will only exist when in portrait mode.
        mHasDetailsContainer = view.findViewById(R.id.containerDetail) != null;

        return view;
    }

    protected void updateDetailsContainer(ChildFragment fragment) {
        if (mHasDetailsContainer) {
            mDetailsFragment = fragment;
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.containerDetail, fragment)
                    .commitNow();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Tell child fragments to save their state to this bundle.
        Bundle masterState = getMasterFragment().getSavedState();
        mergeBundles(masterState, outState);

        if (mDetailsFragment != null) {
            Bundle detailsState = mDetailsFragment.getSavedState();
            mergeBundles(detailsState, outState);
        }
    }

    private void mergeBundles(Bundle state, Bundle outState) {
        if (state != null) {
            outState.putAll(state);
        }
    }
}
