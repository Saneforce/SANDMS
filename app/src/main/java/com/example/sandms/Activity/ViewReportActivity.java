
package com.example.sandms.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Adapter.DateReportAdapter;
import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewReportActivity extends AppCompatActivity {
    TextView toolHeader, txtProductId, txtProductDate;


    ImageView imgBack;
    EditText toolSearch;
    RecyclerView DateRecyclerView;
    String productId;
    String orderDate;
    String formDate;
    String toDate;
    String OrderType;
    String OrderAmt;
    String OrderTax;
    DateReportAdapter mDateReportAdapter;
    Shared_Common_Pref shared_common_pref;
    ArrayList<Integer> mArrayList;
    TextView TotalValue;
    Button PayNow,Delete;
    Double OrderTaxCal,  OrderAmtNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        getToolbar();

        mArrayList = new ArrayList<Integer>();
        TotalValue = findViewById(R.id.total_value);
        shared_common_pref = new Shared_Common_Pref(this);

        OrderType = shared_common_pref.getvalue("OrderType");
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


        DateRecyclerView = (RecyclerView) findViewById(R.id.date_recycler);
        DateRecyclerView.setHasFixedSize(true);
        DateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtProductId = (TextView) findViewById(R.id.txt_product_id);
        txtProductDate = (TextView) findViewById(R.id.txt_order_Date);
        txtProductId.setText(productId);
        txtProductDate.setText(orderDate);

        PayNow = findViewById(R.id.green_btn);
        Delete = findViewById(R.id.red_btn);
        ViewDateReport();
    }


    /*Toolbar*/
    public void getToolbar() {

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentMethod();
                finish();
            }
        });
        toolHeader = (TextView) findViewById(R.id.toolbar_title);
        toolHeader.setText(R.string.View_Rep);
        toolSearch = (EditText) findViewById(R.id.toolbar_search);
        toolSearch.setVisibility(View.GONE);

    }

    public void Delete(View v) {

        AlertDialogBox.showDialog(ViewReportActivity.this, "", "Do you Surely want to delete this order?", "Yes", "NO", false, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {
                DeleteOrder();
            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
            }
        });
    }

    public void PayNow(View v) {
        Intent payIntent = new Intent(getApplicationContext(), PaymentDetailsActivity.class);
        payIntent.putExtra("OrderId", productId);
        payIntent.putExtra("Date", orderDate);
      //  payIntent.putExtra("Amount", OrderAmt);
        payIntent.putExtra("Amount",OrderAmtNew);

        startActivity(payIntent);
        finish();
    }


    public void ViewDateReport() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> responseBodyCall;
        if (OrderType.equalsIgnoreCase("1")) {
            responseBodyCall = apiInterface.dateReport("get/ViewReport_Details", productId, shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        } else {
            responseBodyCall = apiInterface.dateReport("get/secviewreport_details", productId, shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        }


        Log.v("ViewDateREquest", responseBodyCall.request().toString());

        responseBodyCall.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonRootObject = null;


                try {
                    jsonRootObject = new JSONObject(response.body().toString());
                    Log.v("ViewDateResponse", jsonRootObject.toString());
                    JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                    JSONObject jsonObject = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        OrderAmtNew= Double.valueOf(jsonObject.getString("taxval"));
                        TotalValue.setText("Rs."+jsonObject.getString("taxval"));
                        Integer PaymentValue = (Integer) jsonObject.get("Paymentflag");
                        Log.v("PAYMENT_VALUE", String.valueOf(PaymentValue));
                        if (PaymentValue == 0) {
                            PayNow.setVisibility(View.VISIBLE);
                            Delete.setVisibility(View.VISIBLE);
                        } else {
                            PayNow.setVisibility(View.GONE);
                            Delete.setVisibility(View.GONE);
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

            }
        });
    }

    public void DeleteOrder() {
        JSONObject js = new JSONObject();
        try {
            js.put("OrderID", productId);
            js.put("Stkcode", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));

            Log.v("JS_VALUE", js.toString());

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> responseBodyCall;


            responseBodyCall = apiInterface.getDetails("dcr/cancelprimaryorder", js.toString());


            Log.v("ViewDateREquest", responseBodyCall.request().toString());

            responseBodyCall.enqueue(new Callback<JsonObject>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject jsonObject = response.body();
                    Log.v("DELETE_RESPONSE", jsonObject.toString());
                    if (jsonObject.get("success").toString().equalsIgnoreCase("true")) ;
                    finish();

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void intentMethod() {
        Intent intnet = new Intent(getApplicationContext(), ReportActivity.class);
        intnet.putExtra("FromReport", formDate);
        intnet.putExtra("ToReport", toDate);
        intnet.putExtra("count", 1);
        startActivity(intnet);
        finish();
    }
}