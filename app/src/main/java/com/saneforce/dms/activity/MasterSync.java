package com.saneforce.dms.activity;

import static android.view.View.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.R;
import com.saneforce.dms.adapter.DataAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.PrimaryProductDao;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.AlertDialogBox;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.PrimaryProductDatabase;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterSync extends AppCompatActivity {
    List<PrimaryProduct.UOMlist> mMasterSync = new ArrayList<PrimaryProduct.UOMlist>();
    public JSONObject jsonProductuom;
    private ProductAdapter mAdapter;

    Intent dashIntent;
    Shared_Common_Pref shared_common_pref;
    Common_Model mCommon_model_spinner;
    Common_Class mCommon_class;
    String sf_Code;
    JSONObject jsonObject1;
    RecyclerView recyclerView;
    DBController dbController;
    public DataAdapter da;

    String productid;

    TextView tv_channel, tv_class, tv_payment_key, tv_pri_category, tv_product_uom, tv_profile, tv_retailer, tv_route_list, tv_sec_category;
    TextInputEditText edtName, edtAddress, edtMobile, edtStoclistName, edtGst, edtEmail;
    TextInputEditText tie_erp_code, tie_sales_team_name;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_sync);

        tv_channel = findViewById(R.id.tv_channel);
        tv_class = findViewById(R.id.tv_class);
        tv_payment_key = findViewById(R.id.tv_payment_key);
        tv_pri_category = findViewById(R.id.tv_pri_category);
        tv_product_uom = findViewById(R.id.tv_product_uom);
        tv_profile = findViewById(R.id.tv_profile);
        tv_retailer = findViewById(R.id.tv_retailer);
        tv_route_list = findViewById(R.id.tv_route_list);
        tv_sec_category = findViewById(R.id.tv_sec_category);

        dbController = new DBController(MasterSync.this);

        imageView = findViewById(R.id.toolbar_back);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("mMasterSync", String.valueOf(mMasterSync.size()));
                onBackPressed();
            }
        });

        tv_channel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getRouteChannel();
            }
        });

        tv_class.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getRouteClass();
            }
        });

        tv_payment_key.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnlinePaymentKeys();
            }
        });

        tv_pri_category.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                brandPrimaryApi(true);
            }
        });

        tv_product_uom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getProductId();
            }
        });

        tv_profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfileData();
            }
        });

        tv_retailer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RetailerType();
            }
        });

        tv_route_list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getRouteDetails();
            }
        });

        tv_sec_category.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                brandSecondaryApi();
            }
        });
    }


    public void getRouteChannel() {
        String routeMap = "{\"tableName\":\"Doctor_Specialty\",\"coloumns\":\"[\\\"Specialty_Code as id\\\", \\\"Specialty_Name as name\\\"]\",\"where\":\"[\\\"isnull(Deactivate_flag,0)=0\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
        call.enqueue(new Callback<>() {
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

    private void getOnlinePaymentKeys() {
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.getPaymentKey(shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        JSONObject jsonRootObject = new JSONObject(response.body().toString());
                        if (jsonRootObject.get("success").toString().equalsIgnoreCase("true")) {

                            dbController.updateDataResponse(DBController.CLASS_LIST, new Gson().toJson(jsonRootObject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AlertDialogBox.showDialog(MasterSync.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    AlertDialogBox.showDialog(MasterSync.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox.showDialog(MasterSync.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
        }
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
    }

    private class PopulateDbAsyntask extends AsyncTask<Void, Void, Void> {
        private PrimaryProductDao contactDao;
        public PopulateDbAsyntask(PrimaryProductDatabase contactDaos)
        {
            contactDao = contactDaos.contactDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            PrimaryProductDatabase.getInstance(MasterSync.this).clearAllTables();
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
//                String PRate = "1";
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
        mCommon_class.ProgressdialogShow(2, "");
    }


    public void getProductId() {
        mCommon_class.ProgressdialogShow(1, "");
        //   this.sf_Code=sf_Code;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProductuom(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.v("DMS_RESPONSE", response.body().toString());
                mCommon_class.ProgressdialogShow(2, "");
                try {
                    jsonProductuom = new JSONObject(response.body().toString());
                    Log.e("LoginResponse1", jsonProductuom.toString());
                    String js = jsonProductuom.getString(
                            "success");
                    Log.e("LoginResponse133w", js.toString());
                    JSONArray jss = jsonProductuom.getJSONArray(
                            "Data");
                    Log.e("LoginResponse133ws", jss.toString());

                    // JSONArray jsonArray = jsonProductuom.optJSONArray("Data");
                    //  Log.e("LoginResponse133",  jsonProductuom.toString());
                    for (int i = 0; i < jss.length(); i++) {
                        JSONObject jsonObject = jss.optJSONObject(i);
                        String id = "";
                        if(jsonObject.has("id"))
                            id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        Log.v("LoginResponse1nn", name);
                        String productCode = jsonObject.getString("Product_Code");
                        Log.v("LoginResponse1nnq", productCode);
                        String conqty = jsonObject.getString("ConQty");
                        Log.v("LoginResponse1nnq", conqty);
                        if (productid.equals(productCode) || productid.contains(productCode)) {
                            PrimaryProduct.UOMlist pp = new PrimaryProduct.UOMlist(id, name, productCode, conqty);
                            mMasterSync.add(pp);
                        }
//                         priProdAdapter = new ProductsAdapter(PrimaryOrderList.this, name,productCode,conqty);
//                         priUnitRecycler.setAdapter(priProdAdapter);

                        mAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
                Toast.makeText(MasterSync.this, "Invalid products", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getProfileData() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProfile(sf_Code);
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String res = response.body().toString();
                Log.v("DMS_RESPONSE", res);

                try {
                    jsonObject1 = new JSONObject(res);
                    Log.e("LoginResponse1",  jsonObject1.toString());
                    JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("Stockist_Name");
                        Log.e("LoginResponse", name.toString());
                        edtName.setText(name);
                        String email=jsonObject.getString("Email");
                        if(email!=null && !email.equals("") && !email.equals("null"))
                            edtEmail.setText(email);
                        String stocklistname=jsonObject.getString("Stockist_ContactPerson");
                        edtStoclistName.setText(stocklistname);
//                       String designation=jsonObject.getString("Stockist_Designation");
//                       edtDesignation.setText(designation);
                        String address=jsonObject.getString("Stockist_Address");
                        edtAddress.setText(address);
                        String mobile=jsonObject.getString("Stockist_Mobile");
                        edtMobile.setText(mobile);
                        String gst=jsonObject.getString("gstn");
                        edtGst.setText(gst);

                        String erpCode = "";
                        if(jsonObject.has("ERP_Code"))
                            erpCode=jsonObject.getString("ERP_Code");
                        tie_erp_code.setText(erpCode);

                        String salesTeamName = "";
                        if(jsonObject.has("FieldPerson"))
                            salesTeamName=jsonObject.getString("FieldPerson");
                        tie_sales_team_name.setText(salesTeamName);

                        try {
                            shared_common_pref.save(Shared_Common_Pref.USER_NAME, name);
                            shared_common_pref.save(Shared_Common_Pref.USER_EMAIL, email);
                            shared_common_pref.save(Shared_Common_Pref.USER_PHONE, mobile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MasterSync.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

}