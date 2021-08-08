package com.saneforce.dms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.saneforce.dms.R;
import com.saneforce.dms.Utils.Shared_Common_Pref;


public class FinanceActivity extends AppCompatActivity {

    Shared_Common_Pref shared_common_pref;
    ImageView ib_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);

        initViews();
    }


    private void initViews() {
        shared_common_pref = new Shared_Common_Pref(this);
        ib_logout = findViewById(R.id.ib_logout);
        ib_logout.setVisibility(View.VISIBLE);
        ib_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shared_common_pref.logoutUser(FinanceActivity.this);
            }
        });

    }

    public void PendingVerification(View v) {

        startActivity(new Intent(FinanceActivity.this, PendingVerification.class));
    }

    public void VerifiedPayment(View v) {
        startActivity(new Intent(FinanceActivity.this, PaymentVerified.class));
    }
}