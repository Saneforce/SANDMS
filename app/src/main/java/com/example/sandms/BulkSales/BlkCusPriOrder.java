package com.example.sandms.BulkSales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.sandms.Adapter.OrderItem;
import com.example.sandms.BulkSales.Database.DatabaseHandler;
import com.saneforce.dms.R;

import org.json.JSONArray;
import org.json.JSONException;

public class BlkCusPriOrder extends AppCompatActivity {
    DatabaseHandler db;
    JSONArray Prods;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blk_cus_pri_order);
        getWindow().getAttributes().windowAnimations = R.style.Fade;

        mRecyclerView = findViewById(R.id.OrderList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        db = new DatabaseHandler(this);
        try {
            Prods=db.getMasterData("DMSProducts");
            OrderItem listItms= new OrderItem(Prods,this);
            mRecyclerView.setAdapter(listItms);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}