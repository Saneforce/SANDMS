package com.saneforce.dms.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.Interface.DMS;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.CameraPermission;
import com.saneforce.dms.Utils.Common_Model;
import com.saneforce.dms.Utils.CustomListViewDialog;
import com.saneforce.dms.Utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailsActivity extends AppCompatActivity
        implements DMS.Master_Interface,  PaymentResultWithDataListener {//PaymentResultListener,
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
    String keyID="";
    String key_Secret="";
    String razorpay_signature="";
    Shared_Common_Pref mShared_common_pref;
    private static final int CAMERA_REQUEST = 1888;
    String str = "", finalPath = "", filePath = "", OrderIDValue = "", DateValue = "",
            AmountValue = "", PaymntMode = "", PaymentTypecode = "";
    List<Common_Model> modelOffileData = new ArrayList<>();
    Common_Model mCommon_model_spinner;
    int Amount;
   Double AMOUNTFINAL ;
    SoapPrimitive resultString;
    JSONObject jsonObjectRazorpay;
    String TAG = "Response";
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
        getOfflineMode();
        OrderIDValue = String.valueOf(getIntent().getSerializableExtra("OrderId"));
        DateValue = String.valueOf(getIntent().getSerializableExtra("Date"));
        AmountValue = String.valueOf(getIntent().getSerializableExtra("Amount"));

        productId.setText(OrderIDValue);
        productDate.setText(DateValue);
        productAmt.setText(AmountValue);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.online) {
                    offView.setVisibility(View.GONE);
                    PaymntMode = "Online";

                } else if (checkedId == R.id.offline) {
                    offView.setVisibility(View.VISIBLE);
                    PaymntMode = "Offline";
                } else {
                    PaymntMode = "Credit";
                    offView.setVisibility(View.GONE);
                }
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
        if (PaymntMode.equalsIgnoreCase("Offline")) {
            if (offlineMode.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(this, "Please choose payment type", Toast.LENGTH_SHORT).show();
            } else {


             ProceedPayment("","","");
            }

        } else {

            if (PaymntMode.equalsIgnoreCase("Online")) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            } else if(PaymntMode.equalsIgnoreCase("Credit")) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            }else{
                onBackPressed();
//                Intent a=new Intent(PaymentDetailsActivity.this,ReportActivity.class);
//                startActivity(a);
            }

        }

    }


    public void getOfflineMode() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getOfflineMode("get/paymentmode", mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
        Log.v("KArthic_Retailer", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response.body().toString());
                    Log.v("KArthic_Retailer", jsonRootObject.toString());
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


    public void ProceedPayment(String razorid,String responseid,String signature) {
        JSONObject js = new JSONObject();
        try {
            js.put("OrderID", OrderIDValue);
            js.put("StockistCode", mShared_common_pref.getvalue(Shared_Common_Pref.Stockist_Code));
            js.put("divisionCode", mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
            js.put("PaymentMode", PaymntMode);
            js.put("PaymentTypeName", offlineMode.getText().toString());
            js.put("PaymentTypeCode", PaymentTypecode);
            js.put("UTRNumber", edtUTR.getText().toString());
            js.put("Amount", AmountValue);
            js.put("Attachement", str);
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
            responseBodyCall = apiInterface.getDetails("save/primarypayment", js.toString());
            Log.v("Payment_Request", responseBodyCall.request().toString());
            responseBodyCall.enqueue(new Callback<JsonObject>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject jsonObject = response.body();
                    Log.v("Payment_Response", jsonObject.toString());
                    if (jsonObject.get("success").toString().equalsIgnoreCase("true")) ;
                //    Toast.makeText(getApplicationContext(), "sign"+signature, Toast.LENGTH_LONG).show();
               //     Toast.makeText(getApplicationContext(), "razorid"+responseid, Toast.LENGTH_LONG).show();
               //     Toast.makeText(getApplicationContext(), "razpay"+razorid, Toast.LENGTH_LONG).show();
                    onBackPressed();
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
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            getOnlinePaymentOrderId();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");

          //  Toast.makeText(PaymentDetailsActivity.this, "Response" + orderIDRazorpay.toString(), Toast.LENGTH_LONG).show();
           // getOnlinePayment(orderIDRazorpay);//working code commented
        }

    }


    public void OffImg(View v) {
        CameraPermission cameraPermission = new CameraPermission(PaymentDetailsActivity.this, getApplicationContext());
        if (!cameraPermission.checkPermission()) {
            cameraPermission.requestPermission();
            Log.v("PERMISSION_CJEDFLHDSL", "NO");
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            outputFileUri = FileProvider.getUriForFile(PaymentDetailsActivity.this, getApplicationContext().getPackageName() + ".provider", new File(getExternalCacheDir().getPath(), Shared_Common_Pref.Sf_Code + "_" + System.currentTimeMillis() + ".jpeg"));
            Log.v("FILE_PATH", String.valueOf(outputFileUri));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_REQUEST);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                finalPath = "/storage/emulated/0";
                filePath = outputFileUri.getPath();
                filePath = filePath.substring(1);
                str = filePath.replaceAll("external_files/Android/data/com.example.dms/cache/", " ");
                filePath = finalPath + filePath.substring(filePath.indexOf("/"));
                imgSource.setImageURI(Uri.parse(filePath));
                getMulipart(filePath);
            }
        }
    }

    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 10) {
            offlineMode.setText(myDataset.get(position).getName());
            PaymentTypecode = myDataset.get(position).getId();
        }
    }


    public void getMulipart(String path) {
        Log.v("PATH_IMAGE", path);
        MultipartBody.Part imgg = convertimg("file", path);
        Log.v("PATH_IMAGE_imgg", String.valueOf(imgg));
        sendImageToServer(imgg);
    }

    public MultipartBody.Part convertimg(String tag, String path) {
        MultipartBody.Part yy = null;
        try {
            if (!TextUtils.isEmpty(path)) {

                File file = new File(path);
                if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg"))
                    file = new Compressor(getApplicationContext()).compressToFile(new File(path));
                else
                    file = new File(path);
                RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
                yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);
            }
        } catch (Exception e) {
        }
        return yy;
    }

    private void sendImageToServer(MultipartBody.Part imgg) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> mCall = apiInterface.offlineImage("upload/paymentimg", imgg);
        Log.e("SEND_IMAGE_SERVER", mCall.request().toString());
        mCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                Log.v("SEND_IMAGE_RESPPONSE", jsonObject.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SEND_IMAGE_Response", "ERROR");
            }
        });
    }



    public void getOnlinePaymentOrderId() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getPaymentKey( mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code),mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
        Log.v("jeevith", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonRootObject = new JSONObject(response.body().toString());
                    if (jsonRootObject.get("success").toString().equalsIgnoreCase("true")) ;

                    Log.v("NAME_STRING", response.toString());
                    JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                    for (int a = 0; a < jsonArray.length(); a++) {

                        JSONObject jso = jsonArray.getJSONObject(a);
                       keyID = String.valueOf(jso.get("KeyID"));
                       key_Secret = String.valueOf(jso.get("Key_Secret"));
                        Log.v("NAME_STRING1",key_Secret);
                        Log.v("NAME_STRING",keyID);
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

        AMOUNTFINAL= Double.valueOf(100 * Double.parseDouble(AmountValue));


        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyID,key_Secret
                  );
//            RazorpayClient razorpayClient = new RazorpayClient("rzp_live_z2t2tkpQ8YERR0",
//                    "O0wqElBi2A5HUrb2MkbyeNQ4");
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", AMOUNTFINAL); // amount in the smallest currency unit AmountValue*100
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

            }

        } catch (RazorpayException xception ) {
            System.out.println(xception .getMessage());
        } catch (Exception e) {//Razorpay


             System.out.println(e.getMessage());
        }
        //order id createion jul 22

    }

    public void getOnlinePayment(String orderId){


        // on below line we are getting
        // amount that is entered by user.
        String samount = AmountValue;
        Log.v("samout",samount);

        // rounding off the amount.
        int amount = Math.round(Float.parseFloat(samount) * 100);

        // initialize Razorpay account.
        Checkout checkout = new Checkout();
        // set your id as below
        //  checkout.setKeyID("rzp_live_ILgsfZCZoFIKMb");
      //  checkout.setKeyID("rzp_test_JOC0wRKpLH1cVW");//demo
      //  checkout.setKeyID("rzp_live_z2t2tkpQ8YERR0");
        checkout.setKeyID("rzp_live_z2t2tkpQ8YERR0");

        // set image
        checkout.setImage(R.drawable.ic_search_icon);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", "Govind Milk");
            object.put("order_id", orderId);
            // put description
           // object.put("description", "Test payment");
            object.put("description", "Live payment");
            // to set theme color
            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", amount);

            // put mobile number
           // object.put("prefill.contact", "9790844143");
            object.put("prefill.contact", "8939747663");
            // put email
            object.put("prefill.email", "apps@gmail.com");

            // open razorpay to checkout activity
            checkout.open(PaymentDetailsActivity.this, object);





        } catch (JSONException e) {
            e.printStackTrace();
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

                }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        onBackPressed();
//        Intent a=new Intent(PaymentDetailsActivity.this,ReportActivity.class);
//        startActivity(a);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void getToolbar() {
        ImageView imgBack;

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

    }
}