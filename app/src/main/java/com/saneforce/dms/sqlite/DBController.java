package com.saneforce.dms.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.saneforce.dms.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vengat on 26-07-2021
 */

public class DBController extends SQLiteOpenHelper {
    private static final int VERSION_CODE = 13; //versioncode of the database

    private static final String TAG = DBController.class.getSimpleName();
    public static final String TABLE_NAME = "tblSynMaster"; // tablename
    public static final String DATA_KEY = "dataKey"; // column name
    public static final String ID = "ID"; // auto generated ID column
    public static final String DATA_RESPONSE = "dataResponse"; // column name
    private static final String IS_UPDATED_TO_SERVER = "isUpdatedToServer"; // column name
    public static final String AXN_KEY = "axnKey"; // column name
    public static final String IS_ORDER_KEY = "isOrder"; // column name
    private static final String DATABASE_NAME = "dbSynMaster";


    public static final String RETAILER_LIST = "retailer_list";
    public static final String TEMPLATE_LIST = "template_list";
    public static final String ROUTE_LIST = "route_list";
    public static final String CLASS_LIST = "class_list";
    public static final String CHANNEL_LIST = "channel_list";
    public static final String PRIMARY_PRODUCT_BRAND = "primary_product_brand";
    public static final String PRIMARY_PRODUCT_DATA = "primary_product_data";
    public static final String SECONDARY_PRODUCT_BRAND = "secondary_product_brand";
    public static final String SECONDARY_PRODUCT_DATA = "secondary_product_data";


    public static final String TABLE_LOCATION = "table_location"; // tablename
    public static final String Latitude = "column_latitude";
    public static final String Longitude = "column_longitude";
    public static final String Time = "column_time";
    public static final String Address = "column_address";
    public static final String Accuracy = "column_accuracy";
    public static final String Speed = "column_speed";
    public static final String Bearing = "column_bearing";
    public static final String CurrentTime = "column_current_time";


    Context context;
    public DBController(Context context) {
        super(context, DATABASE_NAME, null, VERSION_CODE);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID + " integer primary key, "+ DATA_KEY + " text, " + DATA_RESPONSE + " text, " + IS_UPDATED_TO_SERVER + " text, "  + AXN_KEY + " text, " +   IS_ORDER_KEY + " integer " +")";
        database.execSQL(query);
        String locationQuery;
        locationQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + "(" + ID + " integer primary key, "+ Latitude + " text, " + Longitude + " text, "
                + Time + " text, "  + Address + " text, " +   Accuracy + " text, " +   Speed + " text, " +   Bearing + " text, "+ IS_UPDATED_TO_SERVER + " text, " + CurrentTime + " text "+  ")";
        database.execSQL(locationQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old,
                          int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        database.execSQL(query);
        query = "DROP TABLE IF EXISTS " + TABLE_LOCATION;
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getAllDataKey() {

        ArrayList<HashMap<String, String>> productList = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(3).equals("0")){
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(ID, cursor.getString(0));
                    map.put(DATA_KEY, cursor.getString(1));
                    map.put(DATA_RESPONSE, cursor.getString(2));
                    map.put(IS_UPDATED_TO_SERVER, cursor.getString(3));
                    map.put(AXN_KEY, cursor.getString(4));
                    productList.add(map);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

// return contact list
        return productList;
    }


    public ArrayList<HashMap<String, String>> getAllLocationData() {

        ArrayList<HashMap<String, String>> locationList = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_LOCATION, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(8).equals("0")){
                    HashMap<String, String> map = new HashMap<>();
                    map.put(ID, cursor.getString(0));
                    map.put(Latitude, cursor.getString(1));
                    map.put(Longitude, cursor.getString(2));
                    map.put(Time, cursor.getString(3));
                    map.put(Address, cursor.getString(4));
                    map.put(Accuracy, cursor.getString(5));
                    map.put(Speed, cursor.getString(6));
                    map.put(Bearing, cursor.getString(7));
                    map.put(CurrentTime, cursor.getString(9));
                    locationList.add(map);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

// return contact list
        return locationList;
    }

    public boolean addDataOfflineCalls(String tableName, String tableValue, String axnKey, int isOrder) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DATA_KEY, tableName);
            cv.put(DATA_RESPONSE, tableValue);
            cv.put(IS_UPDATED_TO_SERVER, "0");
            cv.put(DBController.AXN_KEY, axnKey);
            cv.put(DBController.IS_ORDER_KEY, isOrder);

            long value =  db.insert(TABLE_NAME, null, cv);

            Log.d(TAG, "addProduct: value "+ value);
//            db.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }


    public boolean addLocation(Location location, String isUpdatedToServer, String address) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(Latitude, String.valueOf(location.getLatitude()));
            cv.put(Longitude, String.valueOf(location.getLongitude()));
            cv.put(Time, String.valueOf(location.getTime()));
            cv.put(Address, address);
            cv.put(Accuracy, String.valueOf(location.getAccuracy()));
            cv.put(Speed, String.valueOf(location.getSpeed()));
            cv.put(Bearing, String.valueOf(location.getBearing()));
            cv.put(IS_UPDATED_TO_SERVER, isUpdatedToServer);
            cv.put(CurrentTime, TimeUtils.getCurrentTimeStamp(TimeUtils.FORMAT));

            long value =  db.insert(TABLE_LOCATION, null, cv);

            Log.d(TAG, "addProduct: value "+ value);
//            db.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public boolean updateLocation(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(IS_UPDATED_TO_SERVER, "1");
            String[] args = new String[]{id};
            int value = db.update(TABLE_LOCATION,cv, ID + " = ?" , args);
            Log.d(TAG, "updateLocation: " + value);

//            db.close();

            return value > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }


