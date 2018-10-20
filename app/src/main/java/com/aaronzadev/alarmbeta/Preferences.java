package com.aaronzadev.alarmbeta;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Aaron Zuniga on 06/06/2016.
 */
public class Preferences {

    private Context context;
    private SharedPreferences.Editor editor;

    public Preferences(Context c) {
        context = c;
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public String readContactOne(String key) {

        return getPreferences().getString(key, "");

    }

    public String readContactTwo(String key) {

        return getPreferences().getString(key, "");
    }

    public String readContactThree(String key) {

        return getPreferences().getString(key, "");
    }

    public String readProviderEnabled(String key) {

        return getPreferences().getString(key, "");
    }

    public String readNumberOfAlarms(String key) {

        return getPreferences().getString(key, "");

    }

    public String readTimeOfAlarms(String key) {

        return getPreferences().getString(key, "");

    }

    public String readMsjOfAlarm(String key) {

        return getPreferences().getString(key, "");

    }
}
