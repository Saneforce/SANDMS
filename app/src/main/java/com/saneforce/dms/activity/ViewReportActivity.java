
package com.saneforce.dms.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.saneforce.dms.DMSApplication;
import com.saneforce.dms.R;
import com.saneforce.dms.adapter.DateReportAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.listener.PrimaryProductDao;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.AlertDialogBox;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.CameraPermission;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.CustomListViewDialog;
import com.saneforce.dms.utils.ImageFilePath;
import com.saneforce.dms.utils.PrimaryProductDatabase;
import com.saneforce.dms.utils.Shared_Common_Pref;
import com.saneforce.dms.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewReportActivity extends AppCompatActivity implements DMS.Master_Interface{
    private static final String TAG = ViewReportActivity.class.getSimpleName();
    TextView toolHeader, txtProductId, txtProductDate;

    public static final int ACTIVITY_REQUEST_CODE = 2;
    private static final int SELECT_PICTURE = 103;
    private static final int CAMERA_REQUEST = 1888;

    ImageView imgBack,imgShare;

    RecyclerView DateRecyclerView;
    String productId;
    String orderDate;
    String formDate;
    String toDate;
    //secondary or primary
    String reportType;
    //    String OrderAmt;
//    String OrderTax;
    DateReportAdapter mDateReportAdapter;


    Shared_Common_Pref shared_common_pref;
    ArrayList<Integer> mArrayList;
    TextView TotalValue;
    Button PayNow,Delete;
    Double OrderTaxCal,  OrderAmtNew,OrderValueTotal;
    Toolbar toolbar_top;
    LinearLayout  linearLayout;
    //    private Bitmap bitmap;
    DBController dbController;
    Common_Class mCommon_class;
    JSONArray jsonArray;
    TextView tv_order_type;
    LinearLayout ll_order_type;
    int categoryCode = -1;
    ImageView ib_logout;
    //    String orderCreatedDateTime= "";
    String orderNo = "";

    String editOrder ="0";

    int Paymentflag = 1, Dispatch_Flag = 1;
    int orderType =- 1;

    LinearLayout lin_offline;
    EditText edt_utr;
    TextView tv_date;
    TextView txt_offline_mode;
    TextView et_amount;
    ImageView imgSource;
    ImageView iv_choose_photo;
    Uri outputFileUri;
    String serverFileName = "";
    String finalPath = "", filePath = "", PaymentTypecode = "";
    String currentDate ="";

    List<Common_Model> modelOffileData = new ArrayList<>();
    Common_Model mCommon_model_spinner;
    boolean isDisPatch = false;
    CustomListViewDialog customDialog;

    //1 bill desk
    //2 razor pay

    int paymentType = 1;

    LinearLayout ll_dispatch_date;
    TextView tv_dispatch_date;
    LinearLayout ll_payment_type;
    TextView tv_payment_type;
    LinearLayout ll_check_number;
    TextView tv_check_utr_no;
    LinearLayout ll_attachment;
    ImageView iv_attachment;

    TextView cheque_no_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        linearLayout =  findViewById(R.id.linearlayout);

        toolbar_top=findViewById(R.id.toolbar_top);
        tv_order_type=findViewById(R.id.tv_order_type);
        ll_order_type=findViewById(R.id.ll_order_type);

        lin_offline=findViewById(R.id.lin_offline);
        edt_utr=findViewById(R.id.edt_utr);
        tv_date=findViewById(R.id.tv_date);
        et_amount=findViewById(R.id.et_amount);
        imgSource=findViewById(R.id.imgSource);
        txt_offline_mode=findViewById(R.id.txt_offline_mode);
        iv_choose_photo=findViewById(R.id.iv_choose_photo);
        ll_dispatch_date=findViewById(R.id.ll_dispatch_date);
        tv_dispatch_date=findViewById(R.id.tv_dispatch_date);
        ll_payment_type=findViewById(R.id.ll_payment_type);
        tv_payment_type=findViewById(R.id.tv_payment_type);
        ll_check_number=findViewById(R.id.ll_check_number);
        tv_check_utr_no=findViewById(R.id.tv_check_utr_no);
        ll_attachment=findViewById(R.id.ll_attachment);
        iv_attachment=findViewById(R.id.iv_attachment);
        cheque_no_label=findViewById(R.id.cheque_no_label);

        toolbar_top.setVisibility(View.VISIBLE);

        mArrayList = new ArrayList<>();
        TotalValue = findViewById(R.id.total_value);
        shared_common_pref = new Shared_Common_Pref(this);

        reportType = shared_common_pref.getvalue("OrderType");
        dbController = new DBController(this);
        mCommon_class = new Common_Class(this);

        getToolbar();
        //OrderAmt = Double.parseDouble(String.valueOf(getIntent().getSerializableExtra("OderValue")));
        //new code start
        // OrderTax="5%";
        // OrderAmtNew=Double.parseDouble(String.valueOf(getIntent().getSerializableExtra("OderValue")));


        //OrderTaxCal=(OrderAmtNew*(5/100.0f))+OrderAmtNew;
        //TotalValue.setText("Rs."+new DecimalFormat("##.##").format( OrderTaxCal));
        ///new code stop

        //TotalValue.setText("Rs." + String.valueOf(getIntent().getSerializableExtra("OderValue")));

        Intent intent = getIntent();
        productId = intent.getStringExtra("ProductID");
        orderDate = intent.getStringExtra("OrderDate");
        formDate = intent.getStringExtra("FromDate");
        toDate = intent.getStringExtra("ToDate");
        editOrder = intent.getStringExtra("editOrder");
        Paymentflag = intent.getIntExtra("Paymentflag", 1);
        Dispatch_Flag = intent.getIntExtra("Dispatch_Flag", 1);

        String dispatch_date = "", payment_type = "", payment_option = "", check_utr_no = "", attachment = "";

        if(intent.hasExtra("dispatch_date")){
            dispatch_date = intent.getStringExtra("dispatch_date");
        }

        if(intent.hasExtra("payment_type")){
            payment_type = intent.getStringExtra("payment_type");
        }

        if(intent.hasExtra("payment_option")){
            payment_option = intent.getStringExtra("payment_option");
        }

        if(intent.hasExtra("check_utr_no")){
            check_utr_no = intent.getStringExtra("check_utr_no");
        }

        if(intent.hasExtra("attachment")){
            attachment = intent.getStringExtra("attachment");
        }

        if(dispatch_date!=null && !dispatch_date.equals("")){
            ll_dispatch_date.setVisibility(View.VISIBLE);
            tv_dispatch_date.setText(dispatch_date);
        }else
            ll_dispatch_date.setVisibility(View.GONE);

        if(payment_option!=null && !payment_option.equals("")){
            payment_type = payment_type + " - "+payment_option;
        }

        if(payment_type!=null && !payment_type.equals("")){
            ll_payment_type.setVisibility(View.VISIBLE);
            tv_payment_type.setText(payment_type);
        }else
            ll_payment_type.setVisibility(View.GONE);


        if(check_utr_no!=null && !check_utr_no.equals("")){
            ll_check_number.setVisibility(View.VISIBLE);
            tv_check_utr_no.setText(check_utr_no);
        }else
            ll_check_number.setVisibility(View.GONE);


        if(attachment!=null && !attachment.equals("") && !attachment.endsWith("/")){
            ll_attachment.setVisibility(View.VISIBLE);
            try {
                RequestOptions myOptions = new RequestOptions()
                        .fitCenter() // or centerCrop
                        .override(100, 100);

                Glide.with(ViewReportActivity.this)
                        .asBitmap()
                        .apply(myOptions)
                        .load(attachment)
                        .into(iv_attachment);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }else
            ll_attachment.setVisibility(View.GONE);


        try {
            if(intent.hasExtra("orderType") && intent.getStringExtra("orderType")!=null && !intent.getStringExtra("orderType").equals(""))
                orderType = Integer.parseInt(intent.getStringExtra("orderType"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(orderType!=-1){
            ll_order_type.setVisibility(View.VISIBLE);

            String orderValue = "Field Order";
            if(orderType == 1)
                orderValue = "Phone Order";

            tv_order_type.setText(orderValue);

        }else {
            ll_order_type.setVisibility(View.GONE);
        }


        DateRecyclerView = findViewById(R.id.date_recycler);
        DateRecyclerView.setHasFixedSize(true);
        DateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtProductId =  findViewById(R.id.txt_product_id);
        txtProductDate = (TextView) findViewById(R.id.txt_order_Date);
        txtProductId.setText(productId);
        txtProductDate.setText(orderDate);

        PayNow = findViewById(R.id.green_btn);
        Delete = findViewById(R.id.red_btn);
        ib_logout = findViewById(R.id.ib_logout);

        if(ApiClient.APP_TYPE == 2)
            Delete.setText("Delete");
        else
            Delete.setText("Cancel");

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });

        if(reportType.equals("2")){
            if(editOrder.equals("1") && Paymentflag == 0){
                ib_logout.setVisibility(View.VISIBLE);
                ib_logout.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_24, null));
                ib_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Constant.isInternetAvailable(ViewReportActivity.this)){
                            mCommon_class.ProgressdialogShow(1, "");
                            if(!dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_BRAND).equals("") &&
                                    !dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA).equals("")){
                                new PopulateDbAsyntasks(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();
                            }else
                                brandSecondaryApi();
                        }else
                            Toast.makeText(ViewReportActivity.this, "Please check the internet connection", Toast.LENGTH_SHORT).show();

                    }
                });
            }else {
                ib_logout.setVisibility(View.GONE);
            }
            if(reportType.equals("1") || Dispatch_Flag ==0){
                lin_offline.setVisibility(View.VISIBLE);
                isDisPatch = true;

                PayNow.setText("Dispatch");
                PayNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!Constant.isInternetAvailable(ViewReportActivity.this))
                            Toast.makeText(ViewReportActivity.this, "Please check the Internet connection", Toast.LENGTH_SHORT).show();
                        else if(isDisPatch && txt_offline_mode.getText().toString().equals(""))
                            Toast.makeText(ViewReportActivity.this, "Please Select the Payment Type", Toast.LENGTH_SHORT).show();
                        else if(isDisPatch && iv_choose_photo.getVisibility() ==View.VISIBLE && serverFileName.equals("")) {
                            Toast.makeText(ViewReportActivity.this, "Please choose Attachment", Toast.LENGTH_SHORT).show();
                        }else {
                            payOffline();
                        }

                    }
                });
                currentDate = TimeUtils.getCurrentTime(TimeUtils.FORMAT2);
                tv_date.setText(currentDate);
                tv_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int day, month, year;
                        if(!tv_date.getText().toString().equals("")){
                            String[] dateArray =  tv_date.getText().toString().split("/");
                            day = Integer.parseInt(dateArray[0]);
                            month = Integer.parseInt(dateArray[1])-1;
                            year = Integer.parseInt(dateArray[2]);
                        }else {
                            Calendar c = Calendar.getInstance();

                            day = c.get(Calendar.MONTH);
                            month = c.get(Calendar.MONTH);
                            year = c.get(Calendar.YEAR);
                        }
                        DatePickerDialog dialog = new DatePickerDialog(ViewReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String _year = String.valueOf(year);
                                String _month = (month+1) < 10 ? "0" + (month+1) : String.valueOf(month+1);
                                String _date = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                                String _pickedDate = year + "-" + _month + "-" + _date;
                                Log.e("PickedDate: ", "Date: " + _pickedDate); //2019-02-12
                                currentDate = _date +"/"+_month+"/"+_year;
                                tv_date.setText(currentDate);


                            }
                        }, year, month, day);
                        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                        dialog.show();
                    }
                });
                getOfflineMode();
            }else{
                PayNow.setVisibility(View.GONE);
            }

        }else {
            ib_logout.setVisibility(View.GONE);
            PayNow.setText("Pay Now");
            PayNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PayNow();
                }
            });
        }

    }

    private void showDeleteDialog(){
        AlertDialogBox.showDialog(ViewReportActivity.this, "", "Do you Surely want to delete this order?", "Yes", "NO", false, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
                DeleteOrder();
            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constant.isInternetAvailable(this)){
            ViewDateReport();
        }else
            Toast.makeText(ViewReportActivity.this, "Please check the Internet connection", Toast.LENGTH_SHORT).show();

    }

    public void payOffline() {
        JSONObject js = new JSONObject();
        try {


            js.put("OrderID", productId);
            js.put("Stkcode", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("Retcode", custCode);
            js.put("divisionCode", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));

            String option = txt_offline_mode.getText().toString();

            js.put("PaymentTypeName", option);
            js.put("PaymentTypeCode", PaymentTypecode);
            js.put("UTRNumber", edt_utr.getText().toString());
            js.put("Attachement", serverFileName);
            js.put("Retcode", custCode);
            js.put("Amount", OrderValueTotal);

            if(!option.equalsIgnoreCase("cash")){
                js.put("cheque_date", TimeUtils.changeFormat(TimeUtils.FORMAT2,TimeUtils.FORMAT1,tv_date.getText().toString()));
                js.put("cheque_amount", et_amount.getText().toString());
            }else {
                js.put("cheque_date", "");
                js.put("cheque_amount", "");
            }

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> responseBodyCall;
            responseBodyCall = apiInterface.getDetails("save/dispatchpayment", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), js.toString());

            responseBodyCall.enqueue(new Callback<JsonObject>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject jsonObject = response.body();
                    Log.v("Payment_Response", jsonObject.toString());
                    String msg = "";

                    if(jsonObject.has("Msg"))
                        msg = jsonObject.get("Msg").toString();

                    if (jsonObject.get("success").toString().equalsIgnoreCase("true")){
                        //    Toast.makeText(getApplicationContext(), "sign"+signature, Toast.LENGTH_LONG).show();
                        //     Toast.makeText(getApplicationContext(), "razorid"+responseid, Toast.LENGTH_LONG).show();
                        //     Toast.makeText(getApplicationContext(), "razpay"+razorid, Toast.LENGTH_LONG).show();
                        if(msg.equals(""))
                            msg = "Dispatched Successfully";
                        Toast.makeText(DMSApplication.getApplication(), msg, Toast.LENGTH_SHORT).show();

                        onBackPressed();

                    }else {
                        if(msg.equals(""))
                            msg = "Server problem, please try again";
                        Toast.makeText(DMSApplication.getApplication(), msg, Toast.LENGTH_SHORT).show();

                    }



//                    Intent a=new Intent(PaymentDetailsActivity.this,ReportActivity.class);
//                    startActivity(a);

                    //  startActivity(new Intent(PaymentDetailsActivity.this,RazorPayment.class));
//                    if (PaymntMode.equalsIgnoreCase("Online")) {
//
//
//                        AsyncCallWS task = new AsyncCallWS();
//                        task.execute();
//
//                    } else {
//                        Intent a=new Intent(PaymentDetailsActivity.this,ReportActivity.class);
//                        startActivity(a);
//                       // finish();//jul 19 working code commented
//                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Toolbar*/
    public void getToolbar() {

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgShare=findViewById(R.id.toolbar_share);
        imgShare.setVisibility(View.VISIBLE);

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if(!Environment.isExternalStorageManager())
                        requestPermission();
                    else {
                        saveBitmap(createBitmap3(linearLayout, linearLayout.getWidth(), linearLayout.getHeight()));
                    }
                }else {
                    checkPermission();
                }

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intentMethod();
                onBackPressed();
            }
        });
        toolHeader = (TextView) findViewById(R.id.toolbar_title);
        toolHeader.setText("REPORT DETAILS");
