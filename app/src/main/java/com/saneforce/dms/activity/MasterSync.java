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

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterSync extends AppCompatActivity {
    List<PrimaryProduct.UOMlist> mMasterSync = new ArrayList<PrimaryProduct.UOMlist>();
    public JSONObject jsonProductuom;

    Intent dashIntent;
    Shared_Common_Pref shared_common_pref;
    Common_Class mCommon_class;
    JSONObject jsonObject1;
    DBController dbController;

    TextView tv_channel, tv_class, tv_payment_key, tv_pri_category, tv_product_uom, tv_profile, tv_retailer, tv_route_list, tv_sec_category,tv_sync_all_data;
    ImageView imageView,fail_channel,fail_class,fail_payment_key,fail_pri_category,fail_product_uom,fail_profile,fail_retailer,fail_route_list,fail_sec_category;
    GifImageView gif_channel,gif_class,gif_payment_key,gif_pri_category,gif_product_uom,gif_profile,gif_retailer,gif_route_list,gif_sec_category;

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
        tv_sync_all_data=findViewById(R.id.tv_sync_all_data);

        fail_channel=findViewById(R.id.fail_channel);
        fail_class=findViewById(R.id.fail_class);
        fail_payment_key=findViewById(R.id.fail_payment_key);
        fail_pri_category=findViewById(R.id.fail_pri_category);
        fail_product_uom= findViewById(R.id.fail_product_uom);
        fail_profile=findViewById(R.id.fail_profile);
        fail_retailer=findViewById(R.id.fail_retailer);
        fail_route_list=findViewById(R.id.fail_route_list);
        fail_sec_category=findViewById(R.id.fail_sec_category);


        gif_channel=findViewById(R.id.gif_channel);
        gif_class=findViewById(R.id.gif_class);
        gif_payment_key=findViewById(R.id.gif_payment_key);
        gif_pri_category=findViewById(R.id.gif_pri_category);
        gif_product_uom=findViewById(R.id.gif_product_uom);
        gif_profile=findViewById(R.id.gif_profile);
        gif_retailer=findViewById(R.id.gif_retailer);
        gif_route_list=findViewById(R.id.gif_route_list);
        gif_sec_category=findViewById(R.id.gif_sec_category);

        dbController = new DBController(MasterSync.this);
        shared_common_pref=new Shared_Common_Pref(MasterSync.this);
        mCommon_class=new Common_Class(MasterSync.this);

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
                fail_channel.setVisibility(GONE);
                getRouteChannel();
            }
        });

        tv_class.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_class.setVisibility(GONE);
                getRouteClass();
            }
        });

        tv_payment_key.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_payment_key.setVisibility(GONE);
                getOnlinePaymentKeys();
            }
        });

        tv_pri_category.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_pri_category.setVisibility(GONE);
                brandPrimaryApi(true);
            }
        });

        tv_product_uom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_product_uom.setVisibility(GONE);
                getProductId();
            }
        });

        tv_profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_profile.setVisibility(GONE);
                getProfileData();
            }
        });

        tv_retailer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_retailer.setVisibility(GONE);
                RetailerType();
            }
        });

        tv_route_list.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_route_list.setVisibility(GONE);
                getRouteDetails();
            }
        });

        tv_sec_category.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fail_sec_category.setVisibility(GONE);
                brandSecondaryApi();
            }
        });

        tv_sync_all_data.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getRouteChannel();
                getRouteClass();
                getOnlinePaymentKeys();
                brandPrimaryApi(true);
                getProductId();
                getProfileData();
                RetailerType();
                getRouteDetails();
                brandSecondaryApi();
            }
        });
    }

    public void getRouteChannel() {

        gif_channel.setVisibility(VISIBLE);
        String routeMap = "{\"tableName\":\"Doctor_Specialty\",\"coloumns\":\"[\\\"Specialty_Code as id\\\", \\\"Specialty_Name as name\\\"]\",\"where\":\"[\\\"isnull(Deactivate_flag,0)=0\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    fail_channel.setVisibility(VISIBLE);
                    fail_channel.setImageResource(R.drawable.ic_baseline_check_24);
                    gif_channel.setVisibility(GONE);
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
                fail_channel.setVisibility(VISIBLE);
                fail_channel.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_channel.setVisibility(GONE);
                t.printStackTrace();
            }
        });
    }

    public void getRouteClass() {

        gif_class.setVisibility(VISIBLE);
        String routeMap = "{\"tableName\":\"Mas_Doc_Class\",\"coloumns\":\"[\\\"Doc_ClsCode as id\\\", \\\"Doc_ClsSName as name\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    fail_class.setVisibility(VISIBLE);
                    fail_class.setImageResource(R.drawable.ic_baseline_check_24);
                    gif_class.setVisibility(GONE);
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
                fail_class.setVisibility(VISIBLE);
                fail_class.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_class.setVisibility(GONE);
                t.printStackTrace();
            }
        });
    }

    private void getOnlinePaymentKeys() {

        gif_payment_key.setVisibility(VISIBLE);
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.getPaymentKey(shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        fail_payment_key.setVisibility(VISIBLE);
                        fail_payment_key.setImageResource(R.drawable.ic_baseline_check_24);
                        gif_payment_key.setVisibility(GONE);
                        JSONObject jsonRootObject = new JSONObject(response.body().toString());
                        if (jsonRootObject.get("success").toString().equalsIgnoreCase("true")) {

                            dbController.updateDataResponse(DBController.PAYMENT_KEYS, new Gson().toJson(jsonRootObject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AlertDialogBox.showDialog(MasterSync.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    fail_payment_key.setVisibility(VISIBLE);
                    fail_payment_key.setImageResource(R.drawable.ic_baseline_warning_24);
                    gif_payment_key.setVisibility(GONE);
                    AlertDialogBox.showDialog(MasterSync.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox.showDialog(MasterSync.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
        }
    }

    public void brandPrimaryApi(boolean isUpdateOffline) {

        gif_pri_category.setVisibility(VISIBLE);
        String tempalteValue = "{\"tableName\":\"category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code),"", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue, 1);


        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.body()!=null ){
                    fail_pri_category.setVisibility(VISIBLE);
                    fail_pri_category.setImageResource(R.drawable.ic_baseline_check_24);
                    gif_pri_category.setVisibility(GONE);
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
                fail_pri_category.setVisibility(VISIBLE);
                fail_pri_category.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_pri_category.setVisibility(GONE);
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
            return null;
        }
    }

    private void fillingWithStart() {

        String sPrimaryProd = dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_DATA);
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

        gif_product_uom.setVisibility(VISIBLE);
       /* mCommon_class.ProgressdialogShow(1, "");*/
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
                    fail_product_uom.setVisibility(VISIBLE);
                    fail_product_uom.setImageResource(R.drawable.ic_baseline_check_24);
                    gif_product_uom.setVisibility(GONE);
                    jsonProductuom = new JSONObject(response.body().toString());
                    dbController.updateDataResponse(DBController.PRODUCT_UOM, new Gson().toJson(jsonProductuom));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
                fail_product_uom.setVisibility(VISIBLE);
                fail_product_uom.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_product_uom.setVisibility(GONE);
                Toast.makeText(MasterSync.this, "Invalid products", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getProfileData() {
        gif_profile.setVisibility(VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProfile(shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String res = response.body().toString();
                Log.v("DMS_RESPONSE", res);

                try {
                    fail_profile.setVisibility(VISIBLE);
                    fail_profile.setImageResource(R.drawable.ic_baseline_check_24);
                    gif_profile.setVisibility(GONE);
                    jsonObject1 = new JSONObject(res);
                    Log.e("LoginResponse1",  jsonObject1.toString());
                    JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                    dbController.updateDataResponse(DBController.PROFILE_DATA, new Gson().toJson(jsonProductuom));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                fail_profile.setVisibility(VISIBLE);
                fail_profile.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_profile.setVisibility(GONE);
                Toast.makeText(MasterSync.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void RetailerType() {

        gif_retailer.setVisibility(VISIBLE);
        String RetailerDetails = "{\"tableName\":\"vwDoctor_Master_APP\",\"coloumns\":\"[\\\"doctor_code as id\\\", \\\"doctor_name as name\\\",\\\"town_code\\\",\\\"town_name\\\",\\\"lat\\\",\\\"long\\\",\\\"addrs\\\",\\\"ListedDr_Address1\\\",\\\"ListedDr_Sl_No\\\",\\\"Mobile_Number\\\",\\\"Doc_cat_code\\\",\\\"ContactPersion\\\",\\\"Doc_Special_Code\\\",\\\"Slan_Name\\\"]\",\"where\":\"[\\\"isnull(Doctor_Active_flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getRetName(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), RetailerDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject JsonObject = response.body();
                try {
                    fail_retailer.setVisibility(VISIBLE);
                    fail_retailer.setImageResource(R.drawable.ic_baseline_check_24);
                    gif_retailer.setVisibility(GONE);
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");
                    dbController.updateDataResponse(DBController.RETAILER_LIST, new Gson().toJson(jsonArray));
                    shared_common_pref.save(Shared_Common_Pref.YET_TO_SYN, false);
                } catch (Exception io) {
                    io.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                fail_retailer.setVisibility(VISIBLE);
                fail_retailer.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_retailer.setVisibility(GONE);
                Log.d("LeaveTypeList", "Error");
            }
        });
    }

    public void getRouteDetails() {
        gif_route_list.setVisibility(VISIBLE);
        String routeMap = "{\"tableName\":\"vwTown_Master_APP\",\"coloumns\":\"[\\\"town_code as id\\\", \\\"town_name as name\\\",\\\"target\\\",\\\"min_prod\\\",\\\"field_code\\\",\\\"stockist_code\\\"]\",\"where\":\"[\\\"isnull(Town_Activation_Flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.retailerClass(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), routeMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    fail_route_list.setVisibility(VISIBLE);
                    fail_route_list.setImageResource(R.drawable.ic_baseline_check_24);
                    gif_route_list.setVisibility(GONE);
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
                fail_route_list.setVisibility(VISIBLE);
                fail_route_list.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_route_list.setVisibility(GONE);
                t.printStackTrace();
            }
        });
    }

    public void brandSecondaryApi() {
        gif_sec_category.setVisibility(VISIBLE);
        String tempalteValue = "{\"tableName\":\"sec_category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code),"", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue, 2);

        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                fail_sec_category.setVisibility(VISIBLE);
                fail_sec_category.setImageResource(R.drawable.ic_baseline_check_24);
                gif_sec_category.setVisibility(GONE);
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
                fail_sec_category.setVisibility(VISIBLE);
                fail_sec_category.setImageResource(R.drawable.ic_baseline_warning_24);
                gif_sec_category.setVisibility(GONE);
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }
}