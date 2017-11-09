package com.github.positionsfinder.findmyroute;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mosaab on 11/9/2017.
 */

public class Settings {

    public static final String PREFS_NAME = "MainPrefFile";
    private SharedPreferences settings;

    // The instance of this class
    private static Settings instance;

    // There can be no new objects
    private Settings() {}

    // If existent returns the instance - otherwise a new instance (object) will be generated
    public static Settings getInstance () {
        if (Settings.instance == null) {
            Settings.instance = new Settings();
        }
        return Settings.instance;
    }

    // Has to be set, after the application was loaded.
    public void fetchSettings(Context cntx){
        this.settings = cntx.getSharedPreferences(PREFS_NAME,0);
    }

    public void saveSettings(Context cntx, HashMap<String, Object> settingsMap){

        SharedPreferences.Editor editor = settings.edit();

        for(Map.Entry<String,Object> entry : settingsMap.entrySet()){

            if(entry.getValue().getClass().getName().equals("java.lang.String")){
                editor.putString(entry.getKey(),(String) entry.getValue());

            } else if(entry.getValue().getClass().getName().equals("java.lang.Float")) {
                editor.putFloat(entry.getKey(), (float) entry.getValue());

            } else if(entry.getValue().getClass().getName().equals("java.lang.Integer")){
                editor.putInt(entry.getKey(), (int) entry.getValue());

            } else if(entry.getValue().getClass().getName().equals("java.lang.Boolean")){
                editor.putBoolean(entry.getKey(), (boolean) entry.getValue());

            } else {
                Log.e("[ERR]","SharedPreference " + entry.getKey()
                        + " with value " + entry.getValue()
                        + " of type " + entry.getValue().getClass().getName()
                        + " could not be saved.");
            }
        }

        // commit in the background
        editor.apply();

    }

    public boolean getBoolean(String settingsKey){
        boolean value = this.settings.getBoolean(settingsKey, false);
        return value;
    }

    public int getInt(String settingsKey){
        int value = this.settings.getInt(settingsKey, 0);
        return value;
    }

    public float getFloat(String settingsKey){
        float value = this.settings.getFloat(settingsKey, 0);
        return value;
    }

    public String getString(String settingsKey){
        String value = this.settings.getString(settingsKey, settingsKey);
        return value;
    }

}
