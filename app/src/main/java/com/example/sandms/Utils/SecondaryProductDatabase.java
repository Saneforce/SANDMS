package com.example.sandms.Utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.sandms.Interface.PrimaryProductDao;
import com.example.sandms.Interface.SecProductDao;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.SecondaryProduct;
import com.example.sandms.sqlite.DBController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Database(entities = {SecondaryProduct.class}, version = 2)
public abstract class SecondaryProductDatabase extends RoomDatabase {


    private static Context activity;
    private static SecondaryProductDatabase instance;


    public abstract SecProductDao contactDao();


    public static synchronized SecondaryProductDatabase getInstance(Context context) {

        activity = context;
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), SecondaryProductDatabase.class, "contact_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(rooCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback rooCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
           // new PopulateDbAsyntask(instance).execute();

        }
    };

    private static class PopulateDbAsyntask extends AsyncTask<Void, Void, Void> {
        private SecProductDao contactDao;


        public PopulateDbAsyntask(SecondaryProductDatabase contactDaos) {
            contactDao = contactDaos.contactDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
         //   fillingWithStart(activity);

            return null;
        }

    }


/*
    private static void fillingWithStart(Context context) {

        Shared_Common_Pref mShared_common_pref = new Shared_Common_Pref(context);
        String sPrimaryProd =  dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA);
        SecProductDao contact = getInstance(context).contactDao();
        try {
            JSONArray jsonArray = new JSONArray(sPrimaryProd);
            JSONObject jsonObject = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);

                String id = String.valueOf(jsonObject.get("id"));
                String Name = String.valueOf(jsonObject.get("name"));
                String PName = String.valueOf(jsonObject.get("Pname"));
                String PRate = String.valueOf(jsonObject.get("Product_Cat_Code"));
                String PBarCode = String.valueOf(jsonObject.get("Product_Brd_Code"));
                String PId = String.valueOf(jsonObject.get("PID"));
                String PUOM = String.valueOf(jsonObject.get("UOM"));
                String PSaleUnit = String.valueOf(jsonObject.get("Product_Sale_Unit"));
                String PDiscount = String.valueOf(jsonObject.get("Discount"));
                String PTaxValue = String.valueOf(jsonObject.get("Tax_value"));
                String PCon_fac = String.valueOf(jsonObject.get("Conv_Fac"));

                 }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
*/

    public SecondaryProductDatabase getAppDatabase() {
        return instance;
    }
}
