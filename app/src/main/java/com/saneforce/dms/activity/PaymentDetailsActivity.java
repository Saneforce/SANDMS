package com.saneforce.dms.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.billdesk.sdk.LibraryPaymentStatusProtocol;
import com.billdesk.sdk.PaymentOptions;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.saneforce.dms.R;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.utils.AlertDialogBox;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.CameraPermission;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.CustomListViewDialog;
import com.saneforce.dms.utils.ImageFilePath;
import com.saneforce.dms.utils.SampleCallBack;
import com.saneforce.dms.utils.Shared_Common_Pref;
import com.saneforce.dms.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailsActivity extends AppCompatActivity
        implements DMS.Master_Interface,  PaymentResultWithDataListener {//PaymentResultListener,
    private static final String TAG = PaymentDetailsActivity.class.getSimpleName();
    private static final int SELECT_PICTURE = 103;
    private static final int CAMERA_REQUEST = 1888;

    TextView productId, productDate, productAmt, offlineMode;
    private RadioGroup radioGroup;
    LinearLayout offView;
    Uri outputFileUri;
    CustomListViewDialog customDialog;
    ImageView imgSource;
    EditText edtUTR;
    String orderIDRazorpay;
    String razorpay_payment_id="";
    String razorpay_response="";
    //test key id rzp_test_JOC0wRKpLH1cVW
    String keyID="";
    //test key secret 9EzSlxvJbTyQ2Hg0Us5ZX4VD
    String key_Secret="";
    String razorpay_signature="";
    Shared_Common_Pref mShared_common_pref;

    String finalPath = "", filePath = "", DateValue = "",
            PaymntMode = "", PaymentTypecode = "";
    List<Common_Model> modelOffileData = new ArrayList<>();
    Common_Model mCommon_model_spinner;
    //    int Amount;
    double AMOUNTFINAL ;
//    SoapPrimitive resultString;
//    JSONObject jsonObjectRazorpay;

    LinearLayout ll_date;
    TextView tv_date;

    LinearLayout ll_amount;
    EditText et_amount;
    String currentDate ="";
    String serverFileName = "";

    //1 razor pay
    //2 bill desk

    int paymentGateWayType = 2;

    public static String OrderIDValue = "", AmountValue = "", divCode, sfCode , stateCode;

    ImageView iv_attachment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        mShared_common_pref = new Shared_Common_Pref(this);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        productId = findViewById(R.id.ord_id);
        productDate = findViewById(R.id.dis_date);
        productAmt = findViewById(R.id.dis_amt);
        offView = findViewById(R.id.lin_offline);
        imgSource = findViewById(R.id.imgSource);
        edtUTR = findViewById(R.id.edt_utr);
        offlineMode = findViewById(R.id.txt_offline_mode);
        ll_date = findViewById(R.id.ll_date);
        tv_date = findViewById(R.id.tv_date);
        ll_amount = findViewById(R.id.ll_amount);
        et_amount = findViewById(R.id.et_amount);
        iv_attachment = findViewById(R.id.iv_attachment);

        divCode = mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code);
        sfCode  = mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code);
        stateCode = mShared_common_pref.getvalue(Shared_Common_Pref.State_Code);

        getOfflineMode();
        OrderIDValue = String.valueOf(getIntent().getSerializableExtra("OrderId"));
        DateValue = String.valueOf(getIntent().getSerializableExtra("Date"));
