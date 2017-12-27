package com.example.teju.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Teju on 12/24/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "myDatabase";

    public static final String TABLE_NAME_FOR_RECEIVE_MESSAGE = "receiveMessageDetails";
    public static final String NUMBER ="number";
    public static final String MESSAGE_BODY ="messageBody";

    public static final String TABLE_NAME_FOR_SEND_MESSAGE ="sendingMessageDetails";
    public static final String SEND_MESSAGE_BODY = "sendingMessageBody";

    public static final int DATABASE_VERSION =1;
    public static final String DROP_TABLE_FOR_RECEIVE_MESSAGE ="drop table if exists"+TABLE_NAME_FOR_RECEIVE_MESSAGE;
    public static final String DROP_TABLE_FOR_SEND_MESSAGE ="drop table if exists"+TABLE_NAME_FOR_SEND_MESSAGE;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
     sqLiteDatabase.execSQL("create table "+TABLE_NAME_FOR_RECEIVE_MESSAGE+"("+NUMBER+" text,"+MESSAGE_BODY+" text)");
     sqLiteDatabase.execSQL("create table "+TABLE_NAME_FOR_SEND_MESSAGE+"("+SEND_MESSAGE_BODY+"text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE_FOR_RECEIVE_MESSAGE);
        sqLiteDatabase.execSQL(DROP_TABLE_FOR_SEND_MESSAGE);

        onCreate(sqLiteDatabase);

    }
    public void insertMessageDetails(UserTextMessage msg){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("number",msg.getNumber());
        contentValues.put("messageBody",msg.getMessageBody());
        sqLiteDatabase.insert(TABLE_NAME_FOR_RECEIVE_MESSAGE,null,contentValues);
    }
    public void insertSendingMessageDetails(SendMessage sendMessage){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("sendingMessageBody",sendMessage.getSendMessageBody());
        sqLiteDatabase.insert(TABLE_NAME_FOR_SEND_MESSAGE,null,contentValues);
    }

    public ArrayList<UserTextMessage>getAllMessages(){

        ArrayList<UserTextMessage> getAllUserTextMessages;
        getAllUserTextMessages =new ArrayList<UserTextMessage>();
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME_FOR_RECEIVE_MESSAGE,new String[]{NUMBER,MESSAGE_BODY},null,null,null,null,null);
        cursor.moveToFirst();

        while (cursor.isAfterLast()==false){
                UserTextMessage msg = new UserTextMessage();
                String number = cursor.getString(cursor.getColumnIndex(NUMBER));
                String msgBody = cursor.getString(cursor.getColumnIndex(MESSAGE_BODY));

                msg.setNumber(number);
                msg.setMessageBody(msgBody);
                getAllUserTextMessages.add(msg);
                cursor.moveToNext();
        }

        Collections.reverse(getAllUserTextMessages);
        return getAllUserTextMessages;
    }
    public ArrayList<SendMessage>sendAllMessages(){
        ArrayList<SendMessage> sendingMessageList;
        sendingMessageList=new ArrayList<SendMessage>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cur = sqLiteDatabase.query(TABLE_NAME_FOR_SEND_MESSAGE,new String[]{SEND_MESSAGE_BODY},null,null,null,null,null);
       cur.moveToFirst();

       while (cur.isAfterLast()==false){
           SendMessage sendMessage = new SendMessage();
           String sendingMessageBody = cur.getString(cur.getColumnIndex(SEND_MESSAGE_BODY));

           sendMessage.setSendMessageBody(sendingMessageBody);
           sendingMessageList.add(sendMessage);
           cur.moveToNext();
       }
       return sendingMessageList;
    }

}
