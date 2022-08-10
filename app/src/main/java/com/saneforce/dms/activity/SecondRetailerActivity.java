package com.saneforce.dms.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.saneforce.dms.DMSApplication;
import com.saneforce.dms.R;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.listener.PrimaryProductDao;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.CustomListViewDialog;
import com.saneforce.dms.utils.PrimaryProductDatabase;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondRetailerActivity extends AppCompatActivity implements DMS.Master_Interface {

    public static final int ACTIVITY_REQUEST_CODE = 2;

    List<Common_Model> listOrderType = new ArrayList<>();
    List<Common_Model> RetailerType = new ArrayList<>();
    List<Common_Model> modelTemplates = new ArrayList<>();
    ArrayList<String> oderTypeList;
    Common_Model mCommon_model_spinner;
    CustomListViewDialog customDialog9, customDialog123, customDialog10;
    TextView txtOrder, txtRtNme, txtRtAdd;
    LinearLayout linRtDetails;
    Shared_Common_Pref shared_common_pref;
    EditText editRemarks;
    String retailerId = "";
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String latitude, longitude;
    Common_Class mCommon_class;
    TextView txtRetailerChannel, txtClass, txtLastOrderAmount, txtModelOrderValue, txtLastVisited, txtMobile;
    //    , txtReamrks, txtDistributor, txtMobileTwo
//    SecondaryProductViewModel SecViewModel;
    DBController dbController;
    int PhoneOrderTypes = 0;
    TextView tv_sch_enrollment;
    String sfCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_retailer);
        txtOrder = findViewById(R.id.order_type);
        txtRtNme = findViewById(R.id.retailer_name);
        txtRtAdd = findViewById(R.id.txt_retail_add);
        linRtDetails = findViewById(R.id.linear_reatiler_details);
        editRemarks = findViewById(R.id.remarks_sec);
        txtRetailerChannel = findViewById(R.id.retailer_channel);
        txtClass = findViewById(R.id.txt_class);
        txtModelOrderValue = findViewById(R.id.model_order_vlaue);
        txtLastVisited = findViewById(R.id.txt_last_visited);
        txtLastOrderAmount = findViewById(R.id.txt_last_order_amount);
//        txtReamrks = findViewById(R.id.txt_remarks);
        txtMobile = findViewById(R.id.txt_mobile);
        tv_sch_enrollment = findViewById(R.id.tv_sch_enrollment);
//        txtMobileTwo = findViewById(R.id.txt_mobile2);
//        txtDistributor = findViewById(R.id.txt_distributor);
        shared_common_pref = new Shared_Common_Pref(this);
        mCommon_class = new Common_Class(this);
        dbController = new DBController(this);
        if(getIntent().hasExtra("isDSM"))
            sfCode = shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code);
        else
            sfCode = shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code);
/*
        if(!dbController.getResponseFromKey(DBController.TEMPLATE_LIST).equals("")){
            processTemplateList(new Gson().fromJson(dbController.getResponseFromKey(DBController.TEMPLATE_LIST), JsonArray.class));
        }else {
            if(Constants.isInternetAvailable(this))
                getTemplate();
//            else
//                Toast.makeText(this, "Please check the internet connection", Toast.LENGTH_SHORT).show();
        }*/

        oderTypeList = new ArrayList<>();
        oderTypeList.add("Phone Order");
        oderTypeList.add("Field Order");
        for (int i = 0; i < oderTypeList.size(); i++) {
            String id = String.valueOf(oderTypeList.get(i));
            String name = oderTypeList.get(i);
            mCommon_model_spinner = new Common_Model(id, name, "flag");
            listOrderType.add(mCommon_model_spinner);
        }
        customDialog9 = new CustomListViewDialog(SecondRetailerActivity.this, listOrderType, 9);

        txtOrder.setText(oderTypeList.get(1));

        /*Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);*/

        TextView toolHeader = findViewById(R.id.toolbar_title);
        toolHeader.setText("Select Retailer");
        ImageView imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SecondRetailerActivity.this, DashBoardActivity.class));
                onBackPressed();
            }
        });

