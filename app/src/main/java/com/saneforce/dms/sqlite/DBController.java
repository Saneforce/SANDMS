package com.saneforce.dms.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vengat on 26-07-2021
 */

public class DBController extends SQLiteOpenHelper {
    private static final String TAG = DBController.class.getSimpleName();
    public static final String TABLE_NAME = "tblSynMaster"; // tablename
    public static final String DATA_KEY = "dataKey"; // column name
    private static final String ID = "ID"; // auto generated ID column
    public static final String DATA_RESPONSE = "dataResponse"; // column name
    private static final String IS_UPDATED_TO_SERVER = "isUpdatedToServer"; // column name
    public static final String AXN_KEY = "axnKey"; // column name
    public static final String IS_ORDER_KEY = "isOrder"; // column name
    private static final String DATABASE_NAME = "dbSynMaster"; // Dtabasename
    private static final int VERSION_CODE = 9; //versioncode of the database

    public static final String RETAILER_LIST = "retailer_list"; // Dtabasename
    public static final String TEMPLATE_LIST = "template_list"; // Dtabasename
    public static final String ROUTE_LIST = "route_list"; // Dtabasename
    public static final String CLASS_LIST = "class_list"; // Dtabasename
    public static final String CHANNEL_LIST = "channel_list"; // Dtabasename
    public static final String PRIMARY_PRODUCT_BRAND = "primary_product_brand"; // Dtabasename
    public static final String PRIMARY_PRODUCT_DATA = "primary_product_data"; // Dtabasename
    public static final String SECONDARY_PRODUCT_BRAND = "secondary_product_brand"; // Dtabasename
    public static final String SECONDARY_PRODUCT_DATA = "secondary_product_data"; // Dtabasename


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
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old,
                          int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + TABLE_NAME;
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
}