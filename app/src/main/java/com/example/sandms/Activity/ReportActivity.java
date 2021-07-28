package com.example.sandms.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Adapter.ReportViewAdapter;
import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.Model.ReportDataList;
import com.example.sandms.Model.ReportModel;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Model;
import com.example.sandms.Utils.CustomListViewDialog;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//pdf start

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

public class ReportActivity extends AppCompatActivity implements DMS.Master_Interface{
    TextView toolHeader, txtTotalValue, txtProductDate,  txtName,orderStatus ;
    ImageView imgBack,imgShare;
    Button fromBtn, toBtn;
    EditText toolSearch;

    String fromDateString, dateTime, toDateString, SF_CODE, FReport = "", TReport = "", OrderType = "";
    private int mYear, mMonth, mDay, mHour, mMinute;
    ReportViewAdapter mReportViewAdapter;
    RecyclerView mReportList;
    ArrayList<Float> mArrayList;
    Shared_Common_Pref shared_common_pref;
    Integer Count = 0;
    List<Common_Model> modeOrderData = new ArrayList<>();
    Common_Model mCommon_model_spinner;
    CustomListViewDialog customDialog;
    LinearLayout linearOrderMode;
    TextView txtOrderStatus;
    String orderTakenByFilter;
    ArrayList<String> OrderStatusList;
    ArrayList<String> OrderStatusListID;
    LinearLayout linearLayout;

    Toolbar toolbar_top;

    private Bitmap bitmap,bitmapTotal;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;
    LinearLayout totalLayout;
    ImageView filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        toolbar_top=findViewById(R.id.toolbar_top);
        toolbar_top.setVisibility(View.VISIBLE);

        totalLayout=findViewById(R.id.totalLayout);
       // filter=findViewById(R.id.toolbar_filter);
        FReport = getIntent().getStringExtra("FromReport");
        TReport = getIntent().getStringExtra("ToReport");
        Count = getIntent().getIntExtra("count", 100);
        shared_common_pref = new Shared_Common_Pref(this);
        OrderType = shared_common_pref.getvalue("OrderType");
        Log.v("OrderType", OrderType);
        mArrayList = new ArrayList<>();
        txtTotalValue = (TextView) findViewById(R.id.total_value);
        getToolbar();
        txtOrderStatus=findViewById(R.id.txt_orderstatus);
        txtName = findViewById(R.id.dist_name);
        txtName.setText("Name:"+ ""+shared_common_pref.getvalue(Shared_Common_Pref.name) + " ~ " + shared_common_pref.getvalue(Shared_Common_Pref.Sf_UserName));
        @SuppressLint("WrongConstant")
        SharedPreferences sh = getSharedPreferences("MyPrefs", MODE_APPEND);
        SF_CODE = sh.getString("Sf_Code", "");
        Log.e("SF_CODE", SF_CODE);
        fromBtn = (Button) findViewById(R.id.from_picker);
        toBtn = (Button) findViewById(R.id.to_picker);
        linearOrderMode=findViewById(R.id.lin_order);
        txtTotalValue.setText("0");
        DateFormat df = new SimpleDateFormat("yyyy-MM-d");
        Calendar calobj = Calendar.getInstance();
        dateTime = df.format(calobj.getTime());
        System.out.println("Date_and_Time" + dateTime);
        if (Count == 1) {

           // DateFormat dff= new SimpleDateFormat("dd-MM-yyyy");

           // FReport= dff.format(FReport);
            //TReport=dff.format(TReport);
            fromBtn.setText("" + FReport);
            toBtn.setText("" + TReport);
            fromDateString = FReport;
            toDateString = TReport;
        } else {
            //DateFormat dff= new SimpleDateFormat("dd-MM-yyyy");

            //FReport= dff.format(FReport);
          //  TReport=dff.format(TReport);
            fromBtn.setText("" + dateTime);
            toBtn.setText("" + dateTime);
            fromDateString = dateTime;
            toDateString = dateTime;
        }
        ViewDateReport("All");

        fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                fromBtn.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                mArrayList.clear();
//                                OrderStatusList.clear();
                                modeOrderData.clear();
                                txtOrderStatus.setText("All");
                                ViewDateReport("All");

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        toDateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        toBtn.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        mArrayList.clear();
  //                      OrderStatusList.clear();
                        modeOrderData.clear();
                        txtOrderStatus.setText("All");
                        ViewDateReport("All");

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mReportList = (RecyclerView) findViewById(R.id.report_list);
        mReportList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReportList.setLayoutManager(layoutManager);


        linearOrderMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OrderStatusList=new ArrayList<>();
                modeOrderData.clear();
                OrderStatusList.add("All");
               OrderStatusList.add("Payment Pending");
               OrderStatusList.add("Order Dispatched");
               OrderStatusList.add("Payment Verified");
               OrderStatusList.add("Payment Done");
                OrderStatusList.add("Credit Raised");
                OrderStatusList.add("Credit Verified");
                OrderStatusList.add("Credit Dispatched");
                for (int i = 0; i < OrderStatusList.size(); i++) {
                    String id = String.valueOf(OrderStatusList.get(i));
                    String name = OrderStatusList.get(i);
                    mCommon_model_spinner = new Common_Model(id, name, "flag");
                    modeOrderData.add(mCommon_model_spinner);
                }


                customDialog = new CustomListViewDialog(ReportActivity.this,modeOrderData, 11);
                Window window = customDialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                customDialog.show();
            }
        });





    }

    /*Toolbar*/
    public void getToolbar() {
        filter= (ImageView) findViewById(R.id.toolbar_filter);
        filter.setVisibility(View.VISIBLE);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderStatusList=new ArrayList<>();
                modeOrderData.clear();
                OrderStatusList.add("All");
                OrderStatusList.add("Payment Pending");
                OrderStatusList.add("Order Dispatched");
                OrderStatusList.add("Payment Verified");
                OrderStatusList.add("Payment Done");
                OrderStatusList.add("Credit Raised");
                OrderStatusList.add("Credit Verified");
                OrderStatusList.add("Credit Dispatched");
                for (int i = 0; i < OrderStatusList.size(); i++) {
                    String id = String.valueOf(OrderStatusList.get(i));
                    String name = OrderStatusList.get(i);
                    mCommon_model_spinner = new Common_Model(id, name, "flag");
                    modeOrderData.add(mCommon_model_spinner);
                }


                customDialog = new CustomListViewDialog(ReportActivity.this,modeOrderData, 11);
                Window window = customDialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                customDialog.show();
            }
        });

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

