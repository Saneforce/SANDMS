package com.saneforce.dms.activity;

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
import com.saneforce.dms.adapter.CustomViewAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.AlertDialogBox;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.PrimaryProductViewModel;
import com.saneforce.dms.utils.Shared_Common_Pref;
import com.saneforce.dms.sqlite.DBController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCartActivity extends AppCompatActivity {
    private static final String TAG = ViewCartActivity.class.getSimpleName();
    TextView toolHeader;
    ImageView imgBack;
    String SF_CODE, DIVISION_CODE, totalValueString, locationValue, dateTime, dateTime1, checkInTime, keyEk = "EK",
            KeyDate, KeyHyp = "-", keyCodeValue, time;
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
    RecyclerView viewRecyclerview;
    int product_count = 0;
//    ProgressDialog progressDialog = null;
    String GrandTotal = "";
    String SubTotal = "";
    TextView viewTotal;
    PrimaryProductViewModel contactViewModel, deleteViewModel;
//    List<PrimaryProduct> contacts;

    int orderType = 1;
    int PhoneOrderTypes = 4;
    String orderNo = "";

    Common_Class common_class;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        shared_common_pref = new Shared_Common_Pref(this);
        common_class = new Common_Class(this);
        SF_CODE = shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code);
        DIVISION_CODE = shared_common_pref.getvalue(Shared_Common_Pref.Div_Code);
        String carListAsString = getIntent().getStringExtra("list_as_string");
        // Log.v("carListAsString", carListAsString);
        viewTotal = findViewById(R.id.view_total);
        btnSubmt = findViewById(R.id.add_cart);
        deleteViewModel = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);

        if(getIntent().hasExtra("order_type"))
            orderType = getIntent().getIntExtra("order_type", 1);

        if(getIntent().hasExtra("PhoneOrderTypes"))
            PhoneOrderTypes = getIntent().getIntExtra("PhoneOrderTypes", 4);

        if(getIntent().hasExtra("orderNo"))
            orderNo = getIntent().getStringExtra("orderNo");

        getToolbar();
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
                    viewTotal.setText(Constant.roundTwoDecimals(Double.parseDouble(GrandTotal)));
                    shared_common_pref.clear_pref("SubTotal");
                    Log.v("grandsub", GrandTotal);
                } else {
                    Log.v("grand", GrandTotal);
                    viewTotal.setText(Constant.roundTwoDecimals(Double.parseDouble(GrandTotal)));
                }
            }
        }catch (NumberFormatException ee){
            Log.v("11grand", GrandTotal);
            viewTotal.setText(Constant.roundTwoDecimals(Double.parseDouble(GrandTotal)));
        }
//new code added stop
        contactViewModel = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
        contactViewModel.getFilterDatas().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {


                updateTotal(contacts);
/*
                btnSubmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveProduct(contacts);
                    }
                });
*/

            }
        });


        btnSubmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getData().size() >0 && Double.parseDouble(viewTotal.getText().toString().replaceAll("[a-b]", ""))>0){
                    if(orderType == 1)
                        SavePrimaryProduct(adapter.getData());
                    else{
                        if(!Constant.isInternetAvailable(ViewCartActivity.this) && !orderNo.equals("") ){
                            Toast.makeText(ViewCartActivity.this, "Edit order cannot be offline", Toast.LENGTH_SHORT).show();
                        }else
                            SaveSecondaryProduct(adapter.getData());

                    }

                }else {
                    Toast.makeText(ViewCartActivity.this, "Please choose Products", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

        adapter = new CustomViewAdapter(ViewCartActivity.this,GrandTotal,viewTotal, orderType, new DMS.viewProduct() {

            @Override
            public void onViewItemClick(String itemID, String productName, String catName, String catImg, Float productQty, Float productRate) {


            }
        });

        viewRecyclerview = (RecyclerView) findViewById(R.id.report_list);
        viewRecyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        viewRecyclerview.setLayoutManager(layoutManager);
        viewRecyclerview.setAdapter(adapter);
        DateTime();
        currentTime();
        locationInitialize();
        startLocationUpdates();
    }


    /*Toolbar*/
    public void getToolbar() {

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });
        toolHeader = (TextView) findViewById(R.id.toolbar_title);

        if(orderType == 1)
            toolHeader.setText("PRIMARY VIEWCART");
        else
            toolHeader.setText("SECONDARY VIEWCART");

