package com.example.teju.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SecondContentMessageActivity extends Activity implements AdapterView.OnItemClickListener {
    ListView listView;
    SecondContentMessageAdapter adapter;
    ArrayList<UserTextMessage> mSelectedList;
    HashMap<String, ArrayList<UserTextMessage>> mMap;
    String contentNumber;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getSelectedNumberMessages() {
        Cursor cursor = getContentResolver().query(
                Telephony.Sms.CONTENT_URI,
                new String[]{"_id", "address", "date", "body",},
                "address='"+contentNumber.trim()+"'",
                null,
                "date DESC");
        cursor.moveToFirst();
        while (cursor.moveToNext()) {

            String message = cursor.getString(3);
            UserTextMessage textMessage= new UserTextMessage();
            textMessage.setMessageBody(message);

            mSelectedList.add(textMessage);
            adapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_content_message);
        mSelectedList = new ArrayList<UserTextMessage>();



        listView = (ListView)findViewById(R.id.lvSecondContent);
        if (getAssets() != null) {
            adapter = new SecondContentMessageActivity.SecondContentMessageAdapter(SecondContentMessageActivity.this);
            listView.setAdapter(adapter);
        }


        Intent intent = getIntent();
        contentNumber = intent.getStringExtra("contentNumber");
        Log.d("<><><><>", "ContentNumber" + contentNumber);


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
            TextView tvReceive_Message_Body = (TextView)list.findViewById(R.id.tvReceive_Message_Body);
            tvReceive_Message_Body.setText(textMessage.getMessageBody());


            return list;
        }

    }
}
