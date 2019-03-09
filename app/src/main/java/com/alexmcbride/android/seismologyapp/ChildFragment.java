package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;

public interface ChildFragment {
    Bundle getSavedState();
    void setSavedState(Bundle bundle);
}
