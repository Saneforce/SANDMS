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

public class PendingVerification extends AppCompatActivity {
    RecyclerView pendingRecycle;
    Shared_Common_Pref mShared_common_pref;
    Common_Class mCommon_class;
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification);
        pendingRecycle = (RecyclerView) findViewById(R.id.recycler_view);
        getToolbar();

        mShared_common_pref = new Shared_Common_Pref(this);
        mCommon_class = new Common_Class(this);

    }


    public void getToolbar() {
        Intent intent = getIntent();
        if(intent.hasExtra("title") && intent.getStringExtra("title")!=null){
            TextView toolbar_title = findViewById(R.id.toolbar_title);
            toolbar_title.setText(intent.getStringExtra("title"));
        }

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> ca = apiInterface.getPrimaryVerification(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));

        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mCommon_class.ProgressdialogShow(1, "");
                JSONObject jsonObject1;
                Shared_Common_Pref shared_common_pref;
                try {
                    jsonObject1 = new JSONObject(response.body().toString());
//                    JSONObject jsonObject = null;
                    JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                   /* for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);


                    }*/
                    Log.v("JsONDATE", jsonArray.toString());
                    pendingRecycle.setHasFixedSize(true);
                    pendingRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    pendingRecycle.setNestedScrollingEnabled(false);
                    PendingAdapter priProdAdapter = new PendingAdapter(PendingVerification.this, jsonArray);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}


class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
    DMS.CheckingInterface itemClick;


    public PendingAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public PendingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_verficiation, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.setClickable(false);
        return new PendingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingAdapter.MyViewHolder holder, int position) {

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonArray.get(position);
            String OrderID = String.valueOf(jsonObject.get("OrderID"));
            String PayDt = String.valueOf(jsonObject.get("PayDt"));
            String Amount = String.valueOf(jsonObject.get("Amount"));
            String Stockist_Name = String.valueOf(jsonObject.get("Stockist_Name"));
            String UTRNumber = String.valueOf(jsonObject.get("UTRNumber"));
            String Imgurl = String.valueOf(jsonObject.get("Imgurl"));
            Log.v("iage",Imgurl);
            String Payment_Option=String.valueOf(jsonObject.get("Payment_Option"));
            String Payment_Mode=String.valueOf(jsonObject.get("Payment_Mode"));
            holder.orderID.setText(OrderID);
            holder.orderDate.setText(PayDt);
            holder.orderValue.setText(Amount);
            holder.orderDistributor.setText(Stockist_Name);
            holder.txtPaymentOption.setText("Payment Type :"+Payment_Option);
            if(Payment_Option.equals("Offline")){
                holder.txtPaymentMode.setVisibility(View.VISIBLE);
                holder.txtPaymentMode.setText("Payment Mode :"+Payment_Mode);
            }else {
                holder.txtPaymentMode.setVisibility(View.GONE);
                holder.txtPaymentMode.setText("");
            }

            holder.martl_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent MIntent = new Intent(context, PendingVerificationDetails.class);
                    MIntent.putExtra("title", "PENDING VERIFICATION DETAILS");
                    MIntent.putExtra("OrderID", OrderID);
                    MIntent.putExtra("PayDt", PayDt);
                    MIntent.putExtra("Amount", Amount);
                    MIntent.putExtra("Stockist_Name", Stockist_Name);
                    MIntent.putExtra("UTRNumber", UTRNumber);
                    MIntent.putExtra("Imgurl", Imgurl);
                    MIntent.putExtra("paymentType", Payment_Option);
                    MIntent.putExtra("paymentMode", Payment_Mode);
                    context.startActivity(MIntent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, orderDate, orderValue, orderDistributor, orderStatus,txtPaymentOption,txtPaymentMode;;
        CardView martl_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatus = itemView.findViewById(R.id.txt_pending);
            orderDistributor = itemView.findViewById(R.id.product_distributor);
            orderValue = itemView.findViewById(R.id.product_amnt);
            orderDate = itemView.findViewById(R.id.txt_date);
            orderID = itemView.findViewById(R.id.product_order_id);
            martl_view = itemView.findViewById(R.id.card_item);
            txtPaymentOption=itemView.findViewById(R.id.txt_paymentpending);
            txtPaymentMode=itemView.findViewById(R.id.txt_paymentpending_mode);
        }
    }

}
