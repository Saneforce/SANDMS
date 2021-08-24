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
import android.widget.Toast;

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
        mShared_common_pref = new Shared_Common_Pref(this);
        mCommon_class = new Common_Class(this);

        getToolbar();

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
        mCommon_class.ProgressdialogShow(1, "");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> ca = apiInterface.getPrimaryVerification(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));

        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mCommon_class.ProgressdialogShow(2, "");

                JSONObject jsonObject1;
                try {
                    jsonObject1 = new JSONObject(response.body().toString());
                    JSONArray jsonArray = new JSONArray();
//                    JSONObject jsonObject = null;
                    if(!jsonObject1.isNull("success") && jsonObject1.getBoolean("success") && !jsonObject1.isNull("Data")){
                        jsonArray = jsonObject1.optJSONArray("Data");
                    }else
                        Toast.makeText(PendingVerification.this, "No Data, please try again", Toast.LENGTH_SHORT).show();


                    pendingRecycle.setHasFixedSize(true);
                    pendingRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    pendingRecycle.setNestedScrollingEnabled(false);
                    PendingAdapter priProdAdapter = new PendingAdapter(PendingVerification.this, jsonArray);
                    pendingRecycle.setAdapter(priProdAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PendingVerification.this, "something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
                Toast.makeText(PendingVerification.this, "something went wrong, please try again", Toast.LENGTH_SHORT).show();
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
//    DMS.CheckingInterface itemClick;
    String Stockist_Name = "";

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

            if(!jsonObject.isNull("Stockist_Name"))
                Stockist_Name = String.valueOf(jsonObject.get("Stockist_Name"));
            else
                Stockist_Name = "";

            String UTRNumber = String.valueOf(jsonObject.get("UTRNumber"));
            String Imgurl = String.valueOf(jsonObject.get("Imgurl"));
            Log.v("iage",Imgurl);
            String Payment_Option=String.valueOf(jsonObject.get("Payment_Option"));
            String Payment_Mode=String.valueOf(jsonObject.get("Payment_Mode"));
            holder.orderID.setText(OrderID);
            holder.orderDate.setText(PayDt);
            holder.orderValue.setText(Amount);

            if(!jsonObject.isNull("ERP_Code") && !jsonObject.getString("ERP_Code").equals("")){
                Stockist_Name = Stockist_Name+" - "+ jsonObject.getString("ERP_Code");
            }
            holder.orderDistributor.setText(Stockist_Name);

            holder.txtPaymentOption.setText("Payment Type : "+Payment_Option);
            if(Payment_Option.equals("Offline")){
                holder.txtPaymentMode.setVisibility(View.VISIBLE);
                holder.txtPaymentMode.setText("Payment Mode : "+Payment_Mode);
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
                    MIntent.putExtra("editMode", 1);
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
