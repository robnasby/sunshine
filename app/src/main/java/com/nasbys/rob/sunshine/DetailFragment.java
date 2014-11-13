package com.nasbys.rob.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by robnasby on 11/13/14.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        String forecast = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        ((TextView) rootView.findViewById(R.id.detail_forecast)).setText(forecast);
        return rootView;
    }
}

