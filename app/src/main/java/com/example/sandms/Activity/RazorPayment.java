package com.example.sandms.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sandms.R;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RazorPayment extends AppCompatActivity implements PaymentResultListener {
    private EditText amountEdt;
    private Button payBtn;
    Shared_Common_Pref shared_common_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_payment);
        // initializing all our variables.
        amountEdt = findViewById(R.id.idEdtAmount);
        payBtn = findViewById(R.id.idBtnPay);

        shared_common_pref = new Shared_Common_Pref(this);
        String stre = shared_common_pref.getvalue("task");
        Log.v("STORE_DATA_DETAILS", stre);

        // adding on click listener to our button.
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // amount that is entered by user.
                String samount = amountEdt.getText().toString();

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
                    checkout.open(RazorPayment.this, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        // this method is called on payment success.
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        // on payment failed.
        // Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }
}