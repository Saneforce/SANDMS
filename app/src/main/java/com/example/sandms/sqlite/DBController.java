package com.example.sandms.sqlite;

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
    public static final String tablename = "tblSynMaster"; // tablename
    public static final String dataKey = "dataKey"; // column name
    private static final String id = "ID"; // auto generated ID column
    public static final String dataResponse = "dataResponse"; // column name
    private static final String isUpdatedToServer = "isUpdatedToServer"; // column name
    public static final String axnKey = "axnKey"; // column name
    private static final String databasename = "dbSynMaster"; // Dtabasename
    private static final int versioncode = 7; //versioncode of the database

    Context context;
    public DBController(Context context) {
        super(context, databasename, null, versioncode);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE IF NOT EXISTS " + tablename + "(" + id + " integer primary key, "+ dataKey + " text, " + dataResponse + " text, " + isUpdatedToServer + " text, "  + axnKey + " text " +")";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old,
                          int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + tablename;
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getAllDataKey() {

        ArrayList<HashMap<String, String>> productList = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + tablename, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(3).equals("0")){
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(id, cursor.getString(0));
                    map.put(dataKey, cursor.getString(1));
                    map.put(dataResponse, cursor.getString(2));
                    map.put(isUpdatedToServer, cursor.getString(3));
                    map.put(axnKey, cursor.getString(4));
                    productList.add(map);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

// return contact list
        return productList;
    }

    public boolean addDatakey(String tableName, String tableValue, String axnKey) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(dataKey, tableName);
            cv.put(dataResponse, tableValue);
            cv.put(isUpdatedToServer, "0");
            cv.put(DBController.axnKey, axnKey);

            long value =  db.insert(tablename, null, cv);

            Log.d(TAG, "addProduct: value "+ value);
//            db.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public boolean updateDatakey(String tableName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(dataKey, tableName);
            cv.put(isUpdatedToServer, "1");
            String[] args = new String[]{tableName};
            int value = db.update(tablename,cv, dataKey + " = ?" , args);
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


        try (SQLiteDatabase database = this.getWritableDatabase(); Cursor cursor = database.rawQuery("SELECT * FROM " + tablename + " WHERE " + dataKey + " = ?", args)) {
            cursor.moveToFirst();
            Log.d(TAG, "getResponse: keyName : "+keyName );

            Log.d(TAG, "getResponse: "+ cursor.getCount());
            if(cursor.getCount()> 0 && cursor.getColumnCount()> 0)
                return cursor.getString(cursor.getColumnIndex(dataResponse));
            //close cursor & database

        } catch (Exception e) {
            e.printStackTrace();

        }
        return value;
    }

}