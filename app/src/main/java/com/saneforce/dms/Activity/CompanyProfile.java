package com.saneforce.dms.Activity;

import static com.saneforce.dms.Utils.ApiClient.BASE_WEBVIEW;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.saneforce.dms.Interface.DMS;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.AlertDialogBox;

public class CompanyProfile extends AppCompatActivity {

    String fileName = "";
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_company_profile);


        fileName = "CompanyProfile.html";
        if(getIntent().hasExtra("fileName"))
            fileName = getIntent().getStringExtra("fileName");

        WebView mywebview = (WebView) findViewById(R.id.webview);
        mywebview.loadUrl(BASE_WEBVIEW +fileName);

        getToolbar();
        }


    public void getToolbar() {
        TextView toolHeader;
       ImageView imgBack;

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showExitDialog();
            }
        });
        toolHeader = (TextView) findViewById(R.id.toolbar_title);
        if(fileName.contains("rivacy"))
            toolHeader.setText("PRIVACY POLICY");
        else
            toolHeader.setText("COMPANY PROFILE");
//        toolSearch = (EditText) findViewById(R.id.toolbar_search);
//        toolSearch.setVisibility(View.GONE);

    }

    private void showExitDialog() {
        AlertDialogBox.showDialog(CompanyProfile.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }
}
