package com.example.teju.u_and_e;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.teju.u_and_e.UserTextMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


/**
 * Created by Teju on 12/24/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "myDatabase";

    public static final String TABLE_NAME_FOR_RECEIVE_MESSAGE = "receiveMessageDetails";
    public static final String NUMBER = "number";
    public static final String MESSAGE_BODY = "messageBody";
    public static final String MESSAGE_DIRECTION = "direction";

    public static final String TABLE_NAME_FOR_SEND_MESSAGE = "sendingMessageDetails";
    public static final String SEND_MESSAGE_BODY = "sendingMessageBody";

    public static final int DATABASE_VERSION = 1;
    public static final String DROP_TABLE_FOR_RECEIVE_MESSAGE = "drop table if exists" + TABLE_NAME_FOR_RECEIVE_MESSAGE;
    public static final String DROP_TABLE_FOR_SEND_MESSAGE = "drop table if exists" + TABLE_NAME_FOR_SEND_MESSAGE;

    public static final String CURRENT_DATE_AND_TIME = "currentDateAndTime";


    public static final String TABLE_NAME_FOR_NOTIFICATION = "notificationDetails";
    public static final String MESSAGE_COUNT = "count";
    public static final String NOTIFICATION_ID = "id";
    public static final String NOTIFICATION_NUMBER = "notificationNumber";
    public static final String DROP_TABLE_FOR_NOTIFICATION = "drop table if exists" + TABLE_NAME_FOR_NOTIFICATION;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        getReadableDatabase();
    }

    Comparator date_comparator = new Comparator() {
        @Override
        public int compare(Object o, Object t1) {
            UserTextMessage um1 = (UserTextMessage) o;
            UserTextMessage um2 = (UserTextMessage) t1;
            String s1 = um1.getDate();
            String s2 = um2.getDate();
            Date d1 = null;
            Date d2 = null;
            try {
                DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                d1 = readFormat.parse(s1);
                d2 = readFormat.parse(s2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (d1 == null || d2 == null) return 0;

            return d1.compareTo(d2);
        }
    };

    Comparator date_comparator2 = new Comparator() {
        @Override
        public int compare(Object o, Object t1) {
            UserTextMessage um1 = (UserTextMessage) o;
            UserTextMessage um2 = (UserTextMessage) t1;
            String s1 = um1.getDate();
            String s2 = um2.getDate();
            Date d1 = null;
            Date d2 = null;
            try {
                DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                d1 = readFormat.parse(s1);
                d2 = readFormat.parse(s2);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (d1 == null || d2 == null) return 0;

            return d2.compareTo(d1);
        }
    };

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_NAME_FOR_RECEIVE_MESSAGE + "(" + NUMBER + " text,"
                + MESSAGE_BODY + " text,"
                + MESSAGE_DIRECTION + " text,"
                + CURRENT_DATE_AND_TIME + " text,"
                + MESSAGE_COUNT + " text)"
        );
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_NAME_FOR_NOTIFICATION + "(" + NOTIFICATION_NUMBER + " text, "
                + NOTIFICATION_ID + " text, "
                + MESSAGE_COUNT + " text,"
                + "PRIMARY KEY (" + NOTIFICATION_NUMBER + "))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE_FOR_RECEIVE_MESSAGE);
        sqLiteDatabase.execSQL(DROP_TABLE_FOR_SEND_MESSAGE);
        sqLiteDatabase.execSQL(DROP_TABLE_FOR_NOTIFICATION);

        onCreate(sqLiteDatabase);

    }

    public ArrayList<UserTextMessage> getAllRecordsOfEveryNumber(String number) {

        ArrayList<UserTextMessage> mGetAllRecordsOfEveryNumber;
        mGetAllRecordsOfEveryNumber = new ArrayList<UserTextMessage>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query2 = "SELECT * FROM " + TABLE_NAME_FOR_RECEIVE_MESSAGE + " WHERE number = '" + number + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query2, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            UserTextMessage userTextMessage = new UserTextMessage();
            String savedNumber = cursor.getString(cursor.getColumnIndex(NUMBER));
            String msgBody = cursor.getString(cursor.getColumnIndex(MESSAGE_BODY));
            String direction = cursor.getString(cursor.getColumnIndex(MESSAGE_DIRECTION));
            String currentDateAndTime = cursor.getString(cursor.getColumnIndex(CURRENT_DATE_AND_TIME));


            userTextMessage.setNumber(savedNumber);
            userTextMessage.setMessageBody(msgBody);
            userTextMessage.setDirection(direction);
            userTextMessage.setDate(currentDateAndTime);
            mGetAllRecordsOfEveryNumber.add(userTextMessage);

            cursor.moveToNext();
        }
        Log.d("<><><>", "GetAllRecordsOfEveryNumber" + mGetAllRecordsOfEveryNumber);

        Collections.sort(mGetAllRecordsOfEveryNumber, date_comparator);

        Log.d("<><><>", "GetAllRecordsOfEveryNumber" + mGetAllRecordsOfEveryNumber);

        return mGetAllRecordsOfEveryNumber;
    }

    public void insertMessageDetails(UserTextMessage msg) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NUMBER, msg.getNumber());
        contentValues.put(MESSAGE_BODY, msg.getMessageBody());
        contentValues.put(MESSAGE_DIRECTION, msg.getDirection());
        contentValues.put(CURRENT_DATE_AND_TIME, msg.getDate());


        sqLiteDatabase.insert(TABLE_NAME_FOR_RECEIVE_MESSAGE, null, contentValues);
        sqLiteDatabase.close();

    }

    public void insertNotificationDetails(Notification notification) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_COUNT, notification.getMessage_count()+"");
        contentValues.put(NOTIFICATION_ID, notification.getMessage_id()+"");
        contentValues.put(NOTIFICATION_NUMBER, notification.getNumber());
        sqLiteDatabase.insert(TABLE_NAME_FOR_NOTIFICATION, null, contentValues);
        sqLiteDatabase.close();

    }

    public Notification getDetailsOfNotification(String number) {
        String query = "SELECT * FROM " + TABLE_NAME_FOR_NOTIFICATION + " WHERE " + NOTIFICATION_NUMBER + "='" + number + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        cursor.moveToFirst();
        Notification notification = new Notification();
        String notifificationNumber = cursor.getString(cursor.getColumnIndex(NOTIFICATION_NUMBER));
        String notificationId = cursor.getString(cursor.getColumnIndex(NOTIFICATION_ID));
        String count = cursor.getString(cursor.getColumnIndex(MESSAGE_COUNT));

        notification.setNumber(notifificationNumber);
        notification.setMessage_id(Integer.valueOf(notificationId));
        notification.setMessage_count(Integer.valueOf(count));

        Log.d("SERVICE", notification.getMessage_id()+", "+notification.getMessage_count()+", "+
        notification.getNumber());
        cursor.close();
        return notification;
    }

    public void updateNotificationDetails(Notification notification) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME_FOR_NOTIFICATION + " SET " +
                MESSAGE_COUNT + "= ? , " +
                NOTIFICATION_ID + "= ? WHERE " +
                NOTIFICATION_NUMBER + "= ?";
        Log.d("SERVICE","update query: "+query);
        sqLiteDatabase.execSQL(query, new String[]{
                notification.getMessage_count()+"",
                notification.getMessage_id()+"",
                notification.getNumber()
        });
        sqLiteDatabase.close();
    }

    public ArrayList<UserTextMessage> getLastRecordOfEveryNumber() {
        ArrayList<UserTextMessage> mgetLastRecordOfEveryNumber;
        mgetLastRecordOfEveryNumber = new ArrayList<UserTextMessage>();
        String query1 = "SELECT * FROM \n" +
                "(SELECT * FROM " + TABLE_NAME_FOR_RECEIVE_MESSAGE + " ORDER BY number DESC) AS x GROUP BY " + NUMBER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query1, null);

        if (cursor == null) {
            return null;
        }

        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            UserTextMessage msg = new UserTextMessage();
            String number = cursor.getString(cursor.getColumnIndex(NUMBER));
            String msgBody = cursor.getString(cursor.getColumnIndex(MESSAGE_BODY));
            String direction = cursor.getString(cursor.getColumnIndex(MESSAGE_DIRECTION));
            String currentDateAndTime = cursor.getString(cursor.getColumnIndex(CURRENT_DATE_AND_TIME));


            msg.setNumber(number);
            msg.setMessageBody(msgBody);
            msg.setDirection(direction);
            msg.setDate(currentDateAndTime);

            mgetLastRecordOfEveryNumber.add(msg);
            cursor.moveToNext();
        }

        Log.d("<><><>", "getLastRecordOfEveryNumber" + mgetLastRecordOfEveryNumber);
        Collections.sort(mgetLastRecordOfEveryNumber, date_comparator2);
        Log.d("<><><>", "getLastRecordOfEveryNumber" + mgetLastRecordOfEveryNumber);

        return mgetLastRecordOfEveryNumber;
    }
//
//
//    public ArrayList<UserTextMessage>getAllMessages(){
//
//        ArrayList<UserTextMessage> getAllUserTextMessages;
//        getAllUserTextMessages =new ArrayList<UserTextMessage>();
//        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
//        Cursor cursor=sqLiteDatabase.query(TABLE_NAME_FOR_RECEIVE_MESSAGE,new String[]{NUMBER,MESSAGE_BODY},null,null,null,null,null);
//        cursor.moveToFirst();
//
//        while (cursor.isAfterLast()==false){
//                UserTextMessage msg = new UserTextMessage();
//                String number = cursor.getString(cursor.getColumnIndex(NUMBER));
//                String msgBody = cursor.getString(cursor.getColumnIndex(MESSAGE_BODY));
//
//                msg.setNumber(number);
//                msg.setMessageBody(msgBody);
//                getAllUserTextMessages.add(msg);
//                cursor.moveToNext();
//        }
//
//        Collections.reverse(getAllUserTextMessages);
//        return getAllUserTextMessages;
//    }
//    public ArrayList<SendMessage>sendAllMessages(){
//        ArrayList<SendMessage> sendingMessageList;
//        sendingMessageList=new ArrayList<SendMessage>();
//        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
//        Cursor cur = sqLiteDatabase.query(TABLE_NAME_FOR_SEND_MESSAGE, new String[]{SEND_MESSAGE_BODY},null,null,null,null,null);
//       cur.moveToFirst();
//
//       while (cur.isAfterLast()==false){
//           SendMessage sendMessage = new SendMessage();
//           String sendingMessageBody = cur.getString(cur.getColumnIndex(SEND_MESSAGE_BODY));
//
//           sendMessage.setSendMessageBody(sendingMessageBody);
//           sendingMessageList.add(sendMessage);
//           cur.moveToNext();
//       }
//       return sendingMessageList;
//    }

}
