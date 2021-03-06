package com.example.teju.u_and_e;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Teju on 12/24/2017.
 */

public class TestAppSharedPreferences implements SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences androidPref;
    private SharedPreferences.Editor editor;

    private Context context;
    private static TestAppSharedPreferences mySharedPref;

    private TestAppSharedPreferences(Context context) {
        this.context = context;
        this.androidPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        this.androidPref.registerOnSharedPreferenceChangeListener(this);
    }

    public static TestAppSharedPreferences getInstance(Context context){
        if(mySharedPref==null){
            mySharedPref=new TestAppSharedPreferences(context);
        }

        return mySharedPref;
    }

    public void setMessageStorageEnabled(boolean value){
        editor= androidPref.edit();
        editor.putBoolean("isChecked", value);
        editor.commit();
    }

    public boolean getMessageStorageEnabled(){
        boolean ret;
        ret= androidPref.getBoolean("isChecked",false);
        return ret;
    }

    public void setDraftMessage(String number, String value){
        editor = androidPref.edit();
        editor.putString(number,value);
        editor.commit();
    }

    public String getDraftMessage(String number){
        String s1;
        s1 = androidPref.getString(number,"");
        return s1;
    }

    public void setLastPlayedSongPath(String path){
        editor = androidPref.edit();
        Log.d(TestAppSharedPreferences.class.getSimpleName(), "setLastPlayedSongPath: "+path);
        editor.putString("lastPlayedSongPath", path);
        editor.commit();
    }

    public String getLastPlayedSongPath(){
        String path;
        path = androidPref.getString("lastPlayedSongPath", "");
        return path;
    }

    public void setLastPlayedSongThumbUri(String path){
        editor = androidPref.edit();
        Log.d(TestAppSharedPreferences.class.getSimpleName(), "setLastPlayedSongThumbUri: "+path);
        editor.putString("lastPlayedSongThumbUri", path);
        editor.commit();
    }

    public String getLastPlayedSongThumbUri(){
        String path;
        path = androidPref.getString("lastPlayedSongThumbUri", "");
        return path;
    }

    public void setLastPlayedSongTitle(String path){
        editor = androidPref.edit();
        Log.d(TestAppSharedPreferences.class.getSimpleName(), "setLastPlayedSongTitle: "+path);
        editor.putString("lastPlayedSongTitle", path);
        editor.commit();
    }

    public String getLastPlayedSongTitle(){
        String path;
        path = androidPref.getString("lastPlayedSongTitle", "");
        return path;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key == null && key.isEmpty()) return;

      if(key.equals("isChecked")==true){
          boolean ret = sharedPreferences.getBoolean(key,false);
          if(ret==true){
              ((MsgServiceControlInterface)(TestAppSharedPreferences.mySharedPref.context)).startMessageReceiverService();
          }else{
              ((MsgServiceControlInterface)(TestAppSharedPreferences.mySharedPref.context)).stopMessageReceiverService();
          }
      }

    }
}
