package com.example.sandms.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sandms.Interface.DMS;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;

import static com.example.sandms.Utils.ApiClient.BASE_URL;
import static com.example.sandms.Utils.ApiClient.BASE_URLS;

public class CompanyProfile extends AppCompatActivity {

    EditText toolSearch;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_company_profile);
        getToolbar();


        WebView mywebview = (WebView) findViewById(R.id.webview);
        mywebview.loadUrl(BASE_URLS+"privacypolicy.html");


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
        toolHeader.setText("Organisation Info");
        toolSearch = (EditText) findViewById(R.id.toolbar_search);
        toolSearch.setVisibility(View.GONE);

    }
}
