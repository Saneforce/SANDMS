package com.example.sandms.BulkSales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.sandms.Adapter.DMSListItem;
import com.example.sandms.BulkSales.Database.DatabaseHandler;
import com.example.sandms.Interface.onDMSListItemClick;
import com.example.sandms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlkSalCusOrder extends AppCompatActivity {
    DatabaseHandler db;
    JSONArray Custs;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blk_sal_cus_order);
        getWindow().getAttributes().windowAnimations = R.style.Fade;

        mRecyclerView = findViewById(R.id.CustList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        db = new DatabaseHandler(this);
        try {
            Custs=db.getMasterData("DMSCustomers");
            DMSListItem listItms= new DMSListItem(Custs,this);
            mRecyclerView.setAdapter(listItms);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DMSListItem.SetListItemClickListener(new onDMSListItemClick() {
            @Override
            public void onClick(JSONObject item) {

                String CusNm= "",CusMob="";
                try {
                    CusNm = item.getString("Name");
                    CusMob = item.getString("Mobile");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(BlkSalCusOrder.this, OrderIntend.class);
                intent.putExtra("CustName", CusNm);
                intent.putExtra("CustMob", CusMob);
                startActivity(intent);
            }
        });
    }
}