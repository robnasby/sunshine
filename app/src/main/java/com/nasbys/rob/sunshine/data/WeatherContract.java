package com.nasbys.rob.sunshine.data;

import android.provider.BaseColumns;

/**
 * Created by robnasby on 11/13/14.
 */
public class WeatherContract {

    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";

        // The location string used as the query.
        public static final String COLUMN_LOCATION_QUERY = "location_query";

        // City name.
        public static final String COLUMN_CITY_NAME = "city_name";

        // The latitude and longitude of the location, stored as doubles.
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

    }

    public static final class WeatherEntry implements BaseColumns {

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
    }
}
