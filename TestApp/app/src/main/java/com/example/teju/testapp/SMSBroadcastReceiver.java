package com.example.teju.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Teju on 1/6/2018.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static String LOG_TAG = SMSBroadcastReceiver.class.getSimpleName();

    public static void dumpIntent(Intent i) {

        Bundle bundle = i.getExtras();
        Log.e(LOG_TAG, "Dumping Intent start");

        if (bundle != null) {

            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int k = 0; k < pdusObj.length; k++) {
                SmsMessage currentMesssage = SmsMessage.createFromPdu((byte[]) pdusObj[k]);
                String phoneNumber = currentMesssage.getDisplayOriginatingAddress();
                String senderNumber = phoneNumber;
                String message = currentMesssage.getDisplayMessageBody();
                Log.d(LOG_TAG, message + ", " + phoneNumber);
            }
        }
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                Log.e(LOG_TAG, "[" + key + "=" + bundle.get(key) + "]");
            }
        }
        Log.e(LOG_TAG, "Dumping Intent end");

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();

//        dumpIntent(intent);

        Message msg = new Message();
        msg.what = 7;

        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdusObj.length; i++) {
                SmsMessage currentMesssage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                String phoneNumber = currentMesssage.getDisplayOriginatingAddress();
                String senderNumber = phoneNumber;
                String message = currentMesssage.getDisplayMessageBody();

                Log.d("<><>", message + ", " + phoneNumber);

                Bundle args = new Bundle();
                args.putString("no", phoneNumber);
                args.putString("msg", message);

                msg.setData(args);
            }

            if (MainActivity.handler != null) {
                MainActivity.handler.dispatchMessage(msg);
            }

        }
    }
}

