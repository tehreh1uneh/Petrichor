package com.tehreh1uneh.petrichor.model.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferencesHelper() {
    }

    public static void saveToSharedPreferences(Activity activity, String key, String value) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).apply();
    }

}
