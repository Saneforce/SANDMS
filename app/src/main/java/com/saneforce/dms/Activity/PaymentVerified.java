package com.saneforce.dms.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.JsonObject;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.Interface.DMS;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.Common_Class;
import com.saneforce.dms.Utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentVerified extends AppCompatActivity {
    RecyclerView pendingRecycle;
    Shared_Common_Pref mShared_common_pref;
    Common_Class mCommon_class;
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_verified);
        pendingRecycle = (RecyclerView) findViewById(R.id.recycler_view);
        getToolbar();

        mShared_common_pref = new Shared_Common_Pref(this);
        String LoginType = mShared_common_pref.getvalue("Login_details");
        Log.v("LoginType", LoginType);

        mCommon_class = new Common_Class(this);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca;
        if (LoginType.equalsIgnoreCase("Logistics")) {
            //below api is omitted because new
       // ca = apiInterface.getPaymentVerifed(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
            ca = apiInterface.getPendingVer(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        }else{
            ca = apiInterface.getPendingVer(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code),
                    mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        }


        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.v("Product_Requestresponse", response.toString());


                mCommon_class.ProgressdialogShow(1, "");
                JSONObject jsonObject1;
                Shared_Common_Pref shared_common_pref;
                try {
                    jsonObject1 = new JSONObject(response.body().toString());
                    JSONObject jsonObject = null;
                    JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);


                    }
                    Log.v("JsONDATE", jsonArray.toString());
                    pendingRecycle.setHasFixedSize(true);
                    pendingRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    pendingRecycle.setNestedScrollingEnabled(false);
                    VerifiedAdapter priProdAdapter = new VerifiedAdapter(PaymentVerified.this, jsonArray);
                    pendingRecycle.setAdapter(priProdAdapter);
                    mCommon_class.ProgressdialogShow(2, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
            }
        });
    }
    public void getToolbar() {

        imgBack = (ImageView) findViewById(R.id.toolbar_back);

        mShared_common_pref = new Shared_Common_Pref(this);
        String LoginType = mShared_common_pref.getvalue("Login_details");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(  LoginType.equalsIgnoreCase("Logistics")){
                    startActivity(new Intent(getApplicationContext(), LogisticsActivity.class));
                    finish();
                }else{

                    startActivity(new Intent(getApplicationContext(), FinanceActivity.class));
                    finish();
                }
            }
        });

    }
}


class VerifiedAdapter extends RecyclerView.Adapter<VerifiedAdapter.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
    DMS.CheckingInterface itemClick;
    Shared_Common_Pref shared_common_pref;


    public VerifiedAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
        shared_common_pref = new Shared_Common_Pref(context);
    }

    @NonNull
    @Override
    public VerifiedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_verified, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.setClickable(false);
        return new VerifiedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifiedAdapter.MyViewHolder holder, int position) {

        JSONObject jsonObject = null;

        Log.v("JSJJSJSJJSJS",jsonArray.toString());
        try {
            jsonObject = (JSONObject) jsonArray.get(position);
            String OrderID = String.valueOf(jsonObject.get("OrderID"));
            String PayDt = String.valueOf(jsonObject.get("PayDt"));
            String Amount = String.valueOf(jsonObject.get("Amount"));
            String Stockist_Name = String.valueOf(jsonObject.get("Stockist_Name"));
            String UTRNumber = String.valueOf(jsonObject.get("UTRNumber"));
            String Imgurl = String.valueOf(jsonObject.get("Imgurl"));
            String Payment_Option=String.valueOf(jsonObject.get("Payment_Option"));
            String Payment_Mode=String.valueOf(jsonObject.get("Payment_Mode"));

            holder.orderID.setText(OrderID);
            holder.orderDate.setText(PayDt);
            holder.orderValue.setText(Amount);
            holder.orderDistributor.setText(Stockist_Name);
            holder.txtPaymentOption.setText("Payment Type :"+ Payment_Option);
            if(Payment_Option.equals("Offline")){
                holder.txtPaymentMode.setVisibility(View.VISIBLE);
                holder.txtPaymentMode.setText("Payment Mode :"+ Payment_Mode);
            }

            String LoginType = shared_common_pref.getvalue("Login_details");
            Log.v("LoginType", LoginType);
            if (LoginType.equalsIgnoreCase("Logistics")) {

                holder.martl_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent Details = new Intent(context, DispatchEditActivtity.class);
                        Details.putExtra("OrderId", OrderID);
                        context.startActivity(Details);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, orderDate, orderValue, orderDistributor, orderStatus,txtPaymentOption,txtPaymentMode;
        CardView martl_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatus = itemView.findViewById(R.id.txt_pending);
            orderDistributor = itemView.findViewById(R.id.product_distributor);
            orderValue = itemView.findViewById(R.id.product_amnt);
            orderDate = itemView.findViewById(R.id.txt_date);
            orderID = itemView.findViewById(R.id.product_order_id);
            martl_view = itemView.findViewById(R.id.card_item);
            txtPaymentOption=itemView.findViewById(R.id.txt_paymenttaken);
            txtPaymentMode=itemView.findViewById(R.id.txt_paymentmode);
        }
    }




}
