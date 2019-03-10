package com.alexmcbride.android.seismologyapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class EarthquakeRepository implements Closeable {
    private final EarthquakeDbHelper mDbHelper;

    public EarthquakeRepository(Context context) {
        mDbHelper = new EarthquakeDbHelper(context);
    }

    public boolean addEarthquake(Earthquake earthquake) {
        try (SQLiteDatabase db = mDbHelper.getWritableDatabase()) {
            ContentValues contentValues = getContentValues(earthquake);
            long id = db.insert("earthquakes", null, contentValues);
            if (id > -1) {
                earthquake.setId(id);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean updateEarthquake(Earthquake earthquake) {
        try (SQLiteDatabase db = mDbHelper.getWritableDatabase()) {
            ContentValues contentValues = getContentValues(earthquake);
            long affected = db.update("earthquakes", contentValues, "WHERE id=?",
                    new String[]{String.valueOf(earthquake.getId())});
            return affected > 0;
        }
    }

    private ContentValues getContentValues(Earthquake earthquake) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", earthquake.getId());
        contentValues.put("title", earthquake.getTitle());
        contentValues.put("description", earthquake.getDescription());
        contentValues.put("link", earthquake.getLink());
        contentValues.put("pubDate", earthquake.getPubDate().getTime());
        contentValues.put("category", earthquake.getCategory());
        contentValues.put("lat", earthquake.getLat());
        contentValues.put("lon", earthquake.getLon());
        return contentValues;
    }

    public List<Earthquake> getEarthquakes() {
        List<Earthquake> earthquakes = Lists.newArrayList();
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query("earthquakes", null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    earthquakes.add(getEarthquake(cursor));
                } while (cursor.moveToNext());
            }
        }
        return earthquakes;
    }

    public Earthquake getEarthquake(long id) {
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query("earthquakes", null,
                     "WHERE id=?", new String[]{String.valueOf(id)},
                     null, null, null)) {
            if (cursor.moveToFirst()) {
                return getEarthquake(cursor);
            } else {
                return null;
            }
        }
    }

    private Earthquake getEarthquake(Cursor cursor) {
        Earthquake earthquake = new Earthquake();
        earthquake.setId(cursor.getLong(0));
        earthquake.setTitle(cursor.getString(1));
        earthquake.setDescription(cursor.getString(2));
        earthquake.setLink(cursor.getString(3));
        earthquake.setPubDate(new Date(cursor.getLong(4)));
        earthquake.setCategory(cursor.getString(5));
        earthquake.setLat(cursor.getDouble(6));
        earthquake.setLon(cursor.getDouble(7));
        return earthquake;
    }

    @Override
    public void close() throws IOException {
        mDbHelper.close();
    }
}
