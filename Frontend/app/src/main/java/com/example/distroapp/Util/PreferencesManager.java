package com.example.distroapp.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.distroapp.Entity.Utente;

public class PreferencesManager {

    private static final String PREFS_NAME = "DistroAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_EXPERT = "is_expert";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserSession(Utente user) {
        prefs.edit()
                .putInt(KEY_USER_ID, user.getId())
                .putBoolean(KEY_IS_EXPERT, user.isEsperto())
                .putString(KEY_USER_EMAIL, user.getEmail())
                .apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public boolean isExpert() {
        return prefs.getBoolean(KEY_IS_EXPERT, false);
    }

    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public void clearUserSession() {
        prefs.edit().clear().apply();
    }
}
