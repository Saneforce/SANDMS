package com.saneforce.dms.utils;


import static android.content.Context.INPUT_METHOD_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.DMSApplication;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.R;
import com.saneforce.dms.sqlite.DBController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Common_Class {
    private static final String TAG =Common_Class.class.getSimpleName();
    Intent intent;
    Activity activity;
    public Context context;
    Shared_Common_Pref shared_common_pref;
    ProgressDialog nDialog;
    public static int NOTIFICATION_ID =  1001;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    LocationRequest mLocationRequest;


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
    public static boolean isOnProgress = false;

    public void checkData(DBController dbController, Context context){
        isOnProgress = true;
        if(dbController == null)
            dbController = new DBController(context);

        if(shared_common_pref == null)
            shared_common_pref = new Shared_Common_Pref(context);

        Log.d(TAG, "checkData: " + dbController.getAllDataKey());

        if (dbController.getAllDataKey().size() > 0) {
            for (HashMap<String, String> i : dbController.getAllDataKey()) {
                try {
                    sendDataToServer(i, dbController, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isOnProgress = false;
            }
        }, 3000);


    }


    private void sendDataToServer(HashMap<String, String> data, DBController controller, Context context) {
        Log.d(TAG, "sendDataToServer: data=> " + data);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestBody = null;
        try {
            if(data.get(DBController.AXN_KEY)!=null && data.get(DBController.AXN_KEY).equalsIgnoreCase("dcr/retailervisit"))
                requestBody = Constant.toRequestBody(new JSONObject(data.get(DBController.DATA_RESPONSE)));
            else
                requestBody = Constant.toRequestBody(new JSONArray(data.get(DBController.DATA_RESPONSE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> responseBodyCall = apiInterface.submitValue(data.get(DBController.AXN_KEY), shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), requestBody, shared_common_pref.getvalue(Shared_Common_Pref.State_Code), "MGR");
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String res = response.body().string();
                    Log.d(TAG, "onResponse: res "+ res);

                    if(res!=null && !res.equals("")){
                        JSONObject jsonRootObject = new JSONObject(res);
                        Log.d(TAG, "onResponse: "+ jsonRootObject);

                        if(jsonRootObject.has("success") && jsonRootObject.getBoolean("success")){
                            controller.updateDataOfflineCalls(data.get(DBController.DATA_KEY));
                            displayNotification("Order successfull", data.get(DBController.DATA_KEY), context);
                        }

                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }


    public BroadcastReceiver yourReceiver;

    public void registerReceiverGPS(Context context) {
        if (yourReceiver == null) {
            final IntentFilter theFilter = new IntentFilter();
            theFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            yourReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)){
                        if(!isGpsON(context)) {
                            if(DMSApplication.getApplication().isAppInForeground)
                                ShowLocationWarn(context);
                            createNotification( context,context.getString(R.string.gps_require_info),"");

                            try {
                                Location location = new Location("");
                                location.setLatitude(-1);
                                location.setLongitude(-1);
                                location.setAccuracy(-1);
                                location.setBearing(-1);
                                location.setSpeed(-1);
                                location.setTime(TimeUtils.getTimeStamp(TimeUtils.getCurrentTime(TimeUtils.FORMAT3), TimeUtils.FORMAT3));

                                new DBController(context).addLocation(location, "0", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            Common_Class.clearNotification(context);
                        }

                    }
                }
            };

            context.registerReceiver(yourReceiver, theFilter);
        }
    }

    public static boolean isGpsON(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        int mode = Settings.Secure.getInt(
                contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

        return mode != Settings.Secure.LOCATION_MODE_OFF;

    }


    public void ShowLocationWarn(Context context){

        if(mLocationRequest == null){
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(builder.build())
                .addOnSuccessListener(DMSApplication.getActiveScreen(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.d(TAG, "onSuccess: Called ");
                    }
                })
                .addOnFailureListener(DMSApplication.getActiveScreen(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    Log.i(TAG, "PendingIntent INSAP.");
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(DMSApplication.getActiveScreen(), 1000);

                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }


    public static void createNotification(Context mContext, String message, String address) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = mContext.getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = mContext.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.app_name), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.app_name))
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle(name)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if(!address.equals(""))
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText("You are at Location " + address));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void clearNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void displayNotification(String title, String task, Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("sandms", "sandms", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "sandms")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.logo);

        notificationManager.notify(1, notification.build());
    }




}

