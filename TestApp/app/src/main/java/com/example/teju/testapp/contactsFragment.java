package com.example.teju.testapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import static com.example.teju.testapp.R.*;
import static com.example.teju.testapp.R.id.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class contactsFragment extends Fragment {
    FastScrollRecyclerView listView;
    CustomListViewAdapter mAdapter;
    Cursor cursor;
    ArrayList<contacts_Items> mContactsList;

    boolean isContactsScreenSelected = true;

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

                String sorting = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC";
                cursor = getActivity().getContentResolver()
                        .query(CONTENT_URI,
                                null,
                                ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0",
                                null,
                                sorting);

                if (cursor == null) return;

                Log.d("<>", cursor.getCount() + "");
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {

                        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        String contact_id = cursor.getString(cursor.getColumnIndex(ID));
                        Uri picuri = getPhotoUri(contact_id);

                        final contacts_Items contacts_items;
                        contacts_items = new contacts_Items();
                        contacts_items.setFirstName(name);
                        contacts_items.setContact_id(contact_id);

                        Log.d("<><><>", "name " + contacts_items.getFirstName());

                        Log.d("<<<>>>", picuri + "");
                        contacts_items.setImg(picuri);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContactsList.add(contacts_items);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } while (cursor.moveToNext());
                }
            }
        }).start();
    }

    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fr_view = inflater.inflate(layout.fragment_contacts, container, false);
        mContactsList = new ArrayList<contacts_Items>();

        listView = fr_view.findViewById(lst_contacts);

        if (getActivity() != null) {
            mAdapter = new contactsFragment.CustomListViewAdapter(this.getActivity());
            listView.setAdapter(mAdapter);
        }

        requestPermission();

        setRecyclerViewLayoutManager(listView);

        LinearLayout llContactsOption, llCallLogsOption;
        llContactsOption = fr_view.findViewById(R.id.llContactsOption);
        llCallLogsOption = fr_view.findViewById(R.id.llCallLogsOption);

        llContactsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isContactsScreenSelected == true) return;
                isContactsScreenSelected = true;

                View viContactsScreenSelected = fr_view.findViewById(R.id.viContactsScreenSelected);
                View viCallLogsScreenSelected = fr_view.findViewById(R.id.viCallLogsScreenSelected);

                Animation fadeInAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_in);
                Animation fadeOutAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_out);

                viContactsScreenSelected.setVisibility(View.VISIBLE);
                viCallLogsScreenSelected.setVisibility(View.GONE);

                viContactsScreenSelected.startAnimation(fadeInAnim);
                viCallLogsScreenSelected.startAnimation(fadeOutAnim);

            }
        });

        llCallLogsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isContactsScreenSelected == false) return;
                isContactsScreenSelected = false;

                View viContactsScreenSelected = fr_view.findViewById(R.id.viContactsScreenSelected);
                View viCallLogsScreenSelected = fr_view.findViewById(R.id.viCallLogsScreenSelected);

                Animation fadeInAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_in);
                Animation fadeOutAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_out);

                viContactsScreenSelected.setVisibility(View.GONE);
                viCallLogsScreenSelected.setVisibility(View.VISIBLE);

                viContactsScreenSelected.startAnimation(fadeOutAnim);
                viCallLogsScreenSelected.startAnimation(fadeInAnim);
            }
        });

        return fr_view;
    }

    public class CustomListViewAdapter extends RecyclerView.Adapter implements FastScrollRecyclerView.SectionedAdapter,
            FastScrollRecyclerView.MeasurableAdapter {

        Context mContext;

        CustomListViewAdapter(Context c) {
            mContext = c;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup v, int viewType) {

            View row = LayoutInflater.from(v.getContext()).inflate(R.layout.contacts_details, v, false);

            Log.d("HOLDER", "onCreateViewHolder");
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    Log.d("<>HOLDER", position + "");
                    final contacts_Items items = mContactsList.get(position);
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), PhoneListActivity.class);
                        intent.putExtra("ContactId", items.getContact_id());
                        intent.putExtra("ContactName", items.getFirstName());
                        intent.putExtra("Image", items.getImg().toString());

                        startActivity(intent);
                    }
                }
            });

            return new MyViewHolder(row);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final contacts_Items items = mContactsList.get(position);

            ((MyViewHolder) holder).getTvfirstName().setText(items.getFirstName());

            if (getActivity() != null) {
                Glide.with(getActivity())
                        .load(items.getImg())
                        .placeholder(drawable.contact_profile)
                        .into(((MyViewHolder) holder).getIvPhoto());
            }

            ((MyViewHolder) holder).setRootViewRowPositionTag(position);
        }

        @Override
        public int getItemCount() {
            return mContactsList.size();
        }


        @Override
        public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
            return 55;
        }

        @NonNull
        @Override
        public String getSectionName(int position) {
            final contacts_Items items = mContactsList.get(position);
            String contactName = items.getFirstName();
            return ("" + contactName.charAt(0)).toUpperCase();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_firstName;
        ImageView ivPhoto;
        View row;

        public TextView getTvfirstName() {
            return tv_firstName;
        }

        public ImageView getIvPhoto() {
            return ivPhoto;
        }

        public void setRootViewRowPositionTag(int position) {
            row.setTag(position);
        }

        public MyViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.

            tv_firstName = (TextView) v.findViewById(R.id.tv_firstName);
            ivPhoto = (ImageView) v.findViewById(iv_photo);

            row = v;
        }
    }
}