package com.nasbys.rob.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nasbys.rob.sunshine.data.WeatherContract.LocationEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by robnasby on 11/17/14.
 */
public class FetchForecastDataTask extends AsyncTask<String, Void, Void> {

    private static class OpenWeatherMapKeys {
        public static String CITY = "city";
        public static String CITY_NAME = "name";
        public static String COORDINATES = "coord";
        public static String DATETIME = "dt";
        public static String DESCRIPTION = "main";
        public static String HUMIDITY = "humidity";
        public static String LATITUDE = "lat";
        public static String LIST = "list";
        public static String LONGITUDE = "lon";
        public static String MAXIMUM_TEMPERATURE = "max";
        public static String MINIMUM_TEMPERATURE = "min";
        public static String PRESSURE = "pressure";
        public static String TEMPERATURE = "temp";
        public static String WEATHER = "weather";
        public static String WEATHER_ID = "id";
        public static String WIND_DIRECTION = "speed";
        public static String WIND_SPEED = "deg";
    }

    private final String LOG_TAG = FetchForecastDataTask.class.getSimpleName();

    private Context _context = null;

    public FetchForecastDataTask(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) return null;

        final String location = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            final String DAY_COUNT_PARAM = "cnt";
            final String FORMAT_PARAM = "mode";
            final String LOCATION_PARAM = "q";
            final String UNITS_PARAM = "units";

            final Integer dayCount = 14;
            final String format = "json";
            final String units = "metric";

            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http");
            uriBuilder.authority("api.openweathermap.org");
            uriBuilder.path("data/2.5/forecast/daily");
            uriBuilder.appendQueryParameter(LOCATION_PARAM, location);
            uriBuilder.appendQueryParameter(DAY_COUNT_PARAM, dayCount.toString());
            uriBuilder.appendQueryParameter(UNITS_PARAM, units);
            uriBuilder.appendQueryParameter(FORMAT_PARAM, format);
            URL url = new URL(uriBuilder.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                try {
                    if (buffer.length() > 0) {
                        forecastJsonStr = buffer.toString();

                        JSONObject forecastJson = new JSONObject(forecastJsonStr);
                        JSONArray forecastArray = forecastJson.getJSONArray(OpenWeatherMapKeys.LIST);

                        JSONObject cityJson = forecastJson.getJSONObject(OpenWeatherMapKeys.CITY);
                        String cityName = cityJson.getString(OpenWeatherMapKeys.CITY_NAME);

                        JSONObject coordinates = cityJson.getJSONObject(OpenWeatherMapKeys.COORDINATES);
                        double latitude = coordinates.getDouble(OpenWeatherMapKeys.LATITUDE);
                        double longitude = coordinates.getDouble(OpenWeatherMapKeys.LONGITUDE);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    private long addLocation(String location, String cityName, double latitude, double longitude) {
        long id;

        Cursor cursor = _context.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                new String[] { LocationEntry._ID },
                LocationEntry.COLUMN_LOCATION_QUERY + " = ?",
                new String[] { location },
                null
        );

        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex(LocationEntry._ID));
        } else {
            ContentValues values = LocationEntry.makeContentValues(location, cityName, latitude, longitude);

            Uri uri = _context.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
            id = ContentUris.parseId(uri);
        }

        return id;
    }

}
