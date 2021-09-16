package com.saneforce.dms.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.saneforce.dms.listener.PrimaryProductDao;
import com.saneforce.dms.model.PrimaryProduct;

@Database(entities = {PrimaryProduct.class}, version = 10)
public abstract class PrimaryProductDatabase extends RoomDatabase {


    private static Context activity;
    private static PrimaryProductDatabase instance;


    public abstract PrimaryProductDao contactDao();


    public static synchronized PrimaryProductDatabase getInstance(Context context) {

        activity = context;
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), PrimaryProductDatabase.class, "contact_datbase")
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
        private PrimaryProductDao contactDao;


        public PopulateDbAsyntask(PrimaryProductDatabase contactDaos) {
            contactDao = contactDaos.contactDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
           // fillingWithStart(activity);

            return null;
        }

    }

/*
    private static void fillingWithStart(Context context) {

        Shared_Common_Pref mShared_common_pref = new Shared_Common_Pref(context);
        String sPrimaryProd =mShared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Data);
        PrimaryProductDao contact = getInstance(context).contactDao();
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

           *//*     contact.insert(new xPrimaryProduct(id, PId, Name, PName, PBarCode, PUOM, PRate,
                        PSaleUnit, PDiscount, PTaxValue, "0", "0", "0", "0", "0", PCon_fac));*//*
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }*/

    public PrimaryProductDatabase getAppDatabase() {
        return instance;
    }
}
