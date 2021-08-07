package com.saneforce.dms.Activity;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.Interface.PrimaryProductDao;
import com.saneforce.dms.Model.HeaderCat;
import com.saneforce.dms.Model.PrimaryProduct;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.Common_Class;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.PrimaryProductDatabase;
import com.saneforce.dms.Utils.PrimaryProductViewModel;
import com.saneforce.dms.Utils.Shared_Common_Pref;
import com.saneforce.dms.sqlite.DBController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    PrimaryProductViewModel mPrimaryProductViewModel;
    RelativeLayout profileLayout;

    DBController dbController;
    boolean syncData = false;

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
                shared_common_pref.logoutUser(DashBoardActivity.this);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

    }

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Constants.isInternetAvailable(DashBoardActivity.this) "+ Constants.isInternetAvailable(DashBoardActivity.this));
            if (Constants.isInternetAvailable(DashBoardActivity.this)) {
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

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
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
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);

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
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);
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
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", routeMap);
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
        public PopulateDbAsyntask(PrimaryProductDatabase contactDaos) { contactDao = contactDaos.contactDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            PrimaryProductDatabase.getInstance(DashBoardActivity.this).clearAllTables();
            fillingWithStart();
            Log.v("Data_CHeckng", "Checking_data");
            return null;
        }
    }

    private void fillingWithStart() {
        Log.v("Data_CHeckng", "Checking_data");

        String sPrimaryProd = dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_DATA);
//        Shared_Common_Pref mShared_common_pref = new Shared_Common_Pref(this);
        PrimaryProductDao contact = PrimaryProductDatabase.getInstance(this).getAppDatabase()
                .contactDao();

        try {
            JSONArray jsonArray = new JSONArray(sPrimaryProd);
            JSONObject jsonObject = null;
            JSONObject jsonObject1 = null;

            String Scheme = "", Discount="", Scheme_Unit="", Product_Name="", Product_Code="", Package="", Free="", Discount_Type="", Free_Unit="";
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
                String PCon_fac = "";
                if(jsonObject.has("Conv_Fac"))
                    PCon_fac = jsonObject.getString("Conv_Fac");
                if(jsonObject.has("Default_UOMQty"))
                    unitQty = jsonObject.getInt("Default_UOMQty");
                Log.v("PCon_facPCon_fac", PBarCode);
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
                        PCon_fac,schemeList,unitQty, uomList));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        dashIntent = new Intent(getApplicationContext(), PrimaryOrderProducts.class);
        dashIntent.putExtra("Mode", "0");
        dashIntent.putExtra("order_type", 1);
        startActivity(dashIntent);
//        finish();
        mCommon_class.ProgressdialogShow(2, "");
    }
    public void PrimaryOrder(View v) {
        mCommon_class.ProgressdialogShow(1, "");

        if(!dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_BRAND).equals("") &&
        !dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_DATA).equals("")){
            processPrimaryData();
        }else
            brandPrimaryApi(false);
    }
    public void SecondaryOrder(View v) {
        startActivity(new Intent(getApplicationContext(), SecondRetailerActivity.class));
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



    @Override
    public void onBackPressed() {
    }
    public void productApi() {

        String tempalteValue = "{\"tableName\":\"category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<HeaderCat> ca = apiInterface.SubCategory(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), "24", tempalteValue);


        Log.v("Product_request", ca.request().toString());
        ca.enqueue(new Callback<HeaderCat>() {
            @Override
            public void onResponse(Call<HeaderCat> call, Response<HeaderCat> response) {
                Log.v("Product_response", response.body().toString());
                shared_common_pref.save(Shared_Common_Pref.Product_List, gson.toJson(response.body().getData()));
            }

            @Override
            public void onFailure(Call<HeaderCat> call, Throwable t) {

            }
        });
    }

    public void brandProdutApi() {

        String tempalteValue = "{\"tableName\":\"chkprod\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
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
                shared_common_pref.save(Shared_Common_Pref.Product_Brand, gson.toJson(jBrand));
                shared_common_pref.save(Shared_Common_Pref.Product_Data, gson.toJson(jProd));
                mCommon_class.ProgressdialogShow(2, "");
                Log.v("Product_Response_size", String.valueOf(jProd.size()));

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }

    public void brandPrimaryApi(boolean isUpdateOffline) {

        String tempalteValue = "{\"tableName\":\"category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
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
                dbController.updateDataResponse(DBController.PRIMARY_PRODUCT_BRAND, new Gson().toJson(jBrand));
                dbController.updateDataResponse(DBController.PRIMARY_PRODUCT_DATA, new Gson().toJson(jProd));

                Log.v("Product_Response", jsonArray.toString());
                if(!isUpdateOffline)
                    processPrimaryData();
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

}
