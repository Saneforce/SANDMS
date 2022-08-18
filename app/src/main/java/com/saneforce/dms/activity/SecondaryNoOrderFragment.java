package com.saneforce.dms.activity;

import static com.billdesk.utils.ResourceConstants.i;

import static io.realm.Realm.getApplicationContext;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.saneforce.dms.R;
import com.saneforce.dms.adapter.ReportNoOrderAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.model.OrderGroup;
import com.saneforce.dms.model.ReportModel;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.CustomListViewDialog;
import com.saneforce.dms.utils.Shared_Common_Pref;
import com.saneforce.dms.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondaryNoOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondaryNoOrderFragment extends Fragment {
    TextView toolHeader,txtName;
    ImageView imgBack;
    Button fromBtn,toBtn;

    int geoTaggingType=1;

    String fromDateString,dateTime,toDateString,FReport="",TReport="",OrderType="1";
    private int mYear,mMonth,mDay;
    ReportNoOrderAdapter mReportViewAdapter;
    RecyclerView mReportList;
    Shared_Common_Pref shared_common_pref;
    Integer Count=0;
    List<Common_Model>modeOrderData=new ArrayList<>();
    Common_Model mCommon_model_spinner;
    CustomListViewDialog customDialog;
    TextView txtOrderStatus;
    String orderTakenByFilter="All";
    ArrayList<String> OrderStatusList;
    LinearLayout linearLayout;
    LinearLayout totalLayout;
    LinearLayout headingLayout;

    List<ReportModel> filteredList=new ArrayList<>();
    int viewType=1;

    private static final String GEOTAGGING_TYPE="geotaggingType";

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecondaryNoOrderFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondaryNoOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondaryNoOrderFragment newInstance(String param1, String param2) {
        SecondaryNoOrderFragment fragment = new SecondaryNoOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(int type){
        SecondaryNoOrderFragment fragment=new SecondaryNoOrderFragment();
        Bundle args=new Bundle();
        args.putInt(GEOTAGGING_TYPE,type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            geoTaggingType=getArguments().getInt(GEOTAGGING_TYPE);

            FReport=getArguments().getString("FromReport");
            TReport=getArguments().getString("ToReport");
            viewType=getArguments().getInt("viewType",1);
            Count=getArguments().getInt("count",100);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_no_order_list, container, false);

        linearLayout=view.findViewById(R.id.linearLayout);
        headingLayout=view.findViewById(R.id.headingLayout);

        totalLayout=view.findViewById(R.id.totalLayout);

        shared_common_pref=new Shared_Common_Pref(requireActivity());
        OrderType=shared_common_pref.getvalue("OrderType");
        Log.v("OrderType",OrderType);

        txtOrderStatus=view.findViewById(R.id.txt_orderstatus);
        txtName=view.findViewById(R.id.dist_name);
        txtName.setText("Name:" + ""+ shared_common_pref.getvalue(Shared_Common_Pref.name)+"~"+shared_common_pref.getvalue(Shared_Common_Pref.Sf_UserName));

        TextView tv_erp_code=view.findViewById(R.id.tv_erp_code);
        if(!shared_common_pref.getvalue1(Shared_Common_Pref.USER_ERP_CODE).equals("")){
            tv_erp_code.setVisibility(View.VISIBLE);
            tv_erp_code.setText("ERP Code:"+""+shared_common_pref.getvalue(Shared_Common_Pref.USER_ERP_CODE));
        }
        else
            tv_erp_code.setVisibility(View.GONE);

        fromBtn=view.findViewById(R.id.from_picker);
        toBtn=view.findViewById(R.id.to_picker);
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calobj=Calendar.getInstance();
        dateTime=df.format(calobj.getTime());

        if(viewType==2)
            headingLayout.setVisibility(View.GONE);
        else
            headingLayout.setVisibility(View.VISIBLE);

        if(Count==1){
            fromBtn.setText(""+FReport);
            toBtn.setText(""+TReport);
            fromDateString=FReport;
            toDateString=TReport;
        }else{
            fromBtn.setText(""+dateTime);
            toBtn.setText(""+dateTime);
            fromDateString=dateTime;
            toDateString=dateTime;
        }

        fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                mYear=c.get(Calendar.YEAR);
                mMonth=c.get(Calendar.MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(requireActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDateString=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                                fromBtn.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                                modeOrderData.clear();
                                orderTakenByFilter="All";
                                txtOrderStatus.setText(orderTakenByFilter);
                                ViewDateReport(orderTakenByFilter);
                            }
                        },mYear,mMonth,mDay);
                if(!toDateString.equals("")){
                    datePickerDialog.getDatePicker().setMaxDate(TimeUtils.getTimeStamp(toDateString,TimeUtils.FORMAT1));
                }
                datePickerDialog.show();
            }
        });
        final Calendar calendar=Calendar.getInstance();
        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromDateString.equals(""))
                    Toast.makeText(requireActivity(),"Select date from FromDate",Toast.LENGTH_SHORT).show();
                else {
                    final Calendar c=Calendar.getInstance();
                    mYear=c.get(Calendar.YEAR);
                    mMonth=c.get(Calendar.MONTH);
                    mDay=c.get(Calendar.DATE);
                    DatePickerDialog datePickerDialog=new DatePickerDialog(requireActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker View, int year, int monthOfYear, int dayOfMonth) {
                                toDateString=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                                toBtn.setText(year+"-"+(monthOfYear+1+"-"+dayOfMonth));
                                modeOrderData.clear();
                                orderTakenByFilter="All";
                                txtOrderStatus.setText(orderTakenByFilter);
                                ViewDateReport(orderTakenByFilter);
                            }
                        },mYear,mMonth,mDay);
                    datePickerDialog.getDatePicker().setMinDate(TimeUtils.getTimeStamp(fromDateString,TimeUtils.FORMAT1));
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
                    datePickerDialog.show();
                }
            }
        });

        mReportList=view.findViewById(R.id.report_list);
        mReportList.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(requireActivity());
        mReportList.setLayoutManager(layoutManager);

        OrderStatusList=new ArrayList<>();
        OrderStatusList.add("All");


        customDialog=new CustomListViewDialog(requireActivity(),modeOrderData,11);

        mReportViewAdapter = new ReportNoOrderAdapter(requireActivity(), filteredList, orderTakenByFilter, OrderType, viewType);
        mReportList.setAdapter(mReportViewAdapter);

        return view;
    }

    private void updateFilterList(){
        modeOrderData.clear();
        for (int i=0;i<OrderStatusList.size();i++);
        {
            String id = String.valueOf(OrderStatusList.get(i));
            String name = OrderStatusList.get(i);
            mCommon_model_spinner = new Common_Model(id, name, "flag");
            modeOrderData.add(mCommon_model_spinner);
        }
        try {
            if(customDialog!=null)
                customDialog.dataAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Constant.isInternetAvailable(requireActivity())){
            ViewDateReport(orderTakenByFilter);
        }else
            Toast.makeText(requireActivity(),"Please check the Internet Connection",Toast.LENGTH_SHORT).show();
    }

    private void ViewDateReport(String orderTakenByFilters){
        if(TimeUtils.getDate(TimeUtils.FORMAT1,fromDateString).compareTo(TimeUtils.getDate(TimeUtils.FORMAT1,toDateString))<=0){
            ApiInterface apiInterface= ApiClient.getClient().create(ApiInterface.class);
            Call<ResponseBody> responseBodyCall;
            String axn= "get/retailerVisits";


            responseBodyCall=apiInterface.reportNoOrder(axn, shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), shared_common_pref.getvalue(Shared_Common_Pref.Div_Code).replaceAll(",", ""), fromDateString, toDateString);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String res = response.body().string();
                        Log.d("SecNoOrderFragment", "onResponse: "+ res);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ResponseBody mReportActivities = response.body();
                    List<ReportModel> mDReportModels = new ArrayList<>();
                    /*if (mReportActivities != null) {
                        if (mReportActivities.getData() != null)
                            mDReportModels = mReportActivities.getData();

                    }*/
                    float intSum = 0f;

                    try {
                        modeOrderData.clear();
                        filteredList.clear();

                        OrderStatusList.clear();
                        OrderStatusList.add("All");

                        if (mDReportModels != null) {
                            for (ReportModel r : mDReportModels) {
                                if (orderTakenByFilter.equalsIgnoreCase("All") || r.getOrderStatus().equalsIgnoreCase(orderTakenByFilter))
                                    if (viewType == 2 && (r.getSubOrderGroup() == null || r.getSubOrderGroup().size() == 0)) {
                                        List<OrderGroup> orderGroupList = new ArrayList<>();
                                        orderGroupList.add(new OrderGroup(r.getOrderNo(), r.getOrderValue(), r.getReceived_Amt(), r.getOrderValue()));
                                        r.setSubOrderGroup(orderGroupList);
                                    }
                                filteredList.add(r);

                                if (orderTakenByFilter.equalsIgnoreCase("All") || orderTakenByFilter.equals("") && !r.getOrderValue().equals("null")) {
                                    Float orderValue = null;
                                    if (r.getOrderValue() != null && !r.getOrderValue().equals("") && !r.getOrderValue().equals("null")) {
                                        orderValue = Float.valueOf(r.getOrderValue());
                                        intSum = intSum + orderValue;
                                    }
                                }

                                String orderStatus = "";
                                if (r.getOrderStatus() != null && !r.getOrderStatus().equals("") && !r.getOrderStatus().equals("nu" + "null")) {
                                    orderStatus = r.getOrderStatus();
                                    if (!OrderStatusList.contains(orderStatus))
                                        OrderStatusList.add(orderStatus);
                                }
                            }
                        }

                         updateFilterList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mReportViewAdapter.setOrderTakenbyFilter(orderTakenByFilter);
                    mReportViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(requireActivity(), "Something went wrong,please try again", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireActivity(), "Invalid date selection", Toast.LENGTH_SHORT).show();
        }
    }

    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 11) {
            txtOrderStatus.setText(myDataset.get(position).getName());
            orderTakenByFilter = myDataset.get(position).getName();
            Log.e("order filter", orderTakenByFilter);

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

        fileName = String.valueOf(System.currentTimeMillis());

        dirpath = Environment.getExternalStorageDirectory().toString();
        File file = null;
        try {
            File appDir = new File(Environment.getExternalStorageDirectory(), "receiptImage");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            file = new File(appDir, fileName);
            FileOutputStream out = new FileOutputStream(file);
            // Store the bitmap as a picture in jpg format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Image img = Image.getInstance(file.getAbsolutePath());
            img.setAbsolutePosition(0, 0);

            Rectangle pagesize = new Rectangle(img.getScaledWidth(), img.getScaledHeight());
            Document document = new Document(pagesize);
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/" + fileName + ".pdf")); //  Change pdf's name.
            document.open();
            document.add(img);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireActivity(), "image to pdf conversion failure", Toast.LENGTH_SHORT).show();
            return;
        }

        File file1 = new File(dirpath + "/" + fileName + ".pdf");
        Uri fileUri = FileProvider.getUriForFile(requireActivity(), requireActivity().getPackageName() + ".provider", file1);
        Intent intent = ShareCompat.IntentBuilder.from(requireActivity())
                .setType("*/*")
                .setStream(fileUri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);


    }

    public void checkPermission() {
        Dexter.withContext(requireActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            saveBitmap(createBitmap3(linearLayout, linearLayout.getWidth(), linearLayout.getHeight()));
                        } else {
                            Toast.makeText(requireActivity(),"Please give Permission",Toast.LENGTH_SHORT).show();
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
            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
            startActivityForResult(intent, 2296);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 2296);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toolHeader = null;
        txtName = null;
        imgBack = null;
        fromBtn = null;
        toBtn = null;
        fromDateString = null;
        dateTime = null;
        toDateString = null;
        FReport = null;
        TReport = null;
        OrderType = null;
        mReportViewAdapter = null;
        mReportList = null;
        shared_common_pref = null;
        modeOrderData = null;
        mCommon_model_spinner = null;
        customDialog = null;
        txtOrderStatus = null;
        orderTakenByFilter = null;
        OrderStatusList = null;
        linearLayout = null;
        totalLayout = null;
        filteredList = null;
        headingLayout = null;

    }
}
