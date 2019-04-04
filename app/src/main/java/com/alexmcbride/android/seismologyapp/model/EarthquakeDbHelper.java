/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.annotation.Nullable;

class EarthquakeDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "earthquake-db";
    private static final int DB_VERSION = 17;

    EarthquakeDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS earthquakes (" +
                "_id INTEGER PRIMARY KEY," +
                "title TEXT," +
                "description TEXT," +
                "link TEXT," +
                "pubDate DATE," +
                "category TEXT," +
                "lat REAL," +
                "lon REAL," +
                "location TEXT," +
                "depth REAL," +
                "magnitude REAL" +
                ");");

        // As each link links to a different event they should be unique, we use this to make sure
        // no duplicate earthquakes are added.
        db.execSQL("CREATE UNIQUE INDEX idx_links ON earthquakes (link);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On upgrading we just drop and re-create everything.
        db.execSQL("DROP TABLE IF EXISTS earthquakes;");
        onCreate(db);
    }
}
