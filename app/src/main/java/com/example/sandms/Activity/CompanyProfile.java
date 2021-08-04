package com.example.sandms.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sandms.Interface.DMS;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;

import static com.example.sandms.Utils.ApiClient.BASE_WEBVIEW;

public class CompanyProfile extends AppCompatActivity {

    EditText toolSearch;
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

                AlertDialogBox.showDialog(CompanyProfile.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {
                       CompanyProfile.super.onBackPressed();
                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {
                    }
                });
            }
        });
        toolHeader = (TextView) findViewById(R.id.toolbar_title);
        if(fileName.contains("rivacy"))
            toolHeader.setText("Privacy Policy");
        else
            toolHeader.setText("Organisation Info");
        toolSearch = (EditText) findViewById(R.id.toolbar_search);
        toolSearch.setVisibility(View.GONE);

    }
}
