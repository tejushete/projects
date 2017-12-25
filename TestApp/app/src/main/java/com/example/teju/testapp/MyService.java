package com.example.teju.testapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Teju on 12/24/2017.
 */

public class MyService extends Service {


    private BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Received UserTextMessage", Toast.LENGTH_SHORT).show();
            Message msg = new Message();
            msg.what=6;

            Bundle bundle= intent.getExtras();

            if(bundle!=null){
                Object[] pdusObj =(Object[])bundle.get("pdus");
                for(int i=0;i<pdusObj.length;i++){
                    SmsMessage currentMesssage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMesssage.getDisplayOriginatingAddress();
                    String senderNumber = phoneNumber;
                    String message = currentMesssage.getDisplayMessageBody();

                    Log.d("<><>", message+", "+phoneNumber);

                    Bundle args = new Bundle();
                    args.putString("no", phoneNumber);
                    args.putString("msg", message);

                    msg.setData(args);

                }
            }
            if (MainActivity.handler!=null){
                MainActivity.handler.dispatchMessage(msg);
            }



        }
    };

    public void registerMsgReceiver(){
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver,intentFilter);
    }

    public void unregisterMsgReceiver(){
        unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(MyService.this,"Service is Created",Toast.LENGTH_SHORT).show();
        Log.d("<><><><>","Service Create");
        registerMsgReceiver();
        super.onCreate();
    }

    static int caller = 0;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            caller++;
            final Handler handler = new Handler();
            handler.postDelayed(runnable,1000);
          //  Log.d("SERVICE<>", "running "+caller);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MyService.this,"Service is Started",Toast.LENGTH_SHORT).show();
        runnable.run();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterMsgReceiver();
        Log.d("<<", "service onDestroy");
//        Intent broadcastIntent = new Intent("com.example.teju.custom.intent.action.TEST");
//        sendBroadcast(broadcastIntent);
    }
}
