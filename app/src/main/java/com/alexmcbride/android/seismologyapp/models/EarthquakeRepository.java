package com.alexmcbride.android.seismologyapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class EarthquakeRepository implements AutoCloseable {
    private static final String EARTHQUAKES_TABLE = "earthquakes";
    private final EarthquakeDbHelper mDbHelper;

    public EarthquakeRepository(Context context) {
        mDbHelper = new EarthquakeDbHelper(context);
    }

    public void addEarthquakes(List<Earthquake> earthquakes) {
        for (Earthquake earthquake : earthquakes) {
            addEarthquake(earthquake);
        }
    }

    public boolean addEarthquake(Earthquake earthquake) {
        try (SQLiteDatabase db = mDbHelper.getWritableDatabase()) {
            ContentValues contentValues = getContentValues(earthquake);
            long id = db.insertWithOnConflict(EARTHQUAKES_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
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
            long affected = db.update(EARTHQUAKES_TABLE, contentValues, "WHERE _id=?",
                    new String[]{String.valueOf(earthquake.getId())});
            return affected > 0;
        }
    }

    private ContentValues getContentValues(Earthquake earthquake) {
        ContentValues contentValues = new ContentValues();
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
             Cursor cursor = db.query(EARTHQUAKES_TABLE, null, null, null, null, null, null)) {
            EarthquakeCursorWrapper cursorWrapper = new EarthquakeCursorWrapper(cursor);
            if (cursor.moveToFirst()) {
                do {
                    earthquakes.add(cursorWrapper.getEarthquake());
                } while (cursor.moveToNext());
            }
        }
        return earthquakes;
    }

    public Cursor getEarthquakesCursor() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return db.query(EARTHQUAKES_TABLE, null, null, null,
                null, null, null);
    }

    public Earthquake getEarthquake(long id) {
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query(EARTHQUAKES_TABLE, null,
                     "WHERE _id=?", new String[]{String.valueOf(id)},
                     null, null, null)) {
            if (cursor.moveToFirst()) {
                EarthquakeCursorWrapper cursorWrapper = new EarthquakeCursorWrapper(cursor);
                return cursorWrapper.getEarthquake();
            } else {
                return null;
            }
        }
    }

    @Override
    public void close() throws IOException {
        mDbHelper.close();
    }
}
