package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;

public interface FragmentState {
    Bundle getSavedState();
    void setSavedState(Bundle bundle);
}
