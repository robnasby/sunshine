package com.nasbys.rob.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.nasbys.rob.sunshine.data.WeatherContract.LocationEntry;
import com.nasbys.rob.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by robnasby on 11/14/14.
 */
public class WeatherProvider extends ContentProvider {
    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    private static final UriMatcher _uriMatcher = buildUriMatcher();
    private static WeatherDbHelper _dbHelper;

    @Override
    public boolean onCreate() {
        _dbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (_uriMatcher.match(uri)) {
            case LOCATION:
                retCursor = _dbHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case LOCATION_ID:
                String fullSelection = LocationEntry._ID + " = " + ContentUris.parseId(uri);
                if (selection != null) fullSelection += " AND " + selection;
                retCursor = _dbHelper.getReadableDatabase().query(
                        LocationEntry.TABLE_NAME,
                        projection,
                        fullSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case WEATHER:
                retCursor = null;
                break;
            case WEATHER_WITH_LOCATION:
                retCursor = null;
                break;
            case WEATHER_WITH_LOCATION_AND_DATE:
                retCursor = null;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = _uriMatcher.match(uri);

        switch (match) {
            case LOCATION:
                return LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return LocationEntry.CONTENT_ITEM_TYPE;
            case WEATHER:
                return WeatherEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = WeatherContract.CONTENT_AUTHORITY;
        final String locationBasePath = WeatherContract.PATH_LOCATION;
        final String weatherBasePath = WeatherContract.PATH_WEATHER;

        matcher.addURI(authority, weatherBasePath, WEATHER);
        matcher.addURI(authority, weatherBasePath + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, weatherBasePath + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(authority, locationBasePath, LOCATION);
        matcher.addURI(authority, locationBasePath + "/#", LOCATION_ID);

        return matcher;
    }
}
