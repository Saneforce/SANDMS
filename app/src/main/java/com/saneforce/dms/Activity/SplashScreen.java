package com.saneforce.dms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


import com.saneforce.dms.R;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.Shared_Common_Pref;
import com.saneforce.dms.Utils.TimeUtils;
import com.saneforce.dms.sqlite.DBController;


public class SplashScreen extends AppCompatActivity {
    Shared_Common_Pref shared_common_pref;
    Boolean LoginSuccess;
    String LoginType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        shared_common_pref = new Shared_Common_Pref(this);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {


                LoginType= shared_common_pref.getvalue("Login_details");

                if (LoginType.equalsIgnoreCase("Stockist") && TimeUtils.compareCurrentAndLoginDate(shared_common_pref.getvalue(Shared_Common_Pref.LOGIN_DATE)) <0 ) {
                    new DBController(SplashScreen.this).clearDatabase(DBController.TABLE_NAME);
                    shared_common_pref.logoutUser(SplashScreen.this);

                }else{
                    if (LoginType.equalsIgnoreCase("Finance")) {
                        startActivity(new Intent(getApplicationContext(), FinanceActivity.class));
                        finish();
                    } else if (LoginType.equalsIgnoreCase("Logistics")) {
                        startActivity(new Intent(getApplicationContext(), LogisticsActivity.class));
                        finish();
                    } else if (LoginType.equalsIgnoreCase("Stockist")) {
                        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }

                }

            }
        }, 2000);
    }
}