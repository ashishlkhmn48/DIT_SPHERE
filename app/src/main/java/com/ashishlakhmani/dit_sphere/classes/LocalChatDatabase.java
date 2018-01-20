package com.ashishlakhmani.dit_sphere.classes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LocalChatDatabase extends SQLiteOpenHelper {

    Context context;

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME;
    private static final String TABLE_NAME = "Chat";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_SEND_STATUS = "status";


    public LocalChatDatabase(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
        this.context = context;
        DATABASE_NAME = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " TEXT," + COLUMN_MESSAGE + " TEXT," +
                COLUMN_DATE + " TEXT," + COLUMN_SEND_STATUS + " TEXT" + ")";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Add new row to database
    public void addUserDetails(MessageObject messageObject) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, messageObject.getStudent_id());
        cv.put(COLUMN_MESSAGE, messageObject.getMessage());
        cv.put(COLUMN_DATE, messageObject.getDate());
        cv.put(COLUMN_SEND_STATUS, messageObject.getSendStatus());
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }


    //get MessageObject list from local database
    public List<MessageObject> getMessageObjects() {
        List<MessageObject> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            MessageObject messageObject = new MessageObject(DATABASE_NAME, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            list.add(messageObject);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    public void updateSendStatus(String date, MessageObject messageObject) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SEND_STATUS, messageObject.getSendStatus());
        db.update(TABLE_NAME, cv, COLUMN_DATE + " = ?",
                new String[]{date});
    }

    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    public List<MessageObject> getWaitMessageObjects() {
        List<MessageObject> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " where " + COLUMN_SEND_STATUS + " = \"wait\"";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            MessageObject messageObject = new MessageObject(DATABASE_NAME, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            list.add(messageObject);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }
}
