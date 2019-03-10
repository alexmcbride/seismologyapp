package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;

/*
 * Interface to represent a child fragment saving its state. Fragments inside of a parent fragment
 * don't get their bundles saved automatically, so we need to do it for them.
 */
public interface ChildFragment {
    Bundle getSavedState();
    void setSavedState(Bundle bundle);
}
