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
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.teju.testapp.R.*;
import static com.example.teju.testapp.R.id.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class contactsFragment extends Fragment {
    FastScrollRecyclerView listView;
    CustomListViewAdapter mAdapter;
    Cursor cursor;
    ListView lv;
    ArrayList<contacts_Items> mContactsList;
    ArrayList<contacts_Items> mSearchArrayList;
    CallListAdapter listAdapter;

    LinearLayout llContactsView;
    LinearLayout llCallLogsView;


    private static final int CONTACTS_SCREEN = 0;
    private static final int CALL_LOGS_SCREEN = 1;

    int screenSelection = CONTACTS_SCREEN;

    public contactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(new String[]{Manifest.permission.READ_CONTACTS})) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getCallLogs();
                } else {
                    requestPermission();
                }
            }

            if (permission.equals(new String[]{Manifest.permission.READ_CALL_LOG})) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getCallLogs();
                } else {
                    requestPermission();
                }
            }

        }
    }

    private void getCallLogs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri callLogUri = Uri.parse("content://call_log/calls");
                Cursor cursor = getActivity().getContentResolver().query(callLogUri,
                        null,
                        null,
                        null,
                        CallLog.Calls.DATE + " DESC");

                int num = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                if (cursor == null) return;
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        String phnNum = cursor.getString(num);
                        String Name = cursor.getString(name);
                        String callType = cursor.getString(type);
                        String callDate = cursor.getString(date);
                        String callDuration = cursor.getString(duration);

                        final CallLogItem item = new CallLogItem();
                        item.setName(Name);
                        item.setNumber(phnNum);
                        item.setCallType(callType);
                        item.setDate(callDate);
                        item.setCallDuration(callDuration);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.mCallLogList.add(item);
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                    } while (cursor.moveToNext());
                }
                cursor.close();

            }
        }).start();

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

        result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG);
        if (result == PackageManager.PERMISSION_GRANTED) {
            getCallLogs();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    0);
        }

        result = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_CONTACTS},
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