//        Log.v("SHARED_PREFERNCE", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            checkPermission();
        }
    }

    public void OrderType(View v) {
        OrderType();
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

/*    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);


                Log.v("Your_Location: ", "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
            } else {
               // Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


    public void checkPermission(){
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }*/
        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if(!report.areAllPermissionsGranted()){
                            Constant.showSnackbar(SecondRetailerActivity.this, findViewById(R.id.scrolllayout));
                        }else{
                            @SuppressLint("MissingPermission")
                            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (locationGPS != null) {
                                double lat = locationGPS.getLatitude();
                                double longi = locationGPS.getLongitude();
                                latitude = String.valueOf(lat);
                                longitude = String.valueOf(longi);


                                Log.v("Your_Location: ", "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                            }
                        }

//                    Toast.makeText(ViewReportActivity.this, "Please enable storage permission to share pdf ", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
    }




    public void ReatilerName(View v) {
        RetailerDetails();
    }

    public void TemplatesValue(View v) {
        customDialog123 = new CustomListViewDialog(SecondRetailerActivity.this, modelTemplates, 123);
        Window window = customDialog123.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog123.show();
    }

    public void OrderType() {
        if(customDialog9!=null)
            customDialog9.show();
    }

    public void AddRetailer(View v) {
        startActivity(new Intent(SecondRetailerActivity.this, AddNewRetailer.class));
    }

    public void RetailerDetails() {
        customDialog10 = new CustomListViewDialog(SecondRetailerActivity.this, RetailerType, 10);
        Window window = customDialog10.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog10.show();
    }

    public void RetailerType() {
        String RetailerDetails = "{\"tableName\":\"vwDoctor_Master_APP\",\"coloumns\":\"[\\\"doctor_code as id\\\", \\\"doctor_name as name\\\",\\\"town_code\\\",\\\"town_name\\\",\\\"lat\\\",\\\"long\\\",\\\"addrs\\\",\\\"ListedDr_Address1\\\",\\\"ListedDr_Sl_No\\\",\\\"Mobile_Number\\\",\\\"Doc_cat_code\\\",\\\"ContactPersion\\\",\\\"Doc_Special_Code\\\",\\\"Slan_Name\\\"]\",\"where\":\"[\\\"isnull(Doctor_Active_flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getRetName(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), sfCode, sfCode, shared_common_pref.getvalue(Shared_Common_Pref.State_Code), RetailerDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject JsonObject = response.body();
                try {
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");
//                    shared_common_pref.save(Shared_Common_Pref.YET_TO_SYN, false);

                    processRetailerList(jsonArray);

                } catch (Exception io) {
                    io.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("LeaveTypeList", "Error");
                t.printStackTrace();
            }
        });
    }

    private void processRetailerList(JsonArray jsonArray) {
        if(jsonArray.size()>0)
            RetailerType.clear();
        try {
            for (int a = 0; a < jsonArray.size(); a++) {
                JsonObject jsonObject = (JsonObject) jsonArray.get(a);
                String id = jsonObject.get("id").getAsString();
                String name = jsonObject.get("name").getAsString();
                String townName = jsonObject.get("ListedDr_Address1").getAsString();
                String phone = jsonObject.get("Mobile_Number").getAsString();
                String scheme = "";
                if(jsonObject.has("Slan_Name"))
                    scheme = jsonObject.get("Slan_Name").getAsString();
                mCommon_model_spinner = new Common_Model(name, id, scheme, townName, phone);
                RetailerType.add(mCommon_model_spinner);
            }

            dbController.updateDataResponse(DBController.RETAILER_LIST, new Gson().toJson(jsonArray));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {

        if (type == 9) {
            if(customDialog9!=null && customDialog9.isShowing())
                customDialog9.dismiss();

            txtOrder.setText(myDataset.get(position).getName());
            if(position ==0)
                PhoneOrderTypes =1;
            else
                PhoneOrderTypes =0;

        } else if (type == 10) {
            //if(Constant.isInternetAvailable(this)){
                if(customDialog10!=null && customDialog10.isShowing())
                    customDialog10.dismiss();

                txtRtNme.setText(myDataset.get(position).getName());
                linRtDetails.setVisibility(View.VISIBLE);
                retailerId = myDataset.get(position).getId();
                shared_common_pref.save("RetailerID", retailerId);
                //isCallSecActivity = false;
                if(Constant.isInternetAvailable(this)){
                    RetailerViewDetailsMethod(retailerId);

                }else
                    Toast.makeText(SecondRetailerActivity.this, "unable to load retailer details in offline", Toast.LENGTH_SHORT).show();
        } else if (type == 123) {
            if(customDialog123!=null && customDialog123.isShowing())
                customDialog123.dismiss();




            editRemarks.setText(myDataset.get(position).getName());
            editRemarks.setSelection(editRemarks.getText().toString().length());
        }
    }

    public void SaveSecndry(View v) {
        if (txtOrder.getText().toString().equals("")) {
            Toast.makeText(this, "Please Select Order Type", Toast.LENGTH_SHORT).show();
        } else if (txtRtNme.getText().toString().equals("")) {
            Toast.makeText(this, "Please Select Retailer Name", Toast.LENGTH_SHORT).show();
        } /*else if (editRemarks.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Remarks", Toast.LENGTH_SHORT).show();
        }*/ else {
            //isCallSecActivity = true;
            SaveRetials();

        }
    }

    public void RetailerViewDetailsMethod(String retailerID) {
        ApiInterface apiInterface2 = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody>call=apiInterface2.retailerViewDetails1(retailerID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());

                    Log.v("Retailer_Details", jsonObject.toString());
                    String channel = "";
                    if(jsonObject.has("DrSpl"))
                        channel = jsonObject.getString("DrSpl");
                    txtRetailerChannel.setText(channel);

                    String retailerClass = "";
                    if(jsonObject.has("DrCat"))
                        retailerClass = jsonObject.getString("DrCat");
                    txtClass.setText(retailerClass);

                    String lastVisit = "-";
                    if(jsonObject.has("last_visit_date") && !jsonObject.getString("last_visit_date").equals(""))
                        lastVisit = jsonObject.getString("last_visit_date");
                    txtLastVisited.setText(lastVisit);

                    double total = 0;
                    if(!jsonObject.isNull("MOV")){
                        JSONArray jsonArray = jsonObject.getJSONArray("MOV");
                        for(int i = 0; i< jsonArray.length(); i++){
                            try {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if(!jsonObject1.isNull("MorderSum"))
                                    total += jsonObject1.getDouble("MorderSum");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    txtModelOrderValue.setText(Constant.roundTwoDecimals(total));

                    String scheme = "-";
                    if(jsonObject.has("Slan_Name"))
                        scheme = jsonObject.getString("Slan_Name");
                    tv_sch_enrollment.setText(scheme);

                    String lastOrderAmt = "-";
//                        JSONArray jsonArray = jsonObject.getJSONArray("StockistDetails");
//                        if(jsonArray.length()>0){
                    lastOrderAmt = jsonObject.getString("LastOrderAmt");
                    if(!lastOrderAmt.equals(""))
                        lastOrderAmt = lastOrderAmt.equals("-") ? "-" : Constant.roundTwoDecimals(Double.parseDouble(lastOrderAmt));
                    txtLastOrderAmount.setText(lastOrderAmt);

//                        }

                    String mob1 = "-";
//                        String mob2 = "";
                    if( !jsonObject.isNull("POTENTIAL") && jsonObject.getJSONArray("POTENTIAL").length() > 0){

                        if(jsonObject.getJSONArray("POTENTIAL").getJSONObject(0).getString("ListedDr_Mobile")!=null){
                            mob1 = jsonObject.getJSONArray("POTENTIAL").getJSONObject(0).getString("ListedDr_Mobile");
                        }

                        if((mob1.equals("") || mob1.equals("-") || mob1.equals("null")) && jsonObject.getJSONArray("POTENTIAL").getJSONObject(0).getString("ListedDr_Phone")!=null){
                            mob1 = jsonObject.getJSONArray("POTENTIAL").getJSONObject(0).getString("ListedDr_Phone");
                        }
                    }
                    txtMobile.setText(mob1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                brandSecondaryApi();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Retailer_Details", "Error");
            }
        });
    }

    /*public void getTemplate() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getTemplates(sfCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject JsonObject = response.body();
                try {
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.TEMPLATE_LIST, new Gson().toJson(jsonArray));

                    processTemplateList(jsonArray);
                } catch (Exception io) {
                    io.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void processTemplateList(JsonArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            String className = jsonObject.get("content").getAsString();
            Log.v("JSON_OBJECT_VALUE", className);
            mCommon_model_spinner = new Common_Model(className, className, "flag");
            modelTemplates.add(mCommon_model_spinner);
        }

    }*/


    @Override
    public void onBackPressed() {
        finish();
    }


    public void SaveRetials() {

        JSONObject js = new JSONObject();
        try {
            js.put("Retcode", retailerId);
            js.put("Retname", txtRtNme.getText().toString());
            js.put("Stkcode", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("Stkname", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Name));
            js.put("sfCode", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
            js.put("Divcode", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("Remark", editRemarks.getText().toString());
            js.put("save_order", 1);


            mCommon_class.ProgressdialogShow(1, "");
//            brandSecondaryApi();
            Log.v("JS_VALUE", js.toString());

            if(!Constant.isInternetAvailable(SecondRetailerActivity.this)){
                if(dbController.addDataOfflineCalls(String.valueOf(System.currentTimeMillis()), js.toString(), "dcr/retailervisit", 0)){
                    shared_common_pref.save("RetailerID", retailerId);
                    shared_common_pref.save("RetailName", txtRtNme.getText().toString());
                    shared_common_pref.save("Remarks", editRemarks.getText().toString());
                    shared_common_pref.save("orderType", txtOrder.getText().toString());
                    shared_common_pref.save("OrderType", txtOrder.getText().toString());
                    shared_common_pref.save("RetailerName", txtRtNme.getText().toString());
                    shared_common_pref.save("editRemarks", editRemarks.getText().toString());

                    if(!dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_BRAND).equals("") &&
                            !dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA).equals("")){
                        Log.d("SecondRetailerActivity", "processSecondaryOrderList: " );
                        processSecondaryOrderList(1);
                    }else
                        brandSecondaryApi();

                }
                else
                    Toast.makeText(SecondRetailerActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

            }else {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<JsonObject> call = apiInterface.getDetails("dcr/retailervisit", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), js.toString());

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject JsonObject = response.body();
                        shared_common_pref.save("RetailerID", retailerId);
                        shared_common_pref.save("RetailName", txtRtNme.getText().toString());
                        shared_common_pref.save("Remarks", editRemarks.getText().toString());
                        shared_common_pref.save("orderType", txtOrder.getText().toString());
                        shared_common_pref.save("OrderType", txtOrder.getText().toString());
                        shared_common_pref.save("RetailerName", txtRtNme.getText().toString());
                        shared_common_pref.save("editRemarks", editRemarks.getText().toString());

                        if(!dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_BRAND).equals("") &&
                                !dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA).equals("")){
                            processSecondaryOrderList(1);
                        }else
                            brandSecondaryApi();


                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.v("REPONSE_RETAILER", "JsonObject.toString()");
                        Toast.makeText(SecondRetailerActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

                    }
                });

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void brandSecondaryApi() {

        String tempalteValue = "{\"tableName\":\"sec_category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), sfCode, sfCode, retailerId ,shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue, 2);

        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.body()!=null) {
                    JsonObject jsonObject = response.body();
                    JsonObject jsonArray = jsonObject.getAsJsonObject("Data");
                    JsonArray jBrand = jsonArray.getAsJsonArray("Brand");
                    JsonArray jProd = jsonArray.getAsJsonArray("Products");
                    dbController.updateDataResponse(DBController.SECONDARY_PRODUCT_BRAND, new Gson().toJson(jBrand));
                    dbController.updateDataResponse(DBController.SECONDARY_PRODUCT_DATA, new Gson().toJson(jProd));

                    Log.v("Product_Response",jsonArray.toString());
                        processSecondaryOrderList(0);

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }

    private void processSecondaryOrderList(int loadActivity) {
        mCommon_class.ProgressdialogShow(2, "");
        new PopulateDbAsyntasks(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase(), loadActivity).execute();

/*        SecViewModel = ViewModelProviders.of(SecondRetailerActivity.this).get(SecondaryProductViewModel.class);
        SecViewModel.getAllData().observe(SecondRetailerActivity.this, new Observer<List<SecondaryProduct>>() {
            @Override
            public void onChanged(List<SecondaryProduct> contacts) {

                Integer ProductCount = Integer.valueOf(new Gson().toJson(contacts.size()));

                Log.v("DASH_BOARD_COUNT", String.valueOf(ProductCount));

                if (ProductCount == 0) {
                    new PopulateDbAsyntasks(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();
                }
            }
        });*/

    }

    public void onclickNoOrder(View view) {

        if (txtOrder.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Order", Toast.LENGTH_SHORT).show();
        } else if (txtRtNme.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Name", Toast.LENGTH_SHORT).show();
        } /*else if (editRemarks.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Remarks", Toast.LENGTH_SHORT).show();
        }*/ else {

            noOrderCall();

        }
    }

    private void noOrderCall() {
        JSONObject js = new JSONObject();
        try {
            js.put("Retcode", retailerId);
            js.put("Retname", txtRtNme.getText().toString());
            js.put("Stkcode", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("Stkname", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Name));
            js.put("Divcode", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("sfCode", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
            js.put("Remark", editRemarks.getText().toString());
            js.put("save_order", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        shared_common_pref.save("RetailerID", retailerId);
        shared_common_pref.save("RetailName", txtRtNme.getText().toString());
        shared_common_pref.save("Remarks", editRemarks.getText().toString());
        shared_common_pref.save("orderType", txtOrder.getText().toString());
        shared_common_pref.save("OrderType", txtOrder.getText().toString());
        shared_common_pref.save("RetailerName", txtRtNme.getText().toString());
        shared_common_pref.save("editRemarks", editRemarks.getText().toString());

//            brandSecondaryApi();
        Log.v("JS_VALUE", js.toString());

        if(!Constant.isInternetAvailable(this)){
            if(dbController.addDataOfflineCalls(String.valueOf(System.currentTimeMillis()), js.toString(), "dcr/retailervisit", 0)){
                mCommon_class.ProgressdialogShow(2, "");
                Toast.makeText(DMSApplication.getApplication(), "No Order call will be saved in offline", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
                Toast.makeText(SecondRetailerActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
        }else {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.getDetails("dcr/retailervisit",sfCode, js.toString());

            Log.v("REQUEST_VALUE", call.request().toString());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject JsonObject = response.body();
                    shared_common_pref.save("RetailerID", retailerId);
                    shared_common_pref.save("RetailName", txtRtNme.getText().toString());
                    shared_common_pref.save("Remarks", editRemarks.getText().toString());
                    shared_common_pref.save("orderType", txtOrder.getText().toString());
                    shared_common_pref.save("OrderType", txtOrder.getText().toString());
                    shared_common_pref.save("RetailerName", txtRtNme.getText().toString());
                    shared_common_pref.save("editRemarks", editRemarks.getText().toString());

                    mCommon_class.ProgressdialogShow(2, "");
                    Toast.makeText(DMSApplication.getApplication(), "No Order call Submitted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }


    }


    public class PopulateDbAsyntasks extends AsyncTask<Void, Void, Void> {
        private PrimaryProductDao contactDao;
        private int loadActivity;

        public PopulateDbAsyntasks(PrimaryProductDatabase contactDaos, int loadActivity) {
            contactDao = contactDaos.contactDao();
            this.loadActivity=loadActivity;
        }

       /* public PopulateDbAsyntasks(PrimaryProductDatabase appDatabase) {
        }
*/
        @Override
        protected Void doInBackground(Void... voids) {
            PrimaryProductDatabase.getInstance(SecondRetailerActivity.this).clearAllTables();
            secStart(loadActivity);
            Log.v("Data_CHeckng", "Checking_data");
            return null;
        }

    }


    private void secStart(int loadActivity) {

        Log.v("Data_CHeckng", "Checking_data");


        String sPrimaryProd = dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA);
        PrimaryProductDao contact = PrimaryProductDatabase.getInstance(this).getAppDatabase()
                .contactDao();
        try {
            JSONArray jsonArray = new JSONArray(sPrimaryProd);
            JSONObject jsonObject = null;
            JSONObject jsonObject1 = null;



            String Scheme = "", Discount="", Scheme_Unit="", Product_Name="", Product_Code="", Package="", Free="", Discount_Type="", Free_Unit="";
            int unitQty = 1;
            String goldenScheme = "0";

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);

                String id = String.valueOf(jsonObject.get("id"));
                String Name = String.valueOf(jsonObject.get("name"));
                String PName = String.valueOf(jsonObject.get("Pname"));
                String PRate = String.valueOf(jsonObject.get("Product_Cat_Code"));
                String PBarCode = String.valueOf(jsonObject.get("Product_Brd_Code"));
                String PId = String.valueOf(jsonObject.get("PID"));
                String PUOM = String.valueOf(jsonObject.get("UOM"));
                String PSaleUnit = String.valueOf(jsonObject.get("Default_UOM"));
                String PDiscount = String.valueOf(jsonObject.get("Discount"));
                String PTaxValue = String.valueOf(jsonObject.get("Tax_value"));
                if(jsonObject.has("Conv_Fac"))
                    unitQty = jsonObject.getInt("Conv_Fac");

                if(jsonObject.has("Slan_Name"))
                    goldenScheme = jsonObject.getString("Slan_Name");

//                String PCon_fac = "1";
//                if(jsonObject.has("Conv_Fac"))
//                    PCon_fac = jsonObject.getString("Conv_Fac");

                JSONArray jsonArray1 = jsonObject.getJSONArray("SchemeArr");
                JSONArray uomArray = null;
                if(jsonObject.has("UOMList"))
                    uomArray = jsonObject.getJSONArray("UOMList");

                List<PrimaryProduct.SchemeProducts> schemeList = new ArrayList<>();

                for (int j = 0; j < jsonArray1.length(); j++) {
                    try {
                        jsonObject1 = jsonArray1.getJSONObject(j);
                        Scheme = String.valueOf(jsonObject1.get("Scheme"));
                        Discount = String.valueOf(jsonObject1.get("Discount"));
                        Scheme_Unit = String.valueOf(jsonObject1.get("Scheme_Unit"));
                        Product_Name = String.valueOf(jsonObject1.get("Offer_Product_Name"));
                        Product_Code = String.valueOf(jsonObject1.get("Offer_Product"));
                        Package = String.valueOf(jsonObject1.get("Package"));
                        Free = String.valueOf(jsonObject1.get("Free"));
                        if(jsonObject1.has("Discount_Type"))
                            Discount_Type = String.valueOf(jsonObject1.get("Discount_Type"));

                        if(jsonObject1.has("Free_Unit"))
                            Free_Unit = String.valueOf(jsonObject1.get("Free_Unit"));


                        Log.v("JSON_Array_SCHEMA",Scheme);
                        Log.v("JSON_Array_DIS",Discount);
                        schemeList.add(new PrimaryProduct.SchemeProducts(Scheme,Discount,Scheme_Unit,Product_Name,
                                Product_Code, Package, Free, Discount_Type,Free_Unit ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                ArrayList<PrimaryProduct.UOMlist> uomList = new ArrayList<>();

                if(uomArray!=null)
                    for (int j = 0; j < uomArray.length(); j++) {
                        try {
                            JSONObject uomObject = uomArray.getJSONObject(j);
                            String uomId = "", uomProduct_Code = "", uomName = "", uomConQty = "";

                            if(uomObject.has("id"))
                                uomId = uomObject.getString("id");

                            if(uomObject.has("name"))
                                uomName = uomObject.getString("name");

                            if(uomObject.has("ConQty"))
                                uomConQty = uomObject.getString("ConQty");

                            uomList.add(new PrimaryProduct.UOMlist(uomId, uomProduct_Code, uomName, uomConQty));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                contact.insert(new PrimaryProduct(id, PId, Name, PName, PBarCode, PUOM, PRate,
                        PSaleUnit, PDiscount, PTaxValue, "0", "0", "0", "0", "0",
                        schemeList,unitQty, uomList, goldenScheme));

             /*   contact.insert(new PrimaryProduct(id, PId, Name, PName, PBarCode, PUOM, PRate,
                        PSaleUnit, PDiscount, PTaxValue, "0", "0", "0", "0", "0", PCon_fac));
         */   }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        mCommon_class.ProgressdialogShow(2, "");
//        startActivity(new Intent(SecondRetailerActivity.this, SecondaryOrderProducts.class));
        if(loadActivity==1){
            Intent dashIntent = new Intent(getApplicationContext(), PrimaryOrderProducts.class);
            dashIntent.putExtra("Mode", "0");
            dashIntent.putExtra("order_type", 2);
            dashIntent.putExtra("PhoneOrderTypes", PhoneOrderTypes);
            startActivityForResult(dashIntent, ACTIVITY_REQUEST_CODE);
//        startActivity(dashIntent);
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        if(shared_common_pref.getBooleanvalue(Shared_Common_Pref.YET_TO_SYN) || !dbController.getResponseFromKey(DBController.RETAILER_LIST).equals("")){
            processRetailerList(new Gson().fromJson(dbController.getResponseFromKey(DBController.RETAILER_LIST), JsonArray.class));
        }else {

            if(Constant.isInternetAvailable(this))
                RetailerType();
            else
                Toast.makeText(this, "Empty retailer list, please sync it", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if(requestCode == ACTIVITY_REQUEST_CODE) {

                boolean closeActivity = false;
                if(data!=null && data.hasExtra("closeActivity"))
                    closeActivity = data.getBooleanExtra("closeActivity", false);

                if(closeActivity)
                    finish();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listOrderType = null;
        RetailerType = null;
        modelTemplates = null;
        oderTypeList = null;
        mCommon_model_spinner = null;
        customDialog9 = null;
        customDialog123 = null;
        customDialog10 = null;
        linRtDetails = null;
        shared_common_pref = null;
        editRemarks = null;
        retailerId = null;
        locationManager = null;
        latitude = null;
        longitude = null;
        mCommon_class = null;
        txtRetailerChannel = null;
        txtClass = null;
        txtLastOrderAmount = null;
        txtModelOrderValue = null;
        txtLastVisited = null;
        txtMobile = null;
        dbController = null;
        tv_sch_enrollment = null;
        sfCode = null;
    }
}