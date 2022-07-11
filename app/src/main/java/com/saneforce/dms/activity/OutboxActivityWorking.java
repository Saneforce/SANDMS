package com.saneforce.dms.activity;

import static com.saneforce.dms.sqlite.DBController.DATA_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.dms.R;
import com.saneforce.dms.adapter.OutboxAdapter;
import com.saneforce.dms.model.OutboxModel;
import com.saneforce.dms.sqlite.DBController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OutboxActivityWorking extends AppCompatActivity {

    DBController dbController;

    List<OutboxModel.OutboxItem> outboxModelList = new ArrayList<>();
    //    OutboxAdapter adapter;
    int type;

    OutboxAdapter submittedCallAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbox_list);
        initViews();
    }

    private void initViews() {

        dbController = new DBController(this);
        Intent intent = getIntent();

        if(intent.hasExtra("type"))
            type = intent.getIntExtra("type", 2);

        getToolbar();
        updateRecyclerView();
        getOutBoxOfflineData(type);
    }

    private void updateRecyclerView() {

        //see sample project's GenreDataFactory.java class for getGenres() method
        RecyclerView recyclerView =  findViewById(R.id.rv_outbox_list);

        submittedCallAdapter = new OutboxAdapter(outboxModelList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(submittedCallAdapter);

    }


    private void getOutBoxOfflineData(int type) {
        DBController dbController = new DBController(this);
        outboxModelList.clear();
        String axn = "";
        if(type == 1)
            axn = "dcr/save";
        else
            axn = "dcr/secordersave";


        if (dbController.getOfflineData(axn, 1).size() > 0) {
            for (HashMap<String, String> i : dbController.getOfflineData(axn, 1)) {
                try {
                    JSONArray jsonArray = new JSONArray(i.get(DBController.DATA_RESPONSE));
                    if(jsonArray.length()>=2){
                        String orderValue = "";
                        StringBuilder productName = new StringBuilder();
                        JSONObject jsonObjectOrderValue = null;
                        try {
                            if(type == 1)
                                jsonObjectOrderValue = jsonArray.getJSONObject(7);
                            else
                                jsonObjectOrderValue = jsonArray.getJSONObject(1);

                            JSONArray jsonObject1 = jsonObjectOrderValue.getJSONArray("Json_Head");
                            JSONObject jsonObject2 = jsonObject1.getJSONObject(0);
                            orderValue = jsonObject2.getString("order_value");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject jsonObjectProductName = null;
                        try {
                            if(type == 1)
                                jsonObjectProductName = jsonArray.getJSONObject(2);
                            else
                                jsonObjectProductName = jsonArray.getJSONObject(0);

                            JSONArray jsonArray1 = jsonObjectProductName.getJSONArray("Activity_Stk_POB_Report");
                            for(int r = 0; r< jsonArray1.length(); r++){

                                try {
                                    JSONObject jsonObject = jsonArray1.getJSONObject(r);
                                    if(jsonObject.has("Productname"))
                                        productName.append(jsonObject.getString("Productname"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        OutboxModel.OutboxItem submittedCallModel = new OutboxModel.OutboxItem();
                        submittedCallModel.setOrderValue(orderValue);
                        submittedCallModel.setName(productName.toString());
                        outboxModelList.add(submittedCallModel);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            submittedCallAdapter.notifyDataSetChanged();

        }


    }


    /*Toolbar*/
    public void getToolbar() {
        ImageView imagView = findViewById(R.id.toolbar_back);
        ImageView ivRefresh = findViewById(R.id.ib_logout);
        ivRefresh.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_refresh_24));
        TextView toolbar_title = findViewById(R.id.toolbar_title);

        if(type == 1)
            toolbar_title.setText("PRIMARY OUTBOX");
        else
            toolbar_title.setText("SECONDARY OUTBOX");

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

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}