package com.example.locationpinnedapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Addresses.db";
    public static final String TABLE_NAME = "Addresses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "Address";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_ADDRESS + " TEXT," + COLUMN_LATITUDE +
            " TEXT," + COLUMN_LONGITUDE + " TEXT)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.rawQuery("DROP TABLE IF EXISTS " + TABLE_NAME, null).close();
    }

    public Cursor findAddress(String query) {
        SQLiteDatabase sdb = this.getReadableDatabase();

        Cursor address = sdb.query(TABLE_NAME, null, COLUMN_ADDRESS + " LIKE ?",
                new String[] {"%" + query + "%"}, null, null, null);
        return address;
    }

    public Cursor getData() {
        SQLiteDatabase sdb = this.getReadableDatabase();
        return sdb.query(TABLE_NAME, null, null, null, null,
                null, null);
    }

    public long insert(ContentValues values) {
        SQLiteDatabase sdb = this.getWritableDatabase();

        return sdb.insert(TABLE_NAME, null, values);
    }
}
