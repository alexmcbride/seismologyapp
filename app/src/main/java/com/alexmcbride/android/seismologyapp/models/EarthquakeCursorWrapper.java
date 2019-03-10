package com.alexmcbride.android.seismologyapp.models;

import android.database.Cursor;

import java.util.Date;

public class EarthquakeCursorWrapper extends android.database.CursorWrapper {
    public EarthquakeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Earthquake getEarthquake() {
        Earthquake earthquake = new Earthquake();
        earthquake.setId(getLong(0));
        earthquake.setTitle(getString(1));
        earthquake.setDescription(getString(2));
        earthquake.setLink(getString(3));
        earthquake.setPubDate(new Date(getLong(4)));
        earthquake.setCategory(getString(5));
        earthquake.setLat(getDouble(6));
        earthquake.setLon(getDouble(7));
        return earthquake;
    }
}
