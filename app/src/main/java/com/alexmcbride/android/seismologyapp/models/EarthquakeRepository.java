package com.alexmcbride.android.seismologyapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/*
 * Wrapper round the DB that handles inserting, updating, deleting, and querying.
 */
public class EarthquakeRepository implements AutoCloseable {
    private static final String EARTHQUAKES_TABLE = "earthquakes";
    private final EarthquakeDbHelper mDbHelper;

    public EarthquakeRepository(Context context) {
        mDbHelper = new EarthquakeDbHelper(context);
    }

    /*
     * Adds list of earthquakes to the database and returns list of new ones.
     */
    public List<Earthquake> addEarthquakes(List<Earthquake> earthquakes) {
        List<Earthquake> added = Lists.newArrayList();
        for (Earthquake earthquake : earthquakes) {
            if (addEarthquake(earthquake)) {
                added.add(earthquake);
            }
        }
        return added;
    }

    /*
     * Each earthquake has its own link to the BGS site, so we use that to check for uniqueness. We
     * have a unique constraint setup on the link, so if a second is inserted then the method
     * returns a -1.
     */
    private boolean addEarthquake(Earthquake earthquake) {
        try (SQLiteDatabase db = mDbHelper.getWritableDatabase()) {
            ContentValues contentValues = EarthquakeCursorWrapper.getContentValues(earthquake);
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
            ContentValues contentValues = EarthquakeCursorWrapper.getContentValues(earthquake);
            long affected = db.update(EARTHQUAKES_TABLE, contentValues, "_id=?",
                    new String[]{String.valueOf(earthquake.getId())});
            return affected > 0;
        }
    }

    private static String getAscOrDesc(boolean ascending) {
        return ascending ? "ASC" : "DESC";
    }

    public List<Earthquake> getEarthquakesByDate(boolean ascending) {
        return getEarthquakesInternal("pubDate " + getAscOrDesc(ascending));
    }

    public List<Earthquake> getEarthquakesByTitle(boolean ascending) {
        return getEarthquakesInternal("location " + getAscOrDesc(ascending));
    }

    public List<Earthquake> getEarthquakesByDepth(boolean ascending) {
        return getEarthquakesInternal("depth " + getAscOrDesc(ascending));
    }

    public List<Earthquake> getEarthquakesByMagnitude(boolean ascending) {
        return getEarthquakesInternal("magnitude " + getAscOrDesc(ascending));
    }

    public List<Earthquake> getEarthquakesByNearest(double currentLat, double currentLon, boolean ascending) {
        // Can't do this with query, so just sort them with a comparator.
        List<Earthquake> earthquakes = getEarthquakesInternal(null);
        Collections.sort(earthquakes, new EarthquakeDistanceComparator(currentLat, currentLon, ascending));
        return earthquakes;
    }

    private List<Earthquake> getEarthquakesInternal(String orderBy) {
        List<Earthquake> earthquakes = Lists.newArrayList();
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query(EARTHQUAKES_TABLE, null, null,
                     null, null, null, orderBy)) {
            if (cursor.moveToFirst()) {
                EarthquakeCursorWrapper cursorWrapper = new EarthquakeCursorWrapper(cursor);
                do {
                    earthquakes.add(cursorWrapper.getEarthquake());
                } while (cursor.moveToNext());
            }
        }
        return earthquakes;
    }

    public Earthquake getEarthquake(long id) {
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query(EARTHQUAKES_TABLE, null,
                     "_id=?", new String[]{String.valueOf(id)}, null,
                     null, null)) {
            if (cursor.moveToFirst()) {
                EarthquakeCursorWrapper cursorWrapper = new EarthquakeCursorWrapper(cursor);
                return cursorWrapper.getEarthquake();
            } else {
                return null;
            }
        }
    }

    @Override
    public void close() {
        mDbHelper.close();
    }
}
