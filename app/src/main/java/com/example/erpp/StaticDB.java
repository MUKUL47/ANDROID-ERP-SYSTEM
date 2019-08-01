package com.example.erpp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticDB extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "ERP";
    public StaticDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table ERPDB " +
                        "(ID text primary key," +
                        " PASS       text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ERPDB ");
        onCreate(db);
    }

    public void initIdPass(String id, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("PASS", pass);
        db.insert("ERPDB", null, contentValues);
    }

    public void updateIdPass(String id, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PASS",pass);
        db.update("ERPDB", contentValues, "ID = ?",new String[] { id });
    }

    public ArrayList<String> getDb(){
        ArrayList<String> idPass = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ERPDB", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            idPass.add(res.getString(res.getColumnIndex("ID"))+"_"+
                       res.getString(res.getColumnIndex("PASS")));
            res.moveToNext();
        }
        return idPass;
    }
    public void deleteId(String id) {
        this.getReadableDatabase()
            .delete("ERPDB", "ID = ?", new String[]{id}) ;
    }

}
