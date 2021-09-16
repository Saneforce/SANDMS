package com.saneforce.dms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.saneforce.dms.activity.LoginActivity;

public class Shared_Common_Pref {
    SharedPreferences Common_pref;
    SharedPreferences.Editor editor;
    Activity activity;
    Context _context;

    public static final String Sf_Code = "Sf_Code";
    public static final String Sf_Name = "Sf_Name";
    public static final String State_Code = "State_Code";
    public static final String Div_Code = "div_Code";
    public static final String Cut_Off_Time = "Cut_Off_Time";
    public static final String name = "Sf_Name";
    public static final String Sf_UserName = "Sf_UserName";
    public static final String Stockist_Code = "Stockist_Code";
    public static final String Stockist_Mobile = "Stockist_Mobile";
    public static final String sup_code = "sup_code";
    public static final String sup_name = "sup_name";
    public static final String Stockist_Address = "Stockist_Address";
    public static final String sup_addr = "sup_addr";
    public static final String Product_List = "Product_List";
    public static final String Product_Brand = "Product_Brand";
    public static final String Product_Data = "Product_Data";
    public static final String LOGIN_DATE = "LOGIN_DATE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_PHONE = "USER_PHONE";
    public static final String USER_ERP_CODE = "USER_ERP_CODE";

    public static final String YET_TO_SYN = "yet_to_syn";
    public static final String PREF_KEY_APP_AUTO_START = "PREF_KEY_APP_AUTO_START";


    public Shared_Common_Pref(Activity Ac) {
        activity = Ac;
        if (activity != null) {
            Common_pref = activity.getSharedPreferences("Preference_values", Context.MODE_PRIVATE);
            editor = Common_pref.edit();
        }
    }

    public Shared_Common_Pref(Context cc) {
        this._context = cc;
        Common_pref = cc.getSharedPreferences("Preference_values", Context.MODE_PRIVATE);
        editor = Common_pref.edit();
    }

    public void save(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getvalue(String key) {
        String text = null;
        text = Common_pref.getString(key, "0");
        return text;
    }

    public String getvalue1(String key) {
        String text = null;
        text = Common_pref.getString(key, "");
        return text;
    }
    public int getIntvalue(String key) {
        String text = null;
        return Common_pref.getInt(key, 1);
    }

    public boolean getBooleanvalue(String key) {
        boolean value ;
        value = Common_pref.getBoolean(key, false);
        return value;
    }

    public void clear_pref(String key) {
        Common_pref.edit().remove(key).apply();
    }

    public void logoutUser(Activity context) {
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(i);
        context.finishAffinity();

    }



}
