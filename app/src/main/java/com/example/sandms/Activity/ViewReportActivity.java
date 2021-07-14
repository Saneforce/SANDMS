
package com.example.sandms.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewReportActivity extends AppCompatActivity {
    TextView toolHeader, txtProductId, txtProductDate;

    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageView imgBack,imgShare;
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
    Double OrderTaxCal,  OrderAmtNew,OrderValueTotal;
    View supportLayout;
    LinearLayout  linearLayout;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        linearLayout = (LinearLayout) findViewById(R.id.linearproductlayout);
        supportLayout=findViewById(R.id.customtoolbarlayout);
        supportLayout.setVisibility(View.VISIBLE);
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
        imgShare=findViewById(R.id.toolbar_share);
        imgShare.setVisibility(View.VISIBLE);

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportLayout.setVisibility(View.GONE);

                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (checkPermission())
                    {
                        // Code for above or equal 23 API Oriented Device
                        // Your Permission granted already .Do next code

                        bitmap = loadBitmapFromView(linearLayout, linearLayout.getWidth(), linearLayout.getHeight());
                        createPdf();
                    } else {
                        requestPermission(); // Code for permission
                    }
                }
                else
                {

                    // Code for Below 23 API Oriented Device
                    // Do next code
                }
//                bitmap = loadBitmapFromView(linearLayout, linearLayout.getWidth(), linearLayout.getHeight());
//                createPdf();

            }
        });

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
        payIntent.putExtra("Amount", OrderValueTotal);

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
                    //    OrderValueTotal=Double.valueOf(jsonObject.getString("Order_Value"));

                        OrderValueTotal=Double.valueOf(jsonObject.getString("OrderVal"));
                        OrderAmtNew= Double.valueOf(jsonObject.getString("taxval"));
                      //  TotalValue.setText("Rs."+jsonObject.getString("taxval"));//working code commented
                        TotalValue.setText("Rs."+jsonObject.getString("OrderVal"));
                        Integer PaymentValue = (Integer) jsonObject.get("Paymentflag");
                        Log.v("PAYMENT_VALUE", String.valueOf(PaymentValue));
                        Log.v("PAYMENT_VALUE1", String.valueOf(OrderValueTotal));
                        Log.v("PAYMENT_VALUE1", String.valueOf(OrderAmtNew));
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
        document.finishPage(page);






        String targetPdf = "sdcard/sandmssingleproductreport.pdf";
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
        supportLayout.setVisibility(View.VISIBLE);
        openGeneratedPDF();

    }

    private void openGeneratedPDF(){



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File("sdcard/sandmssingleproductreport.pdf");
        if (file.exists())
        {
            Uri pdfUri;
            pdfUri  = Uri.fromFile(file);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
                Toast.makeText(ViewReportActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    //openpdf stop

    //permission start
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ViewReportActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ViewReportActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(ViewReportActivity.this, "Write External Storage permission allows us to do store. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ViewReportActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");

                    bitmap = loadBitmapFromView(linearLayout, linearLayout.getWidth(), linearLayout.getHeight());
                    createPdf();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    //permision stop
}