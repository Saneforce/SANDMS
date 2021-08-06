package com.saneforce.dms.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.Interface.DMS;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.Common_Class;
import com.saneforce.dms.Utils.Common_Model;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.CustomListViewDialog;
import com.saneforce.dms.Utils.Shared_Common_Pref;
import com.saneforce.dms.sqlite.DBController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddNewRetailer extends AppCompatActivity implements DMS.Master_Interface {
    CustomListViewDialog customDialog;
    List<Common_Model> modelRetailDetails = new ArrayList<>();
    List<Common_Model> modelRetailClass = new ArrayList<>();
    List<Common_Model> modelRetailChannel = new ArrayList<>();
    Common_Model mCommon_model_spinner;
    TextView txtRoute, txtClass, txtChannel, txtLat, txtLon;
    Shared_Common_Pref mShared_common_pref;
    String KeyDate = "", keyCodeValue = "", keyEk = "", KeyHyp = "", routeID = "", classID = "", channelID = "",
            locationValue = "", str1 = "", str2 = "";
    JSONObject docMasterObject;
    EditText edtName, edtAdds, edtCity, edtPhone, edtEmail;
    JSONArray mainArray;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private SettingsClient mSettingsClient;
//    private LocationRequest mLocationRequest;
//    private LocationSettingsRequest mLocationSettingsRequest;
//    private LocationCallback mLocationCallback;
//    private Location mCurrentLocation;
//    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
//    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
//    private static final int REQUEST_CHECK_SETTINGS = 100;


    private static final int REQUEST_LOCATION = 1;
    Button btnGetLocation;
    LocationManager locationManager;
    String latitude, longitude;

    DBController dbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_retailer);
        mShared_common_pref = new Shared_Common_Pref(this);

        dbController = new DBController(this);
        if(!dbController.getResponseFromKey(DBController.ROUTE_LIST).equals("")){
            processRouteDetails(new Gson().fromJson(dbController.getResponseFromKey(DBController.ROUTE_LIST), JsonArray.class));
        }else {
            if(Constants.isInternetAvailable(this))
                getRouteDetails();
            else
                Toast.makeText(this, "Please check the internet connection", Toast.LENGTH_SHORT).show();

        }

        if(!dbController.getResponseFromKey(DBController.CLASS_LIST).equals("")){
            processClassList(new Gson().fromJson(dbController.getResponseFromKey(DBController.CLASS_LIST), JsonArray.class));
        }else {
            if(Constants.isInternetAvailable(this))
                getRouteClass();
            else
                Toast.makeText(this, "Please check the internet connection", Toast.LENGTH_SHORT).show();

        }

        if(!dbController.getResponseFromKey(DBController.CHANNEL_LIST).equals("")){
            processChannelList(new Gson().fromJson(dbController.getResponseFromKey(DBController.CHANNEL_LIST), JsonArray.class));
        }else {
            if(Constants.isInternetAvailable(this))
                getRouteChannel();
            else
                Toast.makeText(this, "Please check the internet connection", Toast.LENGTH_SHORT).show();

        }



        txtRoute = findViewById(R.id.txt_route);
        txtClass = findViewById(R.id.txt_retailer_class);
        txtChannel = findViewById(R.id.txt_retailer_channel);
        txtLat = findViewById(R.id.txt_retail_lat);
        txtLon = findViewById(R.id.txt_retail_lon);
        edtName = findViewById(R.id.edt_new_name);
        edtAdds = findViewById(R.id.edt_new_address);
        edtCity = findViewById(R.id.edt_new_city);
        edtPhone = findViewById(R.id.edt_new_phone);
        edtEmail = findViewById(R.id.edt_new_email);


        ImageView imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                AddNewRetailer.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                AddNewRetailer.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                txtLat.setText(latitude);
                txtLon.setText(longitude);

                Log.v("Your_Location: ", "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void LinearRoute(View v) {
        customDialog = new CustomListViewDialog(AddNewRetailer.this, modelRetailDetails, 8);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    public void LinearClass(View v) {
        customDialog = new CustomListViewDialog(AddNewRetailer.this, modelRetailClass, 9);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    public void LinearChannel(View v) {
        customDialog = new CustomListViewDialog(AddNewRetailer.this, modelRetailChannel, 10);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    public void getRouteDetails() {
        String routeMap = "{\"tableName\":\"vwTown_Master_APP\",\"coloumns\":\"[\\\"town_code as id\\\", \\\"town_name as name\\\",\\\"target\\\",\\\"min_prod\\\",\\\"field_code\\\",\\\"stockist_code\\\"]\",\"where\":\"[\\\"isnull(Town_Activation_Flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);

        Log.v("KArthic_Retailer", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonRootObject = response.body();
                    JsonArray jsonArray = jsonRootObject.getAsJsonArray("Data");

                    processRouteDetails(jsonArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
            }
        });
    }

    private void processRouteDetails(JsonArray jsonArray) {
        try {
            for (int a = 0; a < jsonArray.size(); a++) {
                JsonObject jso =(JsonObject) jsonArray.get(a);
                String className = jso.get("name").getAsString();
                String id = jso.get("id").getAsString();
                mCommon_model_spinner = new Common_Model(id, className, "flag");
                modelRetailDetails.add(mCommon_model_spinner);
            }

            dbController.updateDataResponse(DBController.ROUTE_LIST, new Gson().toJson(jsonArray));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getRouteClass() {
        String routeMap = "{\"tableName\":\"Mas_Doc_Class\",\"coloumns\":\"[\\\"Doc_ClsCode as id\\\", \\\"Doc_ClsSName as name\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {


                    JsonObject jsonRootObject = response.body();
                    JsonArray jsonArray = jsonRootObject.getAsJsonArray("Data");
                    processClassList(jsonArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
            }
        });
    }

    private void processClassList(JsonArray jsonArray) {
        try {
            for (int a = 0; a < jsonArray.size(); a++) {
                JsonObject jso = (JsonObject) jsonArray.get(a);
                String className = jso.get("name").getAsString();
                String id = jso.get("id").getAsString();
                mCommon_model_spinner = new Common_Model(id, className, "flag");
                modelRetailClass.add(mCommon_model_spinner);
            }

            dbController.updateDataResponse(DBController.CLASS_LIST, new Gson().toJson(jsonArray));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getRouteChannel() {
        String routeMap = "{\"tableName\":\"Doctor_Specialty\",\"coloumns\":\"[\\\"Specialty_Code as id\\\", \\\"Specialty_Name as name\\\"]\",\"where\":\"[\\\"isnull(Deactivate_flag,0)=0\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {

                    JsonObject jsonRootObject = response.body();
                    JsonArray jsonArray = jsonRootObject.getAsJsonArray("Data");

                    processChannelList(jsonArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
            }
        });
    }

    private void processChannelList(JsonArray jsonArray) {
        try {
            for (int a = 0; a < jsonArray.size(); a++) {
                JsonObject jso = (JsonObject) jsonArray.get(a);
                String className = jso.get("name").getAsString();
                String id = jso.get("id").getAsString();
                mCommon_model_spinner = new Common_Model(id, className, "flag");
                modelRetailChannel.add(mCommon_model_spinner);
            }

            dbController.updateDataResponse(DBController.CHANNEL_LIST, new Gson().toJson(jsonArray));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 8) {
            txtRoute.setText(myDataset.get(position).getName());
            routeID = myDataset.get(position).getId();
        } else if (type == 9) {
            txtClass.setText(myDataset.get(position).getName());
            classID = myDataset.get(position).getId();
        } else if (type == 10) {
            txtChannel.setText(myDataset.get(position).getName());
            channelID = myDataset.get(position).getId();
        }
    }

    public void addSave(View v) {
        if (txtRoute.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Route", Toast.LENGTH_SHORT).show();
        } else if (edtName.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Name", Toast.LENGTH_SHORT).show();
        } else if (edtAdds.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Address", Toast.LENGTH_SHORT).show();
        } else if (edtPhone.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Phone", Toast.LENGTH_SHORT).show();
        } else if (edtCity.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer City", Toast.LENGTH_SHORT).show();
        } else if (txtClass.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Class", Toast.LENGTH_SHORT).show();
        } else if (txtChannel.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Channel", Toast.LENGTH_SHORT).show();
        } else {
            saveRetailer();
        }
    }


    public void saveRetailer() {
        DateFormat dfw = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calobjw = Calendar.getInstance();
        KeyDate = mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code);
        keyCodeValue = keyEk + KeyHyp + KeyDate + dfw.format(calobjw.getTime()).hashCode();
        Log.e("KEY_CODE_HASH", keyCodeValue);
        JSONObject reportObject = new JSONObject();
        docMasterObject = new JSONObject();
        try {
            reportObject.put("town_code", "'" + routeID + "'");
            reportObject.put("wlkg_sequence", "null");
            reportObject.put("unlisted_doctor_name", "'" + edtName.getText().toString() + "'");
            reportObject.put("unlisted_doctor_address", "'" + edtAdds.getText().toString() + "'");
            reportObject.put("unlisted_doctor_phone", "'" + edtPhone.getText().toString() + "'");
            reportObject.put("unlisted_doctor_cityname", "'" + edtCity.getText().toString() + "'");
            reportObject.put("unlisted_doctor_landmark", "''");
            reportObject.put("Compititor_Id", "");
            reportObject.put("Compititor_Name", "");
            reportObject.put("CatUniverSelectId", "");
            reportObject.put("AvailUniverSelectId", "");
            reportObject.put("lat", "");
            reportObject.put("long", "");
            reportObject.put("unlisted_doctor_areaname", "''");
            reportObject.put("unlisted_doctor_contactperson", "''");
            reportObject.put("unlisted_doctor_designation", "''");
            reportObject.put("unlisted_doctor_gst", "''");
            reportObject.put("unlisted_doctor_pincode", "''");
            reportObject.put("unlisted_doctor_phone2", "''");
            reportObject.put("unlisted_doctor_phone3", "''");
            reportObject.put("unlisted_doctor_contactperson2", "''");
            reportObject.put("unlisted_doctor_contactperson3", "''");
            reportObject.put("unlisted_doctor_designation2", "''");
            reportObject.put("unlisted_cat_code", "null");
            reportObject.put("unlisted_specialty_code", channelID);
            reportObject.put("unlisted_qulifi", "'samp'");
            reportObject.put("unlisted_class", classID);
            reportObject.put("DrKeyId", "'" + keyCodeValue + "'");
            docMasterObject.put("unlisted_doctor_master", reportObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainArray = new JSONArray();
        mainArray.put(docMasterObject);
        String totalValueString = mainArray.toString();
        Log.e("TOTAL_VALUE_STRING", totalValueString);


        DBController dbController = new DBController(AddNewRetailer.this);
        if(dbController.addDataOfflineCalls(String.valueOf(System.currentTimeMillis()), totalValueString, "dcr/save", 0)){
            mShared_common_pref.save(Shared_Common_Pref.YET_TO_SYN, true);
            if(Constants.isInternetAvailable(this)){
                new Common_Class(this).checkData(dbController,getApplicationContext());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RetailerType();
                    }
                }, 2000);
            }else{
                Toast.makeText(AddNewRetailer.this, "New Retailer will be saved in offline", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else
            Toast.makeText(AddNewRetailer.this, "Please try again", Toast.LENGTH_SHORT).show();


/*
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        // addNewRetailer
        Call<JsonObject> call = apiInterface.addNewRetailer(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", "MGR", totalValueString);

        Log.v("ADD_NEW_RETAILER", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                Log.e("Add_Retailer_details", String.valueOf(jsonObject));
                String success = String.valueOf(jsonObject.get("success"));
                if (success.equalsIgnoreCase("true")) {
                    startActivity(new Intent(getApplicationContext(), SecondRetailerActivity.class));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
*/

    }

    public void OnBackClick(View v) {
onBackPressed();

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void RetailerType() {
        String RetailerDetails = "{\"tableName\":\"vwDoctor_Master_APP\",\"coloumns\":\"[\\\"doctor_code as id\\\", \\\"doctor_name as name\\\",\\\"town_code\\\",\\\"town_name\\\",\\\"lat\\\",\\\"long\\\",\\\"addrs\\\",\\\"ListedDr_Address1\\\",\\\"ListedDr_Sl_No\\\",\\\"Mobile_Number\\\",\\\"Doc_cat_code\\\",\\\"ContactPersion\\\",\\\"Doc_Special_Code\\\"]\",\"where\":\"[\\\"isnull(Doctor_Active_flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getRetName(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", RetailerDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject JsonObject = response.body();
                try {
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.RETAILER_LIST, new Gson().toJson(jsonArray));
                    finish();
                } catch (Exception io) {
                    io.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("LeaveTypeList", "Error");
            }
        });
    }

}
