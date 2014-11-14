package com.nasbys.rob.sunshine.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by robnasby on 11/13/14.
 */
public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.nasbys.rob.sunshine";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        // The location string used as the query.
        public static final String COLUMN_LOCATION_QUERY = "location_query";

        // City name.
        public static final String COLUMN_CITY_NAME = "city_name";

        // The latitude and longitude of the location, stored as doubles.
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // Foreign key to the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Date, stored as text with format yyyy-MM-dd.
        public static final String COLUMN_DATETEXT = "date";

        // Weather id as returned by API, to identify the icon to be used.
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description of the weather, as provided by API
        // (e.g "clear" vs "sky is clear").
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures, stored as floats.
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity, stored as a float representing percentage.
        public static final String COLUMN_HUMIDITY = "humidity";

        // Pressure, stored as a float.
        public static final String COLUMN_PRESSURE = "pressure";

        // Wind speed, stored as a float.
        public static final String COLUMN_WIND_SPEED = "wind";

        // Wind direction, stored as a float representing meteorological degrees
        // (e.g, 0 is north, 180 is south).
        public static final String COLUMN_WIND_DIRECTION_DEGREES = "degrees";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocationUri(String locationQuery) {
            return CONTENT_URI.buildUpon().appendPath(locationQuery).build();
        }

        public static Uri buildWeatherLocationDateUri(String locationQuery,
                                                      String date) {
            return buildWeatherLocationUri(locationQuery).buildUpon().appendPath(date).build();
        }

        public static Uri buildWeatherLocationStartDateUri(String locationQuery,
                                                           String startDate) {
            return buildWeatherLocationUri(locationQuery).buildUpon().appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getLocationFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }
}
