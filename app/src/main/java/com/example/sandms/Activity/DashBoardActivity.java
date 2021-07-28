package com.example.sandms.Activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.PrimaryProductDao;
import com.example.sandms.Model.HeaderCat;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.PrimaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends AppCompatActivity {
    Intent dashIntent;
    TextView txtName, txtAddress;
    Shared_Common_Pref shared_common_pref;
    Gson gson;
    Common_Class mCommon_class;
    ImageView imagView,profilePic;
    PrimaryProductViewModel mPrimaryProductViewModel;
    RelativeLayout profileLayout;
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


        imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });


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
                            Product_Code),0));

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void PrimaryOrder(View v) {

        mCommon_class.ProgressdialogShow(1, "");
        brandPrimaryApi();
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

    public void brandPrimaryApi() {

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
                finish();
                mCommon_class.ProgressdialogShow(2, "");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }


}