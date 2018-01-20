package com.example.teju.testapp;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Teju on 12/24/2017.
 */

public class MyService extends Service {

    private int noti_id_counter = 0;
    HashMap<String, Object> mMap;
    DBHelper mydb;

   /* private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Received UserTextMessage", Toast.LENGTH_SHORT).show();*/
//            Message msg = new Message();
//            msg.what=6;
//
//            Bundle bundle= intent.getExtras();
//
//            if(bundle!=null){
//                Object[] pdusObj =(Object[])bundle.get("pdus");
//                for(int i=0;i<pdusObj.length;i++){
//                    SmsMessage currentMesssage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
//                    String phoneNumber = currentMesssage.getDisplayOriginatingAddress();
//                    String senderNumber = phoneNumber;
//                    String message = currentMesssage.getDisplayMessageBody();
//
//                    Log.d("<><>", message+", "+phoneNumber);
//
//                    Bundle args = new Bundle();
//                    args.putString("no", phoneNumber);
//                    args.putString("msg", message);
//
//                    msg.setData(args);
//
//                }
//            }
//            if (MainActivity.handler!=null){
//                MainActivity.handler.dispatchMessage(msg);
//            }


      //  }
  //  };

//    public void registerMsgReceiver() {
//        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(broadcastReceiver, intentFilter);
//    }

   /* public void unregisterMsgReceiver() {
        unregisterReceiver(broadcastReceiver);
    }
*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mMap = new HashMap<String, Object>();
        mydb = new DBHelper(getBaseContext());
        Toast.makeText(MyService.this, "Service is Created", Toast.LENGTH_SHORT).show();
        Log.d("SERVICE", "Service Created");
      //  registerMsgReceiver();
        super.onCreate();
    }

    static int caller = 0;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            caller++;
            final Handler handler = new Handler();
            handler.postDelayed(runnable, 1000);
            //  Log.d("SERVICE<>", "running "+caller);
        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MyService.this, "Service is Started", Toast.LENGTH_SHORT).show();
        runnable.run();

        Log.d("SERVICE-ON-START-CMD", "service command");

        if(intent == null) {
            return Service.START_NOT_STICKY;
        }

        String message = intent.getStringExtra("msg");
        String number = intent.getStringExtra("number");
        if(number == null || number.isEmpty()) return Service.START_NOT_STICKY;

        com.example.teju.testapp.Notification noti = (com.example.teju.testapp.Notification) mydb.getDetailsOfNotification(number);

        Log.d("SERVICE-ON-START-CMD", noti+"");
        if (noti == null) {
            noti = new com.example.teju.testapp.Notification();
            noti.setNumber(number);
            mydb.insertNotificationDetails(noti);
        }

        Log.d("SERVICE-ON-START-CMD", noti.getMessage_id()+"");

        noti.setMessageBody(message);

        if (noti.getMessage_id() == -1) {
            issueNotification(noti);
        } else {
            updateNotification(noti);
        }

        UserTextMessage userTextMessage = new UserTextMessage();
        userTextMessage.setNumber(number);
        userTextMessage.setDirection("receive");
        userTextMessage.setMessageBody(message);
        Log.d(MainActivity.class.getSimpleName(), number + ", " + message);

        utility utility = new utility();
        utility.addMessageToUri(getBaseContext().getContentResolver(),
                Telephony.Sms.Inbox.CONTENT_URI, userTextMessage.getNumber(),
                userTextMessage.getMessageBody(),
                userTextMessage.getMessageBody(),
                System.currentTimeMillis(),
                false,
                false);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // unregisterMsgReceiver();
        Log.d("SERVICE-ON-DESTROY", "service onDestroy");
      Intent broadcastIntent = new Intent("com.example.teju.custom.intent.action.TEST");
      sendBroadcast(broadcastIntent);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notificationOerations(com.example.teju.testapp.Notification notification) {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.noti_one_msg);
        mBuilder.setContentTitle(notification.getNumber() + "  " + notification.getMessage_count());
        mBuilder.setContentText(notification.getMessageBody());
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setAutoCancel(true);
        Intent intent = new Intent(this, SecondContentMessageActivity.class);
        intent.putExtra("NotiClick",true);
        Log.d("NOTI-ISSUED", notification.getNumber()+"");
        intent.putExtra("contentNumber", notification.getNumber());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SecondContentMessageActivity.class);
        stackBuilder.addNextIntent(intent);

        if(notification.getMessage_id() == -1){
            noti_id_counter++;
            if(noti_id_counter == Integer.MAX_VALUE){
                noti_id_counter = 1;
            }
            notification.setMessage_id(noti_id_counter);
        }

        mydb.updateNotificationDetails(notification);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notification.getMessage_id(), PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager NM = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);

        NM.notify(notification.getMessage_id(), mBuilder.build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void issueNotification(com.example.teju.testapp.Notification notification) {
        notification.increaseMeassageCountByOne();
        this.notificationOerations(notification);
        Log.d("<><><>", "messageCount" + notification.getMessage_count());
    }

    public void updateNotification(com.example.teju.testapp.Notification notification) {
        notification.increaseMeassageCountByOne();
        this.notificationOerations(notification);
    }
}