//        AmountValue = String.valueOf(getIntent().getSerializableExtra("Amount"));
        AmountValue = "5.00";
        paymentGateWayType = getIntent().getIntExtra("paymentGateWayType", 1);

        productId.setText(OrderIDValue);
        productDate.setText(DateValue);
        productAmt.setText(AmountValue);
        et_amount.setText(AmountValue);

        RadioButton rbOnline = findViewById(R.id.online);
        RadioButton rbCredit = findViewById(R.id.cred);
        RadioButton rbOffline = findViewById(R.id.offline);

        if(ApiClient.APP_TYPE == 2){
            rbOnline.setVisibility(View.GONE);
            rbCredit.setVisibility(View.GONE);
            rbOffline.setChecked(true);
            PaymntMode = "Offline";
            offView.setVisibility(View.VISIBLE);

            ll_amount.setVisibility(View.VISIBLE);
            ll_date.setVisibility(View.VISIBLE);



        }else {
            rbOnline.setVisibility(View.VISIBLE);
            rbCredit.setVisibility(View.VISIBLE);
            rbOffline.setVisibility(View.VISIBLE);
            PaymntMode = "";
            offView.setVisibility(View.GONE);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.online) {
                    offView.setVisibility(View.GONE);
                    PaymntMode = "Online";

                    ll_amount.setVisibility(View.GONE);
                    ll_date.setVisibility(View.GONE);

                } else if (checkedId == R.id.offline) {
                    offView.setVisibility(View.VISIBLE);
                    PaymntMode = "Offline";

                    ll_amount.setVisibility(View.VISIBLE);
                    ll_date.setVisibility(View.VISIBLE);

                } else if (checkedId == R.id.cred) {
                    PaymntMode = "Credit";
                    offView.setVisibility(View.GONE);

                    ll_amount.setVisibility(View.GONE);
                    ll_date.setVisibility(View.GONE);

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
                DatePickerDialog dialog = new DatePickerDialog(PaymentDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        getToolbar();
    }

    public void LinearOfflineMode(View v) {
        customDialog = new CustomListViewDialog(PaymentDetailsActivity.this, modelOffileData, 10);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    public void ProceedOrder(View v) {
        if(!Constant.isInternetAvailable(PaymentDetailsActivity.this))
            Toast.makeText(PaymentDetailsActivity.this, "Please check the Internet connection", Toast.LENGTH_SHORT).show();
        else if(PaymntMode.equals(""))
            Toast.makeText(PaymentDetailsActivity.this, "Please Select the Payment Option", Toast.LENGTH_SHORT).show();
        else if(PaymntMode.equalsIgnoreCase("Offline") && offlineMode.getText().toString().equals("")) {
            Toast.makeText(this, "Please choose any Offline payment Option", Toast.LENGTH_SHORT).show();
        }else if(PaymntMode.equalsIgnoreCase("Offline") && iv_attachment.getVisibility()==View.VISIBLE && serverFileName.equals("")) {
            Toast.makeText(this, "Please choose Valid Image", Toast.LENGTH_SHORT).show();
        }else {
            if (PaymntMode.equalsIgnoreCase("Offline")) {
                ProceedPayment("","","");
            } else if (PaymntMode.equalsIgnoreCase("Online")) {
                if(paymentGateWayType == 2)
                    //razor pay
                    getOnlinePaymentKeys();
                else {
                    //bill desk
                    showPaymentDetailsDialog();
                }

            } else if(PaymntMode.equalsIgnoreCase("Credit")) {
                ProceedPayment("","","");
//                AsyncCallWS task = new AsyncCallWS();
//                task.execute();
            }else {
                Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void showPaymentDetailsDialog() {

        final Dialog dialog = new Dialog(PaymentDetailsActivity.this);
        dialog.setContentView(R.layout.dialog_payment_credential);


        EditText et_email = dialog.findViewById(R.id.et_email);
        EditText et_mob = dialog.findViewById(R.id.et_mob);
        TextView tv_proceed = dialog.findViewById(R.id.tv_proceed);


        tv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Constant.isInternetAvailable(PaymentDetailsActivity.this)){
                    Toast.makeText(PaymentDetailsActivity.this, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }else if(et_email.getText().toString().trim().equals("")) {
                    Toast.makeText(PaymentDetailsActivity.this, "Please enter the valid email id", Toast.LENGTH_SHORT).show();
                }else if(et_mob.getText().toString().trim().equals("")){
                    Toast.makeText(PaymentDetailsActivity.this, "Please enter the valid mobile number", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.dismiss();
/*
//                    AmountValue
                    //    String strPGMsg = "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|892409133";
                    String strPGMsg = "AIRMTST|ARP1523968042763|NA|"+2+"|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|3277831407";
                    String strTokenMsg = null;// "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|723938585|CP1005!AIRMTST!D1DDC94112A3B939A4CFC76B5490DC1927197ABBC66E5BC3D59B12B552EB5E7DF56B964D2284EBC15A11643062FD6F63!NA!NA!NA";

                    SampleCallBack objSampleCallBack = new SampleCallBack();

                    Intent sdkIntent = new Intent(PaymentDetailsActivity.this, PaymentOptions.class);
                    sdkIntent.putExtra("msg",strPGMsg);
                    if(strTokenMsg != null && strTokenMsg.length() > strPGMsg.length()) {
                        sdkIntent.putExtra("token",strTokenMsg);
                    }
                    sdkIntent.putExtra("user-email",et_email.getText().toString());
                    sdkIntent.putExtra("user-mobile",et_mob.getText().toString());
                    sdkIntent.putExtra("callback", objSampleCallBack);

                    startActivity(sdkIntent);
*/

                    getSdkParams(et_email.getText().toString(), et_mob.getText().toString());
                }
            }
        });
        dialog.show();

    }
    public void getSdkParams(String email, String mob) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getSdkParams("get/paymentResponseLive", mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), mShared_common_pref.getvalue(Shared_Common_Pref.State_Code), AmountValue, OrderIDValue, mob, email);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    String res = response.body().toString();
                    if(res!=null && !res.equals("")){

                        JSONObject jsonRootObject = new JSONObject(res);
                        Log.v(TAG, "getSdkParams "+ jsonRootObject.toString());

                        //    String strPGMsg = "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|892409133";
                        String strPGMsg = jsonRootObject.getString("msg");
                        String strTokenMsg = jsonRootObject.getString("token");// "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|723938585|CP1005!AIRMTST!D1DDC94112A3B939A4CFC76B5490DC1927197ABBC66E5BC3D59B12B552EB5E7DF56B964D2284EBC15A11643062FD6F63!NA!NA!NA";
                        String userEmail = jsonRootObject.getString("userEmail");// "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|723938585|CP1005!AIRMTST!D1DDC94112A3B939A4CFC76B5490DC1927197ABBC66E5BC3D59B12B552EB5E7DF56B964D2284EBC15A11643062FD6F63!NA!NA!NA";
                        String userMobile = jsonRootObject.getString("userMobile");// "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|723938585|CP1005!AIRMTST!D1DDC94112A3B939A4CFC76B5490DC1927197ABBC66E5BC3D59B12B552EB5E7DF56B964D2284EBC15A11643062FD6F63!NA!NA!NA";

                        SampleCallBack objSampleCallBack = new SampleCallBack(new DMS.PaymentResponseBilldesk() {
                            @Override
                            public void onResponse(Context context, String response) {
                                updateResponseToServer(PaymentDetailsActivity.this, response);
                            }
                        }, PaymentDetailsActivity.this);

                        Intent sdkIntent = new Intent(PaymentDetailsActivity.this, PaymentOptions.class);
                        sdkIntent.putExtra("msg",strPGMsg);
                        if(strTokenMsg != null && strTokenMsg.length() > strPGMsg.length()) {
                            sdkIntent.putExtra("token",strTokenMsg);
                        }
                        sdkIntent.putExtra("user-email",userEmail);
                        sdkIntent.putExtra("user-mobile",userMobile);
                        sdkIntent.putExtra("callback", objSampleCallBack);
                        startActivity(sdkIntent);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
                Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }



    public void updateResponseToServer(Activity activity, String response1) {

        String[] responseSplit = response1.split("|");

        if(!responseSplit[14].equals("0300")){
            Toast.makeText(PaymentDetailsActivity.this, ""+ responseSplit[responseSplit.length-2], Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "updateResponseToServer: responseSplit "+ responseSplit);
        JSONObject js = new JSONObject();
        try {
            js.put("OrderID", OrderIDValue);
            js.put("StockistCode", mShared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("divisionCode", mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("PaymentMode", PaymntMode);

            js.put("PaymentTypeName", "");
            js.put("PaymentTypeCode", PaymentTypecode);
            js.put("UTRNumber", edtUTR.getText().toString());
            js.put("Amount", AmountValue);
            js.put("Attachement", serverFileName);


            js.put("cheque_date", "");
            js.put("cheque_amount", "");

            js.put("PaymentID", responseSplit[2]);
            js.put("RazorOrderID", OrderIDValue);
            js.put("SignatureID", responseSplit[responseSplit.length-1]);
            js.put("PaymentResponse", response1);


        }catch (Exception e){
            e.printStackTrace();
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getDetails("save/primarypaymentBillDesk",mShared_common_pref.getvalue(Shared_Common_Pref.State_Code), js.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {

                    JsonObject jsonObject = response.body();
                    Log.v("Payment_Response", jsonObject.toString());
                    if (jsonObject!=null && jsonObject.get("success").toString().equalsIgnoreCase("true")){

                        Toast.makeText(PaymentDetailsActivity.this, "Payment done successfully", Toast.LENGTH_SHORT).show();
                        activity.finish();

                    }else
                        Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Route_response", "ERROR");
                Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getOfflineMode() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getOfflineMode("get/paymentmode", mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));

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



    public void getOrderId() {
        String inputXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +"\r\n    <soap:Body>\r\n        <Get_Order_ID xmlns=\"http://tempuri.org/\">" +"\r\n            <Order_Amt>"+(long)AMOUNTFINAL+"</Order_Amt>\r\n            <Stk>"+mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code)+"</Stk>\r\n            <Div>"+mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code)+"</Div>\r\n        </Get_Order_ID>\r\n    </soap:Body>\r\n</soap:Envelope>";

        ApiInterface apiInterface = ApiClient.getXMLClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getOrderId(Constant.toRequestBody(inputXml));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String xmlResponseModel = null;
                try {
                    xmlResponseModel = response.body().string();

                    /*String wordToFind = "<Get_Order_IDResult>";
                    String wordToFind1 = "</Get_Order_IDResult>";
                    Pattern word = Pattern.compile(wordToFind);
                    Pattern word1 = Pattern.compile(wordToFind1);
                    Matcher match = word.matcher(xmlResponseModel);
                    Matcher match1 = word1.matcher(xmlResponseModel);*/
                    orderIDRazorpay = xmlResponseModel.substring(xmlResponseModel.lastIndexOf("<Get_Order_IDResult>")+20, xmlResponseModel.indexOf("</Get_Order_IDResult>"));
                    Log.d(TAG, "orderIDRazorpay: " + orderIDRazorpay);
                    getOnlinePayment(orderIDRazorpay);


                } catch (IOException e) {
                    e.printStackTrace();
                    AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Order Id Error", "Unable to get order Id from server, please try again");
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e("Route_response", "ERROR");
                AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Order Id Error", "Unable to get order Id from server, please try again");
            }
        });
    }


    public void ProceedPayment(String razorid,String responseid,String signature) {
        JSONObject js = new JSONObject();
        try {
            js.put("OrderID", OrderIDValue);
            js.put("StockistCode", mShared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("divisionCode", mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("PaymentMode", PaymntMode);

            String option = "";
            if(PaymntMode.equalsIgnoreCase("Offline"))
                option = offlineMode.getText().toString();

            js.put("PaymentTypeName", option);
            js.put("PaymentTypeCode", PaymentTypecode);
            js.put("UTRNumber", edtUTR.getText().toString());
            js.put("Amount", AmountValue);
            js.put("Attachement", serverFileName);

            if(PaymntMode.equalsIgnoreCase("Offline")){
                js.put("cheque_date", TimeUtils.changeFormat(TimeUtils.FORMAT2,TimeUtils.FORMAT1,tv_date.getText().toString()));
                js.put("cheque_amount", et_amount.getText().toString());
            }else {
                js.put("cheque_date", "");
                js.put("cheque_amount", "");
            }
            if (PaymntMode.equalsIgnoreCase("Online")) {
                js.put("PaymentID",razorid);
                js.put("RazorOrderID", responseid);
                js.put("SignatureID", signature);
            }else{
                js.put("PaymentID","");
                js.put("RazorOrderID", "");
                js.put("SignatureID", "");
            }
            Log.v("JS_VALUEdata", js.toString());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> responseBodyCall;
            responseBodyCall = apiInterface.getDetails("save/primarypayment",mShared_common_pref.getvalue(Shared_Common_Pref.State_Code), js.toString());
//            Log.v("Payment_Request", responseBodyCall.request().toString());
            responseBodyCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject jsonObject = response.body();
                    Log.v("Payment_Response", jsonObject.toString());
                    if (jsonObject.get("success").toString().equalsIgnoreCase("true")){
                        //    Toast.makeText(getApplicationContext(), "sign"+signature, Toast.LENGTH_LONG).show();
                        //     Toast.makeText(getApplicationContext(), "razorid"+responseid, Toast.LENGTH_LONG).show();
                        //     Toast.makeText(getApplicationContext(), "razpay"+razorid, Toast.LENGTH_LONG).show();
                        String successMsg = "Payment done successfully";
                        if(PaymntMode.equals("Credit"))
                            successMsg = "Credit raised successfully";

                        Toast.makeText(PaymentDetailsActivity.this, successMsg, Toast.LENGTH_SHORT).show();

                        paymentCompleted(true);

                    }else if(jsonObject.has("Msg"))
                        Toast.makeText(PaymentDetailsActivity.this, ""+ jsonObject.get("Msg").toString(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();


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
                    Toast.makeText(PaymentDetailsActivity.this, "something went wrong, please try again", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(PaymentDetailsActivity.this, "something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }
    }

    private void paymentCompleted(boolean closeActivity) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("closeActivity", closeActivity);
        setResult(-1, resultIntent);
        finish();
    }

    private void getOnlinePaymentKeys(){

        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.getPaymentKey( mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code),mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        JSONObject jsonRootObject = new JSONObject(response.body().toString());
                        if (jsonRootObject.get("success").toString().equalsIgnoreCase("true")) {

                            Log.v("NAME_STRING", response.toString());
                            JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                            if(jsonArray!=null && jsonArray.length()>0){
                                JSONObject jso = jsonArray.getJSONObject(0);
                                keyID = String.valueOf(jso.get("KeyID"));
                                key_Secret = String.valueOf(jso.get("Key_Secret"));
                                Log.v("NAME_STRING1",key_Secret);
                                Log.v("NAME_STRING",keyID);
                                if(keyID!=null && !keyID.equals("") && key_Secret!=null && !key_Secret.equals("") ){
                                    AMOUNTFINAL= 100 * Double.parseDouble(Constant.roundTwoDecimals(Double.parseDouble(AmountValue)));
                                    if(AMOUNTFINAL>0)
                                        AMOUNTFINAL = Math.ceil(AMOUNTFINAL);

                                    getOrderId();

                                }else
                                    AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
                            }else
                                AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Payment keys Error", "Unable to get payment keys from server, please try again");

                            //order id createion jul 22
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Payment keys Error", "Unable to get payment keys from server, please try again");
        }


    }
/*
    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            if(getOnlinePaymentOrderId()){

                AMOUNTFINAL= 100 * Double.parseDouble(Constant.roundTwoDecimals(Double.parseDouble(AmountValue)));
                if(AMOUNTFINAL>0)
                    AMOUNTFINAL = Math.ceil(AMOUNTFINAL);

                try {
                    if(keyID!=null && !keyID.equals("") && key_Secret!=null && !key_Secret.equals("") ){

*//*                        RazorpayClient razorpayClient = new RazorpayClient(keyID,key_Secret);
//            RazorpayClient razorpayClient = new RazorpayClient("rzp_live_z2t2tkpQ8YERR0",
//                    "O0wqElBi2A5HUrb2MkbyeNQ4");
                        JSONObject orderRequest = new JSONObject();
                        orderRequest.put("amount", (long)AMOUNTFINAL); // amount in the smallest currency unit AmountValue*100
                        orderRequest.put("currency", "INR");
                        orderRequest.put("receipt", OrderIDValue);

                        //orderRequest.put("payment_capture", false);
                        Order OR=razorpayClient.Orders.create(orderRequest);
                        // Log.v("rzp_live_z2t2tkpQ8YERR0", orderRequest.toString());

                        //   Log.v("orderID", OR.toString());
                        try {
                            JSONObject   jsonObject1 = new JSONObject(OR.toString());
                            Log.e("LoginResponse1",  jsonObject1.toString());
                            orderIDRazorpay=jsonObject1.getString("id");

                            Log.v("iddd",orderIDRazorpay);
                            //  Log.v("res",jsonObject1.get("razorpay_signature").toString());


                            getOnlinePayment(orderIDRazorpay);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }*//*
                        getOrderId();

                    }else {
                        Toast.makeText(PaymentDetailsActivity.this, "Empty key ID or secret key", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {//Razorpay
                    e.printStackTrace();
                    Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");


            //  Toast.makeText(PaymentDetailsActivity.this, "Response" + orderIDRazorpay.toString(), Toast.LENGTH_LONG).show();
            // getOnlinePayment(orderIDRazorpay);//working code commented
        }

    }*/


    public void OffImg(View v) {
        CameraPermission cameraPermission = new CameraPermission(PaymentDetailsActivity.this, getApplicationContext());
        if (!cameraPermission.checkPermission()) {
            cameraPermission.requestPermission();
            Log.v("PERMISSION_CJEDFLHDSL", "NO");
        } else {
            showImageChooserDialog();
        }
    }

    private void showImageChooserDialog(){

        final Dialog dialog = new Dialog(PaymentDetailsActivity.this);
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
        outputFileUri = FileProvider.getUriForFile(PaymentDetailsActivity.this, getApplicationContext().getPackageName() + ".provider", new File(getExternalCacheDir().getPath(), Shared_Common_Pref.Sf_Code + "_" + System.currentTimeMillis() + ".jpeg"));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {

                if (outputFileUri != null) {
                    finalPath = "/storage/emulated/0";
                    filePath = outputFileUri.getPath();
                    filePath = filePath.substring(1);
//                    str = filePath.replaceAll("external_files/Android/data/" + BuildConfig.APPLICATION_ID + "/cache/", "");
                    filePath = finalPath + filePath.substring(filePath.indexOf("/"));
                    imgSource.setImageURI(Uri.parse(filePath));
                    getMulipart(filePath);
                }

            } else if (requestCode == SELECT_PICTURE) {
                // compare the resultCode with the
                // SELECT_PICTURE constant
                // Get the url of the image from data
                if(data!=null && data.getData()!=null){
                    Uri selectedImageUri = data.getData();
                    // update the preview image in the layout
                    imgSource.setImageURI(selectedImageUri);
                    String filePath = ImageFilePath.getPath(PaymentDetailsActivity.this, selectedImageUri);
                    getMulipart(filePath);

                }else
                    Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 10) {
            String name = myDataset.get(position).getName();
            offlineMode.setText(name);
            PaymentTypecode = myDataset.get(position).getId();
            if(name.equalsIgnoreCase("cash"))
                iv_attachment.setVisibility(View.GONE);
            else
                iv_attachment.setVisibility(View.VISIBLE);

        }
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
                    serverFileName = "Sf_Code_"+ mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code) +"_"+ System.currentTimeMillis()+ ".jpeg";
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
                    Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SEND_IMAGE_Response", "ERROR");
                Toast.makeText(PaymentDetailsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void getOnlinePayment(String orderId){


        // on below line we are getting
        // amount that is entered by user.
//        String samount = AmountValue;
//        Log.v("samout",samount);

        // rounding off the amount.

        // initialize Razorpay account.
        Checkout checkout = new Checkout();
        // set your id as below
        //  checkout.setKeyID("rzp_live_ILgsfZCZoFIKMb");
        //  checkout.setKeyID("rzp_test_JOC0wRKpLH1cVW");//demo
        //  checkout.setKeyID("rzp_live_z2t2tkpQ8YERR0");
        checkout.setKeyID(keyID);

        // set image
        checkout.setImage(R.drawable.ic_search_icon);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            Shared_Common_Pref shared_common_pref = new Shared_Common_Pref(this);

            // to put name
            object.put("name", "GOVIND MILK");
            object.put("key", keyID);
            object.put("order_id", orderId);
            object.put("theme.color", "#1ac0d6");
            // put description
            // object.put("description", "Test payment");
            object.put("description", "Live payment");
            // to set theme color
//            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");
            // put amount
            object.put("amount", (long) AMOUNTFINAL);

            // put mobile number
            // object.put("prefill.contact", "9790844143");
            object.put("prefill.contact", shared_common_pref.getvalue1(Shared_Common_Pref.USER_PHONE));
//            object.put("prefill.contact", "8608256570");
            // put email
            object.put("prefill.email", shared_common_pref.getvalue1(Shared_Common_Pref.USER_EMAIL));

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 2);
            object.put("retry", retryObj);
            // open razorpay to checkout activity
            Log.d(TAG, "getOnlinePayment: object "+ object);
            checkout.open(PaymentDetailsActivity.this, object);

        } catch (JSONException e) {
            e.printStackTrace();
            AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Checkout Error", "Unable to create checkout, please try again");
        }
    }

    //    @Override
//    public void onPaymentSuccess(String s) {
//        Log.v("pay success",s.toString());
//      //  {  "razorpay_payment_id": "pay_29QQoUBi66xm2f",  "razorpay_order_id": "order_9A33XWu170gUtm",  "razorpay_signature": "9ef4dffbfd84f1318f6739a3ce19f9d85851857ae648f114332d8401e0949a3d"}
//
//        Toast.makeText(this, "Payment is successful : "+s, Toast.LENGTH_LONG).show();
//
//
//      //  ProceedPayment(razorpay_payment_id,razorpay_response,razorpay_signature);
//
//    }
//
//    @Override
//    public void onPaymentError(int i, String s) {
//        Intent a=new Intent(PaymentDetailsActivity.this,ReportActivity.class);
//        startActivity(a);
//    }
    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try{

//                    jsonObjectRazorpay = new JSONObject(paymentData.);
//                    Log.v("LoginResponse1",  jsonObjectRazorpay .toString());
//                    JSONArray jsonArray =  jsonObjectRazorpay .optJSONArray("events");
//                    Log.v("LoginResponse771",  jsonArray .toString());
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        JSONObject razorpay_payment= jsonObject.getJSONObject("onActivityResult result");
//
//                        Log.v("LoginResponse7971", razorpay_payment .toString());
            razorpay_payment_id=paymentData.getPaymentId();
            //razorpay_payment.getString("razorpay_payment_id");
            Log.v("LoginResponse7971i", razorpay_payment_id );
            razorpay_response=paymentData.getOrderId();
            //razorpay_payment.getString("razorpay_order_id");
            Log.v("LoginResponse7971qi", razorpay_response );
            razorpay_signature=paymentData.getSignature();
            //razorpay_payment.getString("razorpay_signature");
            Log.v("LoginResponse7971wi", razorpay_signature);

            Toast.makeText(this, "Payment is successful : "+s, Toast.LENGTH_LONG).show();

            ProceedPayment(razorpay_payment_id,razorpay_response,razorpay_signature);
            //   }
        }catch (Exception eee){
            eee.printStackTrace();
        }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
//        {"error":{"code":"BAD_REQUEST_ERROR","description":"Payment processing cancelled by user",
//                "source":"customer","step":"payment_authentication","reason":"payment_cancelled"}}
        Log.d(TAG, "onPaymentError: s "+ s);
        String error = "Something went wrong, please try again later";
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("description")) {
                error  = jsonObject.getString("description");

            }else if(jsonObject.has("error")){
                JSONObject errorObject = null;

                errorObject = jsonObject.getJSONObject("error");

                if(errorObject.has("description"))
                    error  = errorObject.getString("description");
//                "code":"BAD_REQUEST_ERROR","description":"The id provided does not exist","source":"business","step":"payment_initiation","reason":"input_validation_failed","metadata":{}}

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        AlertDialogBox.showDialog(PaymentDetailsActivity.this, "Payment Error", error, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
                paymentCompleted(false);
            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });



//        Intent a=new Intent(PaymentDetailsActivity.this,ReportActivity.class);
//        startActivity(a);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void getToolbar() {
        ImageView imgBack;
        TextView toolbar_title;

        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Payment Details");

        imgBack = findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }




}
