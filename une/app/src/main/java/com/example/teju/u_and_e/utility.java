package com.example.teju.u_and_e;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.Telephony;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.provider.Telephony.BaseMmsColumns.STATUS;
import static android.provider.Telephony.BaseMmsColumns.THREAD_ID;
import static android.provider.Telephony.Mms.Addr.ADDRESS;
import static android.provider.Telephony.TextBasedSmsColumns.BODY;
import static android.provider.Telephony.ThreadsColumns.DATE;
import static android.provider.Telephony.ThreadsColumns.READ;

/**
 * Created by Teju on 1/8/2018.
 */

public class utility {

    public Uri addMessageToUri(ContentResolver resolver,
                               Uri uri, String address, String body, String subject,
                               Long date, boolean read, boolean deliveryReport) {
        return addMessage(resolver, uri, address, body, subject, date, read, deliveryReport, -1L);
    }

    private Uri addMessage(ContentResolver resolver,
                           Uri uri, String address, String body, String subject,
                           Long date, boolean read, boolean deliveryReport, long threadId) {
        ContentValues values = new ContentValues(7);
        values.put(ADDRESS, address);
        if (date != null) {
            values.put(DATE, date);
        }

        values.put(READ, read ? Integer.valueOf(1) : Integer.valueOf(0));
        values.put(BODY, body);
        if (deliveryReport) {
            values.put(STATUS, Telephony.Sms.STATUS_PENDING);
        }
        if (threadId != -1L) {
            values.put(THREAD_ID, threadId);
        }
        return resolver.insert(uri, values);
    }

    static public boolean validatePhoneNumber(String number){

        String regexStr = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";

        if(number.matches(regexStr) == true){
            return true;
        }

        return false;
    }

    public static byte[] getBytes(Uri uri, Context context) throws IOException {
        InputStream iStream =   context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void updateMsgReadStatus(Context context, String smsId, UserTextMessage msg) {

        Uri uri = Uri.parse("content://sms/inbox");

        ContentValues values = new ContentValues();
        values.put("read", msg.getReadStatus());
        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + smsId, null);
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }
}
