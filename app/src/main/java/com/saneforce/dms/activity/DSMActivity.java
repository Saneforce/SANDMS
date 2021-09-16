package com.saneforce.dms.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Constants;
import com.saneforce.dms.utils.Shared_Common_Pref;
import com.saneforce.dms.sqlite.DBController;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DSMActivity extends AppCompatActivity {

    private static final String TAG =DSMActivity.class.getSimpleName();

    Shared_Common_Pref shared_common_pref;
    ImageView ib_logout;

    LinearLayout ll_secondary_order;
    LinearLayout ll_financial;

    DBController dbController;
    boolean syncData = false;
    Common_Class mCommon_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsm);

        initViews();
    }

    private void initViews() {
        shared_common_pref = new Shared_Common_Pref(this);
        ib_logout = findViewById(R.id.ib_logout);
        ll_secondary_order = findViewById(R.id.ll_secondary_order);
        ll_financial = findViewById(R.id.ll_financial);
        dbController = new DBController(this);
        mCommon_class = new Common_Class(this);

        ib_logout.setVisibility(View.VISIBLE);
        TextView toolbar_title = findViewById(R.id.toolbar_title);;
        toolbar_title.setText("SAN DSM");


        if(getIntent().hasExtra("syncData"))
            syncData = getIntent().getBooleanExtra("syncData", false);


        ImageView toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ib_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbController.clearDatabase(DBController.TABLE_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                shared_common_pref.logoutUser(DSMActivity.this);
            }
        });

        ll_secondary_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(DSMActivity.this, SecondRetailerActivity.class);
                intent.putExtra("isDSM", true);
                startActivity(intent);
            }
        });


        ll_financial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        }
        });



        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);


    }


    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Constants.isInternetAvailable(DashBoardActivity.this) "+ Constants.isInternetAvailable(DSMActivity.this));
            if (Constants.isInternetAvailable(DSMActivity.this)) {
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


    public void brandPrimaryApi(boolean isUpdateOffline) {

        String tempalteValue = "{\"tableName\":\"category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue);

        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();
                JsonObject jsonArray = jsonObject.getAsJsonObject("Data");
                JsonArray jBrand = jsonArray.getAsJsonArray("Brand");
                JsonArray jProd = jsonArray.getAsJsonArray("Products");
                dbController.updateDataResponse(DBController.PRIMARY_PRODUCT_BRAND, new Gson().toJson(jBrand));
                dbController.updateDataResponse(DBController.PRIMARY_PRODUCT_DATA, new Gson().toJson(jProd));


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }


    public void brandSecondaryApi() {

        String tempalteValue = "{\"tableName\":\"sec_category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue);

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
        Call<JsonObject> call = apiInterface.getRetName(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), RetailerDetails);
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
        Call<JsonObject> call = apiInterface.getTemplates(shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
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
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);

        Log.v("KArthic_Retailer", call.request().toString());
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
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
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
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}