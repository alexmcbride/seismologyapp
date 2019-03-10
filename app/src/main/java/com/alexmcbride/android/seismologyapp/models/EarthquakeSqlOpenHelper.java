package com.alexmcbride.android.seismologyapp.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.annotation.Nullable;

public class EarthquakeSqlOpenHelper extends SQLiteOpenHelper {
    public EarthquakeSqlOpenHelper(@Nullable Context context) {
        super(context, "earthquake-db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS earthquakes (" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "link TEXT NOT NULL," +
                "pubDate INTEGER NOT NULL," +
                "category TEXT NOT NULL," +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS earthquakes;");
        onCreate(db);
    }
}
