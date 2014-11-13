package com.nasbys.rob.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by robnasby on 11/13/14.
 */
public class DetailFragment extends Fragment {

    private final static String SUNSHINE_HASH_TAG = "#SunshineApp";

    private String _forecastData;
    private ShareActionProvider _shareActionProvider;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);

        _shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if (_shareActionProvider != null) {
            _shareActionProvider.setShareIntent(createShareActionIntent());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        _forecastData = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        ((TextView) rootView.findViewById(R.id.detail_forecast)).setText(_forecastData);
        return rootView;
    }

    private Intent createShareActionIntent() {
        return new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                .putExtra(Intent.EXTRA_TEXT, String.format("%s %s", _forecastData, SUNSHINE_HASH_TAG))
                .setType("text/plain");
    }
}