//                startActivity(new Intent(getApplicationContext(), ReportDashBoard.class));
                finish();
            }
        });
        toolHeader = (TextView) findViewById(R.id.toolbar_title);
        toolHeader.setText(R.string.View_Rep);
        toolSearch = (EditText) findViewById(R.id.toolbar_search);
        toolSearch.setVisibility(View.GONE);
    }


    public void ViewDateReport(String orderTakenByFilter) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ReportDataList> responseBodyCall;


        if (OrderType.equalsIgnoreCase("1")) {
            responseBodyCall = apiInterface.reportValues("get/ViewReport", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), fromDateString, toDateString);
        } else {
            responseBodyCall = apiInterface.reportValues("get/secviewreport", shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), fromDateString, toDateString);
        }
        Log.v("Request_cal", responseBodyCall.request().toString());
        responseBodyCall.enqueue(new Callback<ReportDataList>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ReportDataList> call, Response<ReportDataList> response) {
                ReportDataList mReportActivities = response.body();
                Log.v("JSOn_VAlue", new Gson().toJson(response.body()));
              //  List<ReportModel> mDReportModels;
                List<ReportModel> mDReportModels = new ArrayList<>();
                if(mReportActivities!=null && mReportActivities.getData()!=null)
                    mDReportModels = mReportActivities.getData();
                if(orderTakenByFilter.equalsIgnoreCase("Payment Pending")){
                    String ordervalue=String.valueOf(mReportActivities.getPaymentPending());


                    BigDecimal bd = new BigDecimal(ordervalue).setScale(2, RoundingMode.HALF_UP);
                    double totalroundoff= bd.doubleValue();

                    txtTotalValue.setText("Rs . "+ (int) totalroundoff);
                    Log.e("pay1",ordervalue);
                }else  if(orderTakenByFilter.equalsIgnoreCase("Payment Verified")){
                    String ordervalue=String.valueOf(mReportActivities.getPaymentVerified());
                    BigDecimal bd = new BigDecimal(ordervalue).setScale(2, RoundingMode.HALF_UP);
                    double totalroundoff= bd.doubleValue();

                    txtTotalValue.setText("Rs . "+String.valueOf(totalroundoff));
                 //   txtTotalValue.setText("Rs . "+ ordervalue);
                    Log.e("pay11",String.valueOf(mReportActivities.getPaymentVerified()));
                }else  if(orderTakenByFilter.equalsIgnoreCase("Credit Verified")){
                    String ordervalue=String.valueOf(mReportActivities.getCreditVerified());
                    BigDecimal bd = new BigDecimal(ordervalue).setScale(2, RoundingMode.HALF_UP);
                    double totalroundoff= bd.doubleValue();

                    txtTotalValue.setText("Rs . "+ String.valueOf(totalroundoff));
                    //txtTotalValue.setText("Rs . "+ordervalue);
                    Log.e("pay111",String.valueOf(mReportActivities.getCreditVerified()));
                }else  if(orderTakenByFilter.equalsIgnoreCase("Order Dispatched")){
                    String ordervalue=String.valueOf(mReportActivities.getOrderDispatched());
                    BigDecimal bd = new BigDecimal(ordervalue).setScale(2, RoundingMode.HALF_UP);
                    double totalroundoff= bd.doubleValue();
                    txtTotalValue.setText("Rs . "+ String.valueOf(totalroundoff));
                  //  txtTotalValue.setText("Rs . "+ ordervalue);
                    Log.e("pay122",String.valueOf(mReportActivities.getOrderDispatched()));
                }else  if(orderTakenByFilter.equalsIgnoreCase("Credit Dispatched")){
                    String ordervalue=String.valueOf(mReportActivities.getCreditDispatched());
                    BigDecimal bd = new BigDecimal(ordervalue).setScale(2, RoundingMode.HALF_UP);
                    double totalroundoff= bd.doubleValue();

                    txtTotalValue.setText("Rs . "+String.valueOf(totalroundoff));
                    //txtTotalValue.setText("Rs . "+ ordervalue);
                    Log.e("pay133",mReportActivities.getCreditDispatched());
                }else  if(orderTakenByFilter.equalsIgnoreCase("Payment Done")){
                    String ordervalue=String.valueOf(mReportActivities.getPaymentDone());
                    BigDecimal bd = new BigDecimal(ordervalue).setScale(2, RoundingMode.HALF_UP);
                    double totalroundoff= bd.doubleValue();

                    txtTotalValue.setText("Rs . "+String.valueOf(totalroundoff));
                  //  txtTotalValue.setText("Rs . "+ ordervalue);
                    Log.e("pay155",String.valueOf(mReportActivities.getPaymentDone()));
                }else  if(orderTakenByFilter.equalsIgnoreCase("Credit Raised")){
                    String ordervalue=String.valueOf(mReportActivities.getCreditRaised());
                    BigDecimal bd = new BigDecimal(ordervalue).setScale(2, RoundingMode.HALF_UP);
                    double totalroundoff= bd.doubleValue();

                    txtTotalValue.setText("Rs . "+String.valueOf(totalroundoff));
                  //  txtTotalValue.setText("Rs . "+ ordervalue);
                    Log.e("pay15",String.valueOf(mReportActivities.getCreditRaised()));
                }else{

                    txtTotalValue.setText("Rs.0");
                }
               try
               {

                   Log.v("JSOn_VAlue", new Gson().toJson(response.body()));
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(mDReportModels));
                    JSONObject JsonObject;
                    modeOrderData.clear();
                   Float intSum = Float.valueOf(0);
                //  String order=mReportActivities.getPayment_Pending();




                   for (int l = 0; l <= jsonArray.length(); l++) {
                        JsonObject = jsonArray.getJSONObject(l);
                        String orderStatus = JsonObject.getString("Order_Status");
                        Log.e("datareportmodels", String.valueOf(mReportActivities.getData()));
                        Float orderValue= Float.valueOf(JsonObject.getString("Order_Value"));
                        Log.v("JSON_OBEJCTSvalue" +
                                "",orderValue.toString());


                        if (orderTakenByFilter.equals(orderStatus)||orderTakenByFilter.equalsIgnoreCase(orderStatus)) {
                            mDReportModels=mReportActivities.getData();

                        } else if(orderTakenByFilter.equals("All")||orderTakenByFilter.equalsIgnoreCase("All")) {
                            mDReportModels=mReportActivities.getData();

                          intSum = intSum +orderValue;

                           txtTotalValue.setText("Rs . "+ new DecimalFormat("##.##").format(intSum));

                        }else{
                            mDReportModels=mReportActivities.getData();
                        }
                    }

                }catch (Exception e){

                }
               try {

                   for (int i = 0; i < mDReportModels.size(); i++) {
                       Log.e("data", String.valueOf(mDReportModels.get(i).getOrderValue()));
                       mArrayList.add(Float.valueOf((mDReportModels.get(i).getOrderValue())));

                   }
               }catch (Exception ee){

               }
                Log.v("DATA_COMING", new Gson().toJson(mDReportModels));
              //  Float intSum = Float.valueOf(0);
