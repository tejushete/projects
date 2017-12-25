package com.example.teju.testapp;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class contactsFragment extends Fragment {
    ListView listView;
    CustomListViewAdapter mAdapter;
    int mCount = 0;
    Cursor cursor;
    ArrayList<contacts_Items> mContactsList;


    public contactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    requestPermission();
                }
            }
        }
    }

    private void requestPermission() {

        int result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);


        if (result == PackageManager.PERMISSION_GRANTED) {
            getContacts();
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    0);
        }

    }

    public Uri getPhotoUri(String id) {
        try {
            Cursor cur = getActivity().getApplicationContext().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void getContacts() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d("<>", "getcontacts");
                Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
                String ID = ContactsContract.Contacts._ID;

                String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
                String PHOTO = ContactsContract.CommonDataKinds.Photo.PHOTO;

                String sorting = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
                cursor = getActivity().getContentResolver()
                        .query(CONTENT_URI,
                                null, null, null, sorting);
                Log.d("<>", "<>");
                Log.d("<>", cursor.getCount() + "");
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {

                        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        Log.d("<>", name);

                        String contact_id = cursor.getString(cursor.getColumnIndex(ID));

                        Uri picuri = getPhotoUri(contact_id);

                        final contacts_Items contacts_items;
                        contacts_items = new contacts_Items();
                        contacts_items.setFirstName(name);
                        contacts_items.setContact_id(contact_id);
                        Log.d("<><><>", "name" + contacts_items.getFirstName());

                        Log.d("<<<>>>", picuri+"");
                        contacts_items.setImg(picuri);

                     if(getActivity()!=null) {
                         getActivity().runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 mContactsList.add(contacts_items);
                                 mAdapter.notifyDataSetChanged();
                             }
                         });
                     }
                    }

                }
            }


        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactsList = new ArrayList<contacts_Items>();

        listView = view.findViewById(R.id.lst_contacts);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final contacts_Items items = mContactsList.get(i);
                if(getActivity()!=null) {

                    Intent intent = new Intent(getActivity(), PhoneListActivity.class);
                    intent.putExtra("ContactId", items.getContact_id());
                    intent.putExtra("ContactName", items.getFirstName());
                    intent.putExtra("Image", items.getImg().toString());
                    startActivity(intent);

                }

            }
        });
        if (getActivity() != null) {
            mAdapter = new contactsFragment.CustomListViewAdapter(this.getActivity());
            listView.setAdapter(mAdapter);
        }
        requestPermission();

        return view;
    }


    public class CustomListViewAdapter extends BaseAdapter {
        Context mContext;

        CustomListViewAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mContactsList.size();
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

            final contacts_Items items = mContactsList.get(i);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.contacts_details, null);
            }

            TextView tv_firstName = list.findViewById(R.id.tv_firstName);
            final TextView tvPhoneNo = list.findViewById(R.id.tvPhoneNo);
            final ImageView ivPhoto = (ImageView) list.findViewById(R.id.iv_photo);

            tv_firstName.setText(items.getFirstName());
            tvPhoneNo.setText(items.getPhoneNumber());

            if(getActivity() != null) {
                Glide.with(getActivity())
                        .load(items.getImg())
                        .placeholder(R.drawable.blank)
                        .into(ivPhoto);
            }

            return list;
        }
    }
}