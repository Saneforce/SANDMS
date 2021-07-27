package com.example.sandms.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Adapter.CustomViewAdapter;
import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.PrimaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCartActivity extends AppCompatActivity {
    TextView toolHeader;
    ImageView imgBack;
    LinearLayout proceedCart;
    String SF_CODE, DIVISION_CODE, totalValueString, locationValue, dateTime, dateTime1, checkInTime, keyEk = "EK",
            KeyDate, KeyHyp = "-", keyCodeValue, disTaxAmt="", time;
    CustomViewAdapter adapter;
    LinearLayout btnSubmt;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    Shared_Common_Pref shared_common_pref;

    /* Submit button */
    SimpleDateFormat simpleDateFormat;
    Calendar calander;
    JSONObject person1, stockReportObjectArray, reportObjectArray, PersonObjectArray, sampleReportObjectArray, eventCapturesObjectArray,
            pendingBillObjectArray, ComProductObjectArray, JSONHEAD;
    JSONArray sendArray;
    ArrayList<String> listV = new ArrayList<>();
    EditText toolSearch;
    RecyclerView viewRecyclerview;
    int product_count = 0;
    ProgressDialog progressDialog = null;
    String GrandTotal = "";
    String SubTotal = "";
    TextView viewTotal;
    PrimaryProductViewModel contactViewModel, deleteViewModel;
    List<PrimaryProduct> contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        getToolbar();
        shared_common_pref = new Shared_Common_Pref(this);
        SF_CODE = shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code);
        DIVISION_CODE = shared_common_pref.getvalue(Shared_Common_Pref.Div_Code);
        String carListAsString = getIntent().getStringExtra("list_as_string");
       // Log.v("carListAsString", carListAsString);
        viewTotal = findViewById(R.id.view_total);
        btnSubmt = findViewById(R.id.add_cart);

        GrandTotal = shared_common_pref.getvalue("GrandTotal");
        Log.v("grandtttt", GrandTotal);
         SubTotal=shared_common_pref.getvalue("SubTotal");
        Log.v("SUBTOT", GrandTotal);
       // viewTotal.setText(GrandTotal);
      //  viewTotal.setText(GrandTotal);
//new code added start
        try {
            if(GrandTotal!="0"||GrandTotal!="0.0") {


                if (SubTotal != "0.0" || !("0.0").equals(SubTotal) || !("0").equals(SubTotal)) {
                    GrandTotal = String.valueOf(Integer.parseInt(GrandTotal) - Integer.parseInt(SubTotal));
                    viewTotal.setText(GrandTotal);
                    shared_common_pref.clear_pref("SubTotal");
                    Log.v("grandsub", GrandTotal);
                } else {
                    Log.v("grand", GrandTotal);
                    viewTotal.setText(GrandTotal);
                }
            }
        }catch (NumberFormatException ee){
            Log.v("11grand", GrandTotal);
            viewTotal.setText(GrandTotal);
        }
