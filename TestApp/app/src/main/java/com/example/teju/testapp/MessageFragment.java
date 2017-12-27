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
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements AdapterView.OnItemClickListener{
    ListView listView;
    MessageFragmentAdapter adapter;
    ArrayList<UserTextMessage>mMessageList;


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
        MainActivity.mydb.getAllMessages();

        ImageView ivAddMsg = (ImageView)view.findViewById(R.id.ivAddMsg);
        ivAddMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SendMessageActivity.class);
                startActivity(intent);
            }
        });


        listView = (ListView)view.findViewById(R.id.lvMessage);
        if(getActivity()!=null){
            adapter = new MessageFragment.MessageFragmentAdapter(this.getActivity());
            listView.setAdapter(adapter);
        }

        requestPermission();

        mMessageList = MainActivity.mydb.getAllMessages();

        return view;
    }

    public void recievedNewMessage(UserTextMessage msg){
        Log.d("MessageFragment", "msg received "+msg.getNumber()+", "+msg.getMessageBody());

        mMessageList.add(0,msg);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.message, null);
            }

            TextView tvPhoneNumber = (TextView)list.findViewById(R.id.tvPhoneNumber);
            TextView tvMessageBody = (TextView)list.findViewById(R.id.tvMessageBody);
            tvPhoneNumber.setText(msg.getNumber());
            tvMessageBody.setText(msg.getMessageBody());
            return list;
        }
    }}