//        toolHeader.setText(R.string.View_Rep);
//        toolSearch = (EditText) findViewById(R.id.toolbar_search);
//        toolSearch.setVisibility(View.GONE);

    }

    public void PayNow() {
        Intent payIntent = new Intent(getApplicationContext(), PaymentDetailsActivity.class);

        payIntent.putExtra("OrderId", productId);
        payIntent.putExtra("Date", orderDate);
        //  payIntent.putExtra("Amount", OrderAmt);
        payIntent.putExtra("Amount", OrderValueTotal);
        payIntent.putExtra("paymentGateWayType", shared_common_pref.getIntvalue(Shared_Common_Pref.PAYMENT_GATEWAY_TYPE));
        startActivityForResult(payIntent, ACTIVITY_REQUEST_CODE);

//        finish();
    }


    public void ViewDateReport() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> responseBodyCall;
        if (reportType.equalsIgnoreCase("1")) {
            responseBodyCall = apiInterface.dateReport("get/ViewReport_Details", productId, shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        } else {
            responseBodyCall = apiInterface.dateReport("get/secviewreport_details", productId, shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        }


//        Log.v("ViewDateREquest", responseBodyCall.request().toString());

        responseBodyCall.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonRootObject = null;


                try {
                    jsonRootObject = new JSONObject(response.body().toString());
                    Log.v("ViewDateResponse", jsonRootObject.toString());
                    jsonArray = jsonRootObject.optJSONArray("Data");
                    JSONObject jsonObject = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        //    OrderValueTotal=Double.valueOf(jsonObject.getString("Order_Value"));
                        if(!jsonObject.isNull("taxval"))
                            OrderAmtNew= Double.valueOf(jsonObject.getString("taxval"));
                        //  TotalValue.setText("Rs."+jsonObject.getString("taxval"));//working code commented
                        String total = "0";
                        if(jsonObject.has("OrderVal") && !jsonObject.getString("OrderVal").equals("")){
                            total = Constant.roundTwoDecimals(Double.parseDouble(jsonObject.getString("OrderVal")));
                            OrderValueTotal=Double.valueOf(jsonObject.getString("OrderVal"));

                        }

                        /*if(jsonObject.has("OrderDate")) {
                            orderCreatedDateTime = jsonObject.getString("OrderDate");

                            try {
                                if(TimeUtils.hoursDifference(TimeUtils.getDate(TimeUtils.FORMAT3, orderCreatedDateTime), TimeUtils.getDate(TimeUtils.FORMAT3, TimeUtils.getCurrentTime(TimeUtils.FORMAT3)))<24)
                                    ib_logout.setVisibility(View.VISIBLE);
                                else
                                    ib_logout.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/

                        if(isDisPatch)
                            et_amount.setText(String.valueOf(OrderValueTotal));
                        if(jsonObject.has("OrderNo")) {
                            orderNo = jsonObject.getString("OrderNo");
                        }

                        TotalValue.setText("Rs. "+total);
                        Integer PaymentValue = (Integer) jsonObject.get("Paymentflag");
//                        Log.v("PAYMENT_VALUE", String.valueOf(PaymentValue));
//                        Log.v("PAYMENT_VALUE1", String.valueOf(OrderValueTotal));
//                        Log.v("PAYMENT_VALUE1", String.valueOf(OrderAmtNew));

                        if(reportType.equals("2")){

                            if (editOrder.equals("1") && Paymentflag == 0) {
                                Delete.setVisibility(View.VISIBLE);
                            } else {
                                Delete.setVisibility(View.GONE);
                                ib_logout.setVisibility(View.GONE);
                            }

                            if (Dispatch_Flag == 0) {
                                PayNow.setVisibility(View.VISIBLE);
                            } else {
                                PayNow.setVisibility(View.GONE);
                                ib_logout.setVisibility(View.GONE);
                            }

                        }else {
                            if (PaymentValue == 0) {
                                PayNow.setVisibility(View.VISIBLE);
                                Delete.setVisibility(View.VISIBLE);
                            } else {
                                PayNow.setVisibility(View.GONE);
                                Delete.setVisibility(View.GONE);
                                ib_logout.setVisibility(View.GONE);
                            }
                        }

                        if(!jsonObject.isNull("Cust_Code")){
                            custCode =jsonObject.getString("Cust_Code");
                        }


                    }
                    mDateReportAdapter = new DateReportAdapter(ViewReportActivity.this, jsonArray);
                    DateRecyclerView.setAdapter(mDateReportAdapter);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ViewReportActivity.this, "Something went wrong, please try again",Toast.LENGTH_SHORT ).show();
            }
        });
    }

    String custCode = "";
    public void DeleteOrder() {
        JSONObject js = new JSONObject();
        try {
            js.put("OrderID", productId);
            js.put("Stkcode", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("Retcode", custCode);

            Log.v("JS_VALUE", js.toString());

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> responseBodyCall;

            if (reportType.equalsIgnoreCase("1")) {
                responseBodyCall = apiInterface.getDetails("dcr/cancelprimaryorder", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), js.toString());
            } else {
                responseBodyCall = apiInterface.getDetails("dcr/cancelsecondaryorder", shared_common_pref.getvalue(Shared_Common_Pref.State_Code), js.toString());
            }


            responseBodyCall.enqueue(new Callback<JsonObject>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject jsonObject = response.body();
//                    Log.v("DELETE_RESPONSE", jsonObject.toString());
                    if (jsonObject.get("success").toString().equalsIgnoreCase("true")){
                        Toast.makeText(ViewReportActivity.this, "Order Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

 /*   public void intentMethod() {
        Intent intnet = new Intent(getApplicationContext(), ReportActivity.class);
        intnet.putExtra("FromReport", formDate);
        intnet.putExtra("ToReport", toDate);
        intnet.putExtra("count", 1);
        startActivity(intnet);
        finish();
    }*/


    public Bitmap createBitmap3(View v, int width, int height) {
        // The measurement makes the view specified size
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        v.measure(measuredWidth, measuredHeight);
        // After calling the layout method, you can get the size of the view
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }



    String dirpath = "";
    String fileName = "";
    private void saveBitmap(Bitmap bitmap) {

        fileName  = String.valueOf(System.currentTimeMillis());

        dirpath = android.os.Environment.getExternalStorageDirectory().toString();
        File file = null;
        try {
            // Step 1: First save the picture
            // Save Bitmap pictures to the specified path / sdcard / Boohee /, the file name is named after the current system time, but the pictures saved by this method are not added to the system gallery
            File appDir = new File (Environment.getExternalStorageDirectory (), "receiptImage");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            file = new File(appDir, fileName);
//            file.createNewFile();

            // Create a file output stream object to write data to the file
            FileOutputStream out = new FileOutputStream(file);
            // Store the bitmap as a picture in jpg format
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            // Refresh the file stream
            out.flush();
            out.close();
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Image img = Image.getInstance(file.getAbsolutePath());
            img.setAbsolutePosition(0, 0);

            Rectangle pagesize = new Rectangle(img.getScaledWidth(), img.getScaledHeight());
            Document document = new Document(pagesize);
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/"+ fileName+".pdf")); //  Change pdf's name.
            document.open();
//            float scaler = (img.getHeight() / img.getWidth()) * 100;
//            img.scalePercent(100);
//            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();

//            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ViewReportActivity.this, "image to pdf conversion failure", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        // change with required  application package
        File file1 = new File( dirpath + "/"+ fileName+".pdf");
        intent.setPackage("com.whatsapp");
        if (intent != null && file.exists()) {
            intent.setType("application/pdf");
            Uri uri = FileProvider.getUriForFile(ViewReportActivity.this, getApplicationContext().getPackageName()+ ".provider", file1);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share File"));
        } else {
            Toast.makeText(ViewReportActivity.this, "App not found", Toast.LENGTH_SHORT).show();
        }


    }


    public void checkPermission(){
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report)
            {
                if(report.areAllPermissionsGranted()){

                    saveBitmap(createBitmap3(linearLayout, linearLayout.getWidth(), linearLayout.getHeight()));
                }else
                    Constant.showSnackbar(ViewReportActivity.this, findViewById(R.id.scrolllayout));
//                    Toast.makeText(ViewReportActivity.this, "Please enable storage permission to share pdf ", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }

        }).check();
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission() {

        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
            startActivityForResult(intent, 2296);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 2296);
        }

    }


    public void OffImg(View v) {
        CameraPermission cameraPermission = new CameraPermission(ViewReportActivity.this, getApplicationContext());
        if (!cameraPermission.checkPermission()) {
            cameraPermission.requestPermission();
            Log.v("PERMISSION_CJEDFLHDSL", "NO");
        } else {
            showImageChooserDialog();
        }
    }

    private void showImageChooserDialog(){

        final Dialog dialog = new Dialog(ViewReportActivity.this);
        dialog.setContentView(R.layout.dialog_upload_image);

        ImageButton ibClose = dialog.findViewById(R.id.ib_close);
        ImageButton ibCamera = dialog.findViewById(R.id.ib_camera);
        ImageButton ibGallery = dialog.findViewById(R.id.ib_gallery);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openCamera();

            }
        });
        ibGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                imageChooser();
            }
        });

        dialog.show();

    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        outputFileUri = FileProvider.getUriForFile(ViewReportActivity.this, getApplicationContext().getPackageName() + ".provider", new File(getExternalCacheDir().getPath(), Shared_Common_Pref.Sf_Code + "_" + System.currentTimeMillis() + ".jpeg"));
        Log.v("FILE_PATH", String.valueOf(outputFileUri));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if(requestCode == ACTIVITY_REQUEST_CODE) {

                boolean closeActivity = false;
                if(data!=null && data.hasExtra("closeActivity"))
                    closeActivity = data.getBooleanExtra("closeActivity", false);

                if(closeActivity)
                    ViewReportActivity.this.finish();
            } else if (requestCode == CAMERA_REQUEST) {

                if (outputFileUri != null) {
                    finalPath = "/storage/emulated/0";
                    filePath = outputFileUri.getPath();
                    filePath = filePath.substring(1);
//                    str = filePath.replaceAll("external_files/Android/data/" + BuildConfig.APPLICATION_ID + "/cache/", "");
                    filePath = finalPath + filePath.substring(filePath.indexOf("/"));
                    imgSource.setImageURI(Uri.parse(filePath));
                    imgSource.setVisibility(View.VISIBLE);
                    getMulipart(filePath);
                }else
                    Toast.makeText(ViewReportActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();


            } else if (requestCode == SELECT_PICTURE) {
                // compare the resultCode with the
                // SELECT_PICTURE constant
                // Get the url of the image from data
                if(data!=null && data.getData()!=null){
                    Uri selectedImageUri = data.getData();
                    // update the preview image in the layout
                    imgSource.setImageURI(selectedImageUri);
                    imgSource.setVisibility(View.VISIBLE);
                    String filePath = ImageFilePath.getPath(ViewReportActivity.this, selectedImageUri);
                    getMulipart(filePath);

                }else
                    Toast.makeText(ViewReportActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //permision stop


    private class PopulateDbAsyntasks extends AsyncTask<Void, Void, Void> {
        private PrimaryProductDao contactDao;


        public PopulateDbAsyntasks(PrimaryProductDatabase contactDaos) {
            contactDao = contactDaos.contactDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PrimaryProductDatabase.getInstance(ViewReportActivity.this).clearAllTables();

            secStart();
            Log.v("Data_CHeckng", "Checking_data");
            return null;
        }

    }


    private void secStart() {

        Log.v("Data_CHeckng", "Checking_data");


        String sPrimaryProd = dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA);
        PrimaryProductDao contact = PrimaryProductDatabase.getInstance(this).getAppDatabase()
                .contactDao();
        try {
            JSONArray jsonArray = new JSONArray(sPrimaryProd);
            JSONObject jsonObject = null;
            JSONObject jsonObject1 = null;

            String Scheme = "", Discount="", Scheme_Unit="", Product_Name="", Product_Code="", Package="", Free="", Discount_Type="", Free_Unit="";
            int unitQty = 1;

            for (int i = 0; i < jsonArray.length(); i++) {
                String qty = "0", finalPrice = "0", PSaleUnit ="", taxAmtValue ="0", selectedDiscPercent ="0",
                        discAmtValue ="0", freeQtValue ="0", selectedSchemeValue = "", selectedProductCode = ""
                        , selectedProductName = "", selectedProductUnit = "", discountType = "";
                double editedDisValue = 0, editedPrice = 0;
                boolean isEditedPrice = false;


//                        contact.setSelectedScheme(selectedScheme.getScheme());
//                        contact.setSelectedDisValue(selectedScheme.getDiscountvalue());
//                        contact.setOff_Pro_code(selectedScheme.getProduct_Code());
//                        contact.setOff_Pro_name(selectedScheme.getProduct_Name());
//                        contact.setOff_Pro_Unit(selectedScheme.getScheme_Unit());
                jsonObject = jsonArray.getJSONObject(i);

                String id = String.valueOf(jsonObject.get("id"));
                String Name = String.valueOf(jsonObject.get("name"));
                String PName = String.valueOf(jsonObject.get("Pname"));
                String PRate = String.valueOf(jsonObject.get("Product_Cat_Code"));
                String PBarCode = String.valueOf(jsonObject.get("Product_Brd_Code"));
                String PId = String.valueOf(jsonObject.get("PID"));
                String PUOM = String.valueOf(jsonObject.get("UOM"));
                PSaleUnit = String.valueOf(jsonObject.get("Default_UOM"));
                String PDiscount = String.valueOf(jsonObject.get("Discount"));
                String PTaxValue = "0";
                if(jsonObject.has("Tax_value") && !jsonObject.getString("Tax_value").equals(""))
                    PTaxValue = jsonObject.getString("Tax_value");

                if(jsonObject.has("Conv_Fac"))
                    unitQty = jsonObject.getInt("Conv_Fac");

//                String PCon_fac = "1";
//                if(jsonObject.has("Conv_Fac"))
//                    PCon_fac = jsonObject.getString("Conv_Fac");

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
                                Product_Code, Package, Free, Discount_Type,Free_Unit ));
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
                qty = getQty(id);

                if(!qty.equals("0")){
                    unitQty = getConQty(id);
                    PSaleUnit = getValue(id, "unit");

                    int product_Sale_Unit_Cn_Qty = 1;
                    if(unitQty!=0)
                        product_Sale_Unit_Cn_Qty= unitQty;
                    int tempQty = Integer.parseInt(qty) * product_Sale_Unit_Cn_Qty;

                    PrimaryProduct.SchemeProducts selectedScheme = null;
                    int previousSchemeCount = 0;
                    for(PrimaryProduct.SchemeProducts scheme : schemeList){
                        if(!scheme.getScheme().equals("")) {
                            int currentSchemeCount = Integer.parseInt(scheme.getScheme());
                            if(previousSchemeCount <= currentSchemeCount &&  currentSchemeCount <= tempQty){
                                previousSchemeCount =currentSchemeCount;
                                selectedScheme = scheme;
                            }
                        }
                    }


                    double discountValue = 0;
                    double productAmt = 0;
                    double schemeDisc = 0;


                    if(PRate!=null && !PRate.equals(""))
                        productAmt = Double.parseDouble(PRate);
//                    String OffFreeUnit = "";
                    String rateMode = getValue(id, "rateMode");
                    isEditedPrice = rateMode.equals("priceEdit");


                    if(!isEditedPrice) {
                        if (selectedScheme != null) {

//                        if(selectedScheme.getFree_Unit()!=null)
//                            OffFreeUnit = selectedScheme.getFree_Unit();

//                        contact.setSelectedScheme(selectedScheme.getScheme());
//                        contact.setSelectedDisValue(selectedScheme.getDiscountvalue());
//                        contact.setOff_Pro_code(selectedScheme.getProduct_Code());
//                        contact.setOff_Pro_name(selectedScheme.getProduct_Name());
//                        contact.setOff_Pro_Unit(selectedScheme.getScheme_Unit());


                            if (!selectedScheme.getDiscount_Type().equals(""))
                                discountType = selectedScheme.getDiscount_Type();
                            else
                                discountType = "%";


                            String packageType = selectedScheme.getPackage();

                            double freeQty = 0;
                            double packageCalc = (tempQty / Double.parseDouble(selectedScheme.getScheme()));

                            if (packageType.equals("Y"))
                                packageCalc = (int) packageCalc;

                            if (!selectedScheme.getFree().equals(""))
                                freeQty = packageCalc * Integer.parseInt(selectedScheme.getFree());
                            freeQtValue = String.valueOf((int) freeQty);

//                        contact.setSelectedFree(String.valueOf((int) freeQty));


                            if (selectedScheme.getDiscountvalue() != null && !selectedScheme.getDiscountvalue().equals(""))
                                schemeDisc = Double.parseDouble(selectedScheme.getDiscountvalue());

                            switch (discountType) {
                                case "%":
                                    discountValue = (productAmt * tempQty) * (schemeDisc / 100);
                                    break;
                                case "Rs":
                                    if (productAmt != 0) {
                                        if (!packageType.equals("Y"))
                                            discountValue = ((double) tempQty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
                                        else
                                            discountValue = ((int) tempQty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
                                        break;
                                    }
                                default:
                                    discountValue = 0;
                            }

                            discAmtValue = String.valueOf(discountValue);

                            selectedDiscPercent = String.valueOf(schemeDisc);
                            selectedSchemeValue = selectedScheme.getScheme();
                            selectedProductCode = selectedScheme.getProduct_Code();
                            selectedProductName = selectedScheme.getProduct_Name();
                            selectedProductUnit = selectedScheme.getScheme_Unit();

//            discountValue = discountValue*product_Sale_Unit_Cn_Qty;


//                        if(!discountType.equals("") && discountValue>0){
//                            contact.setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
//                            contact.setDis_amt(Constants.roundTwoDecimals(discountValue));
//                            contact.setSelectedDisValue(Constants.roundTwoDecimals(discountValue));


//                        }else {
//                            contact.setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
//                            contact.setDis_amt("0");
//                            contact.setSelectedDisValue("0");

//                        }

                        }

                    }else {
                        discountType = "Rs";

                        selectedDiscPercent  = getValue(id, "discount");

                        discAmtValue  = getValue(id, "discount_price");

                        if(discAmtValue!=null && !discAmtValue.equals("")){
                            editedDisValue  = discountValue = Double.parseDouble(discAmtValue)/tempQty;
                            editedPrice = productAmt - editedDisValue;
                        }

//                        if(mContact.getEditedPrice()!=null && !mContact.getEditedPrice().equals(""))
//                            unitDiscountValue = editedPrice * product_Sale_Unit_Cn_Qty;
//                        else
//                            unitDiscountValue = itemPrice;

//                        schemeDisc = Constant.roundTwoDecimals1(((editedDis * product_Sale_Unit_Cn_Qty)/itemPrice) * 100);
//                    holder.ll_disc.setVisibility(View.VISIBLE);
//            holder.ProductDis.setText(String.valueOf(Constant.roundTwoDecimals(schemeDisc)));
//                        task.isEdited(), task.getEditedDiscount(), task.getEditedPrice()


                    }


//                    else {

//                        contact.setDis_amt(Constants.roundTwoDecimals(discountValue));
//                        contact.setSelectedDisValue(Constants.roundTwoDecimals(discountValue));
//                        contact.setSelectedScheme("");
//                        contact.setOff_Pro_code("");
//                        contact.setOff_Pro_name("");
//                        contact.setOff_Pro_Unit("");
//                        contact.setSelectedFree("0");

//                    }

//                    contact.setOff_free_unit(OffFreeUnit);

                    double totalAmt = 0;
                    double taxPercent = 0;
                    double taxAmt = 0;

                    try {
                        totalAmt = Double.parseDouble(PRate) * (Integer.parseInt(qty) *product_Sale_Unit_Cn_Qty);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    try {
                        taxPercent = Double.parseDouble(PTaxValue);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

/*
                    double itemPrice = 0;
    */
/*    if(totalAmt==0)
            itemPrice = totalAmt;
        else*//*

                    itemPrice = Double.parseDouble(PRate)*product_Sale_Unit_Cn_Qty;
*/

                    try {
                        taxAmt =  (totalAmt- discountValue) * (taxPercent/100);
                        taxAmtValue = String.valueOf(taxAmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    subTotal = (float) totalAmt;
//                    valueTotal = (float) taxAmt;
                    finalPrice = String.valueOf((totalAmt - discountValue) + taxAmt);

                }
                PrimaryProduct primaryProduct = new PrimaryProduct();
                primaryProduct.setUID(id);
                primaryProduct.setPID(PId);
                primaryProduct.setName(Name);
                primaryProduct.setPname(PName);
                primaryProduct.setProduct_Bar_Code(PBarCode);
                primaryProduct.setUOM(PUOM);
                primaryProduct.setProduct_Cat_Code(PRate);
                primaryProduct.setProduct_Sale_Unit(PSaleUnit);
                primaryProduct.setDiscount(selectedDiscPercent);
                primaryProduct.setTax_Value(PTaxValue);
                primaryProduct.setQty(qty);
                primaryProduct.setTxtqty(qty);
                primaryProduct.setSubtotal(finalPrice);
                primaryProduct.setTax_amt(taxAmtValue);
                primaryProduct.setSchemeProducts(schemeList);
                primaryProduct.setProduct_Sale_Unit_Cn_Qty(unitQty);
                primaryProduct.setUOMList(uomList);
                primaryProduct.setSelectedFree(freeQtValue);

//                            contact.setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
//                            contact.setDis_amt(Constants.roundTwoDecimals(discountValue));
//                            contact.setSelectedDisValue(Constants.roundTwoDecimals(discountValue));

                primaryProduct.setDis_amt(Constant.roundTwoDecimals(Double.parseDouble(discAmtValue)));
                primaryProduct.setSelectedDisValue(Constant.roundTwoDecimals(Double.parseDouble(discAmtValue)));
                primaryProduct.setSelectedScheme(selectedSchemeValue);
                primaryProduct.setOff_Pro_code(selectedProductCode);
                primaryProduct.setOff_Pro_name(selectedProductName);
                primaryProduct.setOff_Pro_Unit(selectedProductUnit);
                primaryProduct.setOff_disc_type(discountType);
                primaryProduct.setEdited(isEditedPrice);
                primaryProduct.setEditedDiscount(String.valueOf(editedDisValue));
                primaryProduct.setEditedPrice(String.valueOf(editedPrice));

                contact.insert(primaryProduct);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mCommon_class.ProgressdialogShow(2, "");
//        startActivity(new Intent(SecondRetailerActivity.this, SecondaryOrderProducts.class));

        Intent dashIntent = new Intent(getApplicationContext(), PrimaryOrderProducts.class);
        dashIntent.putExtra("Mode", "0");
        dashIntent.putExtra("editMode", 1);
        if(categoryCode ==-1)
            categoryCode =0;
        dashIntent.putExtra("categoryCode", categoryCode);
        dashIntent.putExtra("orderVal", String.valueOf(OrderValueTotal));
        dashIntent.putExtra("order_type", 2);
        dashIntent.putExtra("PhoneOrderTypes", 0);
        dashIntent.putExtra("orderNo", orderNo);
        startActivityForResult(dashIntent, ACTIVITY_REQUEST_CODE);
//        startActivity(dashIntent);

    }

    private String getQty(String id) {
        String qty = "0";
        if(jsonArray!=null && jsonArray.length()>0){
            for(int i = 0; i< jsonArray.length(); i++){
                try {
                    if(jsonArray.getJSONObject(i).getString("Product_Code").equals(id)){
                        qty = jsonArray.getJSONObject(i).getString("CQty");

                        break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return qty;
    }

    private int getConQty(String id) {
        int conQty = 0;
        if(jsonArray!=null && jsonArray.length()>0){
            for(int i = 0; i< jsonArray.length(); i++){
                try {
                    if(jsonArray.getJSONObject(i).getString("Product_Code").equals(id)){
                        conQty = jsonArray.getJSONObject(i).getInt("Cl_bal");
                        if(categoryCode ==-1)
                            categoryCode = jsonArray.getJSONObject(i).getInt("Product_Brd_Code");
                        break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return conQty;
    }

    private String getConQtyName(String id) {
        String conQtyName = "";
        if(jsonArray!=null && jsonArray.length()>0){
            for(int i = 0; i< jsonArray.length(); i++){
                try {
                    if(jsonArray.getJSONObject(i).getString("Product_Code").equals(id)){
                        conQtyName = jsonArray.getJSONObject(i).getString("unit");

                        break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return conQtyName;
    }

    private String getValue(String id, String key) {
        String value = "";
        if(jsonArray!=null && jsonArray.length()>0){
            for(int i = 0; i< jsonArray.length(); i++){
                try {
                    if(jsonArray.getJSONObject(i).getString("Product_Code").equals(id)){
                        value = jsonArray.getJSONObject(i).getString(key);
                        break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }


    public void brandSecondaryApi() {

        String tempalteValue = "{\"tableName\":\"sec_category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), custCode, shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue, 2);

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

                new PopulateDbAsyntasks(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }



    public void getMulipart(String path) {
        Log.v("PATH_IMAGE", path);
        MultipartBody.Part imgg = convertimg("file", path);

        sendImageToServer(imgg);
    }

    public MultipartBody.Part convertimg(String tag, String path) {
        MultipartBody.Part yy = null;
        try {
            if (!TextUtils.isEmpty(path)) {

                File file ;
                if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg"))
                    file = new Compressor(getApplicationContext()).compressToFile(new File(path));
                else
                    file = new File(path);

                try {
                    serverFileName = "Sf_Code_"+ shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code) +"_"+ System.currentTimeMillis()+ ".jpeg";
                } catch (Exception e) {
                    e.printStackTrace();
                    serverFileName = "Sf_Code_"+ System.currentTimeMillis() + ".jpeg";
                }
                RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
                yy = MultipartBody.Part.createFormData(tag, serverFileName, requestBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yy;
    }

    private void sendImageToServer(MultipartBody.Part imgg) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> mCall = apiInterface.offlineImage("upload/paymentimg", imgg);

        mCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                if(jsonObject!=null && !jsonObject.has("success") && jsonObject.get("success").getAsBoolean()){
                    Toast.makeText(ViewReportActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SEND_IMAGE_Response", "ERROR");
                Toast.makeText(ViewReportActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getOfflineMode() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getOfflineMode("get/paymentmode", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response.body().toString());
                    Log.v(TAG, "getOfflineMode"+ jsonRootObject.toString());
                    if(jsonRootObject.has("Data")){
                        JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject jso = jsonArray.getJSONObject(a);
                            String className = String.valueOf(jso.get("Name"));
                            String id = String.valueOf(jso.get("Code"));
                            mCommon_model_spinner = new Common_Model(id, className, "flag");
                            modelOffileData.add(mCommon_model_spinner);

                            Log.v("NAME_STRING", className);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
            }
        });
    }

    public void LinearOfflineMode(View v) {
        customDialog = new CustomListViewDialog(ViewReportActivity.this, modelOffileData, 10);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }


    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 10) {
            String name = myDataset.get(position).getName();
            txt_offline_mode.setText(name);
            PaymentTypecode = myDataset.get(position).getId();

            if(!name.equalsIgnoreCase("cash")){
                iv_choose_photo.setVisibility(View.VISIBLE);
                imgSource.setVisibility(View.VISIBLE);

            }
            else{
                serverFileName = "";
                iv_choose_photo.setVisibility(View.GONE);
                imgSource.setVisibility(View.GONE);
            }

            if(name.contains("Cheque")){
                edt_utr.setHint("Enter Cheque No./UTR");
                cheque_no_label.setText("Enter Cheque No./UTR");
            }else {
                edt_utr.setHint("Enter Challan No./UTR");
                cheque_no_label.setText("Enter Challan No./UTR");
            }
        }
    }



}