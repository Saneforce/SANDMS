package com.example.sandms.Activity;

import android.content.Intent;
import android.net.Uri;
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

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.CameraPermission;
import com.example.sandms.Utils.Common_Model;
import com.example.sandms.Utils.CustomListViewDialog;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailsActivity extends AppCompatActivity implements DMS.Master_Interface, PaymentResultListener {
    TextView productId, productDate, productAmt, offlineMode;
    private RadioGroup radioGroup;
    LinearLayout offView;
    Uri outputFileUri;
    CustomListViewDialog customDialog;
    ImageView imgSource;
    EditText edtUTR;
    Shared_Common_Pref mShared_common_pref;
    private static final int CAMERA_REQUEST = 1888;
    String str = "", finalPath = "", filePath = "", OrderIDValue = "", DateValue = "", AmountValue = "", PaymntMode = "", PaymentTypecode = "";
    List<Common_Model> modelOffileData = new ArrayList<>();
    Common_Model mCommon_model_spinner;

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
                ProceedPayment();
            }

        } else {
            ProceedPayment();
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

                    JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject jso = jsonArray.getJSONObject(a);
                        String className = String.valueOf(jso.get("Name"));
                        String id = String.valueOf(jso.get("Code"));
                        mCommon_model_spinner = new Common_Model(id, className, "flag");
                        modelOffileData.add(mCommon_model_spinner);

                        Log.v("NAME_STRING", className);
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


    public void ProceedPayment() {
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
            Log.v("JS_VALUE", js.toString());
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
                    //  startActivity(new Intent(PaymentDetailsActivity.this,RazorPayment.class));
                    if (PaymntMode.equalsIgnoreCase("Online")) {
                        getOnlinePayment();
                    } else {
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
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


    public void getOnlinePayment() {
        // on below line we are getting
        // amount that is entered by user.
        String samount = AmountValue;

        // rounding off the amount.
        int amount = Math.round(Float.parseFloat(samount) * 100);

        // initialize Razorpay account.
        Checkout checkout = new Checkout();

        // set your id as below
        //  checkout.setKeyID("rzp_live_ILgsfZCZoFIKMb");
        checkout.setKeyID("rzp_test_JOC0wRKpLH1cVW");

        // set image
        checkout.setImage(R.drawable.ic_search_icon);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", "Govind Milk");

            // put description
            object.put("description", "Test payment");

            // to set theme color
            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", amount);

            // put mobile number
            object.put("prefill.contact", "9790844143");

            // put email
            object.put("prefill.email", "apps@gmail.com");

            // open razorpay to checkout activity
            checkout.open(PaymentDetailsActivity.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {

    }
}
