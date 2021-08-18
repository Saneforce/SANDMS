package com.saneforce.dms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saneforce.dms.R;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.Shared_Common_Pref;

public class ReportDashBoard extends AppCompatActivity {

    Shared_Common_Pref mShared_common_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_dash_board);
        mShared_common_pref = new Shared_Common_Pref(this);
        TextView toolHeader = (TextView) findViewById(R.id.toolbar_title);
        toolHeader.setText("REPORT");
        ImageView imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                onBackPressed();
            }

        });
    }

    public void PrimaryReports(View v) {
        if(Constants.isInternetAvailable(this)){
            startActivity(new Intent(getApplicationContext(), ReportActivity.class));
            mShared_common_pref.save("OrderType", "1");
        }else
            Toast.makeText(ReportDashBoard.this, "Please check the Internet connection", Toast.LENGTH_SHORT).show();
    }

    public void SecodaryReports(View v) {
        if(Constants.isInternetAvailable(this)){

            startActivity(new Intent(getApplicationContext(), ReportActivity.class));
            mShared_common_pref.save("OrderType", "2");
        }else
            Toast.makeText(ReportDashBoard.this, "Please check the Internet connection", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onBackPressed() {
        finish();
    }


}