package com.example.sandms.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.sandms.Activity.LoginActivity;

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

    public static final String YET_TO_SYN = "yet_to_syn";


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
