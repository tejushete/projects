package com.example.teju.testapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SendMessageActivity extends Activity implements AdapterView.OnItemClickListener {
    EditText etPhoneNumber;
    EditText etTypeMessage;
    String phoneNo;
    String message;
    ImageView ivSendMessage;
    ListView listView;
    SendMessageAdapter adapter;
    ArrayList<SendMessage>mSendingMessageActivityList;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("<>", "onRequestPermissionsResult");

        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(Manifest.permission.SEND_SMS)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                } else {
                    requestPermission();
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                return;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermission() {

        int result = ActivityCompat.checkSelfPermission(SendMessageActivity.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSMS() {
        phoneNo = etPhoneNumber.getText().toString();
        message = etTypeMessage.getText().toString();
        requestPermission();
        if (phoneNo.isEmpty() || message.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Check Phone No or Msg Body", Toast.LENGTH_SHORT).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(SendMessageActivity.this, "SMS Sent", Toast.LENGTH_SHORT).show();
            UserTextMessage sendMessage = new UserTextMessage();
            sendMessage.setNumber(phoneNo);
            sendMessage.setMessageBody(message);
            sendMessage.setDirection("send");

           // MainActivity.mydb.insertMessageDetails(sendMessage);
//            mSendingMessageActivityList.add(sendMessage);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        mSendingMessageActivityList=new ArrayList<SendMessage>();
     //   mSendingMessageActivityList=MainActivity.mydb.sendAllMessages();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        listView = (ListView) findViewById(R.id.lvSendMessages);
        if (getAssets() != null) {
            adapter = new SendMessageActivity.SendMessageAdapter(SendMessageActivity.this);
            listView.setAdapter(adapter);
        }
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etTypeMessage = (EditText) findViewById(R.id.etTypeMessage);

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivSendMessage = (ImageView) findViewById(R.id.ivSendMessage);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public class SendMessageAdapter extends BaseAdapter {
        Context mContext;

        SendMessageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mSendingMessageActivityList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View list = view;
           // SendMessage sendMessage = mSendingMessageActivityList.get(i);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.send_message, null);
            }
              TextView tvSendMessageBody = (TextView)list.findViewById(R.id.tvSendMessageBody);
         //   tvSendMessageBody.setText(m.getSendMessageBody());




            return list;
        }

    }
}
