package com.example.janiszhang.weatherdemo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.janiszhang.weatherdemo.db.MyDBHelper;

/**
 * Created by janiszhang on 2016/3/10.
 */
public class MyProvider extends ContentProvider{

    public static final int WEATHERDATA_DIR = 0;
    public static final String AUTHORITY = "com.example.janiszhang.weatherdemo.provider";
    private static UriMatcher sUriMatcher;
    private MyDBHelper mMyDBHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "weatherdata", WEATHERDATA_DIR);
    }
    @Override
    public boolean onCreate() {
        mMyDBHelper = new MyDBHelper(getContext(),  "weatherDataDB.db", null, 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
            case WEATHERDATA_DIR:
                cursor = db.query("weatherdata", projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case WEATHERDATA_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.janiszhang.weatherdemo.provider.weatherdata";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mMyDBHelper.getReadableDatabase();
        Uri uriReturn = null;
        switch (sUriMatcher.match(uri)) {
            case WEATHERDATA_DIR:
                long newId = db.insert("weatherdata", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/weatherdata/" + newId);
                break;
        }
        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMyDBHelper.getWritableDatabase();
        int updateRows = 0;
        switch (sUriMatcher.match(uri)) {
            case WEATHERDATA_DIR:
                updateRows = db.update("weatherdata", values, selection, selectionArgs);
                break;
        }
        return updateRows;
    }
}
