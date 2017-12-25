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
    public static final String TABLE_NAME = "messageDetails";
    public static final String NUMBER ="number";
    public static final String MESSAGE_BODY ="messageBody";
    public static final int DATABASE_VERSION =1;
    public static final String DROP_TABLE ="drop table if exists"+TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
     sqLiteDatabase.execSQL("create table "+TABLE_NAME+"("+NUMBER+" text,"+MESSAGE_BODY+" text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);

    }
    public void insertMessageDetails(UserTextMessage msg){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("number",msg.getNumber());
        contentValues.put("messageBody",msg.getMessageBody());
        sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
    }

    public ArrayList<UserTextMessage>getAllMessages(){

        ArrayList<UserTextMessage> getAllUserTextMessages;
        getAllUserTextMessages =new ArrayList<UserTextMessage>();
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME,new String[]{NUMBER,MESSAGE_BODY},null,null,null,null,null);
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
}