    public boolean updateDataOfflineCalls(String tableName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DATA_KEY, tableName);
            cv.put(IS_UPDATED_TO_SERVER, "1");
            String[] args = new String[]{tableName};
            int value = db.update(TABLE_NAME,cv, DATA_KEY + " = ?" , args);
            Log.d(TAG, "updateProduct: " + value);

//            db.close();

            return value > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void clearDatabase(String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();

        String clearDBQuery = "DELETE FROM "+TABLE_NAME;
        db.execSQL(clearDBQuery);
    }

    public String getResponseFromKey(String keyName){

        String value = "";
        String[] args = new String[]{keyName};


        try (SQLiteDatabase database = this.getWritableDatabase(); Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + DATA_KEY + " = ?", args)) {
            cursor.moveToFirst();
            Log.d(TAG, "getResponse: keyName : "+keyName );

            Log.d(TAG, "getResponse: "+ cursor.getCount());
            if(cursor.getCount()> 0 && cursor.getColumnCount()> 0)
                return cursor.getString(cursor.getColumnIndex(DATA_RESPONSE));
            //close cursor & database

        } catch (Exception e) {
            e.printStackTrace();

        }
        return value;
    }


    public boolean updateDataResponse(String tableName, String tableValue) {
        boolean isUpdated = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {

            cv.put(DATA_KEY, tableName);
            cv.put(DATA_RESPONSE, tableValue);
            cv.put(IS_UPDATED_TO_SERVER, "1");
            cv.put(DBController.AXN_KEY, "");

            String[] args = new String[]{tableName};
            int value = db.update(TABLE_NAME,cv, DATA_KEY + " = ?" , args);
            Log.d(TAG, "updateProduct: " + value);

//            db.close();

            isUpdated = value > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(!isUpdated){
            if(db.insert(TABLE_NAME, null, cv)>0)
                isUpdated = true;
        }

        return isUpdated;
    }

    public String getOfflineCount(String axn, int isOrder) {

        int count = 0;

        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = this.getWritableDatabase();
            cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getString(3).equals("0") && cursor.getInt(5)==isOrder && cursor.getString(4).equals(axn)){
                        count++;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }



        return String.valueOf(count);
    }

    public String getOfflineData(String axn, int isOrder) {

        int count = 0;

        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = this.getWritableDatabase();
            cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getString(3).equals("0") && cursor.getInt(5)==isOrder && cursor.getString(4).equals(axn)){
                        count++;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


      /*  ArrayList<HashMap<String, String>> productList = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(3).equals("0")){
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(ID, cursor.getString(0));
                    map.put(DATA_KEY, cursor.getString(1));
                    map.put(DATA_RESPONSE, cursor.getString(2));
                    map.put(IS_UPDATED_TO_SERVER, cursor.getString(3));
                    map.put(AXN_KEY, cursor.getString(4));
                    productList.add(map);
                }
            } while (cursor.moveToNext());
        }
*/


        return String.valueOf(count);
    }

}