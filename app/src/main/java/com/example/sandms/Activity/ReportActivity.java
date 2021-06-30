package com.example.sandms.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


public class ReportActivity extends AppCompatActivity implements DMS.Master_Interface{
    TextView toolHeader, txtTotalValue, txtProductDate,  txtName,orderStatus ;
    ImageView imgBack;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getToolbar();

        FReport = getIntent().getStringExtra("FromReport");
        TReport = getIntent().getStringExtra("ToReport");

        Count = getIntent().getIntExtra("count", 100);
        shared_common_pref = new Shared_Common_Pref(this);

        OrderType = shared_common_pref.getvalue("OrderType");
        Log.v("OrderType", OrderType);
        mArrayList = new ArrayList<>();
        txtTotalValue = (TextView) findViewById(R.id.total_value);
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
            fromBtn.setText("" + FReport);
            toBtn.setText("" + TReport);
            fromDateString = FReport;
            toDateString = TReport;
        } else {
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
                                ViewDateReport("All");
                                mArrayList.clear();
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
                        ViewDateReport("All");
                        mArrayList.clear();
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

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), ReportDashBoard.class));
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
               List<ReportModel> mDReportModels = mReportActivities.getData();
               try {
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(mDReportModels));
                    JSONObject JsonObject;
                    modeOrderData.clear();
                    for (int l = 0; l <= jsonArray.length(); l++) {
                        JsonObject = jsonArray.getJSONObject(l);
                        String orderStatus = JsonObject.getString("Order_Status");
                        Log.e("datareportmodels", String.valueOf(mReportActivities.getData()));
                        if (orderTakenByFilter.equals(orderStatus)) {
                            mDReportModels=mReportActivities.getData();
                        } else {
                            mDReportModels=mReportActivities.getData();

                        }
                    }

                }catch (Exception e){

                }
                for (int i = 0; i < mDReportModels.size(); i++) {
                    Log.e("data", String.valueOf(mDReportModels.get(i).getOrderValue()));
                    mArrayList.add(Float.valueOf((mDReportModels.get(i).getOrderValue())));
                }
                Log.v("DATA_COMING", new Gson().toJson(mDReportModels));
                Float intSum = Float.valueOf(0);
                try {
                    JSONArray jsonArray = new JSONArray(new Gson().toJson(mDReportModels));
                    JSONObject JsonObjects;
                    modeOrderData.clear();
                    for (int l = 0; l <= jsonArray.length(); l++) {
                        JsonObjects = jsonArray.getJSONObject(l);
                        
                        Log.v("JSON_OBEJCTS",JsonObjects.getString("Order_Value"));
                        intSum = intSum +Float.valueOf(JsonObjects.getString("Order_Value"));
                        String orderStatus=JsonObjects.getString("Order_Status");
                         String orderNo=JsonObjects.getString("Order_No");
                        Log.v("JSON_OBEJCTS",JsonObjects.getString("Order_Status"));


//                        mCommon_model_spinner = new Common_Model(orderNo, orderStatus, "flag");
//                      modeOrderData.add(mCommon_model_spinner);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                //Float intSum = Float.valueOf(mArrayList.stream().mapToLong(Float::longValue).sum());
                txtTotalValue.setText("Rs . "+ new DecimalFormat("##.##").format(intSum));
                Log.e("Total_Value", String.valueOf(intSum));
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
                },orderTakenByFilter);
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

}

