package com.example.teju.testapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.media.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.LogRecord;

public class MainActivity extends FragmentActivity implements MsgServiceControlInterface {
    public static Handler handler;
    Dialog dialog;
    Context context;
    static DBHelper mydb;
    MessageFragment messageFm;
    SettingFragment settingFm;
    ContentMessageFragment contentMessageFm;
    boolean isMessageFragmentActive = false;
    boolean isContentMessageFragmentActive = false;
    HashMap<String, Object> mMap;

    private int noti_id_counter = 0;
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
        Log.d("NOTI-ISSUED", notification.getNumber());
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

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notification.getMessage_id(), PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager NM = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);


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

    public void issueAnotherNotification(com.example.teju.testapp.Notification notification) {
        this.notificationOerations(notification);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();


    }

    public void showDialogForBatteryLow() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        textView.setText("Your Phone battery is low");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForPowerConnected() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        textView.setText("Power is connected");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForAirplaneModeOn() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        textView.setText("Your Phone Airplane mode is ON");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForAirplaneModeOff() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        textView.setText("Your Phone Airplane mode is OFF");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void showDialogForPowerDisconnected() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        textView.setText("Power is disconnected");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForBatteryOk() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView) dialog.findViewById(R.id.text);
        textView.setText("Your Phone battery is Ok");
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = new HashMap<String, Object>();
        context = MainActivity.this;
      //  startService(new Intent(this,MyService.class));
        TestAppSharedPreferences preferences = TestAppSharedPreferences.getInstance(context);
        if (preferences.getMessageStorageEnabled() == true) {
            Log.d(MainActivity.class.getSimpleName(), "msg rcv is enabled in preferences");
            startMessageReceiverService();
        }

        mydb = new DBHelper(MainActivity.this);

        LinearLayout llContacts = (LinearLayout) findViewById(R.id.llContacts);
        LinearLayout llCallLogs = (LinearLayout) findViewById(R.id.llCallLogs);
        LinearLayout llMusic = (LinearLayout) findViewById(R.id.llMusic);
        LinearLayout llVideo = (LinearLayout) findViewById(R.id.llVideo);

        LinearLayout llAlarm = (LinearLayout) findViewById(R.id.llAlarm);
        LinearLayout llSettings = (LinearLayout) findViewById(R.id.llSettings);
        LinearLayout llMessages = (LinearLayout) findViewById(R.id.llMessages);
        LinearLayout llContentMessages = (LinearLayout) findViewById(R.id.llContentMessages);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        showDialogForBatteryLow();
                        break;
                    case 1:
                        showDialogForPowerConnected();
                        break;
                    case 2:
                        showDialogForPowerDisconnected();
                        break;
                    case 3:
                        showDialogForBatteryOk();
                        break;
                    case 4:
                        showDialogForAirplaneModeOn();
                        break;
                    case 5:
                        showDialogForAirplaneModeOff();
                        break;
                    case 6: {
                        Bundle data = msg.getData();
                        Log.d("BUNDLE", data.toString());
                        String number = data.getString("no");
                        String messageBody = data.getString("msg");
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

                        String currentTimeDate = format1.format(currentTime);
                        Log.d("currentTimeDate", "currentTimeDate" + currentTimeDate);


                        UserTextMessage userTextMessage = new UserTextMessage();
                        userTextMessage.setNumber(number);
                        userTextMessage.setDirection("receive");
                        userTextMessage.setMessageBody(messageBody);
                        userTextMessage.setDate(currentTimeDate);
                        Log.d("mainactivity", number + ", " + messageBody);

                        mydb.insertMessageDetails(userTextMessage);

                        Log.d("messageFm", messageFm + ", " + isMessageFragmentActive);
                        Log.d("contentMessageFm", contentMessageFm + ", " + isContentMessageFragmentActive);

                        if (messageFm != null && isMessageFragmentActive == true) {
                            Log.d("msgFragment", number + ", " + messageBody);
                            messageFm.recievedNewMessage(userTextMessage);
                        }
                        break;
                    }

                    case 7: {
                        Bundle data = msg.getData();
                        Log.d("BUNDLE", data.toString());
                        String number = data.getString("no");
                        String messageBody = data.getString("msg");

                       /* com.example.teju.testapp.Notification noti = (com.example.teju.testapp.Notification) mMap.get(number);
                        Log.d("NOTI", noti+"");
                        if (noti == null) {
                            noti = new com.example.teju.testapp.Notification();
                            noti.setMessageBody(messageBody);
                            noti.setNumber(number);
                            mMap.put(number, noti);
                        }

                        if (noti.getMessage_id() == -1) {
                            issueNotification(noti);
                        } else {
                            updateNotification(noti);
                        }*/

                        UserTextMessage userTextMessage = new UserTextMessage();
                        userTextMessage.setNumber(number);
                        userTextMessage.setDirection("receive");
                        userTextMessage.setMessageBody(messageBody);
                        Log.d(MainActivity.class.getSimpleName(), number + ", " + messageBody);

                        utility utility = new utility();
                        utility.addMessageToUri(MainActivity.this.getContentResolver(),
                                Telephony.Sms.Inbox.CONTENT_URI, userTextMessage.getNumber(),
                                userTextMessage.getMessageBody(),
                                userTextMessage.getMessageBody(),
                                System.currentTimeMillis(),
                                false,
                                false);

                        if (contentMessageFm != null && isContentMessageFragmentActive == true) {
                            Log.d("contentMessageFragment", number + ", " + messageBody);
                            contentMessageFm.getNewMessage(userTextMessage);
                        }
                        break;
                    }
                }

            }
        };

        llContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                contactsFragment contactsFm = new contactsFragment();
                fragmentTransaction.replace(R.id.fmMainView, contactsFm);
                fragmentTransaction.commit();
                isMessageFragmentActive = false;
                isContentMessageFragmentActive = false;
            }
        });

        llCallLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                callLogsFragment callLogsFm = new callLogsFragment();
                fragmentTransaction.replace(R.id.fmMainView, callLogsFm);
                fragmentTransaction.commit();
                isMessageFragmentActive = false;
                isContentMessageFragmentActive = false;
            }
        });

        llMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                messageFm = new MessageFragment();
                fragmentTransaction.replace(R.id.fmMainView, messageFm);
                fragmentTransaction.commit();
                isMessageFragmentActive = true;
                isContentMessageFragmentActive = false;
            }


        });

        llContentMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                contentMessageFm = new ContentMessageFragment();
                fragmentTransaction.replace(R.id.fmMainView, contentMessageFm);
                fragmentTransaction.commit();
                isMessageFragmentActive = false;
                isContentMessageFragmentActive = true;

                final String myPackageName = getPackageName();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (!Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals(myPackageName)) {
                        // App is not default.
                        // Show the "not currently set as the default SMS app" interface
                        Intent intent =
                                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                                myPackageName);
                        startActivity(intent);
                    }
                }
            }
        });


        llMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                MusicFragment musicFm = new MusicFragment();
                fragmentTransaction.replace(R.id.fmMainView, musicFm);
                fragmentTransaction.commit();
                isMessageFragmentActive = false;
                isContentMessageFragmentActive = false;
            }
        });

        llVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                VideoFragment videoFm = new VideoFragment();
                fragmentTransaction.replace(R.id.fmMainView, videoFm);
                fragmentTransaction.commit();
                isMessageFragmentActive = false;
                isContentMessageFragmentActive = false;
            }
        });
        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                SettingFragment settingFm = new SettingFragment();
                fragmentTransaction.replace(R.id.fmMainView, settingFm);
                fragmentTransaction.commit();
                isMessageFragmentActive = false;
                isContentMessageFragmentActive = false;
            }
        });

    }

    @Override
    public void startMessageReceiverService() {

    }

    @Override
    public void stopMessageReceiverService() {

    }

    /*Intent serviceIntent;

    @Override
    public void startMessageReceiverService() {
        Log.d("MainActivity", "starting service now");
        serviceIntent = new Intent(MainActivity.this, MyService.class);
        startService(serviceIntent);
    }

    @Override
    public void stopMessageReceiverService() {
        Log.d("MainActivity", "stopping service now");
        //stopService(serviceIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopMessageReceiverService();
    }*/

}

