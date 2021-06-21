package com.example.sandms.Utils;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import static android.content.Context.INPUT_METHOD_SERVICE;


public class Common_Class {

    Intent intent;
    Activity activity;
    Dialog dialog_invitation = null;
    public Context context;
    Shared_Common_Pref shared_common_pref;
    ProgressDialog nDialog;
    Type userType;
    ;
    Gson gson;

    // Gson gson;
    String Result = "false";
    public static String Version_Name = "Ver 2.3.2";
    public static String Work_Type = "0";

    public void CommonIntentwithFinish(Class classname) {
        intent = new Intent(activity, classname);

        activity.startActivity(intent);
        activity.finish();
    }

    public Common_Class(Context context) {
        this.context = context;
        shared_common_pref = new Shared_Common_Pref(context);

    }
    public void CommonIntentwithoutFinish(Class classname) {
        intent = new Intent(activity, classname);

        activity.startActivity(intent);

    }

    public Common_Class(Activity activity) {
        this.activity = activity;
        nDialog = new ProgressDialog(activity);
        shared_common_pref = new Shared_Common_Pref(activity);

    }

    public void ProgressdialogShow(int flag, String message) {

        if (flag == 1) {
            nDialog.setMessage("Loading...");
            nDialog.setTitle(message);
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();


        } else {
            nDialog.dismiss();
        }
    }

    public static String GetDateOnly() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dpln = new SimpleDateFormat("yyyy-MM-dd");
        String plantime = dpln.format(c.getTime());
        return plantime;
    }

    public static String GetDay() {
        Date dd = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");

        return simpleDateformat.format(dd);
    }

    public boolean isNetworkAvailable(final Context context) {
        this.context = context;

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static String GetDatewothouttime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dpln = new SimpleDateFormat("yyyy-MM-dd");
        String plantime = dpln.format(c.getTime());
        return plantime;
    }


    public void makeCall(int mobilenumber) {
        final int REQUEST_PHONE_CALL = 1;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mobilenumber));
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            activity.startActivity(callIntent);
        }


    }

    public static double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    public static double Check_Distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public List<Common_Model> getfilterList(List<Common_Model> Jointworklistview) {
        List<Common_Model> Jointworklistviewsave = new ArrayList<>();
        for (int i = 0; i < Jointworklistview.size(); i++) {
            if (Jointworklistview.get(i).isSelected()) {
                Log.e("SELECTED", String.valueOf(Jointworklistview.get(i).isSelected()));
                Jointworklistviewsave.add(new Common_Model(Jointworklistview.get(i).getName(), Jointworklistview.get(i).getId(), true));
            }

        }

        return Jointworklistviewsave;
    }

    ;


    public JsonArray FilterGson(final Iterable<JsonObject> SrcArray, String colName, String searchVal) {
        JsonArray ResArray = new JsonArray();
        for (JsonObject jObj : SrcArray) {
            if (jObj.get(colName).getAsString().equalsIgnoreCase(searchVal)) {
                ResArray.add(jObj);
            }
        }
        return ResArray;
    }
   /* public void Reurnypeface(class cl,){
        userType = new TypeToken<ArrayList<Work_Type_Model>>() {
        }.getType();
        worktypelist = gson.fromJson(new Gson().toJson(noticeArrayList), userType);

    }*/


//    public void CustomerMe(final Context context_) {
//        this.context = context_;
//        shared_common_pref = new Shared_Common_Pref(activity);
//        gson = new Gson();
//        apiService = ApiClient.getClient().create(ApiInterface.class);
//        Type type = new TypeToken<List<CustomerMe>>() {
//        }.getType();
//        System.out.println("TYPETOKEN_LIST" + type);
//        CustomerMeList = gson.fromJson(shared_common_pref.getvalue(Shared_Common_Pref.cards_pref), type);
//        JSONObject paramObject = new JSONObject();
//        try {
//            paramObject.put("name","dd");
//            paramObject.put("password","sddfdf");
//
//            Call<JsonObject> Callto = apiService.LoginJSON(paramObject.toString());
//            Callto.enqueue(CheckUser);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            System.out.println("JSON Expections" + paramObject.toString());
//
//        }
//
//
//    }


    /* public void showToastMSG(Activity Ac, String MSg, int s) {
         TastyToast.makeText(Ac, MSg,
                 TastyToast.LENGTH_SHORT, s);
     }*/
    public static boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty())
            return false;
        return true;
    }

    public void CommonIntentwithNEwTask(Class classname) {
        intent = new Intent(activity, classname);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static String GetEkey() {
        DateFormat dateformet = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calander = Calendar.getInstance();
        return "EK" + Shared_Common_Pref.Sf_Code + dateformet.format(calander.getTime()).hashCode();

    }

    public void hideKeybaord(View v, Context context) {
        this.context = context;
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    public static String addquote(String s) {
        return new StringBuilder()
                .append('\'')
                .append(s)
                .append('\'')
                .toString();
    }


    public void CommonIntentwithoutFinishputextra(Class classname, String key, String value) {
        intent = new Intent(activity, classname);
        intent.putExtra(key, value);
        Log.e("commanclasstitle", value);
        activity.startActivity(intent);
    }

    public void CommonIntentwithoutFinishputextratwo(Class classname, String key, String value, String key2, String value2) {
        intent = new Intent(activity, classname);
        intent.putExtra(key, value);
        intent.putExtra(key2, value2);
        Log.e("commanclasstitle", value);
        activity.startActivity(intent);
    }

    public String getintentValues(String name) {
        Intent intent = activity.getIntent();
        return intent.getStringExtra(name);
    }

    public static String GetDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dpln = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String plantime = dpln.format(c.getTime());
        return plantime;
    }


}

