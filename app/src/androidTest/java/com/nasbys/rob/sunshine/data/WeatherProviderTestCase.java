package com.nasbys.rob.sunshine.data;

import android.app.Application;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ApplicationTestCase;

import com.nasbys.rob.sunshine.data.WeatherContract.LocationEntry;
import com.nasbys.rob.sunshine.data.WeatherContract.WeatherEntry;

import java.util.Map;
import java.util.Set;

/**
 * Created by robnasby on 11/17/14.
 */
public class WeatherProviderTestCase extends ApplicationTestCase<Application> {

    public static class TestData {
        public static String CITY_NAME = "North Pole";
        public static String DATE = "20141205";
        public static String LOCATION = "99705";
    }

    public WeatherProviderTestCase() {
        super(Application.class);
    }

    public void testDeleteAllRows() {
        mContext.getContentResolver().delete(WeatherEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(LocationEntry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
        cursor = mContext.getContentResolver().query(LocationEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testGetType() {
        String type;

        type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "60126";
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocationUri(testLocation));
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20141117";
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocationDateUri(testLocation, testDate));
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertReadProvider() {
        ContentValues locationValues = getLocationContentValues();
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
        long locationRowId = ContentUris.parseId(locationUri);

        Cursor locationCursor = mContext.getContentResolver().query(LocationEntry.buildLocationUri(locationRowId), null, null, null, null);
        if (locationCursor.moveToFirst()) {
            validateCursor(locationCursor, locationValues);

            ContentValues weatherValues = getWeatherContentValues(locationRowId);
            mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);

            Cursor weatherCursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI, null, null, null, null);
            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);
            } else {
                fail("No values returned  =(");
            }
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationUri(TestData.LOCATION),
                    null,
                    null,
                    null,
                    null
            );
            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);
            } else {
                fail("No values returned  =(");
            }
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationStartDateUri(TestData.LOCATION, TestData.DATE),
                    null,
                    null,
                    null,
                    null
            );
            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);
            } else {
                fail("No values returned  =(");
            }
            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationDateUri(TestData.LOCATION, TestData.DATE),
                    null,
                    null,
                    null,
                    null
            );
            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);
            } else {
                fail("No values returned  =(");
            }
            weatherCursor.close();
        } else {
            fail("No values returned  =(");
        }
    }

    public void testUpdateLocation() {
        testDeleteAllRows();

        ContentValues values = getLocationContentValues();

        Uri uri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);

        assertTrue(id != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LocationEntry._ID, id);
        updatedValues.put(LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int updateCount = mContext.getContentResolver().update(LocationEntry.CONTENT_URI,
                updatedValues,
                LocationEntry._ID + " = ?",
                new String[] { Long.toString(id) }
        );
        assertEquals(1, updateCount);

        Cursor cursor = mContext.getContentResolver().query(LocationEntry.buildLocationUri(id), null, null, null, null);
        if (cursor.moveToFirst()) {
            validateCursor(cursor, updatedValues);
        } else {
            fail("No values returned  =(");
        }
        cursor.close();
    }

    private ContentValues getLocationContentValues() {
        return LocationEntry.makeContentValues(TestData.LOCATION, TestData.CITY_NAME, 64.772, -147.355);
    }

    private ContentValues getWeatherContentValues(long locationId) {
        ContentValues values = new ContentValues();

        values.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
        values.put(WeatherEntry.COLUMN_DATETEXT, TestData.DATE);
        values.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        values.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        values.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        values.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        values.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        values.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        values.put(WeatherEntry.COLUMN_WIND_DIRECTION_DEGREES, 1.1);
        values.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return values;
    }

    private static void validateCursor(Cursor cursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            int columnIndex = cursor.getColumnIndex(entry.getKey());
            assertFalse(-1 == columnIndex);
            assertEquals(entry.getValue().toString(), cursor.getString(columnIndex));
        }
    }
}
