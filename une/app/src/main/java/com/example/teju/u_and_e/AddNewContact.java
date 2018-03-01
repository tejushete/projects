package com.example.teju.u_and_e;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AddNewContact extends Activity {
    ListView lv;
    List<NewNumber> PhoneNumberList;
    ImageView ivAddContactProfile;
    NewContactNumberListAdapter listAdapter;
    private static final int SELECT_PICTURE = 0;
    EditText etContactName;
    Uri picData = null;
    long rawContactInsertIndex = 0;

    private boolean addContactToSystemDB() {

        RelativeLayout rlAddNewContactProgressView = findViewById(R.id.rlAddNewContactProgressView);
        rlAddNewContactProgressView.setVisibility(View.VISIBLE);
        RelativeLayout rlAddContactParent = findViewById(R.id.rlAddContactParent);
        rlAddContactParent.setEnabled(false);
        rlAddContactParent.setAlpha((float) 0.6);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                rawContactInsertIndex = ops.size();

                Log.d(AddNewContact.class.getSimpleName(), "contact Id: " + rawContactInsertIndex);

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, (int) rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, etContactName.getText().toString()) // Name of the person
                        .build());

                for (int i = 0; i < listAdapter.mNumberList.size(); i++) {
                    String number = listAdapter.mNumberList.get(i).getNumber();
                    if (number == null || number.isEmpty() == true || utility.validatePhoneNumber(number) == false)
                        continue;
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(
                                    ContactsContract.Data.RAW_CONTACT_ID, (int) rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, listAdapter.mNumberList.get(i).getNumber()) // Number of the person
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number
                }

                byte photoByteArray[] = null;

                try {
                    photoByteArray = utility.getBytes(picData, AddNewContact.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, (int) rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photoByteArray)
                        .build());

                try {
                    ContentProviderResult[] res = AddNewContact.this.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    rawContactInsertIndex = Integer.parseInt(res[0].uri.getLastPathSegment());

                    final String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID};
                    final Cursor cursor = getContentResolver().query(res[0].uri, projection, null, null, null);
                    cursor.moveToNext();
                    rawContactInsertIndex = cursor.getLong(0);
                    cursor.close();

                    Log.d(AddNewContact.class.getSimpleName(), "rawContactInsertIndex after insert: " + rawContactInsertIndex);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout rlAddNewContactProgressView = findViewById(R.id.rlAddNewContactProgressView);
                        rlAddNewContactProgressView.setVisibility(View.GONE);
                        RelativeLayout rlAddContactParent = findViewById(R.id.rlAddContactParent);
                        rlAddContactParent.setEnabled(true);
                        rlAddContactParent.setAlpha((float) 1);

                        Intent intent = new Intent();
                        intent.putExtra("ContactName", etContactName.getText().toString());
                        intent.putExtra("ContactId", rawContactInsertIndex);

                        String uri = "";
                        if(picData.toString().isEmpty() == false){
                            uri = "content://com.android.contacts/contacts/"+rawContactInsertIndex+"/photo";
                        }

                        intent.putExtra("ContactURI", uri);

                        Log.d(AddNewContact.class.getSimpleName(), "setResult");

                        setResult(-1, intent);

                        finish();
                    }
                });
            }
        }).start();

        return true;
    }

    private void requestPermission() {
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        PhoneNumberList = new ArrayList<>();

        requestPermission();

        ImageView ivCameraImg = findViewById(R.id.ivCameraImg);
        ivAddContactProfile = findViewById(R.id.ivAddContactProfile);

        ivCameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        lv = findViewById(R.id.lvNumbersListInput);

        listAdapter = new NewContactNumberListAdapter(this);
        listAdapter.mNumberList = new ArrayList<>();
        NewNumber num = new NewNumber();
        listAdapter.mNumberList.add(num);
        lv.setAdapter(listAdapter);

        etContactName = findViewById(R.id.etContactName);

        ImageView btnTickSave = findViewById(R.id.btnTickSave);
        btnTickSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contactName = etContactName.getText().toString();
                if (contactName == null || contactName.isEmpty() == true) {
                    Toast.makeText(AddNewContact.this, "Enter Valid Contact Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                String numbers = contactName + "; ";
                for (int i = 0; i < listAdapter.mNumberList.size(); i++) {
                    String num = listAdapter.mNumberList.get(i).getNumber();
                    if (num == null || num.isEmpty() || utility.validatePhoneNumber(num) == false)
                        continue;
                    numbers += num + ", ";
                }

                Toast.makeText(AddNewContact.this, numbers, Toast.LENGTH_SHORT).show();

                //save this list in database
                addContactToSystemDB();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            if (data != null) {
                picData = data.getData();
                if (getAssets() != null) {
                    Glide.with(AddNewContact.this)
                            .load(picData)
                            .placeholder(R.drawable.phone_list_profile)
                            .into(ivAddContactProfile);
                }
            }
        }
    }
}
