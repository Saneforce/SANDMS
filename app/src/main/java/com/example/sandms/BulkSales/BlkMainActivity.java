package com.example.sandms.BulkSales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sandms.Activity.LoginActivity;
import com.example.sandms.BulkSales.Database.DatabaseHandler;
import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.onDMSListItemClick;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlkMainActivity extends AppCompatActivity {
    private static String Tag = "HAPDMS_Log";
    SharedPreferences UserDetails;
    public static final String UserInfo = "UserInfo";
    DatabaseHandler db;

    LinearLayout btnTkOrder,btnPriOrder;
    TextView txDistName,txDistId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blk_main);
        getWindow().getAttributes().windowAnimations = R.style.Fade;
        UserDetails = getSharedPreferences(UserInfo, Context.MODE_PRIVATE);

        txDistName=findViewById(R.id.txDistName);
        txDistId=findViewById(R.id.txDistId);
        txDistName.setText(UserDetails.getString("StkName",""));
        txDistId.setText(UserDetails.getString("Sf_UserName",""));
        /*
                                .putString("Cut_Off_Time", Cut_Off_Time)
                                .putString("StkName", Sf_Name)
                                .putString("Sf_UserName", SfUsrNme)
                                .putString("Stockist_Code", StckLstCde)*/
        db = new DatabaseHandler(this);

        loadCustomer();
        loadProducts();

        btnTkOrder=findViewById(R.id.btnTkOrder);
        btnTkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BlkMainActivity.this, BlkSalCusOrder.class);
                startActivity(intent);
            }
        });

        btnPriOrder=findViewById(R.id.btnPriOrder);
        btnPriOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BlkMainActivity.this, BlkCusPriOrder.class);
                startActivity(intent);
            }
        });
    }
    private void loadCustomer(){
        try {
            JSONArray data = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("StkCode", UserDetails.getString("StkCode", ""));
            item.put("DivCode", UserDetails.getString("Div_Code", ""));
            data.put(item);

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonArray> ca = apiInterface.getDetailsArray("get/customers", data.toString());
            ca.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    JsonArray jData = response.body();
                    db.addMasterData("DMSCustomers",response.body());
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.d(Tag,"Error: "+t.getLocalizedMessage());
                }
            });
        }
        catch (Exception e){

        }
    }
    private void loadProducts(){
        try {
            JSONArray data = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("StkCode", UserDetails.getString("StkCode", ""));
            item.put("DivCode", UserDetails.getString("Div_Code", ""));
            data.put(item);

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonArray> apiCallProd = apiInterface.getDetailsArray("get/products", data.toString());
            apiCallProd.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    JsonArray jData = response.body();
                    db.addMasterData("DMSProducts",response.body());
                }
                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.d(Tag,"Error Products : "+t.getLocalizedMessage());
                }
            });
        }
        catch (Exception e){

        }
    }

}