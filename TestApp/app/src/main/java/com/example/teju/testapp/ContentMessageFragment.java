package com.example.teju.testapp;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentMessageFragment extends Fragment {
    ListView listView;
    ContentMessageAdapter adapter;
    ArrayList<UserTextMessage> mContentMessageList;
    int REQUEST_CODE_BUTTON_SEND_ACTIVITY = 1;
    boolean isMessageRead = false;
    HashMap<String, Integer[]> mMap;
    TestAppSharedPreferences testAppSharedPreferences;

    public ContentMessageFragment() {
        // Required empty public constructor
    }




    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("<>", "onRequestPermissionsResult");

        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(Manifest.permission.READ_SMS + Manifest.permission.RECEIVE_SMS)) {
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

        int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) + ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS);

        if (result == PackageManager.PERMISSION_GRANTED) {
            getSms();
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                    0);
        }
    }


    public void getNewMessage(UserTextMessage userTextMessage) {

        Log.d("ContentMessageFragment", "msg received " + userTextMessage.getNumber() + ", " + userTextMessage.getMessageBody());
        String no=userTextMessage.getNumber().replaceAll("\\s+", "");

        for (int i = 0; i < mContentMessageList.size(); i++) {
            UserTextMessage listMesg = mContentMessageList.get(i);
            if (listMesg.getNumber().equals(no)) {
                mContentMessageList.remove(listMesg);
                break;
            }
        }
        mContentMessageList.add(0, userTextMessage);
        Integer array[] = mMap.get(userTextMessage.getNumber());
        array[0] = array[0] + 1;
        mMap.put(userTextMessage.getNumber(), array);
        adapter.notifyDataSetChanged();
    }

    public void getSms() {
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Log.d("<><><>", "getsmscalled");
                mMap = new HashMap<String, Integer[]>();
                Cursor cursor = getActivity().getContentResolver().query(
                        Telephony.Sms.CONTENT_URI,
                        new String[]{"_id", "address", "date", "body", "read", "type"},
                        null,
                        null,
                        "date COLLATE NOCASE DESC");

                cursor.moveToFirst();
                do {
                    String address = cursor.getString(1);
                    address=address.replaceAll("\\s+", "");

                    if (address == null || address.isEmpty()) {
                        continue;
                    }
                    String body = cursor.getString(3);
                    String date = cursor.getString(2);
                    int type = cursor.getInt(5);

                    int read = cursor.getInt(4);


                    Long timestamp = Long.parseLong(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);
                    Date finalTime = calendar.getTime();
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                    String currentTimeDate = format1.format(finalTime);

                    Log.d("Date", "date" + currentTimeDate);
                    Log.d("<><><>", "readValue" + read);

                    final UserTextMessage userTextMessage;

                    userTextMessage = new UserTextMessage();
                    userTextMessage.setMessageBody(body);
                    userTextMessage.setNumber(address);
                    userTextMessage.setDate(currentTimeDate);
                    userTextMessage.setReadStatus(read == 1);


                    if (mMap.get(userTextMessage.getNumber()) == null) {
                        Log.d("<><><>", "Address" + address);

                        if(getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContentMessageList.add(userTextMessage);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        Integer array[] = new Integer[2];
                        array[1] = 0;
                        if (userTextMessage.getReadStatus() == true) {
                            array[0] = 0;
                            mMap.put(userTextMessage.getNumber(), array);
                        } else {
                            array[0] = 1;
                            mMap.put(userTextMessage.getNumber(), array);
                        }
                    } else {
                        if (userTextMessage.getReadStatus() == false) {
                            Integer array[] = mMap.get(userTextMessage.getNumber());
                            array[0] = array[0] + 1;
                            array[1] = 1;
                            mMap.put(userTextMessage.getNumber(), array);
                        }
                    }

                } while (cursor.moveToNext());


            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content_message, container, false);
        testAppSharedPreferences = TestAppSharedPreferences.getInstance(getContext());
        //mCompleteMessageList =new ArrayList<UserTextMessage>();
        mContentMessageList = new ArrayList<UserTextMessage>();


        listView = (ListView) view.findViewById(R.id.lvContentMessage);
        if (getActivity() != null) {
            adapter = new ContentMessageFragment.ContentMessageAdapter(getActivity());
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserTextMessage userTextMessage = mContentMessageList.get(i);
                userTextMessage.setReadStatus(true);
                Integer[] array = mMap.get(userTextMessage.getNumber());
                array[0] = 0;
                mMap.put(userTextMessage.getNumber(), array);
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(getActivity(), SecondContentMessageActivity.class);
                intent.putExtra("contentNumber", userTextMessage.getNumber());
                startActivityForResult(intent, 0);
            }
        });

        ImageView ivSendContentMessageByImage = (ImageView) view.findViewById(R.id.ivSendContentMessageByImage);
        ivSendContentMessageByImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SendMessageContentByBtn.class);
                startActivityForResult(intent, REQUEST_CODE_BUTTON_SEND_ACTIVITY);
            }
        });
        requestPermission();
        return view;
    }

    public class ContentMessageAdapter extends BaseAdapter {
        Context mContext;

        ContentMessageAdapter(Context c) {
            mContext = c;
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
            if (list == null) {
                list = inflater.inflate(R.layout.message_content, null);
            }

            TextView tvContentMessageBody = (TextView) list.findViewById(R.id.tvContentMessageBody);
            TextView tvContentPhoneNumber = (TextView) list.findViewById(R.id.tvContentPhoneNumber);
            TextView tvContentMessageDateAndTime = (TextView) list.findViewById(R.id.tvContentMessageDateAndTime);
            RelativeLayout rlMessageBodyHolder = (RelativeLayout) list.findViewById(R.id.rlMessageBodyHolder);
            tvContentMessageBody.setText(item1.getMessageBody());
            tvContentPhoneNumber.setText(item1.getNumber());
            tvContentMessageDateAndTime.setText(item1.getDate());
            TextView tvUnreadMessages = (TextView) list.findViewById(R.id.tvUnreadMessages);
            TextView tvContentMessageDraft = (TextView) list.findViewById(R.id.tvContentMessageDraft);


            Integer finalUnreadMsgValue[] = mMap.get(item1.getNumber());

            Log.d("getView", item1.getNumber()+"");

            int unRead = (finalUnreadMsgValue == null) ? 0 : finalUnreadMsgValue[0];
            if (unRead == 0) {
                tvUnreadMessages.setVisibility(View.GONE);
                rlMessageBodyHolder.setVisibility(View.GONE);
            } else {
                rlMessageBodyHolder.setVisibility(View.VISIBLE);
                tvUnreadMessages.setVisibility(View.VISIBLE);
                tvUnreadMessages.setText(finalUnreadMsgValue[0] + "");
            }

            if (testAppSharedPreferences.getDraftMessage(item1.getNumber()).isEmpty() == true) {
                tvContentMessageDraft.setVisibility(View.GONE);
            } else {
                tvContentMessageDraft.setVisibility(View.VISIBLE);
            }

            return list;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("<>", "onActivityResult");
        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {

            Log.d("<>", "secondMsgActivity result");
            String message = data.getStringExtra("message");
            String phoneNumber = data.getStringExtra("number");



            UserTextMessage userTextMessage = new UserTextMessage();
            userTextMessage.setNumber(phoneNumber);
            userTextMessage.setMessageBody(message);
            userTextMessage.setReadStatus(true);


            for (int i = 0; i < mContentMessageList.size(); i++) {
                UserTextMessage lMsg = mContentMessageList.get(i);
                if (lMsg.getNumber().equals(userTextMessage.getNumber())) {
                    mContentMessageList.remove(lMsg);
                    break;
                }
            }

            mContentMessageList.add(0, userTextMessage);
            adapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_CODE_BUTTON_SEND_ACTIVITY && resultCode == getActivity().RESULT_OK) {

            String Number = data.getStringExtra("Number");
            String Message = data.getStringExtra("Message");

            UserTextMessage userTextMessage = new UserTextMessage();
            userTextMessage.setNumber(Number);
            userTextMessage.setMessageBody(Message);
            userTextMessage.setReadStatus(true);
            if (mMap.get(userTextMessage.getNumber()) == null) {
                Integer array[] = new Integer[2];
                array[0] = 0;
                array[1] = 0;
                mMap.put(userTextMessage.getNumber(), array);
            }

            Log.d("<>", "map value:" + mMap.get(userTextMessage.getNumber()));

            for (int i = 0; i < mContentMessageList.size(); i++) {
                UserTextMessage listMsg = mContentMessageList.get(i);
                if (listMsg.getNumber().equals(userTextMessage.getNumber())) {
                    mContentMessageList.remove(listMsg);
                    break;
                }
            }
            mContentMessageList.add(0, userTextMessage);
            adapter.notifyDataSetChanged();

            Intent intent = new Intent(getActivity(), SecondContentMessageActivity.class);
            intent.putExtra("contentNumber", Number);
            startActivityForResult(intent, 0);
        }
    }
}


