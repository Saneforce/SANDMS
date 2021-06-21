package com.example.sandms.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingVerificationDetails extends AppCompatActivity {
    String OrderID = "", PayDt = "", Amount = "", Stockist_Name = "", UTRNumber = "", Imgurl = "";
    TextView txtOrderID, txtPayDt, txtAmount, txtStockist_Name, txtUTRNumber, txtImgurl;
    ImageView imgView;
    Shared_Common_Pref mShared_common_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification_details);

        OrderID = String.valueOf(getIntent().getSerializableExtra("OrderID"));
        PayDt = String.valueOf(getIntent().getSerializableExtra("PayDt"));
        Amount = String.valueOf(getIntent().getSerializableExtra("Amount"));
        Stockist_Name = String.valueOf(getIntent().getSerializableExtra("Stockist_Name"));
        UTRNumber = String.valueOf(getIntent().getSerializableExtra("UTRNumber"));
        Imgurl = String.valueOf(getIntent().getSerializableExtra("Imgurl"));
        mShared_common_pref = new Shared_Common_Pref(this);

        txtOrderID = findViewById(R.id.product_order_id);
        txtAmount = findViewById(R.id.product_amnt);
        txtStockist_Name = findViewById(R.id.product_distributor);
        txtUTRNumber = findViewById(R.id.product_number);
        imgView = findViewById(R.id.bank_receipt);

        txtOrderID.setText(OrderID);
        txtAmount.setText(Amount);
        txtStockist_Name.setText(Stockist_Name);
        txtUTRNumber.setText(UTRNumber);
        imgView.setImageURI(Uri.parse(Imgurl));

    }

    public void Cancel(View v) {
        startActivity(new Intent(PendingVerificationDetails.this,PendingVerification.class));
        finish();
    }

    public void Verify(View v) {
        verify();
    }


    public void verify() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.getPayVerification(OrderID, mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));

        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
              startActivity(new Intent(PendingVerificationDetails.this,PendingVerification.class));
                finish();
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }
}