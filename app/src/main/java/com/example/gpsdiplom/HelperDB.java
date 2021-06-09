package com.example.gpsdiplom;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HelperDB extends SQLiteOpenHelper {
    final static String DB_NAME = "gpsdiplom2.db";
    final static String TABLE_NAME = "gpsdiplom2";

    public HelperDB(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME + "(_id integer primary key autoincrement, datetime date, latitude text, longitude text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+ TABLE_NAME);
        this.onCreate(db);
    }

    public Cursor getCursor() {
        SQLiteDatabase gpsDB = this.getWritableDatabase();
        Cursor infoDB = gpsDB.rawQuery("SELECT * FROM gpsdiplom2", null);

        return infoDB;
    }

}
