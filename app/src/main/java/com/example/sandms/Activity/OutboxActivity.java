package com.example.sandms.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sandms.R;
import com.example.sandms.sqlite.DBController;

public class OutboxActivity extends AppCompatActivity {

    TextView tv_primary_count;
    TextView tv_secondary_count;
    TextView tv_new_retailer;
    DBController dbController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbox);
        initViews();
    }

    private void initViews() {
        tv_primary_count = findViewById(R.id.tv_primary_count);
                tv_secondary_count = findViewById(R.id.tv_secondary_count);
        tv_new_retailer = findViewById(R.id.tv_new_retailer);
        getToolbar();
        dbController = new DBController(this);

    }

    /*Toolbar*/
    public void getToolbar() {
        ImageView imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        update(tv_primary_count, "dcr/save", 1);
        update(tv_secondary_count, "dcr/secordersave", 1);
        update(tv_new_retailer, "dcr/save", 0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void update(TextView tvCount, String axn, int isOrder) {

        String count = "0";

        try {
            count = dbController.getOfflineCount(axn, isOrder);
        }catch (Exception e){
            e.printStackTrace();
        }

        tvCount.setText(count);

    }
}