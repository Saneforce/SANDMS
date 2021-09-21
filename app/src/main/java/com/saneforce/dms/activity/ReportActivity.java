package com.saneforce.dms.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.saneforce.dms.adapter.ReportViewAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.model.OrderGroup;
import com.saneforce.dms.model.ReportDataList;
import com.saneforce.dms.model.ReportModel;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.CustomListViewDialog;
import com.saneforce.dms.utils.Shared_Common_Pref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity implements DMS.Master_Interface{
    TextView toolHeader, txtTotalValue, txtProductDate,  txtName;
    ImageView imgBack,imgShare;
    Button fromBtn, toBtn;

    String fromDateString, dateTime, toDateString, SF_CODE, FReport = "", TReport = "", OrderType = "1";
    private int mYear, mMonth, mDay, mHour, mMinute;
    ReportViewAdapter mReportViewAdapter;
    RecyclerView mReportList;
//    ArrayList<Float> mArrayList;
    Shared_Common_Pref shared_common_pref;
    Integer Count = 0;
    List<Common_Model> modeOrderData = new ArrayList<>();
    Common_Model mCommon_model_spinner;
    CustomListViewDialog customDialog;
    LinearLayout linearOrderMode;
    TextView txtOrderStatus;
    String orderTakenByFilter ="All";
    ArrayList<String> OrderStatusList;
//    ArrayList<String> OrderStatusListID;
    LinearLayout linearLayout;

    Toolbar toolbar_top;

//    private Bitmap bitmap,bitmapTotal;

    // constant code for runtime permissions
//    private static final int PERMISSION_REQUEST_CODE = 200;
    LinearLayout totalLayout;
//    TextView tv_type;
//    ImageView filter;

//    List<ReportModel> mDReportModels = new ArrayList<>();
List<ReportModel> filteredList = new ArrayList<>();
    int viewType = 1;

    LinearLayout headingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        headingLayout = (LinearLayout) findViewById(R.id.headingLayout);
        toolbar_top=findViewById(R.id.toolbar_top);
        toolbar_top.setVisibility(View.VISIBLE);

        totalLayout=findViewById(R.id.totalLayout);
        // filter=findViewById(R.id.toolbar_filter);
        FReport = getIntent().getStringExtra("FromReport");
        TReport = getIntent().getStringExtra("ToReport");
        viewType = getIntent().getIntExtra("viewType", 1);
        Count = getIntent().getIntExtra("count", 100);
        shared_common_pref = new Shared_Common_Pref(this);
        OrderType = shared_common_pref.getvalue("OrderType");
        Log.v("OrderType", OrderType);

//        mArrayList = new ArrayList<>();
        txtTotalValue = (TextView) findViewById(R.id.total_value);
        getToolbar();
        txtOrderStatus=findViewById(R.id.txt_orderstatus);
        txtName = findViewById(R.id.dist_name);
        txtName.setText("Name: "+ ""+shared_common_pref.getvalue(Shared_Common_Pref.name) + " ~ " + shared_common_pref.getvalue(Shared_Common_Pref.Sf_UserName));

        TextView tv_erp_code = findViewById(R.id.tv_erp_code);
        if(!shared_common_pref.getvalue1(Shared_Common_Pref.USER_ERP_CODE).equals("")){
            tv_erp_code.setVisibility(View.VISIBLE);
            tv_erp_code.setText("ERP Code: "+ ""+shared_common_pref.getvalue(Shared_Common_Pref.USER_ERP_CODE));
        }else
            tv_erp_code.setVisibility(View.GONE);

        @SuppressLint("WrongConstant")
        SharedPreferences sh = getSharedPreferences("MyPrefs", MODE_APPEND);
        SF_CODE = sh.getString("Sf_Code", "");
        Log.e("SF_CODE", SF_CODE);
        fromBtn = (Button) findViewById(R.id.from_picker);
        toBtn = (Button) findViewById(R.id.to_picker);
        linearOrderMode=findViewById(R.id.lin_order);
//        tv_type=findViewById(R.id.tv_type);
        txtTotalValue.setText("0");
        DateFormat df = new SimpleDateFormat("yyyy-MM-d");
        Calendar calobj = Calendar.getInstance();
        dateTime = df.format(calobj.getTime());
//        System.out.println("Date_and_Time" + dateTime);

     /*   if(OrderType.equals("1"))
            tv_type.setVisibility(View.GONE);
        else
            tv_type.setVisibility(View.VISIBLE);
*/
        if(viewType ==2)
            headingLayout.setVisibility(View.GONE);
        else
            headingLayout.setVisibility(View.VISIBLE);

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
//                                mArrayList.clear();
//                                OrderStatusList.clear();
                                modeOrderData.clear();
                                orderTakenByFilter = "All";
                                txtOrderStatus.setText(orderTakenByFilter);
                                ViewDateReport(orderTakenByFilter);

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
//                        mArrayList.clear();
//                      OrderStatusList.clear();
                        modeOrderData.clear();
                        orderTakenByFilter = "All";
                        txtOrderStatus.setText(orderTakenByFilter);
                        ViewDateReport(orderTakenByFilter);

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mReportList = (RecyclerView) findViewById(R.id.report_list);
        mReportList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReportList.setLayoutManager(layoutManager);


        OrderStatusList=new ArrayList<>();
        OrderStatusList.add("All");
        /*OrderStatusList.add("Order Dispatched");

        if(OrderType.equals("2")){
            OrderStatusList.add("Dispatch Pending");

        }else {
            OrderStatusList.add("Payment Pending");
            OrderStatusList.add("Payment Verified");
            OrderStatusList.add("Payment Done");
            OrderStatusList.add("Credit Raised");
            OrderStatusList.add("Credit Verified");
            OrderStatusList.add("Credit Dispatched");

        }*/
        updateFilterList();


        customDialog = new CustomListViewDialog(ReportActivity.this,modeOrderData, 11);
//        Window window = customDialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        customDialog.show();


        linearOrderMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customDialog!=null)
                    customDialog.show();
            }
        });


    }

    private void updateFilterList() {
        modeOrderData.clear();
        for (int i = 0; i < OrderStatusList.size(); i++) {
            String id = String.valueOf(OrderStatusList.get(i));
            String name = OrderStatusList.get(i);
            mCommon_model_spinner = new Common_Model(id, name, "flag");
            modeOrderData.add(mCommon_model_spinner);
        }

        try {
            if(customDialog!=null)
                customDialog.da.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constant.isInternetAvailable(this)){
            ViewDateReport(orderTakenByFilter);
        }else
            Toast.makeText(ReportActivity.this, "Please check the Internet connection", Toast.LENGTH_SHORT).show();

    }

    /*Toolbar*/
    public void getToolbar() {
        /*filter= (ImageView) findViewById(R.id.toolbar_filter);
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
        });*/

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
                onBackPressed();
//                startActivity(new Intent(getApplicationContext(), ReportDashBoard.class));

            }
        });

        toolHeader = (TextView) findViewById(R.id.toolbar_title);

        String title  = "";
        if(viewType ==1){
            if(OrderType.equals("1"))
                title= "PRIMARY REPORT";
            else
                title= "SECONDARY REPORT";
        }else
            title = "FINANCIAL REPORT";

        toolHeader.setText(title);


