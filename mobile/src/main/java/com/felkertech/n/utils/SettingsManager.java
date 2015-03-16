package com.felkertech.n.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by N on 9/14/2014.
 */
public class SettingsManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context c;
    private String TAG = "PreferenceManager";
    public SettingsManager(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
//        sharedPreferences = getDefaultSharedPreferences(activity);
//        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
//        sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.PREFERENCES), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        Log.d(TAG, sharedPreferences.getAll().keySet().iterator().next());
        c = activity;
    }
    public SettingsManager(Application app){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);
        editor = sharedPreferences.edit();
        c = app;
    }
    public SettingsManager(Context c) {
        sharedPreferences = getDefaultSharedPreferences(c);
        editor = sharedPreferences.edit();
        this.c = c;
    }
    public String getString(String key) {
        return getString(key, "NULL", "");
    }
    public String getString(String key, String def) {
        return getString(key, "NULL", def);
    }
    public String getString(String key, String val, String def) {
//        Log.d(TAG, key + " - " + val + " - " + def);
        String result = sharedPreferences.getString(key, val);
        if(result == "NULL" || result == "0") {
            editor.putString(key, def);
            Log.d(TAG, key + ", " + def);
            editor.commit();
            result = def;
        }
        return result;
    }
    public String getString(int key) {
        return getString(c.getString(key), "NULL", "");
    }
    public int getInt(String key, int def) {
        return Integer.parseInt(getString(key, "0", ""+def));
    }
    public int getInt(int key, int def) {
        return getInt(c.getString(key), def);
    }
    public int getInt(int key) {
        return getInt(key, 0);
    }
    public long getLong(int key) {
        return Long.parseLong(getString(c.getString(key), "0", "0"));
    }
    public float getFloat(String key, float def) {
        return Float.parseFloat(getString(key, "0", ""+def));
    }
    public float getFloat(int key, float def) {
        return getFloat(c.getString(key), def);
    }
    public float getFloat(int key) {
        return getFloat(key, 0);
    }
    public String setString(String key, String val) {
        editor.putString(key, val);
        editor.commit();
        return val;
    }
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }
    public boolean getBoolean(String key, boolean def) {
        boolean result = sharedPreferences.getBoolean(key, def);
        editor.putBoolean(key, result);
        editor.commit();
        return result;
    }
    public boolean setBoolean(String key, boolean val) {
        editor.putBoolean(key, val);
        editor.commit();
        return val;
    }

    //Default Stuff
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context),
                getDefaultSharedPreferencesMode());
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    private static int getDefaultSharedPreferencesMode() {
        return Context.MODE_PRIVATE;
    }


}
