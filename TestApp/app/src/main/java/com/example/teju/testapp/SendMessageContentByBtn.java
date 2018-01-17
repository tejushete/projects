package com.example.teju.testapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SendMessageContentByBtn extends Activity {
    String Number, Message;
    EditText etPhoneNumber;
    EditText etTypeMessage;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(Manifest.permission.SEND_SMS)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
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

        int result = ActivityCompat.checkSelfPermission(SendMessageContentByBtn.this, Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void sendMessage() {


        Number = etPhoneNumber.getText().toString();
        Message = etTypeMessage.getText().toString();

        if (Number.isEmpty() || Message.isEmpty()) {
            Toast.makeText(SendMessageContentByBtn.this, "please Type Message", Toast.LENGTH_SHORT).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Number, null, Message, null, null);
            etTypeMessage.setText("");
            etTypeMessage.setHint("Type message");
            utility uti = new utility();
            uti.addMessageToUri(SendMessageContentByBtn.this.getContentResolver(),
                    Telephony.Sms.Sent.CONTENT_URI,
                    Number,
                    Message,
                    null,
                    System.currentTimeMillis(),
                    false,
                    false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_content_by_btn);

        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etTypeMessage = (EditText) findViewById(R.id.etTypeMessage);

        ImageView ivSendMessage = (ImageView) findViewById(R.id.ivSendMessage);
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                Intent intent = new Intent();
                intent.putExtra("Number", Number);
                intent.putExtra("Message", Message);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }
}