//                Log.d("<>", cursor.getCount() + "");
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

                        contacts_items.setImg(picuri);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContactsList.add(contacts_items);
                                    mSearchArrayList.add(contacts_items);
                                    mAdapter.notifyItemChanged(mContactsList.size()-1);
                                }
                            });
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
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
        mSearchArrayList = new ArrayList<contacts_Items>();

        listView = fr_view.findViewById(R.id.lst_contacts);

        ItemClickSupport.addTo(listView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    final contacts_Items items = mSearchArrayList.get(position);
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), PhoneListActivity.class);
                        intent.putExtra("ContactId", items.getContact_id());
                        intent.putExtra("ContactName", items.getFirstName());
                        intent.putExtra("Image", items.getImg().toString());

                        startActivity(intent);
                    }
            }
        });

        lv = fr_view.findViewById(id.lvCallLogs);
        SearchView searchView = (SearchView) fr_view.findViewById(R.id.svContactsFragment);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchArrayList.clear();
                Log.d(contactsFragment.class.getSimpleName(), "onQueryTextChange");
                for (int i = 0; i < mContactsList.size(); i++) {

                    contacts_Items item = mContactsList.get(i);
                    Log.d(contactsFragment.class.getSimpleName(), "item First Name: " + item.getFirstName() +
                            " query: " + newText + " " +
                            "item Last Name: " + item.getLastName());

                    if (newText.isEmpty() == true
                            || (item.getFirstName() != null &&
                            utility.containsIgnoreCase(item.getFirstName(), newText) == true)
                            || (item.getLastName() != null &&
                            utility.containsIgnoreCase(item.getFirstName(), newText) == true
                    )) {
                        mSearchArrayList.add(item);
                    }
                }

                mAdapter.notifyDataSetChanged();

                return false;
            }
        });

        if (getActivity() != null) {
            mAdapter = new contactsFragment.CustomListViewAdapter(this.getActivity());
            listView.setAdapter(mAdapter);
        }

        if (getActivity() != null) {
            listAdapter = new CallListAdapter(this.getActivity());
            listAdapter.mCallLogList = new ArrayList<CallLogItem>();
            lv.setAdapter(listAdapter);
        }

        llContactsView = fr_view.findViewById(id.llContactsView);
        llCallLogsView = fr_view.findViewById(id.llCallLogsView);

        requestPermission();

        GridLayoutManager manager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        listView.setItemAnimator(null);
        listView.setAnimation(null);
        listView.setLayoutAnimation(null);

        LinearLayout llContactsOption, llCallLogsOption, llDialerOption;
        llContactsOption = fr_view.findViewById(R.id.llContactsOption);
        llCallLogsOption = fr_view.findViewById(R.id.llCallLogsOption);
        //  llDialerOption = fr_view.findViewById(R.id.llDialerOption);

      /*  llDialerOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (screenSelection == DIALER_SCREEN) return;

                View viContactsScreenSelected = fr_view.findViewById(R.id.viContactsScreenSelected);
                View viCallLogsScreenSelected = fr_view.findViewById(R.id.viCallLogsScreenSelected);
                View viDialerScreenSelected = fr_view.findViewById(R.id.viDialerScreenSelected);

                Animation fadeInAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_in);
                Animation fadeOutAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_out);

                viContactsScreenSelected.setVisibility(View.GONE);
                viCallLogsScreenSelected.setVisibility(View.GONE);
                viDialerScreenSelected.setVisibility(View.VISIBLE);

                if(screenSelection == CONTACTS_SCREEN) {
                    viContactsScreenSelected.startAnimation(fadeOutAnim);
                }

                if(screenSelection == CALL_LOGS_SCREEN){
                    viCallLogsScreenSelected.startAnimation(fadeOutAnim);
                }

                viDialerScreenSelected.startAnimation(fadeInAnim);

                llContactsView.setVisibility(View.GONE);
                llCallLogsView.setVisibility(View.GONE);
                llDialerView.setVisibility(View.VISIBLE);

                screenSelection = DIALER_SCREEN;
            }
        });
*/
        llContactsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (screenSelection == CONTACTS_SCREEN) return;

                View viContactsScreenSelected = fr_view.findViewById(R.id.viContactsScreenSelected);
                View viCallLogsScreenSelected = fr_view.findViewById(R.id.viCallLogsScreenSelected);
                LinearLayout llContactsView = fr_view.findViewById(R.id.llContactsView);
                LinearLayout llCallLogsView = fr_view.findViewById(R.id.llCallLogsView);

                Animation fadeInAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_in);
                Animation fadeOutAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_out);

                viContactsScreenSelected.setVisibility(View.VISIBLE);
                viCallLogsScreenSelected.setVisibility(View.GONE);
                llContactsView.setVisibility(View.VISIBLE);
                llCallLogsView.setVisibility(View.GONE);

                viContactsScreenSelected.startAnimation(fadeInAnim);

                if (screenSelection == CALL_LOGS_SCREEN) {
                    viCallLogsScreenSelected.startAnimation(fadeOutAnim);
                }

                llContactsView.setVisibility(View.VISIBLE);
                llCallLogsView.setVisibility(View.GONE);

                screenSelection = CONTACTS_SCREEN;
            }
        });

        llCallLogsOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (screenSelection == CALL_LOGS_SCREEN) return;

                View viContactsScreenSelected = fr_view.findViewById(R.id.viContactsScreenSelected);
                View viCallLogsScreenSelected = fr_view.findViewById(R.id.viCallLogsScreenSelected);
                llContactsView = fr_view.findViewById(R.id.llContactsView);
                llCallLogsView = fr_view.findViewById(R.id.llCallLogsView);


                Animation fadeInAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_in);
                Animation fadeOutAnim = AnimationUtils.loadAnimation(getActivity(), anim.fade_out);

                viContactsScreenSelected.setVisibility(View.GONE);
                viCallLogsScreenSelected.setVisibility(View.VISIBLE);

                if (screenSelection == CONTACTS_SCREEN) {
                    viContactsScreenSelected.startAnimation(fadeOutAnim);
                }
                viCallLogsScreenSelected.startAnimation(fadeInAnim);

                llContactsView.setVisibility(View.GONE);
                llCallLogsView.setVisibility(View.VISIBLE);
                llContactsView.setVisibility(View.GONE);
                llCallLogsView.setVisibility(View.VISIBLE);

                screenSelection = CALL_LOGS_SCREEN;
            }
        });

        LinearLayout llCreateContactView = (LinearLayout)fr_view.findViewById(R.id.llCreateContactView);
        llCreateContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddNewContact.class);
                startActivityForResult(intent, 0);
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
            return new MyViewHolder(row);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final contacts_Items items = mSearchArrayList.get(position);

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
            return mSearchArrayList.size();
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

        private TextView tv_firstName;
        private ImageView ivPhoto;
        private View row;

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
            tv_firstName = (TextView) v.findViewById(R.id.tv_firstName);
            ivPhoto = (ImageView) v.findViewById(iv_photo);
            row = v;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(contactsFragment.class.getSimpleName(), "onActivityResult, requestCode:"
        +requestCode+", resultCode:"+resultCode);

        if(requestCode == 0 && resultCode == -1){
            contacts_Items item = new contacts_Items();
            String contactName = data.getStringExtra("ContactName");
            int contactId = data.getIntExtra("ContactId", 0);
            String contactURI = data.getStringExtra("ContactURI");

            item.setFirstName(contactName);
            item.setContact_id(contactId+"");
            item.setImg(Uri.parse(contactURI));
            Log.d(contactsFragment.class.getSimpleName(), "contactName: "
                    +contactName+", contactId:"+contactId+", contactURI:"+contactURI);

            mContactsList.add(item);
            mSearchArrayList.add(item);

            if(getActivity()!= null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Collections.sort(mContactsList, new Comparator<contacts_Items>() {
                            @Override
                            public int compare(contacts_Items lhs, contacts_Items rhs) {
                                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                int result = 0;
                                result = lhs.getFirstName().compareToIgnoreCase(rhs.getFirstName());
                                return result;
                            }
                        });

                        mSearchArrayList = mContactsList;

                        mAdapter.notifyDataSetChanged();

                    }
                });}
        }

    }
}