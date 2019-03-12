package com.alexmcbride.android.seismologyapp.models;

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
