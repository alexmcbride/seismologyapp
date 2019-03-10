package com.alexmcbride.android.seismologyapp.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CloseableCursor implements AutoCloseable {
    private SQLiteDatabase mDatabase;
    private Cursor mCursor;

    public Cursor getCursor() {
        return mCursor;
    }

    CloseableCursor(SQLiteDatabase database, Cursor cursor) {
        mDatabase = database;
        mCursor = cursor;
    }

    @Override
    public void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        if (mCursor != null) {
            mCursor.close();
        }
    }
}
