package com.example.teju.testapp;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Handler;

/**
 * Created by Teju on 12/11/2017.
 */

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Message message = new Message();
        boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);

        if (action.equals(Intent.ACTION_BATTERY_LOW)) {
            message.what = 0;
        } else if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
            message.what = 1;
        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            message.what = 2;
        } else if (action.equals(Intent.ACTION_BATTERY_OKAY)) {
            message.what = 3;
        } else if (isAirplaneModeOn) {
            Toast.makeText(context, "Airplane mode on", Toast.LENGTH_SHORT).show();
            message.what = 4;
        } else {
            Toast.makeText(context, "Airplane mode off", Toast.LENGTH_SHORT).show();
            message.what = 5;
        }

        if (MainActivity.handler != null) {
            MainActivity.handler.dispatchMessage(message);
        }
    }
}