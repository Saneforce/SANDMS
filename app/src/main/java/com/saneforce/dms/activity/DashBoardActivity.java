package com.saneforce.dms.activity;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.saneforce.dms.R;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.PrimaryProductDao;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.PrimaryProductDatabase;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends AppCompatActivity {
    private static final String TAG =DashBoardActivity.class.getSimpleName();
    Intent dashIntent;
    TextView txtName, txtAddress;
    Shared_Common_Pref shared_common_pref;
    Gson gson;
    Common_Class mCommon_class;
    ImageView imagView,profilePic, ib_logout;
    //    PrimaryProductViewModel mPrimaryProductViewModel;
    RelativeLayout profileLayout;

    DBController dbController;
    boolean syncData = false;


/*
    TextClock tc_current_time;
    LinearLayout ll_cut_off;
    TextView tv_cut_off_time;
    String cutOffTime;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mCommon_class = new Common_Class(this);
        shared_common_pref = new Shared_Common_Pref(this);
        dbController = new DBController(this);
        gson = new Gson();
        txtName = findViewById(R.id.dis_name);
        txtAddress = findViewById(R.id.dis_place);
        profilePic=findViewById(R.id.profileImg);
        profileLayout=findViewById(R.id.imageLayout);
        txtName.setText(shared_common_pref.getvalue(Shared_Common_Pref.name) + " ~ " + shared_common_pref.getvalue(Shared_Common_Pref.Sf_UserName));
        // brandProdutApi();

        if(getIntent().hasExtra("syncData"))
            syncData = getIntent().getBooleanExtra("syncData", false);

        txtAddress.setText(shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Address));
        imagView = findViewById(R.id.toolbar_back);
        ib_logout = findViewById(R.id.ib_logout);

/*        tc_current_time = findViewById(R.id.tc_current_time);
        ll_cut_off = findViewById(R.id.ll_cut_off);
        tv_cut_off_time = findViewById(R.id.tv_cut_off_time);
        cutOffTime ="10:00 pm";
        tc_current_time.setFormat24Hour("yyyy-MM-dd HH:mm");
        tv_cut_off_time.setText(cutOffTime);*/

        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });

        ib_logout.setVisibility(View.VISIBLE);
        ib_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MyLocationWorker.stopLocationTracker();
                dbController.clearDatabase(DBController.TABLE_NAME);
