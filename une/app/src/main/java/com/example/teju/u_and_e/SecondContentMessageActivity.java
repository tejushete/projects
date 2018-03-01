package com.example.teju.u_and_e;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashMap;

public class SecondContentMessageActivity extends Activity implements AdapterView.OnItemClickListener {
    ListView listView;
    SecondContentMessageAdapter adapter;
    ArrayList<UserTextMessage> mSelectedList;
    HashMap<String, ArrayList<UserTextMessage>> mMap;
    String contentNumber, message;
    EditText etSendMessage;
    TestAppSharedPreferences testAppSharedPreferences;
    DBHelper mydb;
    Uri picsUri;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public  void seneMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        0);
            }
            return;
        }

        message = etSendMessage.getText().toString();

        if (contentNumber.isEmpty() || message.isEmpty()) {
            Toast.makeText(SecondContentMessageActivity.this, "please Type Message", Toast.LENGTH_SHORT).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contentNumber, null, message, null, null);
            etSendMessage.setText("");
            etSendMessage.setHint("Type message");
            final UserTextMessage userTextMessage = new UserTextMessage();
            userTextMessage.setMessageBody(message);
            userTextMessage.setNumber(contentNumber);
            userTextMessage.setType("2");
            userTextMessage.setReadStatus(true);

            utility uti = new utility();
            uti.addMessageToUri(SecondContentMessageActivity.this.getContentResolver(),
                    Telephony.Sms.Sent.CONTENT_URI,
                    contentNumber,
                    message,
                    null,
                    System.currentTimeMillis(),
                    false,
                    false);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mSelectedList.add(userTextMessage);
                    adapter.notifyDataSetChanged();

                }
            });

            Intent intent = new Intent();
            intent.putExtra("message",message);
            intent.putExtra("number",contentNumber);
            intent.putExtra("type", TestConstants.MESSAGE_TYPE_SEND);
            setResult(RESULT_OK,intent);

            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getSelectedNumberMessages() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Cursor cursor = getContentResolver().query(
                        Telephony.Sms.CONTENT_URI,
                        new String[]{"_id", "address", "date", "body", "type"},
                        "address='" + contentNumber.trim() + "'",
                        null,
                        "date COLLATE NOCASE ASC");
                cursor.moveToFirst();

                if(cursor.getCount() == 0) return;

                do {
                    String message = cursor.getString(3);
                    String id = cursor.getString(0);
                    String type = cursor.getString(4);
                    Log.d("<><><>", "message: " + message);

                    final UserTextMessage textMessage = new UserTextMessage();
                    textMessage.setMessageBody(message);
                    textMessage.setReadStatus(true);
                    textMessage.setType(type);
                    utility uti1 = new utility();
                    uti1.updateMsgReadStatus(SecondContentMessageActivity.this, id, textMessage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSelectedList.add(textMessage);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } while (cursor.moveToNext());

            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_content_message);
        mSelectedList = new ArrayList<UserTextMessage>();
        etSendMessage = (EditText)findViewById(R.id.etSendMessage);
        testAppSharedPreferences = TestAppSharedPreferences.getInstance(SecondContentMessageActivity.this);
        listView = (ListView)findViewById(R.id.lvSecondContent);
        mydb = new DBHelper(SecondContentMessageActivity.this);
        if (getAssets() != null) {
            adapter = new SecondContentMessageActivity.SecondContentMessageAdapter(SecondContentMessageActivity.this);
            listView.setAdapter(adapter);
        }

        ImageView ivSend = (ImageView)findViewById(R.id.ivSend);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seneMessage();
            }
        });

        Intent intent = getIntent();
        contentNumber = intent.getStringExtra("contentNumber");

        Boolean value = intent.getBooleanExtra("NotiClick",false);
         String img = intent.getExtras().getString("Image");
         if(img!=null) {
             picsUri = Uri.parse(img);
         }

        if(value==null){
            return;
        }else{
            Notification notification = new Notification(contentNumber);
            mydb.updateNotificationDetails(notification);
        }

        Log.d("<><><><>", "ContentNumber" + contentNumber);

        etSendMessage.setText(testAppSharedPreferences.getDraftMessage(contentNumber));
        TextView tvTitlePhoneNumber = (TextView) findViewById(R.id.tvTitlePhoneNumber);
        tvTitlePhoneNumber.setText(contentNumber);

        getSelectedNumberMessages();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    public class SecondContentMessageAdapter extends BaseAdapter {
        Context mContext;

        SecondContentMessageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mSelectedList.size();
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
            UserTextMessage textMessage = mSelectedList.get(i);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (list == null) {
                list = inflater.inflate(R.layout.receive_send, null);
            }

            if(textMessage.getType().equals("1")) {
                TextView tvReceive_Message_Body = (TextView) list.findViewById(R.id.tvReceive_Message_Body);
                TextView tvSend_Message_Body = (TextView) list.findViewById(R.id.tvSend_Message_Body);
                ImageView ivReceiveMsg =(ImageView)list.findViewById(R.id.ivReceiveMsg);
                ImageView ivSendMsg = (ImageView)list.findViewById(R.id.ivSendMsg);
                tvReceive_Message_Body.setText(textMessage.getMessageBody());
                tvReceive_Message_Body.setVisibility(View.VISIBLE);
                tvSend_Message_Body.setVisibility(View.INVISIBLE);
                ivReceiveMsg.setVisibility(View.VISIBLE);
                ivSendMsg.setVisibility(View.INVISIBLE);
                if(getAssets() != null) {
                    Glide.with(SecondContentMessageActivity.this)
                            .load(picsUri)
                            .placeholder(R.drawable.receive_send_profile)
                            .into(ivReceiveMsg);
                }
            }else {
                TextView tvSend_Message_Body = (TextView) list.findViewById(R.id.tvSend_Message_Body);
                TextView tvReceive_Message_Body = (TextView) list.findViewById(R.id.tvReceive_Message_Body);
                ImageView ivReceiveMsg =(ImageView)list.findViewById(R.id.ivReceiveMsg);
                ImageView ivSendMsg = (ImageView)list.findViewById(R.id.ivSendMsg);
                tvSend_Message_Body.setText(textMessage.getMessageBody());
                tvSend_Message_Body.setVisibility(View.VISIBLE);
                tvReceive_Message_Body.setVisibility(View.INVISIBLE);
                ivReceiveMsg.setVisibility(View.INVISIBLE);
                ivSendMsg.setVisibility(View.VISIBLE);
            }

            return list;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    seneMessage();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onBackPressed() {
        message = etSendMessage.getText().toString();
        if(message!=null&& !message.isEmpty()){
            testAppSharedPreferences.setDraftMessage(contentNumber,message);
            Intent intent = new Intent();
            intent.putExtra("message",message);
            intent.putExtra("number",contentNumber);
            setResult(RESULT_OK,intent);

        }else{

            if(mSelectedList.size() != 0) {
                UserTextMessage msg = mSelectedList.get(mSelectedList.size() - 1);
                String message = msg.getMessageBody();
                testAppSharedPreferences.setDraftMessage(contentNumber, "");

                Intent intent = new Intent();
                Log.d("<><><>", "message" + message);
                intent.putExtra("message", message);
                intent.putExtra("number", contentNumber);
                setResult(RESULT_OK, intent);
            }
        }

        super.onBackPressed();

    }
}

