
package com.saneforce.dms.Activity;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.saneforce.dms.Adapter.DateReportAdapter;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.Interface.DMS;
import com.saneforce.dms.Interface.PrimaryProductDao;
import com.saneforce.dms.Model.PrimaryProduct;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.AlertDialogBox;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.Common_Class;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.PrimaryProductDatabase;
import com.saneforce.dms.Utils.Shared_Common_Pref;
import com.saneforce.dms.sqlite.DBController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewReportActivity extends AppCompatActivity {
    TextView toolHeader, txtProductId, txtProductDate;

    public static final int ACTIVITY_REQUEST_CODE = 2;
    ImageView imgBack,imgShare;

    RecyclerView DateRecyclerView;
    String productId;
    String orderDate;
    String formDate;
    String toDate;
    String OrderType;
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
    String orderType = "";
    int categoryIndex = -1;
    ImageView ib_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        toolbar_top=findViewById(R.id.toolbar_top);
        tv_order_type=findViewById(R.id.tv_order_type);
        ll_order_type=findViewById(R.id.ll_order_type);
        toolbar_top.setVisibility(View.VISIBLE);


        mArrayList = new ArrayList<>();
        TotalValue = findViewById(R.id.total_value);
        shared_common_pref = new Shared_Common_Pref(this);

        OrderType = shared_common_pref.getvalue("OrderType");
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
        if(intent.hasExtra("orderType") && intent.getStringExtra("orderType")!=null){
            ll_order_type.setVisibility(View.VISIBLE);
            orderType = intent.getStringExtra("orderType");
            tv_order_type.setText(orderType);
        }else {
            ll_order_type.setVisibility(View.GONE);
        }


        DateRecyclerView = (RecyclerView) findViewById(R.id.date_recycler);
        DateRecyclerView.setHasFixedSize(true);
        DateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtProductId = (TextView) findViewById(R.id.txt_product_id);
        txtProductDate = (TextView) findViewById(R.id.txt_order_Date);
        txtProductId.setText(productId);
        txtProductDate.setText(orderDate);

        PayNow = findViewById(R.id.green_btn);
        Delete = findViewById(R.id.red_btn);
        ib_logout = findViewById(R.id.ib_logout);

        if(Constants.APP_TYPE == 2)
            Delete.setText("Delete");
        else
            Delete.setText("Cancel");

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });
        if(OrderType.equals("2")){
            if(Constants.APP_TYPE ==2){
                ib_logout.setVisibility(View.VISIBLE);
                ib_logout.setImageDrawable(getResources().getDrawable(R.drawable.edit));
                ib_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCommon_class.ProgressdialogShow(1, "");
                        if(!dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_BRAND).equals("") &&
                                !dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA).equals("")){
                            new PopulateDbAsyntasks(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();
                        }else
                            brandSecondaryApi();

                    }
                });

            }else {
                ib_logout.setVisibility(View.GONE);
            }
            PayNow.setText("Dispatch");
            PayNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    payOffline();
                }
            });
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
        ViewDateReport();
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

    public void payOffline() {
        JSONObject js = new JSONObject();
        try {

            js.put("OrderID", productId);
            js.put("StockistCode", shared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("divisionCode", shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("PaymentMode", "Offline");

            String option = "";
//            if(PaymntMode.equalsIgnoreCase("Offline"))
//                option = offlineMode.getText().toString();

            js.put("PaymentTypeName", option);
            js.put("PaymentTypeCode", "");
            js.put("UTRNumber", "");
            js.put("Amount", OrderValueTotal);
            js.put("Attachement", "");

            js.put("PaymentID","");
            js.put("RazorOrderID", "");
            js.put("SignatureID", "");
            js.put("dispatch", "1");

            Log.v("JS_VALUEdata", js.toString());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> responseBodyCall;
            responseBodyCall = apiInterface.getDetails("save/primarypayment", js.toString());
            Log.v("Payment_Request", responseBodyCall.request().toString());
            responseBodyCall.enqueue(new Callback<JsonObject>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject jsonObject = response.body();
                    Log.v("Payment_Response", jsonObject.toString());
                    if (jsonObject.get("success").toString().equalsIgnoreCase("true")){
                        //    Toast.makeText(getApplicationContext(), "sign"+signature, Toast.LENGTH_LONG).show();
                        //     Toast.makeText(getApplicationContext(), "razorid"+responseid, Toast.LENGTH_LONG).show();
                        //     Toast.makeText(getApplicationContext(), "razpay"+razorid, Toast.LENGTH_LONG).show();
                        String successMsg = "Dispatched successfully";

                        Toast.makeText(ViewReportActivity.this, successMsg, Toast.LENGTH_SHORT).show();

                        onBackPressed();

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
        startActivityForResult(payIntent, ACTIVITY_REQUEST_CODE);

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
                    jsonArray = jsonRootObject.optJSONArray("Data");
                    JSONObject jsonObject = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        //    OrderValueTotal=Double.valueOf(jsonObject.getString("Order_Value"));
                        if(jsonObject.has("OrderVal"))
                            OrderValueTotal=Double.valueOf(jsonObject.getString("OrderVal"));
                        if(jsonObject.has("taxval"))
                            OrderAmtNew= Double.valueOf(jsonObject.getString("taxval"));
                        //  TotalValue.setText("Rs."+jsonObject.getString("taxval"));//working code commented
                        String total = "0";
                        if(jsonObject.has("OrderVal") && !jsonObject.getString("OrderVal").equals(""))
                            total = Constants.roundTwoDecimals(Double.parseDouble(jsonObject.getString("OrderVal")));

                        TotalValue.setText("Rs. "+total);
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
                            ib_logout.setVisibility(View.GONE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if(requestCode == ACTIVITY_REQUEST_CODE) {

                boolean closeActivity = false;
                if(data!=null && data.hasExtra("closeActivity"))
                    closeActivity = data.getBooleanExtra("closeActivity", false);

                if(closeActivity)
                    ViewReportActivity.this.finish();
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
                String qty = "0";

                jsonObject = jsonArray.getJSONObject(i);

                String id = String.valueOf(jsonObject.get("id"));
                String Name = String.valueOf(jsonObject.get("name"));
                String PName = String.valueOf(jsonObject.get("Pname"));
                String PRate = String.valueOf(jsonObject.get("Product_Cat_Code"));
                String PBarCode = String.valueOf(jsonObject.get("Product_Brd_Code"));
                String PId = String.valueOf(jsonObject.get("PID"));
                String PUOM = String.valueOf(jsonObject.get("UOM"));
                String PSaleUnit = String.valueOf(jsonObject.get("Default_UOM"));
                String PDiscount = String.valueOf(jsonObject.get("Discount"));
                String PTaxValue = String.valueOf(jsonObject.get("Tax_value"));
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
                    if(categoryIndex ==-1)
                        categoryIndex = i;
                }
                contact.insert(new PrimaryProduct(id, PId, Name, PName, PBarCode, PUOM, PRate,
                        PSaleUnit, PDiscount, PTaxValue, qty, qty, "0", "0", "0",
                        schemeList,unitQty, uomList));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mCommon_class.ProgressdialogShow(2, "");
//        startActivity(new Intent(SecondRetailerActivity.this, SecondaryOrderProducts.class));

        Intent dashIntent = new Intent(getApplicationContext(), PrimaryOrderProducts.class);
        dashIntent.putExtra("Mode", "0");
        dashIntent.putExtra("editMode", 1);
        if(categoryIndex ==-1)
            categoryIndex =0;
        dashIntent.putExtra("categoryIndex", categoryIndex);
        dashIntent.putExtra("orderVal", String.valueOf(OrderValueTotal));
        dashIntent.putExtra("order_type", 2);
        dashIntent.putExtra("PhoneOrderTypes", 0);
        startActivity(dashIntent);

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
                        break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return conQty;
    }


    public void brandSecondaryApi() {

        String tempalteValue = "{\"tableName\":\"sec_category_master\",\"coloumns\":\"[\\\"Category_Code as id\\\", \\\"Category_Name as name\\\"]\",\"sfCode\":0,\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Category(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.State_Code), tempalteValue);

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

                new PopulateDbAsyntasks(PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()).execute();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }


}