//                dbController.clearDatabase(DBController.TABLE_LOCATION);
                shared_common_pref.logoutUser(DashBoardActivity.this);
            }
        });
        LinearLayout llProfile = findViewById(R.id.ll_profile);
        if(ApiClient.APP_TYPE == 1)
            llProfile.setVisibility(View.GONE);
        else
            llProfile.setVisibility(View.VISIBLE);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

        checkPermission();

       /* try {
            Constant.checkOptimizationDialog(DashBoardActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
//            startDialog(KillerManager.Actions.ACTION_AUTOSTART);
//            startDialog(KillerManager.Actions.ACTION_POWERSAVING);

    }
    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Constant.isInternetAvailable(DashBoardActivity.this)) {
                Log.d(TAG, "Network Available ");

                // Do something
//                displayNotification("Connectivity", "Available");

                if(dbController==null)
                    dbController = new DBController(context);
                if(!Common_Class.isOnProgress)
                    mCommon_class.checkData(dbController,context);

                if(syncData || dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_BRAND).equals("")){
                    syncData = false;
                    brandPrimaryApi(true);
                }
                if(syncData || dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_BRAND).equals("")){
                    syncData = false;
                    brandSecondaryApi();
                }
                if(syncData || shared_common_pref.getBooleanvalue(Shared_Common_Pref.YET_TO_SYN) || dbController.getResponseFromKey(DBController.RETAILER_LIST).equals("")){
                    syncData = false;
                    RetailerType();
                }

              /* if(syncData || dbController.getResponseFromKey(DBController.TEMPLATE_LIST).equals("")){
                    syncData = false;
                    getTemplate();
                }*/
                if(syncData || dbController.getResponseFromKey(DBController.ROUTE_LIST).equals("")){
                    syncData = false;
                    getRouteDetails();
                }
                if(syncData || dbController.getResponseFromKey(DBController.CLASS_LIST).equals("")){
                    syncData = false;
                    getRouteClass();
                }
                if(syncData || dbController.getResponseFromKey(DBController.CHANNEL_LIST).equals("")){
                    syncData = false;
                    getRouteChannel();
                }
            }
        }
    };

    public void brandSecondaryApi() {

        String tempalteValue = "{\"tableName\":\"sec_category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code),"", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue, 2);

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

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }


    public void RetailerType() {
        String RetailerDetails = "{\"tableName\":\"vwDoctor_Master_APP\",\"coloumns\":\"[\\\"doctor_code as id\\\", \\\"doctor_name as name\\\",\\\"town_code\\\",\\\"town_name\\\",\\\"lat\\\",\\\"long\\\",\\\"addrs\\\",\\\"ListedDr_Address1\\\",\\\"ListedDr_Sl_No\\\",\\\"Mobile_Number\\\",\\\"Doc_cat_code\\\",\\\"ContactPersion\\\",\\\"Doc_Special_Code\\\",\\\"Slan_Name\\\"]\",\"where\":\"[\\\"isnull(Doctor_Active_flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getRetName(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), RetailerDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject JsonObject = response.body();
                try {
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.RETAILER_LIST, new Gson().toJson(jsonArray));
                    shared_common_pref.save(Shared_Common_Pref.YET_TO_SYN, false);
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

  /*  public void getTemplate() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getTemplates(shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject JsonObject = response.body();
                try {
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.TEMPLATE_LIST, new Gson().toJson(jsonArray));

                } catch (Exception io) {
                    io.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
*/

    public void getRouteDetails() {
        String routeMap = "{\"tableName\":\"vwTown_Master_APP\",\"coloumns\":\"[\\\"town_code as id\\\", \\\"town_name as name\\\",\\\"target\\\",\\\"min_prod\\\",\\\"field_code\\\",\\\"stockist_code\\\"]\",\"where\":\"[\\\"isnull(Town_Activation_Flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonRootObject = response.body();

                    JsonArray jsonArray = jsonRootObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.ROUTE_LIST, new Gson().toJson(jsonArray));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
                t.printStackTrace();
            }
        });
    }


    public void getRouteClass() {
        String routeMap = "{\"tableName\":\"Mas_Doc_Class\",\"coloumns\":\"[\\\"Doc_ClsCode as id\\\", \\\"Doc_ClsSName as name\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonRootObject = response.body();

                    JsonArray jsonArray = jsonRootObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.CLASS_LIST, new Gson().toJson(jsonArray));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
                t.printStackTrace();
            }
        });
    }

    public void getRouteChannel() {
        String routeMap = "{\"tableName\":\"Doctor_Specialty\",\"coloumns\":\"[\\\"Specialty_Code as id\\\", \\\"Specialty_Name as name\\\"]\",\"where\":\"[\\\"isnull(Deactivate_flag,0)=0\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    JsonObject jsonRootObject = response.body();

                    JsonArray jsonArray = jsonRootObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.CHANNEL_LIST, new Gson().toJson(jsonArray));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
                t.printStackTrace();
            }
        });
    }

    private class PopulateDbAsyntask extends AsyncTask<Void, Void, Void> {
        private PrimaryProductDao contactDao;
        public PopulateDbAsyntask(PrimaryProductDatabase contactDaos)
        {
            contactDao = contactDaos.contactDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            PrimaryProductDatabase.getInstance(DashBoardActivity.this).clearAllTables();
            fillingWithStart();
//            Log.v("Data_CHeckng", "Checking_data");
            return null;
        }
    }

    private void fillingWithStart() {
//        Log.v("Data_CHeckng", "Checking_data");

        String sPrimaryProd = dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_DATA);
