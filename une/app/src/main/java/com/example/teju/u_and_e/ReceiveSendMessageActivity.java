package com.example.teju.u_and_e;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReceiveSendMessageActivity extends Activity implements AdapterView.OnItemClickListener {
    ListView listView;
    ReceiveSendmessageAdapter adapter;
    ArrayList<UserTextMessage>mReceiveSendMessagesList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_send_message);
         mReceiveSendMessagesList=new ArrayList<UserTextMessage>();

        Intent intent =getIntent();
        String number = intent.getStringExtra("number");
       mReceiveSendMessagesList=MainActivity.mydb.getAllRecordsOfEveryNumber(number);

        listView = (ListView) findViewById(R.id.lvSendReceive);

        if (getAssets() != null) {
            adapter = new ReceiveSendMessageActivity.ReceiveSendmessageAdapter(ReceiveSendMessageActivity.this);
            listView.setAdapter(adapter);
        }

        TextView tvMoNo = (TextView)findViewById(R.id.tvMoNo);
        tvMoNo.setText(number);

        Log.d("<><><><>", "mReceiveSendMessagesList" + mReceiveSendMessagesList);









    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public class ReceiveSendmessageAdapter extends BaseAdapter {
        Context mContext;

        ReceiveSendmessageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mReceiveSendMessagesList.size();
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
            UserTextMessage userTextMessage = mReceiveSendMessagesList.get(i);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.receive_send, null);
            }

            TextView tvReceive_Message_Body = (TextView)list.findViewById(R.id.tvReceive_Message_Body);
            TextView tvSend_Message_Body = (TextView)list.findViewById(R.id.tvSend_Message_Body);

            Log.d("<>", userTextMessage.getDirection());
            if(userTextMessage.getDirection().equals("receive")) {
                tvReceive_Message_Body.setText(userTextMessage.getMessageBody());
                tvReceive_Message_Body.setVisibility(View.VISIBLE);
                tvSend_Message_Body.setVisibility(View.GONE);
            }else{
                tvSend_Message_Body.setText(userTextMessage.getMessageBody());
                tvSend_Message_Body.setVisibility(View.VISIBLE);
                tvReceive_Message_Body.setVisibility(View.GONE);
            }


            return list;

        }

    }
}
