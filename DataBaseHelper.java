package com.example.cyclogard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {


    public static final String CYCLOGARD = "CYCLOGARD";
    public static final String COLUNA_DATA = "DATA";
    public static final String COLUNA_LATITUDE = "LATITUDE";

    public static final String COLUNA_LONGITUDE = "LONGITUDE";
    public static final String COLUNA_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "cyclogard.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + CYCLOGARD + " (" + COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUNA_DATA + " TEXT, " + COLUNA_LATITUDE + " TEXT, " +  COLUNA_LONGITUDE + " TEXT)";

        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne (Base base) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUNA_DATA, base.getData());
        cv.put(COLUNA_LATITUDE, base.getLatitude());
        cv.put(COLUNA_LONGITUDE, base.getLongitude());

        long insert = db.insert(CYCLOGARD, null, cv);
        if(insert == -1){
            return false;
        } else {
            return true;
        }

    }

    public List<Base> getEveryone() {
        List<Base> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CYCLOGARD;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToLast()){
            do {
                int ID = cursor.getInt(0);
                String data = cursor.getString(1);
                String latitude = cursor.getString(2);
                String longitude = cursor.getString(3);

                Base novaLinha= new Base(ID,data, latitude, longitude);
                returnList.add(novaLinha);

            } while (cursor.moveToPrevious());

        } else {

        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List getLat () {
        List getLat = new ArrayList();

        String queryString = "SELECT * FROM " + CYCLOGARD;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            do {
                Double latitude = Double.valueOf(cursor.getString(2));

                getLat.add(latitude);

            } while (cursor.moveToNext());

        } else {

        }

        cursor.close();
        db.close();



        return getLat;
    }

    public List getLon () {
        List getLon = new ArrayList();

        String queryString = "SELECT * FROM " + CYCLOGARD;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            do {
                Double longitude = Double.valueOf(cursor.getString(3));

                getLon.add(longitude);

            } while (cursor.moveToNext());

        } else {

        }

        cursor.close();
        db.close();

        return getLon;
    }

    public List getData () {
        List getData = new ArrayList();

        String queryString = "SELECT * FROM " + CYCLOGARD;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()){
            do {
                String data = cursor.getString(1);

                getData.add(data);

            } while (cursor.moveToNext());

        } else {

        }

        cursor.close();
        db.close();

        return getData;
    }

    public String obterId () {

        String queryString = "SELECT * FROM " + CYCLOGARD;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        String ID="";

        if (cursor.moveToLast()){

                ID = cursor.getString(0);


            }

        cursor.close();
        db.close();

        return ID;
    }

    public List<Data> getDataFromSQLite() {
        List<Data> dataList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CYCLOGARD;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);


        if (cursor.moveToFirst()) {
            do {
                String value1 = cursor.getString(0);
                String value2 = cursor.getString(1);
                String value3 = cursor.getString(2);
                String value4 = cursor.getString(3);

                Data data = new Data(value1, value2, value3, value4);
                dataList.add(data);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dataList;
    }



}