//        Shared_Common_Pref mShared_common_pref = new Shared_Common_Pref(this);
        PrimaryProductDao contact = PrimaryProductDatabase.getInstance(this).getAppDatabase()
                .contactDao();

        try {
            JSONArray jsonArray = new JSONArray(sPrimaryProd);


            String Scheme = "", Discount="", Scheme_Unit="", Product_Name="", Product_Code="", Package="", Free="", Discount_Type="", Free_Unit="";
            int unitQty = 1;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
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
                String goldenScheme = "0";
//                String PCon_fac = "1";
//                if(jsonObject.has("Conv_Fac"))
//                    PCon_fac = jsonObject.getString("Conv_Fac");
                if(jsonObject.has("Conv_Fac"))
                    unitQty = jsonObject.getInt("Conv_Fac");

                if(jsonObject.has("Slan_Name"))
                    goldenScheme = jsonObject.getString("Slan_Name");
//                Log.v("PCon_facPCon_fac", PBarCode);
                JSONArray jsonArray1 = jsonObject.getJSONArray("SchemeArr");
                JSONArray uomArray = null;
                if(jsonObject.has("UOMList"))
                    uomArray = jsonObject.getJSONArray("UOMList");

                List<PrimaryProduct.SchemeProducts> schemeList = new ArrayList<>();

                for (int j = 0; j < jsonArray1.length(); j++) {
                    try {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        Scheme = String.valueOf(jsonObject1.get("Scheme"));
                        Discount = String.valueOf(jsonObject1.get("Discount"));
                        Scheme_Unit = String.valueOf(jsonObject1.get("Scheme_Unit"));
                        Product_Name = String.valueOf(jsonObject1.get("Offer_Product_Name"));
                        Product_Code = String.valueOf(jsonObject1.get("Offer_Product"));
                        Package = String.valueOf(jsonObject1.get("Package"));
                        Free = String.valueOf(jsonObject1.get("Free"));
                        if(jsonObject1.has("Discount_Type"))
                            Discount_Type = jsonObject1.getString("Discount_Type");

                        if(jsonObject1.has("Free_Unit"))
                            Free_Unit = jsonObject1.getString("Free_Unit");


                        Log.v("JSON_Array_SCHEMA",Scheme);
                        Log.v("JSON_Array_DIS",Discount);
                        schemeList.add(new PrimaryProduct.SchemeProducts(Scheme,Discount,Scheme_Unit,Product_Name,
                                Product_Code, Package, Free, Discount_Type, Free_Unit));
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        dashIntent = new Intent(getApplicationContext(), PrimaryOrderProducts.class);
        dashIntent.putExtra("Mode", "0");
        dashIntent.putExtra("order_type", 1);
        dashIntent.putExtra("PhoneOrderTypes", 4);
        startActivity(dashIntent);
//        finish();
        mCommon_class.ProgressdialogShow(2, "");
    }
    public void PrimaryOrder(View v) {

//        if(!checkCutOffTime(cutOffTime)){
        mCommon_class.ProgressdialogShow(1, "");

        if(!dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_BRAND).equals("") &&
                !dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_DATA).equals("")){
            processPrimaryData();
        }else
            brandPrimaryApi(false);
//        }else
//            Toast.makeText(DashBoardActivity.this, "Cut off time is over, please try again later", Toast.LENGTH_SHORT).show();
    }

    private boolean checkCutOffTime(String cutOffTime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date date1=new Date();

        //            date1 = simpleDateFormat.parse(cutOffTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        date1 = cal.getTime();
        Date date2 = Calendar.getInstance().getTime();
        int isCutOffGreater = 1;
        isCutOffGreater = date1.compareTo(date2);
        Log.d(TAG, "checkCutOffTime: isCutOffGreater "+ isCutOffGreater);
        return isCutOffGreater<=0;
    }

    public void SecondaryOrder(View v) {
//        if(!checkCutOffTime(cutOffTime)) {
        startActivity(new Intent(getApplicationContext(), SecondRetailerActivity.class));
//        }else
//            Toast.makeText(DashBoardActivity.this, "Cut off time is over, please try again later", Toast.LENGTH_SHORT).show();
    }

    public void CounterOrder(View v) {
        startActivity(new Intent(getApplicationContext(), CounterSaleActivity.class));
    }
    public void ProfileImage(View v) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }
    public void MyOrder(View v) {
        startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
    }
    public void ReportData(View v) {
        startActivity(new Intent(getApplicationContext(), ReportDashBoard.class));
    }
    public void Payment(View v) {
        startActivity(new Intent(getApplicationContext(), PaymentDashAcitivity.class));
    }

    public void onclickOutbox(View view) {
        startActivity(new Intent(getApplicationContext(), OutboxActivity.class));
    }

    public void Company(View v) {
        Intent intent =new Intent(getApplicationContext(), CompanyProfile.class);
        intent.putExtra("fileName", "CompanyProfile.html");
        startActivity(intent);
    }
    public void PrivacyPolicy(View v) {
        Intent intent =new Intent(getApplicationContext(), CompanyProfile.class);
        intent.putExtra("fileName", "PrivacyPolicy.html");
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {

    }



    public void brandPrimaryApi(boolean isUpdateOffline) {

        String tempalteValue = "{\"tableName\":\"category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code),"", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue, 1);


        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.body()!=null ){
                    JsonObject jsonObject = response.body();
                    JsonObject jsonArray = jsonObject.getAsJsonObject("Data");
                    JsonArray jBrand = jsonArray.getAsJsonArray("Brand");
                    JsonArray jProd = jsonArray.getAsJsonArray("Products");
                    dbController.updateDataResponse(DBController.PRIMARY_PRODUCT_BRAND, new Gson().toJson(jBrand));
                    dbController.updateDataResponse(DBController.PRIMARY_PRODUCT_DATA, new Gson().toJson(jProd));

                    Log.v("Product_Response", jsonArray.toString());
                    if(!isUpdateOffline)
                        processPrimaryData();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }

    private void processPrimaryData() {
        new PopulateDbAsyntask(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();

/*
        mPrimaryProductViewModel = ViewModelProviders.of(DashBoardActivity.this).get(PrimaryProductViewModel.class);
        mPrimaryProductViewModel.getAllData().observe(DashBoardActivity.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {


                Integer ProductCount = Integer.valueOf(new Gson().toJson(contacts.size()));
//
                Log.v("DASH_BOARD_COUNT", String.valueOf(ProductCount));
//
//                if (ProductCount == 0) {
//                }
            }
        });

*/

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        unregisterReceiver(networkChangeReceiver);
    }



    public void checkPermission(){
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }*/
        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if(!report.areAllPermissionsGranted()){
                            Constant.showSnackbar(DashBoardActivity.this, findViewById(R.id.scrolllayout));
                        }/*else
                    MyLocationWorker.createMyLocationWorker(DashBoardActivity.this);
*/

//                    Toast.makeText(ViewReportActivity.this, "Please enable storage permission to share pdf ", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dashIntent = null;
        txtName = null;
        txtAddress = null;
        shared_common_pref = null;
        gson = null;
        mCommon_class = null;
        imagView = null;
        profilePic = null;
        ib_logout = null;
        profileLayout = null;
        dbController = null;
    }
}
