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
 * Created by robnasby on 11/14/14.
 */
public class WeatherDbHelperTestCase extends ApplicationTestCase<Application> {
    public WeatherDbHelperTestCase() {
        super(Application.class);
    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();

        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        locationValues.put(LocationEntry.COLUMN_LOCATION_QUERY, "99705");
        locationValues.put(LocationEntry.COLUMN_LATITUDE, 64.772);
        locationValues.put(LocationEntry.COLUMN_LONGITUDE, -147.355);

        long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);
        assertTrue(locationRowId != -1);

        Cursor locationCursor = db.query(LocationEntry.TABLE_NAME, null, null, null, null, null, null);

        if (locationCursor.moveToFirst()) {
            validateCursor(locationCursor, locationValues);

            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
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

            Cursor weatherCursor = db.query(
                    WeatherEntry.TABLE_NAME, null, null, null, null, null, null);

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);
            } else {
                fail("No values returned  =(");
            }

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
