package com.example.sandms.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Model;
import com.example.sandms.Utils.CustomListViewDialog;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
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
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;


    private static final int REQUEST_LOCATION = 1;
    Button btnGetLocation;
    LocationManager locationManager;
    String latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_retailer);
        mShared_common_pref = new Shared_Common_Pref(this);

        getRouteDetails();
        getRouteClass();
        getRouteChannel();


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

                startActivity(new Intent(getApplicationContext(), SecondRetailerActivity.class));
                finish();
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
                    JSONObject jsonRootObject = new JSONObject(response.body().toString());
                    Log.v("KArthic_Retailer", jsonRootObject.toString());

                    JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject jso = jsonArray.getJSONObject(a);
                        String className = String.valueOf(jso.get("name"));
                        String id = String.valueOf(jso.get("id"));
                        mCommon_model_spinner = new Common_Model(id, className, "flag");
                        modelRetailDetails.add(mCommon_model_spinner);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
            }
        });
    }

    public void getRouteClass() {
        String routeMap = "{\"tableName\":\"Mas_Doc_Class\",\"coloumns\":\"[\\\"Doc_ClsCode as id\\\", \\\"Doc_ClsSName as name\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response.body().toString());
                    JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject jso = jsonArray.getJSONObject(a);
                        String className = String.valueOf(jso.get("name"));
                        String id = String.valueOf(jso.get("id"));
                        mCommon_model_spinner = new Common_Model(id, className, "flag");
                        modelRetailClass.add(mCommon_model_spinner);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
            }
        });
    }

    public void getRouteChannel() {
        String routeMap = "{\"tableName\":\"Doctor_Specialty\",\"coloumns\":\"[\\\"Specialty_Code as id\\\", \\\"Specialty_Name as name\\\"]\",\"where\":\"[\\\"isnull(Deactivate_flag,0)=0\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response.body().toString());
                    JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject jso = jsonArray.getJSONObject(a);
                        String className = String.valueOf(jso.get("name"));
                        String id = String.valueOf(jso.get("id"));
                        mCommon_model_spinner = new Common_Model(id, className, "flag");
                        modelRetailChannel.add(mCommon_model_spinner);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
            }
        });
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
        if (txtRoute.getText().equals("")) {
            Toast.makeText(this, "Enter Retailer Route", Toast.LENGTH_SHORT).show();
        } else if (edtName.getText().equals("")) {
            Toast.makeText(this, "Enter Retailer Name", Toast.LENGTH_SHORT).show();
        } else if (edtAdds.getText().equals("")) {
            Toast.makeText(this, "Enter Retailer Address", Toast.LENGTH_SHORT).show();
        } else if (edtPhone.getText().equals("")) {
            Toast.makeText(this, "Enter Retailer Phone", Toast.LENGTH_SHORT).show();
        } else if (edtCity.getText().equals("")) {
            Toast.makeText(this, "Enter Retailer City", Toast.LENGTH_SHORT).show();
        } else if (txtClass.getText().equals("")) {
            Toast.makeText(this, "Enter Retailer Class", Toast.LENGTH_SHORT).show();
        } else if (txtChannel.getText().equals("")) {
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

            }
        });

    }

    public void OnBackClick(View v) {
        AddNewRetailer.super.onBackPressed();
    }

    @Override
    public void onBackPressed() {

    }

}
