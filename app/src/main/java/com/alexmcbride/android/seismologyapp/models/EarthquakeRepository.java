package com.alexmcbride.android.seismologyapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexmcbride.android.seismologyapp.Util;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EarthquakeRepository implements AutoCloseable {
    private static final String EARTHQUAKES_TABLE = "earthquakes";
    private final EarthquakeDbHelper mDbHelper;

    public EarthquakeRepository(Context context) {
        mDbHelper = new EarthquakeDbHelper(context);
    }

    public List<Earthquake> addEarthquakes(List<Earthquake> earthquakes) {
        List<Earthquake> added = Lists.newArrayList();
        for (Earthquake earthquake : earthquakes) {
            if (addEarthquake(earthquake)) {
                added.add(earthquake);
            }
        }
        return added;
    }

    public boolean addEarthquake(Earthquake earthquake) {
        try (SQLiteDatabase db = mDbHelper.getWritableDatabase()) {
            ContentValues contentValues = getContentValues(earthquake);
            // we have a unique constraint setup on links, so if a row doesn't get inserted it
            // returns -1.
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
            long affected = db.update(EARTHQUAKES_TABLE, contentValues, "_id=?",
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
        contentValues.put("location", earthquake.getLocation());
        contentValues.put("depth", earthquake.getDepth());
        contentValues.put("magnitude", earthquake.getMagnitude());
        return contentValues;
    }

    public List<Earthquake> getEarthquakes() {
        return getEarthquakesInternal(null);
    }

    public List<Earthquake> getEarthquakesByDate() {
        return getEarthquakesInternal("pubDate ASC");
    }

    public List<Earthquake> getEarthquakesByTitle() {
        return getEarthquakesInternal("location ASC");
    }

    public List<Earthquake> getEarthquakesByDepth() {
        return getEarthquakesInternal("depth ASC");
    }

    public List<Earthquake> getEarthquakesByMagnitude() {
        return getEarthquakesInternal("magnitude ASC");
    }

    private List<Earthquake> getEarthquakesInternal(String orderBy) {
        List<Earthquake> earthquakes = Lists.newArrayList();
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query(EARTHQUAKES_TABLE, null, null, null, null, null, orderBy)) {
            EarthquakeCursorWrapper cursorWrapper = new EarthquakeCursorWrapper(cursor);
            if (cursor.moveToFirst()) {
                do {
                    earthquakes.add(cursorWrapper.getEarthquake());
                } while (cursor.moveToNext());
            }
        }
        return earthquakes;
    }

    public List<Earthquake> getEarthquakesByNearest(final double currentLat, final double currentLon) {
        List<Earthquake> earthquakes = getEarthquakes();
        Collections.sort(earthquakes, new EarthquakeDistanceComparator(currentLat, currentLon));
        return earthquakes;
    }

    public Earthquake getEarthquake(long id) {
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query(EARTHQUAKES_TABLE, null,
                     "_id=?", new String[]{String.valueOf(id)},
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
