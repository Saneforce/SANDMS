
package com.saneforce.dms.Activity;

import android.Manifest;
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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.saneforce.dms.Adapter.DateReportAdapter;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.Interface.DMS;

import com.saneforce.dms.R;
import com.saneforce.dms.Utils.AlertDialogBox;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.Shared_Common_Pref;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION.SDK_INT;

public class ViewReportActivity extends AppCompatActivity {
    TextView toolHeader, txtProductId, txtProductDate;

    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageView imgBack,imgShare;

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
    TextView TotalValue,total_value;
    Button PayNow,Delete;
    Double OrderTaxCal,  OrderAmtNew,OrderValueTotal,OderDiscount;
    Toolbar toolbar_top;
    LinearLayout  linearLayout;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        toolbar_top=findViewById(R.id.toolbar_top);
        toolbar_top.setVisibility(View.VISIBLE);


        mArrayList = new ArrayList<Integer>();
        TotalValue = findViewById(R.id.total_value);
        shared_common_pref = new Shared_Common_Pref(this);

        OrderType = shared_common_pref.getvalue("OrderType");
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
        toolHeader.setText(R.string.View_Rep);
//        toolSearch = (EditText) findViewById(R.id.toolbar_search);
//        toolSearch.setVisibility(View.GONE);

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
//        finish();
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
                        if(jsonObject.has("OrderVal"))
                        OrderValueTotal=Double.valueOf(jsonObject.getString("OrderVal"));
                        if(jsonObject.has("taxval"))
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
                t.printStackTrace();
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
        finish();
    }

    public void intentMethod() {
        Intent intnet = new Intent(getApplicationContext(), ReportActivity.class);
        intnet.putExtra("FromReport", formDate);
        intnet.putExtra("ToReport", toDate);
        intnet.putExtra("count", 1);
        startActivity(intnet);
        finish();
    }


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

    //permision stop
}