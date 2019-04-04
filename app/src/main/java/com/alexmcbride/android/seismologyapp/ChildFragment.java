/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/*
 * Abstract class to represent a child fragment. Fragments inside of a parent fragment don't get
 * their bundles saved automatically, so we need to do it for them.
 */
public abstract class ChildFragment extends Fragment {
    public abstract Bundle getSavedState();
    public abstract void loadSavedState(Bundle bundle);
}
