package com.saneforce.dms.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.saneforce.dms.R;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.Shared_Common_Pref;


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
        TextView toolbar_title = findViewById(R.id.toolbar_title);;
        toolbar_title.setText("FINANCE");

        ImageView toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ib_logout.setVisibility(View.VISIBLE);
        ib_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(FinanceActivity.this);
                alert.setTitle("Confirmation");
                alert.setCancelable(false);
                alert.setMessage("Do you want to Logout?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shared_common_pref.logoutUser(FinanceActivity.this);
                       // onBackPressed();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    public void PendingVerification(View v) {


        Intent intent =new Intent(FinanceActivity.this, PendingVerification.class);
        intent.putExtra("title", "PENDING VERIFICATIONS");
        startActivity(intent);
    }

    public void VerifiedPayment(View v) {
        Intent intent =new Intent(FinanceActivity.this, PaymentVerified.class);
        intent.putExtra("title", "VERIFIED PAYMENT");
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClickReport(View view) {
        Intent intent =new Intent(FinanceActivity.this, ReportActivity.class);
        shared_common_pref.save("OrderType", "1");
        intent.putExtra("title", "VERIFIED PAYMENT");
        intent.putExtra("viewType", 2);
        startActivity(intent);

    }
}