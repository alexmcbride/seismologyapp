package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;

import com.google.common.collect.Lists;

import java.util.List;

class ChildFragmentManager {
    private List<ChildFragment> mFragments = Lists.newArrayList();

    void add(ChildFragment fragment) {
        mFragments.add(fragment);
    }

    void saveState(Bundle bundle) {
        for (ChildFragment fragment : mFragments) {
            fragment.setSavedState(bundle);
        }
    }

    void loadState(Bundle bundle) {
        if (bundle != null) {
            for (ChildFragment fragment : mFragments) {
                Bundle state = fragment.getSavedState();
                bundle.putAll(state);
            }
        }
    }
}