//        toolSearch = (EditText) findViewById(R.id.toolbar_search);
//        toolSearch.setVisibility(View.GONE);

    }

    private void showExitDialog() {
        AlertDialogBox.showDialog(ViewCartActivity.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
//                        Intent aa=new Intent(ViewCartActivity.this,PrimaryOrderProducts.class);
//                        aa.putExtra("GrandTotal",GrandTotal);
//                        startActivity(aa);
            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
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
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        dialog.setIndeterminate(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_progress);
        return dialog;
    }

    /*Save to server*/

    public void SavePrimaryProduct(List<PrimaryProduct> carsList) {

//        Log.v("SAVE_PRODUCT", "INCIKC");
        common_class.ProgressdialogShow(1, "");

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
            stockReportObject.put("Super_Stck_code", null);
            stockReportObject.put("Worked_With", "''");
            stockReportObject.put("location", locationValue);
            stockReportObject.put("geoaddress", "");
            stockReportObject.put("stockist_code", sFCODE);
            stockReportObject.put("superstockistid", "''");
            stockReportObject.put("Stk_Meet_Time", dateTime);
            stockReportObject.put("modified_time", dateTime);
            stockReportObject.put("orderValue", Constant.roundTwoDecimals(Double.parseDouble(viewTotal.getText().toString().replaceAll("[a-b]", ""))));
            stockReportObject.put("Aob", null);
            stockReportObject.put("CheckinTime", checkInTime);
            stockReportObject.put("CheckoutTime", checkInTime);
            stockReportObject.put("f_key", fkeyStock);
            stockReportObject.put("PhoneOrderTypes", PhoneOrderTypes);

            fkeyStock.put("Activity_Report_Code", "'Activity_Report_APP'");
            stockReportObjectArray.put("Activity_Stockist_Report", stockReportObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String stockReport = stockReportObjectArray.toString();
        System.out.println("Activity_Stockist_Report" + stockReport);

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
            eventCapturesObject.put("imgurl", "''");
            eventCapturesObject.put("title", "''");
            eventCapturesObject.put("remarks", "''");
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
            JsonObjtHead.put("order_value", Constant.roundTwoDecimals(Double.parseDouble(viewTotal.getText().toString().replaceAll("[a-b]", ""))));
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
        List<JSONObject> myJSONObjects = new ArrayList<>(carsList.size());

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
                person1.put("free", carsList.get(z).getSelectedFree());
                person1.put("f_key", fkeyprodcut);
                person1.put("Productunit", carsList.get(z).getProduct_Sale_Unit());
                person1.put("dis", carsList.get(z).getDiscount());
                person1.put("tax", carsList.get(z).getTax_Value());
                String disAmt = "0";
                if(!carsList.get(z).getDis_amt().equals(""))
                    disAmt = Constant.roundTwoDecimals(Double.parseDouble(carsList.get(z).getDis_amt().replaceAll("-", "")));
                person1.put("dis_value", disAmt);

                String taxAmt = "0";
                if(!carsList.get(z).getTax_amt().equals(""))
                    taxAmt = Constant.roundTwoDecimals(Double.parseDouble(carsList.get(z).getTax_amt().replaceAll("-", "")));
                person1.put("tax_value", taxAmt);
                person1.put("Off_Pro_code", carsList.get(z).getOff_Pro_code());
                person1.put("Off_Pro_name", carsList.get(z).getOff_Pro_name());
                person1.put("Off_Pro_Unit", carsList.get(z).getOff_free_unit());
                person1.put("Off_Scheme_Unit", carsList.get(z).getOff_Pro_Unit());
                person1.put("Con_Fac", carsList.get(z).getProduct_Sale_Unit_Cn_Qty());
                person1.put("UOM", carsList.get(z).getUOM());
                String subTot = "0";
                if(!carsList.get(z).getSubtotal().equals(""))
                    subTot = Constant.roundTwoDecimals(Double.parseDouble(carsList.get(z).getSubtotal()));
                person1.put("Val", subTot);
                person1.put("discount_type", carsList.get(z).getOff_disc_type());
                fkeyprodcut.put("activity_stockist_code", "Activity_Stockist_Report");
                myJSONObjects.add(person1);
                listV.add(String.valueOf((person1)));
                product_count = myJSONObjects.size();

                personarray.put(person1);
                PersonObjectArray.put("Activity_Stk_POB_Report", personarray);
//                String JsonData = PersonObjectArray.toString();

//                System.out.println("Activity_Stk_POB_Report: " + JsonData);

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


        if(Constant.isInternetAvailable(ViewCartActivity.this)){
            HashMap<String, String> data = new HashMap<>();
            data.put(DBController.AXN_KEY,"dcr/save");
            data.put(DBController.DATA_RESPONSE,totalValueString);
            sendDataToServer(data);

        }else {
            DBController dbController = new DBController(ViewCartActivity.this);
            if (dbController.addDataOfflineCalls(String.valueOf(System.currentTimeMillis()), totalValueString, "dcr/save", 1)) {

                if (Constant.isInternetAvailable(this))
                    new Common_Class(this).checkData(dbController, getApplicationContext());
                else
                    Toast.makeText(ViewCartActivity.this, "Primary Order saved in offline", Toast.LENGTH_SHORT).show();

                if (product_count != 0) {

                    deleteViewModel.getAllData().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
                        @Override
                        public void onChanged(List<PrimaryProduct> contacts) {
                            common_class.ProgressdialogShow(2, "");
                            deleteViewModel.delete(contacts);

                            completePreviousActivity(true);

                        }
                    });

//                Toast.makeText(ViewCartActivity.this, "Your order submitted successfully", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                } else {
                    common_class.ProgressdialogShow(2, "");

                    completePreviousActivity(true);
                }

            } else
                Toast.makeText(ViewCartActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
        }
       /* ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
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
        });*/
    }


    @Override
    public void onBackPressed() {
        //     startActivity(new Intent(getApplicationContext(), PrimaryOrderProducts.class));
        showExitDialog();
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
        /*PrimaryProductViewModel contactViewModels = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
        contactViewModels.getFilterDatas().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {
                Log.v("TotalviewcartSize", new Gson().toJson(contacts.size()));
                // item_count.setText("Items :" + new Gson().toJson(contacts.size()));
                // viewTotal.setText("" + sum);
                updateTotal(contacts);
                // mShared_common_pref.save("SubTotal", String.valueOf("0.0"));
            }
        });*/

//        sum= Float.parseFloat(getIntent().getStringExtra("GrandTotal"));
//        if(!"".equals(sum)|| !("0".equals(sum))||sum!=0){
//            grandTotal.setText("" + sum);
//        }


    }

    private void updateTotal(List<PrimaryProduct> contacts) {
        double itemTotal = 0;
        for (PrimaryProduct cars : contacts) {

            double discountValue = 0;
            double taxValue = 0;
            double totalAmt = 0;
            double productCode = 0;
            double productQty = 0;

            if(!cars.getSelectedDisValue().equals("") )
                discountValue = Double.parseDouble(cars.getSelectedDisValue());
            int unitQty = 1;
            if(cars.getProduct_Sale_Unit_Cn_Qty()!=0)
                unitQty = cars.getProduct_Sale_Unit_Cn_Qty();

            if(cars.getProduct_Cat_Code()!=null && !cars.getProduct_Cat_Code().equals(""))
                productCode = Double.parseDouble(cars.getProduct_Cat_Code());

            if(cars.getQty()!=null && !cars.getQty().equals(""))
                productQty = Double.parseDouble(cars.getQty());

            totalAmt = productCode * (productQty * unitQty);
            if(cars.getTax_amt()!=null && !cars.getTax_amt().equals(""))
                taxValue = Double.parseDouble(cars.getTax_amt());

            itemTotal = itemTotal + ((totalAmt- discountValue)+ taxValue);

//                    sum = sum + Float.parseFloat(cars.getSubtotal());
            // sum = sum +Float.parseFloat( cars.getTax_Value())+ Float.parseFloat(cars.getSubtotal())+ Float.parseFloat(cars.getDis_amt())+;
            ;
            //  Log.v("taxamttotal_valbefore", String.valueOf(tax));
//            Log.v("Total_foreviewcart", String.valueOf(itemTotal));
        }

        if(itemTotal <= 0)
            itemTotal = 0;

        viewTotal.setText("" + Constant.roundTwoDecimals(itemTotal));
        shared_common_pref.save("GrandTotal", String.valueOf(itemTotal));

        try {
            adapter.filteredContact(contacts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void SaveSecondaryProduct(List<PrimaryProduct> carsList) {

        common_class.ProgressdialogShow(1, "");

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
            stockReportObject.put("Super_Stck_code", null);
            stockReportObject.put("Worked_With", "''");
            stockReportObject.put("location", locationValue);
            stockReportObject.put("geoaddress", "");
            stockReportObject.put("stockist_code", sFCODE);
            stockReportObject.put("superstockistid", "''");
            stockReportObject.put("Stk_Meet_Time", dateTime);
            stockReportObject.put("modified_time", dateTime);
            stockReportObject.put("orderValue", Constant.roundTwoDecimals(Double.parseDouble(viewTotal.getText().toString().replaceAll("[a-b]", ""))));
            stockReportObject.put("Aob", null);
            stockReportObject.put("CheckinTime", checkInTime);
            stockReportObject.put("CheckoutTime", checkInTime);
            stockReportObject.put("f_key", fkeyStock);

            fkeyStock.put("Activity_Report_Code", "'Activity_Report_APP'");
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
            eventCapturesObject.put("imgurl", "''");
            eventCapturesObject.put("title", "''");
            eventCapturesObject.put("remarks", "''");
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

        Log.d("Compititor_Product", ""+ ComProduct);


        /*product_order_list*/
        List<JSONObject> myJSONObjects = new ArrayList<>(carsList.size());

        JSONArray personarray = new JSONArray();
        PersonObjectArray = new JSONObject();
        JSONObject fkeyprodcut = new JSONObject();

        for (int z = 0; z < carsList.size(); z++) {
            person1 = new JSONObject();

            try {

                double currentPiecePrice = 0;
                double currentUOMPrice = 0;
                double currentUOMDis = 0;
                double currentPieceDis = 0;


                //adding items to first json object
                person1.put("Productname", carsList.get(z).getPname());
                person1.put("ProductCode", carsList.get(z).getUID());
                person1.put("Qty", carsList.get(z).getQty());
                person1.put("Rate", carsList.get(z).getProduct_Cat_Code());
                person1.put("cb_qty", 0);
                person1.put("ProductRate", carsList.get(z).getProduct_Cat_Code());

                String rateMode = "free";
                if(carsList.get(z).isEdited())

                if(carsList.get(z).isEdited()){
                    rateMode = "priceEdit";
                try {
                    if(carsList.get(z).getEditedDiscount()!=null && !carsList.get(z).getEditedDiscount().equals("")){
                        currentPieceDis = Constant.roundTwoDecimals1(Double.parseDouble(carsList.get(z).getEditedDiscount()));
                        currentUOMDis = Constant.roundTwoDecimals1(Double.parseDouble(carsList.get(z).getEditedDiscount()) * carsList.get(z).getProduct_Sale_Unit_Cn_Qty());
                    }

                    if(carsList.get(z).getEditedPrice()!=null && !carsList.get(z).getEditedPrice().equals("")){

                        currentPiecePrice = Constant.roundTwoDecimals1(Double.parseDouble(carsList.get(z).getEditedPrice()));

                        currentUOMPrice = Constant.roundTwoDecimals1(Double.parseDouble(carsList.get(z).getEditedPrice()) * carsList.get(z).getProduct_Sale_Unit_Cn_Qty());

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


                person1.put("currentPiecePrice", currentPiecePrice);
                person1.put("currentUOMPrice", currentUOMPrice);
                person1.put("currentUOMDis", currentUOMDis);
                person1.put("currentPieceDis", currentPieceDis);
                person1.put("totalDis", currentUOMDis * Integer.parseInt(carsList.get(z).getQty()));
                person1.put("totalPrice", currentUOMPrice * Integer.parseInt(carsList.get(z).getQty()));
                }

                person1.put("rateMode", rateMode);
                person1.put("free", carsList.get(z).getSelectedFree());
                person1.put("f_key", fkeyprodcut);
                person1.put("Productunit", carsList.get(z).getProduct_Sale_Unit());
                person1.put("dis", carsList.get(z).getDiscount());
                person1.put("tax", carsList.get(z).getTax_Value());


                String disAmt = "0";
                if(!carsList.get(z).getDis_amt().equals(""))
                    disAmt = Constant.roundTwoDecimals(Double.parseDouble(carsList.get(z).getDis_amt().replaceAll("-", "")));
                person1.put("dis_value", disAmt);

                String taxAmt = "0";
                if(!carsList.get(z).getTax_amt().equals(""))
                    taxAmt = Constant.roundTwoDecimals(Double.parseDouble(carsList.get(z).getTax_amt().replaceAll("-", "")));
                person1.put("tax_value", taxAmt);

                person1.put("Off_Pro_code", carsList.get(z).getOff_Pro_code());
                person1.put("Off_Pro_name",carsList.get(z).getOff_Pro_name());
                person1.put("Off_Pro_Unit", carsList.get(z).getOff_free_unit());
                person1.put("Off_Scheme_Unit", carsList.get(z).getOff_Pro_Unit());
                person1.put("Con_Fac", carsList.get(z).getProduct_Sale_Unit_Cn_Qty());
                person1.put("UOM", carsList.get(z).getUOM());

                String subTot = "0";
                if(!carsList.get(z).getSubtotal().equals(""))
                    subTot = Constant.roundTwoDecimals(Double.parseDouble(carsList.get(z).getSubtotal()));
                person1.put("Val", subTot);

                person1.put("discount_type", carsList.get(z).getOff_disc_type());
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

           /* try {
                //adding items to first json object
                person1.put("Productname", carsList.get(z).getProductName());
                person1.put("ProductCode", carsList.get(z).getProductcode());
                person1.put("Qty", carsList.get(z).getProductqty());
                person1.put("Rate", carsList.get(z).getProductRate());
                person1.put("cb_qty", 0);
                person1.put("ProductRate", carsList.get(z).getProductActualTotal());
                person1.put("free", 0);
                person1.put("f_key", fkeyprodcut);
                person1.put("Productunit", carsList.get(z).getProductUnit());
                person1.put("dis", carsList.get(z).getDiscount());
                person1.put("tax", carsList.get(z).getTaxValue());
                person1.put("dis_value", carsList.get(z).getDisAmt().toString().replace("-",""));
                person1.put("tax_value", carsList.get(z).getTaxAmt().toString().replace("-",""));
                person1.put("Off_Pro_code", "0");
                person1.put("Off_Pro_name", "0");
                person1.put("Off_Pro_Unit", "0");
                person1.put("Con_Fac", carsList.get(z).getConFac());

                person1.put("Val",new DecimalFormat("##0.00").format(totalVal));

                person1.put("UOM", carsList.get(z).getProductUOM());
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
            }*/
        }




        /*Head Details*/
        JSONArray JsonArryHead = new JSONArray();
        JSONHEAD = new JSONObject();
        JSONObject JsonObjtHead = new JSONObject();

        double OrderValue = Float.parseFloat(GrandTotal);
        Log.v("OrderValue", String.valueOf(OrderValue));
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
            JsonObjtHead.put("order_value", Constant.roundTwoDecimals(Double.parseDouble(viewTotal.getText().toString().replaceAll("[a-b]", ""))));
            JsonObjtHead.put("sub_tot", 0);
            JsonObjtHead.put("dis_tot", 0);
            JsonObjtHead.put("tax_tot", 0);
            JsonArryHead.put(JsonObjtHead);
            JSONHEAD.put("Json_Head", JsonArryHead);
            Log.v("VIEW_CART_ACTIVITY", JSONHEAD.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray JsonArryProceed = new JSONArray();
        JSONObject JSONPROCEED = new JSONObject();
        JSONObject js = new JSONObject();
        try {

            js.put("Retcode", shared_common_pref.getvalue("RetailerID"));
            js.put("Retname", shared_common_pref.getvalue("RetailName"));
            js.put("sfCode", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
            js.put("Stkcode", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("Stkname", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Name));
            js.put("Divcode", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("Remark", shared_common_pref.getvalue1("Remarks"));
            js.put("orderNo", orderNo);
            js.put("PhoneOrderTypes", PhoneOrderTypes);

            if(!orderNo.equals(""))
                js.put("isExistingOrder", "true");
            JsonArryProceed.put(js);
            JSONPROCEED.put("Json_proceed", js);
            Log.v("VIEW_CART_ACTIVITY", JSONPROCEED.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        sendArray = new JSONArray();
      /*  sendArray.put(reportObjectArray);
        sendArray.put(stockReportObjectArray);*/
        sendArray.put(PersonObjectArray);
        /* sendArray.put(sampleReportObjectArray);
        sendArray.put(eventCapturesObjectArray);
        sendArray.put(pendingBillObjectArray);
        sendArray.put(ComProductObjectArray);*/
        sendArray.put(JSONHEAD);
        sendArray.put(JSONPROCEED);
        totalValueString = sendArray.toString();

        if(Constant.isInternetAvailable(ViewCartActivity.this)){
            HashMap<String, String> data = new HashMap<>();
            data.put(DBController.AXN_KEY,"dcr/secordersave");
            data.put(DBController.DATA_RESPONSE,totalValueString);
            sendDataToServer(data);

        }else {
            DBController dbController = new DBController(ViewCartActivity.this);

            if(dbController.addDataOfflineCalls(String.valueOf(System.currentTimeMillis()), totalValueString, "dcr/secordersave", 1)){
                if(Constant.isInternetAvailable(this))
                    new Common_Class(this).checkData(dbController,getApplicationContext());
                else
                    Toast.makeText(ViewCartActivity.this, "Secondary Order saved in offline", Toast.LENGTH_SHORT).show();

//                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));

                if (product_count != 0) {

                    deleteViewModel = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
                    deleteViewModel.getAllData().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
                        @Override
                        public void onChanged(List<PrimaryProduct> contacts) {
                            common_class.ProgressdialogShow(2, "");

                            deleteViewModel.delete(contacts);
                            completePreviousActivity(true);
                        }
                    });

//                    startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                } else {
                    completePreviousActivity(true);
                }
            }
            else {
                common_class.ProgressdialogShow(2, "");
                Toast.makeText(ViewCartActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
            }


        }

    }



    private void sendDataToServer(HashMap<String, String> data) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestBody = null;
        try {
            requestBody = Constant.toRequestBody(new JSONArray(data.get(DBController.DATA_RESPONSE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> responseBodyCall = apiInterface.submitValue(data.get(DBController.AXN_KEY), shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), requestBody, shared_common_pref.getvalue(Shared_Common_Pref.State_Code), "MGR");
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    common_class.ProgressdialogShow(2, "");

                    String res = response.body().string();
                    Log.d(TAG, "onResponse: res "+ res);

                    if(res!=null && !res.equals("")){
                        JSONObject jsonRootObject = new JSONObject(res);
                        Log.d(TAG, "onResponse: "+ jsonRootObject);

                        if(jsonRootObject.has("success") && jsonRootObject.getBoolean("success")){
                            if(jsonRootObject.has("Msg"))
                                Toast.makeText(ViewCartActivity.this,jsonRootObject.getString("Msg") , Toast.LENGTH_SHORT).show();

                            if (product_count != 0) {

                                deleteViewModel = ViewModelProviders.of(ViewCartActivity.this).get(PrimaryProductViewModel.class);
                                deleteViewModel.getAllData().observe(ViewCartActivity.this, new Observer<List<PrimaryProduct>>() {
                                    @Override
                                    public void onChanged(List<PrimaryProduct> contacts) {
                                        deleteViewModel.delete(contacts);
                                        completePreviousActivity(true);
                                    }
                                });

//                                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                            } else {
                                completePreviousActivity(true);
                            }

                        }else {

                            if(jsonRootObject.has("Msg"))
                                Toast.makeText(ViewCartActivity.this,jsonRootObject.getString("Msg") , Toast.LENGTH_SHORT).show();
                        }

                    }else
                        Toast.makeText(ViewCartActivity.this,"Something went wrong, please try again", Toast.LENGTH_SHORT).show();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ViewCartActivity.this,"Something went wrong, please try again", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                common_class.ProgressdialogShow(2, "");
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(ViewCartActivity.this,"Something went wrong, please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void completePreviousActivity(boolean closeActivity) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("closeActivity", closeActivity);
        setResult(-1, resultIntent);
        finish();
    }



}
