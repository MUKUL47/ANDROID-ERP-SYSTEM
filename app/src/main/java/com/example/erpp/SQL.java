package com.example.erpp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQL extends SQLiteOpenHelper {


    public SQL(Context context) {
        super(context, "erp", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table coe (notice text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS coe");
        onCreate(db);
    }
    public void initTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("notice", "This is your notification center!");
        db.insert("coe", null, contentValues);
    }
    public ArrayList getNotice() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from coe", null );
        res.moveToFirst();
        ArrayList<String> A = new ArrayList<>();
        while(!res.isAfterLast()){
            A.add(res.getString(res.getColumnIndex("notice")));
            res.moveToNext();
        }
        return A;
    }
    public void updateNotice (String notice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("notice", notice);
        db.insert("coe", null, contentValues);
    }
}
