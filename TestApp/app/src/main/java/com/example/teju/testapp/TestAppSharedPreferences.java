package com.example.teju.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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


    public boolean getMessageStorageEnabled(){
        boolean ret;
        ret= androidPref.getBoolean("isChecked",false);
        return ret;
    }

   public void setMessageStorageEnabled(boolean value){
         editor= androidPref.edit();
         editor.putBoolean("isChecked", value);
         editor.commit();
   }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

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
