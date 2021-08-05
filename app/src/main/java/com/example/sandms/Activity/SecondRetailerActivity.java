package com.example.sandms.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.Interface.SecProductDao;
import com.example.sandms.Model.SecondaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.Common_Model;
import com.example.sandms.Utils.Constants;
import com.example.sandms.Utils.CustomListViewDialog;
import com.example.sandms.Utils.SecondaryProductDatabase;
import com.example.sandms.Utils.SecondaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.example.sandms.sqlite.DBController;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondRetailerActivity extends AppCompatActivity implements DMS.Master_Interface {

    List<Common_Model> listOrderType = new ArrayList<>();
    List<Common_Model> RetailerType = new ArrayList<>();
    List<Common_Model> modelTemplates = new ArrayList<>();
    ArrayList<String> oderTypeList;
    Common_Model mCommon_model_spinner;
    CustomListViewDialog customDialog;
    TextView txtOrder, txtRtNme, txtRtAdd;
    LinearLayout linRtDetails;
    Shared_Common_Pref shared_common_pref;
    EditText editRemarks;
    String retailerId = "";
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String latitude, longitude;
    Common_Class mCommon_class;
    TextView txtRetailerChannel, txtClass, txtLastOrderAmount, txtModelOrderValue, txtLastVisited, txtReamrks, txtMobile, txtMobileTwo, txtDistributor;
    SecondaryProductViewModel SecViewModel;
    DBController dbController;
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
        txtReamrks = findViewById(R.id.txt_remarks);
        txtMobile = findViewById(R.id.txt_mobile);
        txtMobileTwo = findViewById(R.id.txt_mobile2);
        txtDistributor = findViewById(R.id.txt_distributor);
        shared_common_pref = new Shared_Common_Pref(this);
        mCommon_class = new Common_Class(this);
        dbController = new DBController(this);

/*
        if(!dbController.getResponseFromKey(DBController.TEMPLATE_LIST).equals("")){
            processTemplateList(new Gson().fromJson(dbController.getResponseFromKey(DBController.TEMPLATE_LIST), JsonArray.class));
        }else {
            if(Constants.isInternetAvailable(this))
                getTemplate();
//            else
//                Toast.makeText(this, "Please check the internet connection", Toast.LENGTH_SHORT).show();
        }*/

        ImageView imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SecondRetailerActivity.this, DashBoardActivity.class));
                onBackPressed();
            }
        });

        Log.v("SHARED_PREFERNCE", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    public void OrderType(View v) {
        listOrderType.clear();
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

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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
    }

    public void ReatilerName(View v) {
        RetailerDetails();
    }

    public void TemplatesValue(View v) {
        customDialog = new CustomListViewDialog(SecondRetailerActivity.this, modelTemplates, 123);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    public void OrderType() {
        oderTypeList = new ArrayList<>();
        oderTypeList.add("Phone Order");
        oderTypeList.add("Field Order");
        for (int i = 0; i < oderTypeList.size(); i++) {
            String id = String.valueOf(oderTypeList.get(i));
            String name = oderTypeList.get(i);
            mCommon_model_spinner = new Common_Model(id, name, "flag");
            listOrderType.add(mCommon_model_spinner);
        }
        customDialog = new CustomListViewDialog(SecondRetailerActivity.this, listOrderType, 9);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    public void AddRetailer(View v) {
        startActivity(new Intent(SecondRetailerActivity.this, AddNewRetailer.class));
    }

    public void RetailerDetails() {
        customDialog = new CustomListViewDialog(SecondRetailerActivity.this, RetailerType, 10);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    public void RetailerType() {
        String RetailerDetails = "{\"tableName\":\"vwDoctor_Master_APP\",\"coloumns\":\"[\\\"doctor_code as id\\\", \\\"doctor_name as name\\\",\\\"town_code\\\",\\\"town_name\\\",\\\"lat\\\",\\\"long\\\",\\\"addrs\\\",\\\"ListedDr_Address1\\\",\\\"ListedDr_Sl_No\\\",\\\"Mobile_Number\\\",\\\"Doc_cat_code\\\",\\\"ContactPersion\\\",\\\"Doc_Special_Code\\\"]\",\"where\":\"[\\\"isnull(Doctor_Active_flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getRetName(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", RetailerDetails);
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
                mCommon_model_spinner = new Common_Model(name, id, "flag", townName, phone);
                RetailerType.add(mCommon_model_spinner);
            }

            dbController.updateDataResponse(DBController.RETAILER_LIST, new Gson().toJson(jsonArray));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 9) {
            txtOrder.setText(myDataset.get(position).getName());
        } else if (type == 10) {
            txtRtNme.setText(myDataset.get(position).getName());
            linRtDetails.setVisibility(View.VISIBLE);
            retailerId = myDataset.get(position).getId();
            RetailerViewDetailsMethod(retailerId);
        } else if (type == 123) {
            editRemarks.setText(myDataset.get(position).getName());
            editRemarks.setSelection(editRemarks.getText().toString().length());
        }
    }

    public void SaveSecndry(View v) {
        if (txtOrder.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Order", Toast.LENGTH_SHORT).show();
        } else if (txtRtNme.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Name", Toast.LENGTH_SHORT).show();
        } /*else if (editRemarks.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Retailer Remarks", Toast.LENGTH_SHORT).show();
        }*/ else {

            SaveRetials();

      }
    }

    public void RetailerViewDetailsMethod(String retailerID) {
        ApiInterface apiInterface2 = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface2.retailerViewDetails(retailerID, shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));

        Log.v("Retailer_Details_req", call.request().toString());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                if(jsonObject!=null){
                    Log.v("Retailer_Details", jsonObject.toString());
                    if(jsonObject.has("DrSpl"))
                    txtRetailerChannel.setText(jsonObject.get("DrSpl").getAsString());
                    if(jsonObject.has("DrCat"))
                    txtClass.setText(jsonObject.get("DrCat").getAsString());

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Retailer_Details", "Error");
            }
        });
    }

    /*public void getTemplate() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getTemplates(shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
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
    }*/

    private void processTemplateList(JsonArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            String className = jsonObject.get("content").getAsString();
            Log.v("JSON_OBJECT_VALUE", className);
            mCommon_model_spinner = new Common_Model(className, className, "flag");
            modelTemplates.add(mCommon_model_spinner);
        }

    }


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
            js.put("Divcode", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("Remark", editRemarks.getText().toString());

            shared_common_pref.save("RetailerID", retailerId);
            shared_common_pref.save("RetailName", txtRtNme.getText().toString());
            shared_common_pref.save("Remarks", editRemarks.getText().toString());
            shared_common_pref.save("orderType", txtOrder.getText().toString());
            shared_common_pref.save("OrderType", txtOrder.getText().toString());
            shared_common_pref.save("RetailerName", txtRtNme.getText().toString());
            shared_common_pref.save("editRemarks", editRemarks.getText().toString());

            mCommon_class.ProgressdialogShow(1, "");
//            brandSecondaryApi();
            Log.v("JS_VALUE", js.toString());


            dbController = new DBController(SecondRetailerActivity.this);
            if(dbController.addDataOfflineCalls(String.valueOf(System.currentTimeMillis()), js.toString(), "dcr/retailervisit", 0)){

                if(!dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_BRAND).equals("") &&
                        !dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA).equals("")){
                    processSecondaryOrderList();
                }else
                    brandSecondaryApi();

            }
            else
                Toast.makeText(SecondRetailerActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

/*
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.getDetails("dcr/retailervisit", js.toString());

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

                    mCommon_class.ProgressdialogShow(1, "");
                    brandSecondaryApi();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.v("REPONSE_RETAILER", "JsonObject.toString()");
                }
            });*/

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void brandSecondaryApi() {

        String tempalteValue = "{\"tableName\":\"sec_category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "15", tempalteValue);

        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();
                JsonObject jsonArray = jsonObject.getAsJsonObject("Data");
                JsonArray jBrand = jsonArray.getAsJsonArray("Brand");
                JsonArray jProd = jsonArray.getAsJsonArray("Products");
                dbController.updateDataResponse(DBController.SECONDARY_PRODUCT_BRAND, new Gson().toJson(jBrand));
                dbController.updateDataResponse(DBController.SECONDARY_PRODUCT_DATA, new Gson().toJson(jProd));
                Log.v("Product_Response", jsonArray.toString());

                processSecondaryOrderList();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }

    private void processSecondaryOrderList() {
        SecViewModel = ViewModelProviders.of(SecondRetailerActivity.this).get(SecondaryProductViewModel.class);
        SecViewModel.getAllData().observe(SecondRetailerActivity.this, new Observer<List<SecondaryProduct>>() {
            @Override
            public void onChanged(List<SecondaryProduct> contacts) {

                Integer ProductCount = Integer.valueOf(new Gson().toJson(contacts.size()));

                Log.v("DASH_BOARD_COUNT", String.valueOf(ProductCount));

                if (ProductCount == 0) {
                    new PopulateDbAsyntasks(SecondaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();
                }
            }
        });

        mCommon_class.ProgressdialogShow(2, "");
        startActivity(new Intent(SecondRetailerActivity.this, SecondaryOrderProducts.class));

    }


    private class PopulateDbAsyntasks extends AsyncTask<Void, Void, Void> {
        private SecProductDao contactDao;


        public PopulateDbAsyntasks(SecondaryProductDatabase contactDaos) {
            contactDao = contactDaos.contactDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            secStart();
            Log.v("Data_CHeckng", "Checking_data");
            return null;
        }

    }


    private void secStart() {

        Log.v("Data_CHeckng", "Checking_data");


        String sPrimaryProd = dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA);
        SecProductDao contact = SecondaryProductDatabase.getInstance(this).getAppDatabase().contactDao();
        try {
            JSONArray jsonArray = new JSONArray(sPrimaryProd);
            JSONObject jsonObject = null;
            JSONObject jsonObject1 = null;



            String Scheme = "", Discount="", Scheme_Unit="", Product_Name="", Product_Code="", Package="", Free="", Discount_Type="";
            int unitQty = 1;

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);

                String id = String.valueOf(jsonObject.get("id"));
                String Name = String.valueOf(jsonObject.get("name"));
                String PName = String.valueOf(jsonObject.get("Pname"));
                String PRate = String.valueOf(jsonObject.get("Product_Cat_Code"));
                String PBarCode = String.valueOf(jsonObject.get("Product_Brd_Code"));
                String PId = String.valueOf(jsonObject.get("PID"));
                String PUOM = String.valueOf(jsonObject.get("UOM"));
                String PSaleUnit = String.valueOf(jsonObject.get("product_unit"));
                String PDiscount = String.valueOf(jsonObject.get("Discount"));
                String PTaxValue = String.valueOf(jsonObject.get("Tax_value"));
                String PCon_fac = String.valueOf(jsonObject.get("Conv_Fac"));

                JSONArray jsonArray1 = jsonObject.getJSONArray("SchemeArr");
                JSONArray uomArray = null;
                if(jsonObject.has("UOMList"))
                    uomArray = jsonObject.getJSONArray("UOMList");

                List<SecondaryProduct.SchemeProducts> schemeList = new ArrayList<>();

                for (int j = 0; j < jsonArray1.length(); j++) {
                    try {
                    jsonObject1 = jsonArray1.getJSONObject(j);
                    Scheme = String.valueOf(jsonObject1.get("Scheme"));
                    Discount = String.valueOf(jsonObject1.get("Discount"));
                    Scheme_Unit = String.valueOf(jsonObject1.get("Scheme_Unit"));
                    Product_Name = String.valueOf(jsonObject1.get("Product_Name"));
                    Product_Code = String.valueOf(jsonObject1.get("Product_Code"));
                    Package = String.valueOf(jsonObject1.get("Package"));
                    Free = String.valueOf(jsonObject1.get("Free"));
                    if(jsonObject1.has("Discount_Type"))
                    Discount_Type = String.valueOf(jsonObject1.get("Discount_Type"));


                    Log.v("JSON_Array_SCHEMA",Scheme);
                    Log.v("JSON_Array_DIS",Discount);
                    schemeList.add(new SecondaryProduct.SchemeProducts(Scheme,Discount,Scheme_Unit,Product_Name,
                            Product_Code, Package, Free, Discount_Type));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                ArrayList<SecondaryProduct.UOMlist> uomList = new ArrayList<>();

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

                            uomList.add(new SecondaryProduct.UOMlist(uomId, uomProduct_Code, uomName, uomConQty));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                contact.insert(new SecondaryProduct(id, PId, Name, PName, PBarCode, PUOM, PRate,
                        PSaleUnit, PDiscount, PTaxValue, "0", "0", "0", "0", "0",
                        PCon_fac,schemeList,unitQty, uomList));

             /*   contact.insert(new SecondaryProduct(id, PId, Name, PName, PBarCode, PUOM, PRate,
                        PSaleUnit, PDiscount, PTaxValue, "0", "0", "0", "0", "0", PCon_fac));
         */   }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shared_common_pref.getBooleanvalue(Shared_Common_Pref.YET_TO_SYN) || !dbController.getResponseFromKey(DBController.RETAILER_LIST).equals("")){
            processRetailerList(new Gson().fromJson(dbController.getResponseFromKey(DBController.RETAILER_LIST), JsonArray.class));
        }else {
            if(Constants.isInternetAvailable(this))
                RetailerType();
            else
                Toast.makeText(this, "Empty retailer list, please sync it", Toast.LENGTH_SHORT).show();
        }
    }
}