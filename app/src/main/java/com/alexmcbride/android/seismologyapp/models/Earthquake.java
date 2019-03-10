package com.alexmcbride.android.seismologyapp.models;

import java.util.Date;

class Earthquake {
    private long id;
    private String mTitle;
    private String mDescription;
    private String mLink;
    private Date mPubDate;
    private String mCategory;
    private double mLat;
    private double mLon;

    long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        mTitle = title;
    }

    String getDescription() {
        return mDescription;
    }

    void setDescription(String description) {
        mDescription = description;
    }

    String getLink() {
        return mLink;
    }

    void setLink(String link) {
        mLink = link;
    }

    Date getPubDate() {
        return mPubDate;
    }

    void setPubDate(Date pubDate) {
        mPubDate = pubDate;
    }

    String getCategory() {
        return mCategory;
    }

    void setCategory(String category) {
        mCategory = category;
    }

    double getLat() {
        return mLat;
    }

    void setLat(double lat) {
        mLat = lat;
    }

    double getLon() {
        return mLon;
    }

    void setLon(double lon) {
        mLon = lon;
    }
}
