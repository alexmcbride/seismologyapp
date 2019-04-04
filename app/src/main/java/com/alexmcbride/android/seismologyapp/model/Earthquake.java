/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp.model;

import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class Earthquake {
    private long id;
    private String mTitle;
    private String mDescription;
    private String mLink;
    private Date mPubDate;
    private String mCategory;
    private double mLat;
    private double mLon;
    private String mLocation;
    private double mDepth;
    private double mMagnitude;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public Date getPubDate() {
        return mPubDate;
    }

    public void setPubDate(Date pubDate) {
        mPubDate = pubDate;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public double getDepth() {
        return mDepth;
    }

    public void setDepth(double depth) {
        this.mDepth = depth;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public void setMagnitude(double magnitude) {
        this.mMagnitude = magnitude;
    }
}
