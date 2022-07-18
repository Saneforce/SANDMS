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
import com.saneforce.dms.utils.Shared_Common_Pref;


public class LogisticsActivity extends AppCompatActivity {

    Shared_Common_Pref shared_common_pref;
    ImageView ib_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics2);

        initViews();
    }

    private void initViews() {
        shared_common_pref = new Shared_Common_Pref(this);
        ib_logout = findViewById(R.id.ib_logout);
        ib_logout.setVisibility(View.VISIBLE);
        TextView toolbar_title = findViewById(R.id.toolbar_title);;
        toolbar_title.setText("LOGISTICS");
        ImageView toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ib_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(LogisticsActivity.this);
                alert.setTitle("Confirmation");
                alert.setCancelable(false);
                alert.setMessage("Do you want to Logout?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shared_common_pref.logoutUser(LogisticsActivity.this);
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

    public void Pendig(View v) {
        Intent intent =new Intent(LogisticsActivity.this, PaymentVerified.class);
        intent.putExtra("title", "PENDING DISPATCHES");
        startActivity(intent);
    }

    public void Confrimed(View v) {
        Intent intent =new Intent(LogisticsActivity.this, DispatchCreditedActivity.class);
        intent.putExtra("title", "CONFIRMED DISPATCHES");
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}