//                try {
//                    JSONArray jsonArray = new JSONArray(new Gson().toJson(mDReportModels));
//                    JSONObject JsonObjects;
//                    modeOrderData.clear();
////                    Float intSum = Float.valueOf(0);
////                    txtTotalValue.setText("Rs . "+ new DecimalFormat("##.##").format(intSum));
//                    for (int l = 0; l <= jsonArray.length(); l++) {
//                        JsonObjects = jsonArray.getJSONObject(l);
//
//
//                      //  intSum = intSum +Float.valueOf(JsonObjects.getString("Order_Value"));
//                        String orderStatus=JsonObjects.getString("Order_Status");
//                         String orderNo=JsonObjects.getString("Order_No");
//                        Log.v("status",JsonObjects.getString("Order_Status"));
//
//                   //   Float orderValue= Float.valueOf(JsonObjects.getString("Order_Value"));
//                     //   Log.v("JSON_OBEJCTSvalue" + "",orderValue.toString());
////                        intSum = in
////                        tSum +Float.valueOf(JsonObjects.getString("Order_Value"));
////                        txtTotalValue.setText("Rs . "+ new DecimalFormat("##.##").format(intSum));
//                        Log.v("JSON_OBEJCTSvaluest" ,   orderTakenByFilter.toString());
//                        Log.v("JSON_OBEJCTSstattus" ,  orderStatus.toString());
////                        if (orderTakenByFilter.equals(orderStatus)||orderTakenByFilter.equalsIgnoreCase(orderStatus)) {
////                            intSum = intSum +orderValue;
////                            txtTotalValue.setText("Rs . "+ new DecimalFormat("##.##").format(intSum));
////                            Log.e("Total_Value11", String.valueOf(intSum));
////                          //  mDReportModels=mReportActivities.getData();
////                        } else {
////                            intSum = intSum +orderValue;
////                           // intSum = intSum +Float.valueOf(JsonObjects.getString("Order_Value"));
////                            txtTotalValue.setText("Rs . "+ new DecimalFormat("##.##").format(intSum));
////                            Log.e("Total_Value22", String.valueOf(intSum));
////                           // mDReportModels=mReportActivities.getData();
////
////                        }
//
//
////                        mCommon_model_spinner = new Common_Model(orderNo, orderStatus, "flag");
////                      modeOrderData.add(mCommon_model_spinner);
//
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                //Float intSum = Float.valueOf(mArrayList.stream().mapToLong(Float::longValue).sum());
               // txtTotalValue.setText("Rs . "+ new DecimalFormat("##.##").format(intSum));//working code commented jul 15
              //  Log.e("Total_Value", String.valueOf(intSum));
                mReportViewAdapter = new ReportViewAdapter(ReportActivity.this, mDReportModels, new DMS.ViewReport() {
                    @Override
                    public void reportCliick(String productId, String orderDate, String OrderValue) {//,String TaxValue,String Tax
                        Intent intnet = new Intent(ReportActivity.this, ViewReportActivity.class);
                        intnet.putExtra("ProductID", productId);
                        intnet.putExtra("OrderDate", orderDate);
                        intnet.putExtra("FromDate", fromBtn.getText().toString());
                        intnet.putExtra("ToDate", toBtn.getText().toString());
                        intnet.putExtra("OderValue", OrderValue);

                        startActivity(intnet);
                        //  finish();
                    }
                },orderTakenByFilter,txtTotalValue);
                mReportList.setAdapter(mReportViewAdapter);
            }

            @Override
            public void onFailure(Call<ReportDataList> call, Throwable t) {

            }
        });
    }


    @Override
    public void onBackPressed() {
    }



    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 11) {
           txtOrderStatus.setText(myDataset.get(position).getName());
           orderTakenByFilter=myDataset.get(position).getName();
           Log.e("order filter",orderTakenByFilter);

            ViewDateReport(orderTakenByFilter);
            mArrayList.clear();

        }
    }

    //open pdf


    public static Bitmap loadBitmapFromView(View v, int width, int height) {


        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void createPdf(){

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;


        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet,
                1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);
        paint.setColor(Color.WHITE);
        //paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);

//        bitmapTotal = Bitmap.createScaledBitmap(bitmapTotal, convertWidth, convertHighet, true);
//        paint.setColor(Color.WHITE);
//        //paint.setColor(Color.BLUE);
//        canvas.drawBitmap(bitmapTotal, 0, 0 , null);


        document.finishPage(page);






        String targetPdf = "sdcard/sandmsreport.pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));





        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();
        openGeneratedPDF();

    }

    private void openGeneratedPDF(){



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File("sdcard/sandmsreport.pdf");
        if (file.exists())
        {
            Uri  pdfUri;
           pdfUri  = Uri.fromFile(file);

            if (SDK_INT >= Build.VERSION_CODES.N) {
                 pdfUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
            } else {
                  pdfUri = Uri.fromFile(file);
            }
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, pdfUri);
            startActivity(Intent.createChooser(share, "Share"));


            try
            {

                startActivity(Intent.createChooser(share, "Share"));
           //     startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(ReportActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    //openpdf stop

/*    //permission start
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ReportActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }*/

/*
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ReportActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(ReportActivity.this, "Write External Storage permission allows us to do store. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ReportActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
*/

/*    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    supportLayout.setVisibility(View.GONE);
                    bitmap = loadBitmapFromView(scrollLayout, scrollLayout.getWidth(), scrollLayout.getHeight());
                 //   bitmap = loadBitmapFromView(linearLayout, linearLayout.getWidth(), linearLayout.getHeight());
             //       bitmapTotal = loadBitmapFromView(totalLayout, linearLayout.getWidth(), linearLayout.getHeight());

                    createPdf();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }*/

    //permision stop


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
            Toast.makeText(ReportActivity.this, "image to pdf conversion failure", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        // change with required  application package
        File file1 = new File( dirpath + "/"+ fileName+".pdf");
        intent.setPackage("com.whatsapp");
        if (intent != null && file.exists()) {
            intent.setType("application/pdf");
            Uri uri = FileProvider.getUriForFile(ReportActivity.this, getApplicationContext().getPackageName()+ ".provider", file1);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share File"));
        } else {
            Toast.makeText(ReportActivity.this, "App not found", Toast.LENGTH_SHORT).show();
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
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

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


}

