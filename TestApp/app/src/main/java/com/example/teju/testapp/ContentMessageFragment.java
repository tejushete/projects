package com.example.teju.testapp;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentMessageFragment extends Fragment{
    ListView listView;
    ContentMessageAdapter adapter;
    ArrayList<UserTextMessage> mContentMessageList;

    public ContentMessageFragment() {
        // Required empty public constructor
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("<>", "onRequestPermissionsResult");

        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(Manifest.permission.READ_SMS+Manifest.permission.RECEIVE_SMS)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getSms();
                } else {
                    requestPermission();
                }
                return;
            }
        }
    }
    private void requestPermission() {

        int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS)+ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.RECEIVE_SMS);

        if (result == PackageManager.PERMISSION_GRANTED) {
            getSms();
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                    0);
        }

    }

    public void getNewMessage(UserTextMessage userTextMessage){
        Log.d("ContentMessageFragment", "msg received "+userTextMessage.getNumber()+", "+userTextMessage.getMessageBody());
        //mCompleteMessageList.add(0, userTextMessage);
        adapter.notifyDataSetChanged();
    }
    public void getSms() {
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Log.d("<><><>","getsmscalled");
                HashMap<String,String>mMap = new HashMap<String, String>();
                Cursor cursor = getActivity().getContentResolver().query(
                        Telephony.Sms.CONTENT_URI,
                        new String[]{"_id", "address", "date", "body",},
                        null,
                        null,
                        "date DESC");

                cursor.moveToFirst();
                do {
                    String address = cursor.getString(1);
                    String body = cursor.getString(3);
                    String date = cursor.getString(2);
                    Long timestamp = Long.parseLong(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);
                    Date finalTime = calendar.getTime();
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                    String currentTimeDate = format1.format(finalTime);

                    Log.d("Date","date"+currentTimeDate);
                    final UserTextMessage userTextMessage;

                    userTextMessage = new UserTextMessage();
                    userTextMessage.setMessageBody(body);
                    userTextMessage.setNumber(address);
                    userTextMessage.setDate(currentTimeDate);

                    if(mMap.get(userTextMessage.getNumber())==null){
                        Log.d("<><><>", "Address" + address);
                        mMap.put(userTextMessage.getNumber(),userTextMessage.getMessageBody());
                        mContentMessageList.add(userTextMessage);
                    }

                }while(cursor.moveToNext());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        }).start();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content_message, container, false);
        //mCompleteMessageList =new ArrayList<UserTextMessage>();
        mContentMessageList=new ArrayList<UserTextMessage>();


        listView=(ListView)view.findViewById(R.id.lvContentMessage);
        if(getActivity()!=null){
            adapter = new ContentMessageFragment.ContentMessageAdapter(getActivity());
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               UserTextMessage userTextMessage = mContentMessageList.get(i);

                Intent intent =new Intent(getActivity(),SecondContentMessageActivity.class);
               intent.putExtra("contentNumber",userTextMessage.getNumber());
                startActivity(intent);
            }
        });
        requestPermission();

        return view;
    }


    public class ContentMessageAdapter extends BaseAdapter{
      Context mContext;

      ContentMessageAdapter(Context c){
          mContext=c;
      }
        @Override
        public int getCount() {
            return mContentMessageList.size();
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

          UserTextMessage item1 = mContentMessageList.get(i);


            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(list==null){
                list=inflater.inflate(R.layout.message_content,null);
            }

            TextView tvContentMessageBody = (TextView)list.findViewById(R.id.tvContentMessageBody);
            TextView tvContentPhoneNumber = (TextView)list.findViewById(R.id.tvContentPhoneNumber);
            TextView tvContentMessageDateAndTime  = (TextView)list.findViewById(R.id.tvContentMessageDateAndTime);
            tvContentMessageBody.setText(item1.getMessageBody());
            tvContentPhoneNumber.setText(item1.getNumber());
            tvContentMessageDateAndTime.setText(item1.getDate());

            return list;
        }
    }
}
