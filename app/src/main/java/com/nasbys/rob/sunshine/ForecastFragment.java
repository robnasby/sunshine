package com.nasbys.rob.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A fragment containing a forecast of the upcoming weather.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> _forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_forecast, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forecast_listview, container, false);

        new FetchForecastDataTask().execute();

        ArrayList<String> forecastData = new ArrayList<String>(Arrays.asList(
                "Today — Sunny — 88/63",
                "Tomorrow — Foggy — 70/40",
                "Weds — Cloudy — 72/63",
                "Thurs — Asteroids — 75/65",
                "Fri — Heavy Rain — 65/56",
                "Sat — HELP TRAPPED IN WEATHER STATION — 76/68",
                "Sun — Sunny — 80/68"
        ));

        _forecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.forecast_listview_item,
                R.id.forecast_listview_item,
                forecastData
        );

        ListView forecastListView = (ListView) rootView.findViewById(R.id.forecast_listview);
        forecastListView.setAdapter(_forecastAdapter);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new FetchForecastDataTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchForecastDataTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchForecastDataTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
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

                final String dayCount = "7";
                final String format = "json";
                final String location = "60126";
                final String units = "metric";

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http");
                uriBuilder.authority("api.openweathermap.org");
                uriBuilder.path("data/2.5/forecast/daily");
                uriBuilder.appendQueryParameter(LOCATION_PARAM, location);
                uriBuilder.appendQueryParameter(DAY_COUNT_PARAM, dayCount);
                uriBuilder.appendQueryParameter(UNITS_PARAM, units);
                uriBuilder.appendQueryParameter(FORMAT_PARAM, format);
                URL url = new URL(uriBuilder.toString());

                Log.v(LOG_TAG, "Forecast data URL " + url.toString());

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

                    if (buffer.length() > 0) {
                        forecastJsonStr = buffer.toString();

                        Log.v(LOG_TAG, forecastJsonStr);
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

    }

}
