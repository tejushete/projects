package com.example.teju.testapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.logging.LogRecord;

public class MainActivity extends FragmentActivity implements MsgServiceControlInterface {
    public static Handler handler;
    Dialog dialog;
    Context context;
    static DBHelper mydb;
    MessageFragment messageFm;
    boolean isMessageFragmentActive = false;


    public void showDialogForBatteryLow(){
        if( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView)dialog.findViewById(R.id.text);
        textView.setText("Your Phone battery is low");
        TextView tvCancel = (TextView)dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void showDialogForPowerConnected(){
        if( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView)dialog.findViewById(R.id.text);
        textView.setText("Power is connected");
        TextView tvCancel = (TextView)dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForAirplaneModeOn(){
        if( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView)dialog.findViewById(R.id.text);
        textView.setText("Your Phone Airplane mode is ON");
        TextView tvCancel = (TextView)dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForAirplaneModeOff(){
        if( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView)dialog.findViewById(R.id.text);
        textView.setText("Your Phone Airplane mode is OFF");
        TextView tvCancel = (TextView)dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }




    public void showDialogForPowerDisconnected(){
        if( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView)dialog.findViewById(R.id.text);
        textView.setText("Power is disconnected");
        TextView tvCancel = (TextView)dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void showDialogForBatteryOk(){
        if( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_pop_up_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = (TextView)dialog.findViewById(R.id.text);
        textView.setText("Your Phone battery is Ok");
        TextView tvCancel = (TextView)dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        mydb=new DBHelper(MainActivity.this);

        LinearLayout llContacts = (LinearLayout)findViewById(R.id.llContacts);
        LinearLayout llCallLogs = (LinearLayout)findViewById(R.id.llCallLogs);
        LinearLayout llMusic = (LinearLayout)findViewById(R.id.llMusic);
        LinearLayout llVideo = (LinearLayout)findViewById(R.id.llVideo);

        LinearLayout llAlarm = (LinearLayout)findViewById(R.id.llAlarm);
        LinearLayout llSettings = (LinearLayout)findViewById(R.id.llSettings);
        LinearLayout llMessages =(LinearLayout)findViewById(R.id.llMessages);

         handler = new Handler(){
             public void handleMessage(Message msg){
                 switch (msg.what){
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

                         UserTextMessage userTextMessage = new UserTextMessage();
                         userTextMessage.setNumber(number);
                         userTextMessage.setMessageBody(messageBody);

                         Log.d("mainactivity", number+", "+messageBody);

                         mydb.insertMessageDetails(userTextMessage);

                         if(messageFm!=null&&isMessageFragmentActive==true){
                             messageFm.recievedNewMessage(userTextMessage);

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
            if(oldFragment != null) {
                fragmentTransaction.remove(oldFragment);
            }
            contactsFragment contactsFm = new contactsFragment();
            fragmentTransaction.replace(R.id.fmMainView, contactsFm);
            fragmentTransaction.commit();
            isMessageFragmentActive=false;
        }
    });

        llCallLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if(oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                callLogsFragment callLogsFm = new callLogsFragment();
                fragmentTransaction.replace(R.id.fmMainView, callLogsFm);
                fragmentTransaction.commit();
                isMessageFragmentActive=false;
            }
        });

       llMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);

                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if(oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                 messageFm = new MessageFragment();
                fragmentTransaction.replace(R.id.fmMainView, messageFm);
                fragmentTransaction.commit();
                isMessageFragmentActive=true;
            }
        });


        llMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if(oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                MusicFragment musicFm = new MusicFragment();
                fragmentTransaction.replace(R.id.fmMainView, musicFm);
                fragmentTransaction.commit();
                isMessageFragmentActive=false;
            }
        });

        llVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if(oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                VideoFragment videoFm = new VideoFragment();
                fragmentTransaction.replace(R.id.fmMainView, videoFm);
                fragmentTransaction.commit();
                isMessageFragmentActive=false;
            }
        });
        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if(oldFragment != null) {
                    fragmentTransaction.remove(oldFragment);
                }
                SettingFragment settingFm = new SettingFragment();
                fragmentTransaction.replace(R.id.fmMainView, settingFm);
                fragmentTransaction.commit();
                isMessageFragmentActive=false;
            }
        });

    }

    Intent serviceIntent;
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
    }
}

