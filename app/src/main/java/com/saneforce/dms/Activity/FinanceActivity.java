package com.saneforce.dms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.saneforce.dms.R;


public class FinanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
    }

    public void PendingVerification(View v) {

        startActivity(new Intent(FinanceActivity.this, PendingVerification.class));
    }

    public void VerifiedPayment(View v) {
        startActivity(new Intent(FinanceActivity.this, PaymentVerified.class));
    }
}