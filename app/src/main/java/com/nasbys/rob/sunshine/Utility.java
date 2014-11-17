package com.nasbys.rob.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by robnasby on 11/17/14.
 */
public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default)
        );

    }
}
