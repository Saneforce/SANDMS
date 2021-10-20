package com.saneforce.dms.billdesk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.billdesk.sdk.PaymentOptions;
import com.saneforce.dms.R;

public class SamplePayNowActivity extends AppCompatActivity {
//    String strPGMsg = "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|892409133";
    String strPGMsg = "AIRMTST|ARP1523968042763|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|3277831407";
    String strTokenMsg = null;// "AIRMTST|ARP1553593909862|NA|2|NA|NA|NA|INR|NA|R|airmtst|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|https://uat.billdesk.com/pgidsk/pgmerc/pg_dump.jsp|723938585|CP1005!AIRMTST!D1DDC94112A3B939A4CFC76B5490DC1927197ABBC66E5BC3D59B12B552EB5E7DF56B964D2284EBC15A11643062FD6F63!NA!NA!NA";

    Button btnPayNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_pay_now);

        btnPayNow = (Button) findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payNowCalled();
            }
        });
    }

    private void payNowCalled() {
        // call BillDesk SDK

        SampleCallBack objSampleCallBack = new SampleCallBack();

        Intent sdkIntent = new Intent(this, PaymentOptions.class);
        sdkIntent.putExtra("msg",strPGMsg);
        if(strTokenMsg != null && strTokenMsg.length() > strPGMsg.length()) {

            sdkIntent.putExtra("token",strTokenMsg);
        }
        sdkIntent.putExtra("user-email","test@bd.com");
        sdkIntent.putExtra("user-mobile","9800000000");
        sdkIntent.putExtra("callback", objSampleCallBack);

        startActivity(sdkIntent);
    }
}
