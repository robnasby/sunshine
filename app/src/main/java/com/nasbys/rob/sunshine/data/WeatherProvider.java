package com.nasbys.rob.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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

    private static final SQLiteQueryBuilder _weatherByLocationSettingQueryBuilder;
    static {
        _weatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        _weatherByLocationSettingQueryBuilder.setTables(
                WeatherEntry.TABLE_NAME + " INNER JOIN " + LocationEntry.TABLE_NAME +
                        " ON " + WeatherEntry.TABLE_NAME + "." + WeatherEntry.COLUMN_LOC_KEY +
                        " = " + LocationEntry.TABLE_NAME + "." + LocationEntry._ID
        );
    }

    private static final String _locationSelection =
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_LOCATION_QUERY + " = ? ";
    private static final String _locationWithStartDateSelection =
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_LOCATION_QUERY + " = ?" +
                    " AND " + WeatherEntry.COLUMN_DATETEXT + " >= ? ";
    private static final String _locationWithDateSelection =
            LocationEntry.TABLE_NAME + "." + LocationEntry.COLUMN_LOCATION_QUERY + " = ?" +
                    " AND " + WeatherEntry.COLUMN_DATETEXT + " = ? ";

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
                retCursor = _dbHelper.getReadableDatabase().query(
                        WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case WEATHER_WITH_LOCATION:
                retCursor = getWeatherByLocation(uri, projection, sortOrder);
                break;
            case WEATHER_WITH_LOCATION_AND_DATE:
                retCursor = getWeatherByLocationAndDate(uri, projection, sortOrder);
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
        Uri returnUri;

        final SQLiteDatabase db = _dbHelper.getWritableDatabase();
        final int match = _uriMatcher.match(uri);
        long id;

        switch (match) {
            case LOCATION:
                id = db.insert(LocationEntry.TABLE_NAME, null, contentValues);
                if (id > 0)
                    returnUri = LocationEntry.buildLocationUri(id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            case WEATHER:
                id = db.insert(WeatherEntry.TABLE_NAME, null, contentValues);
                if (id > 0)
                    returnUri = WeatherEntry.buildWeatherUri(id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        
        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = _dbHelper.getWritableDatabase().delete(getTableNameForUri(uri), selection, selectionArgs);

        if (selection == null || deleteCount != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int updateCount = _dbHelper.getWritableDatabase().update(getTableNameForUri(uri), contentValues, selection, selectionArgs);

        if (selection == null || updateCount != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
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

    private String getTableNameForUri(Uri uri) {
        String tableName;

        final int match = _uriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                tableName = LocationEntry.TABLE_NAME;
                break;
            case WEATHER:
                tableName = WeatherEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        return tableName;
    }

    private Cursor getWeatherByLocation(Uri uri, String[] projection, String sortOrder) {
        String location = WeatherEntry.getLocationFromUri(uri);
        String startDate = WeatherEntry.getStartDateFromUri(uri);

        String selection;
        String[] selectionArgs;

        if (startDate == null) {
            selection = _locationSelection;
            selectionArgs = new String[]{ location };
        } else {
            selection = _locationWithStartDateSelection;
            selectionArgs = new String[] { location, startDate };
        }

        return _weatherByLocationSettingQueryBuilder.query(_dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationAndDate(Uri uri, String[] projection, String sortOrder) {
        String location = WeatherEntry.getLocationFromUri(uri);
        String date = WeatherEntry.getDateFromUri(uri);

        return _weatherByLocationSettingQueryBuilder.query(_dbHelper.getReadableDatabase(),
                projection,
                _locationWithDateSelection,
                new String[] { location, date },
                null,
                null,
                sortOrder
        );
    }
}
