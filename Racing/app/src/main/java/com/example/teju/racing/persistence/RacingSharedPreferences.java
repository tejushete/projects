package com.example.teju.racing.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.teju.racing.RacingEventListener;
import com.example.teju.racing.data.RacingConstants;

/**
 * Created by Teju on 2/21/2018.
 */

public class RacingSharedPreferences implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences androidPref;
    private SharedPreferences.Editor editor;

    private Context context;
    private static RacingSharedPreferences mySharedPref;

    private RacingSharedPreferences(Context context) {
        this.context = context;
        this.androidPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        this.androidPref.registerOnSharedPreferenceChangeListener(this);
    }

    public static RacingSharedPreferences getInstance(Context context) {
        if (mySharedPref == null) {
            mySharedPref = new RacingSharedPreferences(context);
        }

        return mySharedPref;
    }

    public void setIsLoggedIn(boolean value) {
        editor = androidPref.edit();
        editor.putBoolean(RacingConstants.IS_LOGGED_IN_KEY, value);
        editor.commit();
    }

    public boolean getIsLoggedIn() {
        boolean ret;
        ret = androidPref.getBoolean(RacingConstants.IS_LOGGED_IN_KEY, false);
        return ret;
    }

    public void setUserFirstName(String value) {
        editor = androidPref.edit();
        editor.putString(RacingConstants.FIRST_NAME_KEY, value);
        editor.commit();
    }

    public String getUserFirstName() {
        String ret;
        ret = androidPref.getString(RacingConstants.FIRST_NAME_KEY, "");
        return ret;
    }

    public void setUserLastName(String value) {
        editor = androidPref.edit();
        editor.putString(RacingConstants.LAST_NAME_KEY, value);
        editor.commit();
    }

    public String getUserLastName() {
        String ret;
        ret = androidPref.getString(RacingConstants.LAST_NAME_KEY, "");
        return ret;
    }

    public void setGender(String value) {
        editor = androidPref.edit();
        editor.putString(RacingConstants.GENDER_KEY, value);
        editor.commit();
    }

    public String getGender() {
        String ret;
        ret = androidPref.getString(RacingConstants.GENDER_KEY, "");
        return ret;
    }

    public void setProfilePath(String value) {
        editor = androidPref.edit();
        editor.putString(RacingConstants.PIC_PATH_URI_KEY, value);
        editor.commit();
    }

    public String getProfilePath() {
        String ret;
        ret = androidPref.getString(RacingConstants.PIC_PATH_URI_KEY, "");
        return ret;
    }

    public void setEmailId(String value) {
        editor = androidPref.edit();
        editor.putString(RacingConstants.EMAIL_ID_KEY, value);
        editor.commit();
    }

    public String getEmailId() {
        String ret;
        ret = androidPref.getString(RacingConstants.EMAIL_ID_KEY, "");
        return ret;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(RacingConstants.IS_LOGGED_IN_KEY) == true) {
            boolean logInNewValue = getIsLoggedIn();
            if (context instanceof RacingEventListener) {
                ((RacingEventListener) context).logInStatusChanged(logInNewValue);
            }
        }
    }
}