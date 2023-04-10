package com.example.elec390_proj_demo;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    private SharedPreferences sharedPreferences;


    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    /**
     * save individual button presses
     * @param b boolean value
     */
    public void saveDarkMode(boolean b) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("darkMode", b);
        editor.commit();
        return;
    }
    /**
     * get total count from shared preferences
     * @return
     */
    public boolean readDarkMode() {
        return sharedPreferences.getBoolean("darkMode", false);
    }
}