//new code added stop
        contactViewModel = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
        contactViewModel.getFilterDatas().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {
                adapter.filteredContact(contacts);

                btnSubmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveProduct(contacts);
                    }
                });

            }
        });

        adapter = new CustomViewAdapter(ViewCartActivity.this,GrandTotal,viewTotal, new DMS.viewProduct() {

            @Override
            public void onViewItemClick(String itemID, String productName, String catName, String catImg, Float productQty, Float productRate) {


            }
        });

        viewRecyclerview = (RecyclerView) findViewById(R.id.report_list);
        viewRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewRecyclerview.setLayoutManager(layoutManager);
        DateTime();
        currentTime();
        locationInitialize();
        startLocationUpdates();
        viewRecyclerview.setAdapter(adapter);
    }


    /*Toolbar*/
    public void getToolbar() {

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialogBox.showDialog(ViewCartActivity.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                        ViewCartActivity.super.onBackPressed();
//                        Intent aa=new Intent(ViewCartActivity.this,PrimaryOrderProducts.class);
//                        aa.putExtra("GrandTotal",GrandTotal);
//                        startActivity(aa);
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {
                    }
                });
            }
        });
        toolHeader = (TextView) findViewById(R.id.toolbar_title);
        toolHeader.setText(R.string.view_cart);
        toolSearch = (EditText) findViewById(R.id.toolbar_search);
        toolSearch.setVisibility(View.GONE);

    }


    /*Date and Time Format*/
    public void DateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar calobj = Calendar.getInstance();
        Calendar calobj2 = Calendar.getInstance();
        dateTime = "'" + df.format(calobj.getTime()) + "'";
        dateTime1 = "'" + df2.format(calobj2.getTime()) + "'";

    }


    /*Current Time for Cuttoff Process*/
    public void currentTime() {
        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        time = simpleDateFormat.format(calander.getTime());
        checkInTime = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
    }


    /*Location Initialize*/

    public void locationInitialize() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();

                getLoactionValue();
            }
        };


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /*Displaying Location Result*/
    private void getLoactionValue() {
        if (mCurrentLocation != null) {
            locationValue = String.valueOf(mCurrentLocation.getLatitude());
            locationValue = locationValue.concat(":");
            locationValue = "'" + locationValue.concat(String.valueOf(mCurrentLocation.getLongitude())) + "'";
        }
    }


    /*Getting Location Result*/
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        getLoactionValue();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ViewCartActivity.this, REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException sie) {

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        }
                    }
                });
    }


    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_progress);
        return dialog;
    }

    /*Save to server*/

    public void SaveProduct(List<PrimaryProduct> carsList) {

        Log.v("SAVE_PRODUCT", "INCIKC");
        progressDialog = createProgressDialog(this);
        /*ActivityReport*/
        DateFormat dfw = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calobjw = Calendar.getInstance();
        KeyDate = SF_CODE;
        keyCodeValue = keyEk + KeyDate + KeyHyp + dfw.format(calobjw.getTime()).hashCode();

        JSONObject reportObject = new JSONObject();
        reportObjectArray = new JSONObject();
        String sFCODE = "'" + SF_CODE + "'";
        try {
            reportObject.put("Worktype_code", "'6'");
            reportObject.put("Town_code", "'11'");
            reportObject.put("dcr_activity_date", dateTime1);
            reportObject.put("Daywise_Remarks", "''");
            reportObject.put("eKey", keyCodeValue);
            reportObject.put("rx", "'1'");
            reportObject.put("rx_t", "''");
            reportObject.put("DataSF", sFCODE);
            reportObjectArray.put("Activity_Report_APP", reportObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*StockListReport*/
        JSONObject stockReportObject = new JSONObject();
        stockReportObjectArray = new JSONObject();
        JSONObject fkeyStock = new JSONObject();

        try {
            stockReportObject.put("Stockist_POB", 0);
            stockReportObject.put("Super_Stck_code", "'SS96'");
            stockReportObject.put("Worked_With", "''");
            stockReportObject.put("location", locationValue);
            stockReportObject.put("geoaddress", "");
            stockReportObject.put("stockist_code", sFCODE);
            stockReportObject.put("superstockistid", "''");
            stockReportObject.put("Stk_Meet_Time", dateTime);
            stockReportObject.put("modified_time", dateTime);
            stockReportObject.put("orderValue", 5370);
            stockReportObject.put("Aob", 69);
            stockReportObject.put("CheckinTime", checkInTime);
            stockReportObject.put("CheckoutTime", checkInTime);
            stockReportObject.put("f_key", fkeyStock);
            fkeyStock.put("Activity_Report_Code", "'Activity_Report_APP'");
            stockReportObject.put("PhoneOrderTypes", 1);
            stockReportObjectArray.put("Activity_Stockist_Report", stockReportObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String stockReport = stockReportObjectArray.toString();
        System.out.println(" Activity_Stockist_Report" + stockReport);

        /*Activity_Stk_Sample_Report*/
        JSONArray sampleReportArray = new JSONArray();
        sampleReportObjectArray = new JSONObject();

        try {

            sampleReportObjectArray.put("Activity_Stk_Sample_Report", sampleReportArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sampleReport = sampleReportObjectArray.toString();
        System.out.println(" Activity_Stk_Sample_Report" + sampleReport);

        /*Activity_Event_Captures*/
        JSONObject eventCapturesObject = new JSONObject();
        JSONArray eventCapturesArray = new JSONArray();
        eventCapturesObjectArray = new JSONObject();
        JSONObject fkeyEcap = new JSONObject();

        try {
            eventCapturesObject.put("imgurl", "'1585714374958.jpg'");
            eventCapturesObject.put("title", "'Primary capture'");
            eventCapturesObject.put("remarks", "'Testing for native'");
            eventCapturesObject.put("f_key", fkeyEcap);
            fkeyEcap.put("Activity_Report_Code", "Activity_Report_APP");
            eventCapturesArray.put(eventCapturesObject);
            eventCapturesObjectArray.put("Activity_Event_Captures", eventCapturesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String EventCap = eventCapturesObjectArray.toString();
        System.out.println("Activity_Event_Captures" + EventCap);

        /*PENDING_Bills*/
        JSONArray pendingBillArray = new JSONArray();
        pendingBillObjectArray = new JSONObject();

        try {
            pendingBillObjectArray.put("PENDING_Bills", pendingBillArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String PendingBill = pendingBillObjectArray.toString();
        System.out.println(" PENDING_Bills" + PendingBill);

        /*Compititor_Product*/
        JSONArray ComProductArray = new JSONArray();
        ComProductObjectArray = new JSONObject();

        try {
            ComProductObjectArray.put("Compititor_Product", ComProductArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String ComProduct = ComProductObjectArray.toString();
        System.out.println(" Compititor_Product" + ComProduct);

        /*Head Details*/
        JSONArray JsonArryHead = new JSONArray();
        JSONHEAD = new JSONObject();
        JSONObject JsonObjtHead = new JSONObject();

        try {
            //adding items to first json object
            JsonObjtHead.put("bill_add", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Address));
            JsonObjtHead.put("ship_add", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Address));
            JsonObjtHead.put("order_date", Common_Class.GetDate());
            JsonObjtHead.put("exp_date", Common_Class.GetDate());
            JsonObjtHead.put("div_code", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            JsonObjtHead.put("sup_no", shared_common_pref.getvalue(Shared_Common_Pref.sup_code));
            JsonObjtHead.put("sup_name", shared_common_pref.getvalue(Shared_Common_Pref.sup_name));
            JsonObjtHead.put("sf_code", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
            JsonObjtHead.put("stk_code", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            JsonObjtHead.put("com_add", shared_common_pref.getvalue(Shared_Common_Pref.sup_addr));
            JsonObjtHead.put("order_value", GrandTotal);
            JsonObjtHead.put("sub_tot", 0);
            JsonObjtHead.put("dis_tot", 0);
            JsonObjtHead.put("tax_tot", 0);
            JsonArryHead.put(JsonObjtHead);
            JSONHEAD.put("Json_Head", JsonArryHead);
            Log.v("VIEW_CART_ACTIVITY", JSONHEAD.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*product_order_list*/
        List<JSONObject> myJSONObjects = new ArrayList<JSONObject>(carsList.size());

        JSONArray personarray = new JSONArray();
        PersonObjectArray = new JSONObject();
        JSONObject fkeyprodcut = new JSONObject();

        for (int z = 0; z < carsList.size(); z++) {
            person1 = new JSONObject();

            try {
                person1.put("Productname", carsList.get(z).getPname());
                person1.put("ProductCode", carsList.get(z).getUID());
                person1.put("Qty", carsList.get(z).getQty());
                person1.put("Rate", carsList.get(z).getProduct_Cat_Code());
                person1.put("cb_qty", 0);
                person1.put("ProductRate", carsList.get(z).getProduct_Cat_Code());
                person1.put("free", 0);
                person1.put("f_key", fkeyprodcut);
                person1.put("Productunit", carsList.get(z).getProduct_Sale_Unit());
                person1.put("dis", carsList.get(z).getDiscount());
                person1.put("tax", carsList.get(z).getTax_Value());
                person1.put("dis_value", carsList.get(z).getDis_amt().toString().replace("-", ""));
                person1.put("tax_value", carsList.get(z).getTax_amt().toString().replace("-", ""));
                person1.put("Off_Pro_code", "0");
                person1.put("Off_Pro_name", "0");
                person1.put("Off_Pro_Unit", "0");
                person1.put("Con_Fac", carsList.get(z).getCon_fac());
                person1.put("UOM", carsList.get(z).getUOM());
                person1.put("Val", carsList.get(z).getSubtotal());
                fkeyprodcut.put("activity_stockist_code", "Activity_Stockist_Report");
                myJSONObjects.add(person1);
                listV.add(String.valueOf((person1)));
                product_count = myJSONObjects.size();

                personarray.put(person1);
                PersonObjectArray.put("Activity_Stk_POB_Report", personarray);
                String JsonData = PersonObjectArray.toString();

                System.out.println("Activity_Stk_POB_Report: " + JsonData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sendArray = new JSONArray();
        sendArray.put(reportObjectArray);
        sendArray.put(stockReportObjectArray);
        sendArray.put(PersonObjectArray);
        sendArray.put(sampleReportObjectArray);
        sendArray.put(eventCapturesObjectArray);
        sendArray.put(pendingBillObjectArray);
        sendArray.put(ComProductObjectArray);
        sendArray.put(JSONHEAD);
        totalValueString = sendArray.toString();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> responseBodyCall = apiInterface.submitValue("dcr/save", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), totalValueString);
        Log.v("View_Cart_Primary", responseBodyCall.request().toString());
        Log.v("View_Cart_Primary", totalValueString.toString());
        responseBodyCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.v("View_Cart_Primary_res", response.body().toString());
                    try {
                        JSONObject jsonObjects = new JSONObject(response.body().toString());
                        String san = jsonObjects.getString("success");
                        if (product_count != 0) {

                            deleteViewModel = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
                            deleteViewModel.getAllData().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
                                @Override
                                public void onChanged(List<PrimaryProduct> contacts) {
                                    deleteViewModel.delete(contacts);
                                    progressDialog.dismiss();
                                }
                            });

                            Toast.makeText(ViewCartActivity.this, "Your order submitted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                        } else {
                            finish();
                        }

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SUBMIT_VALUE", "ERROR");
                deleteViewModel = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
                deleteViewModel.getAllData().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
                    @Override
                    public void onChanged(List<PrimaryProduct> contacts) {
                        deleteViewModel.delete(contacts);
                        progressDialog.dismiss();
                    }
                });
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            }
        });
    }


    @Override
    public void onBackPressed() {
   //     startActivity(new Intent(getApplicationContext(), PrimaryOrderProducts.class));

    }


    @Override
    protected void onResume() {


        super.onResume();
        Log.v("Primary_order", "onResume");
//        if (productBarCode.equalsIgnoreCase("")) {
//            mPrimaryProductViewModel = ViewModelProviders.of(this).get(PrimaryProductViewModel.class);
//            mPrimaryProductViewModel.getAllData().observe(this, new Observer<List<PrimaryProduct>>() {
//                @Override
//                public void onChanged(List<PrimaryProduct> contacts) {
//                    priProdAdapter.setContact(contacts, "Nil");
//                    mPrimaryProduct = contacts;
//                    Log.v("mPrimaryProduct_123456", String.valueOf(contacts.size()));
//                    Log.v("mPrimaryProductOrder",new Gson().toJson(contacts));
//
//                }
//            });
//        } else {
//
//            loadFilteredTodos(productBarCode);
//        }
        PrimaryProductViewModel contactViewModels = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
        contactViewModels.getFilterDatas().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {
                Log.v("TotalviewcartSize", new Gson().toJson(contacts.size()));
               // item_count.setText("Items :" + new Gson().toJson(contacts.size()));
               // viewTotal.setText("" + sum);
                float sum = 0;
                float tax=0;
                for (PrimaryProduct cars : contacts) {
                    sum = sum + Float.parseFloat(cars.getSubtotal());
                    // sum = sum +Float.parseFloat( cars.getTax_Value())+ Float.parseFloat(cars.getSubtotal())+ Float.parseFloat(cars.getDis_amt())+;
                    ;
                    //  Log.v("taxamttotal_valbefore", String.valueOf(tax));
                    Log.v("Total_foreviewcart", String.valueOf(sum));
                }
                viewTotal.setText("" + sum);
            shared_common_pref.save("GrandTotal", String.valueOf(sum));
               // mShared_common_pref.save("SubTotal", String.valueOf("0.0"));
            }
        });

//        sum= Float.parseFloat(getIntent().getStringExtra("GrandTotal"));
//        if(!"".equals(sum)|| !("0".equals(sum))||sum!=0){
//            grandTotal.setText("" + sum);
//        }

    }

}