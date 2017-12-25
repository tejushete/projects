package com.example.teju.testapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PhoneListActivity extends Activity {
    ListView listView;
    PhoneListAdapter adapter;
    int mCount = 5;
    List<String> mPhoneNumberList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_list);
        mPhoneNumberList = new ArrayList<String>();

        listView = (ListView) findViewById(R.id.lvPhn_list);
        if (PhoneListActivity.this != null) {
            adapter = new PhoneListActivity.PhoneListAdapter(PhoneListActivity.this);
            listView.setAdapter(adapter);
        }
        Intent intent = getIntent();
        String contact_id = intent.getExtras().getString("ContactId");
        Log.d("<><>", "id" + contact_id);
        String contactName = intent.getExtras().getString("ContactName");
        String img = intent.getExtras().getString("Image");
        Log.d("<><>", "URI" + img);

        Uri picsUri = Uri.parse(img);

        TextView tvContactName = (TextView) findViewById(R.id.tvContactName);
        ImageView tvProfilePic = (ImageView) findViewById(R.id.tvProfilePic);

        tvContactName.setText(contactName);

        Glide.with(PhoneListActivity.this)
                .load(picsUri)
                .placeholder(R.drawable.blank)
                .into(tvProfilePic);


        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contact_id}, null);

        while (cursor.moveToNext()) {

            String no = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            for (int i=0;i<mPhoneNumberList.size();i++){
                if(no.equals(mPhoneNumberList.get(i))){

                }

            }

            mPhoneNumberList.add(no);
            adapter.notifyDataSetChanged();
        }

        cursor.close();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String PhoneData = mPhoneNumberList.get(i);

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+PhoneData));
                if (ActivityCompat.checkSelfPermission(PhoneListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhoneListActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, 0);
                    return;
                }
                startActivity(callIntent);
          }
      });

    }


    public class PhoneListAdapter extends BaseAdapter {
        Context mContext;

        PhoneListAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mPhoneNumberList.size();
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

              String PhoneData = mPhoneNumberList.get(i);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.phone_numbers_list, null);
            }
            TextView tvPhoneNumbers = (TextView)list.findViewById(R.id.tvPhoneNumbers);
            tvPhoneNumbers.setText(PhoneData);




            return list;
        }
    }}

