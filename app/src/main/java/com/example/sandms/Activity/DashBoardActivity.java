package com.example.sandms.Activity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.PrimaryProductDao;
import com.example.sandms.Model.HeaderCat;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.Constants;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.PrimaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.example.sandms.sqlite.DBController;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    ImageView imagView,profilePic;
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
        gson = new Gson();
        //productApi();
        txtName = findViewById(R.id.dis_name);
        txtAddress = findViewById(R.id.dis_place);
        profilePic=findViewById(R.id.profileImg);
        profileLayout=findViewById(R.id.imageLayout);
        txtName.setText(shared_common_pref.getvalue(Shared_Common_Pref.name) + " ~ " + shared_common_pref.getvalue(Shared_Common_Pref.Sf_UserName));
        txtAddress.setText(shared_common_pref.getvalue(Shared_Common_Pref.sup_addr));
       // brandProdutApi();

        if(getIntent().hasExtra("syncData"))
            syncData = getIntent().getBooleanExtra("syncData", false);

        imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
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

                checkData();

                if(syncData || shared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Brand).equals("0")){
                    syncData = false;
                    brandPrimaryApi(true);
                }
                if(syncData || shared_common_pref.getvalue(Shared_Common_Pref.SecProduct_Brand).equals("0")){
                    syncData = false;
                    brandSecondaryApi();
                }
                if(syncData || shared_common_pref.getvalue(Shared_Common_Pref.RETAILER_LIST).equals("0")){
                    syncData = false;
                    RetailerType();
                }

               if(syncData || shared_common_pref.getvalue(Shared_Common_Pref.TEMPLATE_LIST).equals("0")){
                    syncData = false;
                    getTemplate();
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
                shared_common_pref.save(Shared_Common_Pref.SecProduct_Brand, new Gson().toJson(jBrand));
                shared_common_pref.save(Shared_Common_Pref.SecProduct_Data, new Gson().toJson(jProd));
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
                    shared_common_pref.save(Shared_Common_Pref.RETAILER_LIST, new Gson().toJson(jsonArray));


                } catch (Exception io) {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("LeaveTypeList", "Error");
            }
        });
    }

    public void getTemplate() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getTemplates(shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject JsonObject = response.body();
                try {
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");

                    shared_common_pref.save(Shared_Common_Pref.TEMPLATE_LIST, new Gson().toJson(jsonArray));

                } catch (Exception io) {
                    io.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private void checkData(){

            if(dbController == null)
                dbController = new DBController(DashBoardActivity.this);

            if(shared_common_pref == null)
                shared_common_pref = new Shared_Common_Pref(DashBoardActivity.this);

            Log.d(TAG, "checkData: " + dbController.getAllDataKey());

            if (dbController.getAllDataKey().size() > 0) {
                for (HashMap<String, String> i : dbController.getAllDataKey()) {
                    try {
                        sendDataToServer(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

    }


    private void sendDataToServer(HashMap<String, String> data) {
        Log.d(TAG, "sendDataToServer: data=> " + data);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestBody = null;
        try {
            if(data.get(DBController.AXN_KEY)!=null && data.get(DBController.AXN_KEY).equalsIgnoreCase("dcr/retailervisit"))
                requestBody = Constants.toRequestBody(new JSONObject(data.get(DBController.DATA_RESPONSE)));
            else
                requestBody = Constants.toRequestBody(new JSONArray(data.get(DBController.DATA_RESPONSE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> responseBodyCall = apiInterface.submitValue(data.get(DBController.AXN_KEY), shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), requestBody);
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
                            dbController.updateDatakey(data.get(DBController.DATA_KEY));
                            displayNotification("Order successfull", data.get(DBController.DATA_KEY));
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

    private void displayNotification(String title, String task) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("sandms", "sandms", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "sandms")
                .setContentTitle(title)
                .setContentText(task)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }

    private class PopulateDbAsyntask extends AsyncTask<Void, Void, Void> {
        private PrimaryProductDao contactDao;


        public PopulateDbAsyntask(PrimaryProductDatabase contactDaos) { contactDao = contactDaos.contactDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fillingWithStart();
            Log.v("Data_CHeckng", "Checking_data");
            return null;
        }

    }


    private void fillingWithStart() {

        Log.v("Data_CHeckng", "Checking_data");

        Shared_Common_Pref mShared_common_pref = new Shared_Common_Pref(this);
        String sPrimaryProd = mShared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Data);
        PrimaryProductDao contact = PrimaryProductDatabase.getInstance(this).getAppDatabase()
                .contactDao();
        try {
            JSONArray jsonArray = new JSONArray(sPrimaryProd);
            JSONObject jsonObject = null;
            JSONObject jsonObject1 = null;

            String Scheme = "", Discount="", Scheme_Unit="", Product_Name="", Product_Code="";
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String id = String.valueOf(jsonObject.get("id"));
                String Name = String.valueOf(jsonObject.get("name"));
                String PName = String.valueOf(jsonObject.get("Pname"));
                String PRate = String.valueOf(jsonObject.get("Product_Cat_Code"));
                String PBarCode = String.valueOf(jsonObject.get("Product_Brd_Code"));
                String PId = String.valueOf(jsonObject.get("PID"));
                String PUOM = String.valueOf(jsonObject.get("UOM"));
                String PSaleUnit = String.valueOf(jsonObject.get("Product_Sale_Unit"));
                String PDiscount = String.valueOf(jsonObject.get("Discount"));
                String PTaxValue = String.valueOf(jsonObject.get("Tax_value"));
                String PCon_fac = String.valueOf(jsonObject.get("Conv_Fac"));
                Log.v("PCon_facPCon_fac", PBarCode);
                JSONArray jsonArray1 = jsonObject.getJSONArray("SchemeArr");


                for (int j = 0; j < jsonArray1.length(); j++) {
                    jsonObject1 = jsonArray1.getJSONObject(j);
                    Scheme = String.valueOf(jsonObject1.get("Scheme"));
                    Discount = String.valueOf(jsonObject1.get("Discount"));
                    Scheme_Unit = String.valueOf(jsonObject1.get("Scheme_Unit"));
                    Product_Name = String.valueOf(jsonObject1.get("Product_Name"));
                    Product_Code = String.valueOf(jsonObject1.get("Product_Code"));

                    Log.v("JSON_Array_SCHEMA",Scheme);
                    Log.v("JSON_Array_DIS",Discount);
                    contact.insert(new PrimaryProduct(id, PId, Name, PName, PBarCode, PUOM, PRate,
                            PSaleUnit, PDiscount, PTaxValue, "0", "0", "0", "0", "0",
                            PCon_fac,new PrimaryProduct.SchemeProducts(Scheme,Discount,Scheme_Unit,Product_Name,
                            Product_Code)));

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void PrimaryOrder(View v) {

        mCommon_class.ProgressdialogShow(1, "");

        if(shared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Brand)!=null &&
                !shared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Brand).equals("") &&
                !shared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Brand).equals("0") &&
        shared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Data)!=null &&
        !shared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Data).equals("") &&
                !shared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Data).equals("0")){
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
                shared_common_pref.save(Shared_Common_Pref.PriProduct_Brand, gson.toJson(jBrand));
                shared_common_pref.save(Shared_Common_Pref.PriProduct_Data, gson.toJson(jProd));

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



        mPrimaryProductViewModel = ViewModelProviders.of(DashBoardActivity.this).get(PrimaryProductViewModel.class);
        mPrimaryProductViewModel.getAllData().observe(DashBoardActivity.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {


                Integer ProductCount = Integer.valueOf(new Gson().toJson(contacts.size()));

                Log.v("DASH_BOARD_COUNT", String.valueOf(ProductCount));

                if (ProductCount == 0) {
                    new PopulateDbAsyntask(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();
                }
            }
        });


        dashIntent = new Intent(getApplicationContext(), PrimaryOrderProducts.class);
        dashIntent.putExtra("Mode", "0");
        startActivity(dashIntent);
//        finish();
        mCommon_class.ProgressdialogShow(2, "");
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
