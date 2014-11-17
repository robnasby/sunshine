package com.nasbys.rob.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

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

        _forecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.forecast_listview_item,
                R.id.forecast_listview_item,
                new ArrayList<String>()
        );

        ListView forecastListView = (ListView) rootView.findViewById(R.id.forecast_listview);
        forecastListView.setAdapter(_forecastAdapter);

        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecastData = _forecastAdapter.getItem(position);
                startActivity(new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecastData));
            }
        });
        
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
            updateForecastData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateForecastData();
    }

    private void updateForecastData() {
        new FetchForecastDataTask(getActivity()).execute(Utility.getPreferredLocation(getActivity()));
    }


}
