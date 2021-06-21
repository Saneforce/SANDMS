package com.example.sandms.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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


    public static final String PriProduct_Brand = "Pri_Product_Brand";
    public static final String PriProduct_Data = "Pri_Product_Data";

    public static final String SecProduct_Brand = "Sec_Product_Brand";
    public static final String SecProduct_Data = "Sec_Product_Data";


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

    public String getvalue(String key) {
        String text = null;
        text = Common_pref.getString(key, "0");
        return text;
    }

    public void clear_pref(String key) {
        Common_pref.edit().remove(key).apply();
    }

}
