package com.nasbys.rob.sunshine.data;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
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
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();

        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationEntry.COLUMN_CITY_NAME, TestData.CITY_NAME);
        locationValues.put(LocationEntry.COLUMN_LOCATION_QUERY, TestData.LOCATION);
        locationValues.put(LocationEntry.COLUMN_LATITUDE, 64.772);
        locationValues.put(LocationEntry.COLUMN_LONGITUDE, -147.355);

        long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);
        assertTrue(locationRowId != -1);

        Cursor locationCursor = mContext.getContentResolver().query(LocationEntry.buildLocationUri(locationRowId), null, null, null, null);
        if (locationCursor.moveToFirst()) {
            validateCursor(locationCursor, locationValues);

            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TestData.DATE);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            weatherValues.put(WeatherEntry.COLUMN_WIND_DIRECTION_DEGREES, 1.1);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

            long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);

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

    private static void validateCursor(Cursor cursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            int columnIndex = cursor.getColumnIndex(entry.getKey());
            assertFalse(-1 == columnIndex);
            assertEquals(entry.getValue().toString(), cursor.getString(columnIndex));
        }
    }
}
