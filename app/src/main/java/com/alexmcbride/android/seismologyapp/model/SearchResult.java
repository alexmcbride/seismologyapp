/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp.model;

public class SearchResult {
    private String mTitle;
    private Earthquake mEarthquake;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Earthquake getEarthquake() {
        return mEarthquake;
    }

    public void setEarthquake(Earthquake earthquake) {
        mEarthquake = earthquake;
    }
}
