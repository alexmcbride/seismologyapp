package com.alexmcbride.android.seismologyapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Date;
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

    private static String getAscOrDesc(boolean ascending) {
        return ascending ? "ASC" : "DESC";
    }

    public List<Earthquake> getEarthquakes() {
        return getEarthquakesInternal(null);
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

    public SearchResult getNorthernmostEarthquake(Date start, Date end) {
        return getSearchResult("Northernmost", "lat DESC", start, end);
    }

    public SearchResult getSouthernmostEarthquake(Date start, Date end) {
        return getSearchResult("Southernmost", "lat ASC", start, end);
    }

    public SearchResult getEasternmostEarthquake(Date start, Date end) {
        return getSearchResult("Easternmost", "lon DESC", start, end);
    }

    public SearchResult getWesternmostEarthquake(Date start, Date end) {
        return getSearchResult("Westernmost", "lon ASC", start, end);
    }

    public SearchResult getLargestMagnitudeEarthquake(Date start, Date end) {
        return getSearchResult("Largest Magnitude", "magnitude DESC", start, end);
    }

    public SearchResult getLowestDepthEarthquake(Date start, Date end) {
        return getSearchResult("Lowest Depth", "depth DESC", start, end);
    }

    private SearchResult getSearchResult(String title, String orderBy, Date start, Date end) {
        try (SQLiteDatabase db = mDbHelper.getReadableDatabase();
             Cursor cursor = db.query(EARTHQUAKES_TABLE, null,
                     "pubDate BETWEEN ? AND ?",
                     new String[]{String.valueOf(start.getTime()), String.valueOf(end.getTime())},
                     null,
                     null,
                     orderBy,
                     "1")) {
            if (cursor.moveToFirst()) {
                EarthquakeCursorWrapper cursorWrapper = new EarthquakeCursorWrapper(cursor);
                Earthquake earthquake = cursorWrapper.getEarthquake();
                SearchResult searchResult = new SearchResult();
                searchResult.setTitle(title);
                searchResult.setEarthquake(earthquake);
                return searchResult;
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
