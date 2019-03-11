package com.alexmcbride.android.seismologyapp.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

class EarthquakeCursorWrapper extends android.database.CursorWrapper {
    EarthquakeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    Earthquake getEarthquake() {
        Earthquake earthquake = new Earthquake();
        earthquake.setId(getLong(0));
        earthquake.setTitle(getString(1));
        earthquake.setDescription(getString(2));
        earthquake.setLink(getString(3));
        earthquake.setPubDate(new Date(getLong(4)));
        earthquake.setCategory(getString(5));
        earthquake.setLat(getDouble(6));
        earthquake.setLon(getDouble(7));
        earthquake.setLocation(getString(8));
        earthquake.setDepth(getDouble(9));
        earthquake.setMagnitude(getDouble(10));
        return earthquake;
    }

    static ContentValues getContentValues(Earthquake earthquake) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", earthquake.getTitle());
        contentValues.put("description", earthquake.getDescription());
        contentValues.put("link", earthquake.getLink());
        contentValues.put("pubDate", earthquake.getPubDate().getTime());
        contentValues.put("category", earthquake.getCategory());
        contentValues.put("lat", earthquake.getLat());
        contentValues.put("lon", earthquake.getLon());
        contentValues.put("location", earthquake.getLocation());
        contentValues.put("depth", earthquake.getDepth());
        contentValues.put("magnitude", earthquake.getMagnitude());
        return contentValues;
    }
}
