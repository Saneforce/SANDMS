package com.saneforce.dms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.dms.model.OutboxModel;
import com.saneforce.dms.R;
import com.saneforce.dms.sqlite.DBController;

import java.util.ArrayList;
import java.util.List;

public class OutboxActivityWorking extends AppCompatActivity {

    TextView tv_primary_count;
    TextView tv_secondary_count;
    TextView tv_new_retailer;
    TextView tv_retailer_visit;
//    TextView tv_non_productive_calls;
    DBController dbController;

    List<OutboxModel> outboxModelList = new ArrayList<>();
//    OutboxAdapter adapter;
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
        tv_retailer_visit = findViewById(R.id.tv_retailer_visit);
//        tv_non_productive_calls = findViewById(R.id.tv_non_productive_calls);

        dbController = new DBController(this);
        getToolbar();
        updateRecyclerView();

    }

    private void updateRecyclerView() {
        getOutboxData();
         //see sample project's GenreDataFactory.java class for getGenres() method
        RecyclerView recyclerView =  findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //instantiate your adapter with the list of genres
//        adapter = new OutboxAdapter(outboxModelList);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);

    }

    private void getOutboxData() {

    }

    /*Toolbar*/
    public void getToolbar() {
        ImageView imagView = findViewById(R.id.toolbar_back);
        ImageView ivRefresh = findViewById(R.id.ib_logout);
        ivRefresh.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_refresh_24));
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("OUTBOX");

        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

    }

    private void loadData() {
        update(tv_primary_count, "dcr/save", 1);
        update(tv_secondary_count, "dcr/secordersave", 1);
        update(tv_new_retailer, "dcr/save", 0);
        update(tv_retailer_visit, "dcr/retailervisit", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
//        update(tv_non_productive_calls, "dcr/retailervisit", 1);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        adapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        adapter.onRestoreInstanceState(savedInstanceState);
    }
}