//        toolSearch = (EditText) findViewById(R.id.toolbar_search);
//        toolSearch.setVisibility(View.GONE);
    }


    public void ViewDateReport(String orderTakenByFilter) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ReportDataList> responseBodyCall;
        String axn = "";
        if(viewType ==1){
            if (OrderType.equalsIgnoreCase("1")) {
                axn = "get/ViewReport";
            } else {
                axn = "get/secviewreport";
            }

        }else
            axn = "get/finreport";

        responseBodyCall = apiInterface.reportValues(axn, shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Div_Code).replaceAll(",", ""), fromDateString, toDateString);

//        Log.v("Request_cal", responseBodyCall.request().toString());
        responseBodyCall.enqueue(new Callback<ReportDataList>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ReportDataList> call, Response<ReportDataList> response) {
                ReportDataList mReportActivities = response.body();
                //  List<ReportModel> mDReportModels;
                List<ReportModel> mDReportModels = new ArrayList<>();
                if (mReportActivities != null) {

                    if (mReportActivities.getData() != null)
                        mDReportModels = mReportActivities.getData();

                }

                try
                {

                    modeOrderData.clear();
                    filteredList.clear();
                    Float intSum = 0f;

                    OrderStatusList.clear();
                    OrderStatusList.add("All");

                    if(mDReportModels!=null){
                        for(ReportModel r : mDReportModels){
                            if(orderTakenByFilter.equalsIgnoreCase("All") || r.getOrderStatus().equalsIgnoreCase(orderTakenByFilter))
                                if(viewType == 2 && (r.getSubOrderGroup()==null || r.getSubOrderGroup().size()==0)){
                                    List<OrderGroup> orderGroupList = new ArrayList<>();
                                    orderGroupList.add(new OrderGroup(r.getOrderNo(), r.getOrderValue(), r.getReceived_Amt(), r.getOrderValue()));
                                    r.setSubOrderGroup(orderGroupList);
                                }
                                filteredList.add(r);

                            if(orderTakenByFilter.equalsIgnoreCase("All") || orderTakenByFilter.equalsIgnoreCase(r.getOrderStatus())){

                                Float orderValue= null;
                                if (r.getOrderValue()!=null && !r.getOrderValue().equals("")  && !r.getOrderValue().equals("null") ) {
                                    orderValue = Float.valueOf(r.getOrderValue());
                                    intSum = intSum +orderValue;
                                }

                            }

                            String orderStatus = "";
                            if(r.getOrderStatus()!=null && !r.getOrderStatus().equals("")  && !r.getOrderStatus().equals("null") ){
                                orderStatus = r.getOrderStatus();
                                if(!OrderStatusList.contains(orderStatus))
                                    OrderStatusList.add(orderStatus);
                            }

                        }
                        txtTotalValue.setText("Rs . "+ Constant.roundTwoDecimals(intSum));
                    }

                    updateFilterList();
                }catch (Exception e){
                    e.printStackTrace();
                }

                mReportViewAdapter = new ReportViewAdapter(ReportActivity.this, filteredList, new DMS.ViewReport() {
                    @Override
                    public void reportCliick(String productId, String orderDate, String OrderValue, String orderType, String editOrder, int Paymentflag, int Dispatch_Flag) {//,String TaxValue,String Tax
                        Intent intnet = new Intent(ReportActivity.this, ViewReportActivity.class);
                        intnet.putExtra("ProductID", productId);
                        intnet.putExtra("OrderDate", orderDate);
                        intnet.putExtra("FromDate", fromBtn.getText().toString());
                        intnet.putExtra("ToDate", toBtn.getText().toString());
                        intnet.putExtra("OderValue", OrderValue);
                        intnet.putExtra("orderType", orderType);
                        intnet.putExtra("editOrder", editOrder);
                        intnet.putExtra("Paymentflag", Paymentflag);
                        intnet.putExtra("Dispatch_Flag", Dispatch_Flag);

                        startActivity(intnet);
                        //  finish();
                    }
                },orderTakenByFilter,txtTotalValue, OrderType, viewType);
                mReportList.setAdapter(mReportViewAdapter);
            }

            @Override
            public void onFailure(Call<ReportDataList> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ReportActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }



    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 11) {
            txtOrderStatus.setText(myDataset.get(position).getName());
            orderTakenByFilter=myDataset.get(position).getName();
            Log.e("order filter",orderTakenByFilter);

            ViewDateReport(orderTakenByFilter);
//            mArrayList.clear();

        }
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
                }else {
//                    Toast.makeText(ReportActivity.this, "Please enable storage permission to share pdf", Toast.LENGTH_SHORT).show();
                    Constant.showSnackbar(ReportActivity.this, findViewById(R.id.scrolllayout));
                }

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


}

