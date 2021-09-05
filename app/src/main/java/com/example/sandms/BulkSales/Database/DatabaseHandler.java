package com.example.sandms.BulkSales.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DBHAPBulkDMS";
    private static final String TABLE_Track = "Tracking_Location";
    private static final String Loc_Date = "Loc_Date";
    private static final String Loc_Lat = "Loc_Lat";
    private static final String Loc_Lng = "Loc_Lng";
    private static final String SF_Code = "Loc_Lng";
    private static final String Speed = "Speed";
    private static final String Bearing = "Bearing";
    private static final String Accuracy = "Accuracy";
    private static final String ET = "et";
    private static final String Alt = "alt";
    private static final String vAcc = "vAcc";
    private static final String sAcc = "sAcc";
    private static final String bAcc = "bAcc";
    private static final String Mock = "Mock";
    private static final String Batt = "Batt";
    private static final String Flag = "Flag";


    private static final String TABLE_Masters = "HAP_Masters";
    private static final String ID = "ID";
    private static final String Data = "Data";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_Track + "("
                + Loc_Date + " TEXT PRIMARY KEY,"
                + Loc_Lat + " TEXT," + Loc_Lng + " TEXT," + Speed+ " TEXT," + Bearing+ " TEXT,"
                + Accuracy + " TEXT,"
                + ET + " TEXT,"
                + Alt + " TEXT,"
                + vAcc + " TEXT,"
                + sAcc + " TEXT,"
                + bAcc + " TEXT,"
                + Mock + " TEXT,"
                + Batt + " TEXT,"
                + Flag + " INT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_Master_TABLE = "CREATE TABLE " + TABLE_Masters + "("
                + ID + " TEXT PRIMARY KEY,"
                + Data + " TEXT" + ")";
        db.execSQL(CREATE_Master_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Track);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Masters);
        // Create tables again
        onCreate(db);
    }
    public void deleteMasterData(String Key){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "delete from " + TABLE_Masters+" WHERE "+ID+" = '"+Key+"' ";
        db.execSQL(selectQuery);
    }
    public void addMasterData(String Key, JsonArray uData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, Key);
        values.put(Data, String.valueOf(uData));
        db.insert(TABLE_Masters, null, values);
        db.close();
    }

    public JSONArray getMasterData(String Key) throws JSONException {
        // Select All Query
        String selectQuery = "SELECT  "+Data+" FROM " + TABLE_Masters+" WHERE "+ID+"='"+Key+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        JSONArray uData=new JSONArray();
        if (cursor.moveToFirst()) {
            uData=new JSONArray(cursor.getString(0));
        }

        // return contact list
        return uData;
    }

    void addTrackDetails(JSONObject Location) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Loc_Date, Location.getString("Time"));
            values.put(Loc_Lat, Location.getString("Latitude"));
            values.put(Loc_Lng, Location.getString("Longitude"));
            values.put(Speed, Location.getString("Speed"));
            values.put(Bearing, Location.getString("bear"));
            values.put(Accuracy, Location.getString("hAcc"));

            values.put(ET, Location.getString("et"));
            values.put(Alt, Location.getString("alt"));
            values.put(vAcc, Location.getString("vAcc"));
            values.put(sAcc, Location.getString("sAcc"));
            values.put(bAcc, Location.getString("bAcc"));
            values.put(Mock, Location.getString("mock"));
            values.put(Batt, Location.getString("batt"));
            values.put(Flag, 0);


            // Inserting Row
            db.insert(TABLE_Track, null, values);
            //2nd argument is String containing nullColumnHack
            db.close(); // Closing database connection
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void updateTrackDetails(JSONObject Location,int flag) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Flag, flag);

            // updating row
            db.update(TABLE_Track, values, Loc_Date + " = ?",
                    new String[] { Location.getString("Time") });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Deleting single TrackDetails
    public void deleteTrackDetails(JSONObject Location) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_Track, Loc_Date + " = ?",
                    new String[]{Location.getString("Time")});
            db.close();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Deleting single TrackDetails
    public void deleteAllTrackDetails() {
        try {
            String selectQuery = "DELETE FROM " + TABLE_Track+" WHERE "+Flag+"=0";
            SQLiteDatabase db = this.getWritableDatabase();
            db.rawQuery(selectQuery, null);
            db.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public JSONArray getAllPendingTrackDetails() {
         // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_Track+" WHERE "+Flag+"=0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        JSONArray jsonArray=new JSONArray();
        if (cursor.moveToFirst()) {
            do {
                JSONObject item = new JSONObject();
                try {
                    item.put("Time", cursor.getString(0));
                    item.put("Latitude", cursor.getString(1));
                    item.put("Longitude", cursor.getString(2));
                    item.put("Speed", cursor.getString(3));
                    item.put("bear", cursor.getString(4));
                    item.put("hAcc", cursor.getString(5));
                    item.put("et", cursor.getString(6));
                    item.put("alt", cursor.getString(7));
                    item.put("vAcc", cursor.getString(8));
                    item.put("sAcc", cursor.getString(9));
                    item.put("bAcc", cursor.getString(10));
                    item.put("mock", cursor.getString(11));
                    item.put("batt", cursor.getString(12));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(item);

            } while (cursor.moveToNext());
        }

        // return contact list
        return jsonArray;
    }
}
