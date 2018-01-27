package com.example.teju.testapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private static final int RESULT_OK = 0;
    ListView listView;
    MessageFragmentAdapter adapter;
    ArrayList<UserTextMessage>mMessageList;
    ArrayList<UserTextMessage>mList;
    HashMap<String,ArrayList<UserTextMessage>> mMap ;

    public MessageFragment() {
        // Required empty public constructor
    }

    public void requestPermission(){

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        mMessageList=new ArrayList<UserTextMessage>();
        mList = new ArrayList<UserTextMessage>();
        mMap= new HashMap<String, ArrayList<UserTextMessage>>();

        ImageView ivAddMsg = (ImageView)view.findViewById(R.id.ivAddMsg);
        ivAddMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getActivity(),SendMessageActivity.class);
                startActivityForResult(intent1,0);
            }
        });


        listView = (ListView)view.findViewById(R.id.lvMessage);
        if(getActivity()!=null){
            adapter = new MessageFragment.MessageFragmentAdapter(this.getActivity());
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserTextMessage userTextMessage = mMessageList.get(i);
                Intent intent2 = new Intent(getActivity(),ReceiveSendMessageActivity.class);
                intent2.putExtra("number",userTextMessage.getNumber().toString());
                startActivity(intent2);
            }
        });

        requestPermission();

        mMessageList = MainActivity.mydb.getLastRecordOfEveryNumber();

        return view;
    }

    public void recievedNewMessage(UserTextMessage msg)  {
        Log.d("MessageFragment", "msg received "+msg.getNumber()+", "+msg.getMessageBody());

        for(int i=0;i<mMessageList.size();i++){
            UserTextMessage lMsg = mMessageList.get(i);
            if(lMsg.getNumber().equals(msg.getNumber())){
                mMessageList.remove(lMsg);
                break;
            }
        }

        mMessageList.add(0, msg);
        adapter.notifyDataSetChanged();
    }

    public class MessageFragmentAdapter extends BaseAdapter {
        Context mContext;

        MessageFragmentAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mMessageList.size();
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
            UserTextMessage msg =mMessageList.get(i);

            mList = MainActivity.mydb.getAllRecordsOfEveryNumber(msg.getNumber());

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.message, null);
            }

            TextView tvPhoneNumber = (TextView)list.findViewById(R.id.tvPhoneNumber);
            TextView tvMessageBody = (TextView)list.findViewById(R.id.tvMessageBody);
            TextView tvSize = (TextView)list.findViewById(R.id.tvSize);
            tvPhoneNumber.setText(msg.getNumber());
            tvMessageBody.setText(msg.getMessageBody());

           tvSize.setText(mList.size()+"");
            return list;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("<><>","onActivity");
        switch(requestCode){
            case 0:String message = data.getStringExtra("Message Body");
                String phoneNo = data.getStringExtra("Phone No");
                String currentTimeDate = data.getStringExtra("current Time and date");

                UserTextMessage userTextMessage= new UserTextMessage();

                userTextMessage.setMessageBody(message);
                userTextMessage.setNumber(phoneNo);
                userTextMessage.setDate(currentTimeDate);

                for(int i=0;i<mMessageList.size();i++) {
                    UserTextMessage lMsg = mMessageList.get(i);
                    if (lMsg.getNumber().equals(userTextMessage.getNumber())) {
                        mMessageList.remove(lMsg);
                        break;
                    }
                }

                mMessageList.add(0,userTextMessage);
                adapter.notifyDataSetChanged();
                break;
        }
        }
    }
