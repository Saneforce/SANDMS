package com.saneforce.dms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.saneforce.dms.R;


public class LogisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics2);
    }
    public void Pendig(View v) {

        startActivity(new Intent(LogisticsActivity.this, PaymentVerified.class));
    }

    public void Confrimed(View v) {
        startActivity(new Intent(LogisticsActivity.this, DispatchCreditedActivity.class));
    }
}