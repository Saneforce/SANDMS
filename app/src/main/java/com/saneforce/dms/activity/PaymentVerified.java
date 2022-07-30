package com.saneforce.dms.activity;

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
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.Shared_Common_Pref;

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
    String LoginType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_verified);
        pendingRecycle = findViewById(R.id.recycler_view);
        getToolbar();

        mShared_common_pref = new Shared_Common_Pref(this);
        String LoginType = mShared_common_pref.getvalue("Login_details");
        Log.v("LoginType", LoginType);

        mCommon_class = new Common_Class(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        mCommon_class.ProgressdialogShow(1, "");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca;
        if (LoginType.equalsIgnoreCase("Logistics")) {

            //below api is omitted because new
            // ca = apiInterface.getPaymentVerifed(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code), mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
            ca = apiInterface.getPendingVer(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code),
                    mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        }else{
            ca = apiInterface.getPendingVer(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code),
                    mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        }


        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mCommon_class.ProgressdialogShow(2, "");

                JSONObject jsonObject1;
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonObject1 = new JSONObject(response.body().toString());
                    if(!jsonObject1.isNull("success") && jsonObject1.getBoolean("success") && !jsonObject1.isNull("Data") ){
                        jsonArray = jsonObject1.optJSONArray("Data");

                    }else
                        Toast.makeText(PaymentVerified.this, "No Data, please try again", Toast.LENGTH_SHORT).show();

                    pendingRecycle.setHasFixedSize(true);
                    pendingRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    pendingRecycle.setNestedScrollingEnabled(false);
                    VerifiedAdapter priProdAdapter = new VerifiedAdapter(PaymentVerified.this, jsonArray);
                    pendingRecycle.setAdapter(priProdAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentVerified.this, "something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
                Toast.makeText(PaymentVerified.this, "something went wrong, please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getToolbar() {

        Intent intent = getIntent();
        if(intent.hasExtra("title") && intent.getStringExtra("title")!=null){
            TextView toolbar_title = findViewById(R.id.toolbar_title);
            toolbar_title.setText(intent.getStringExtra("title"));
        }

        imgBack = (ImageView) findViewById(R.id.toolbar_back);

        mShared_common_pref = new Shared_Common_Pref(this);
        String LoginType = mShared_common_pref.getvalue("Login_details");
        if(intent.hasExtra("title") && intent.getStringExtra("title")!=null){
            TextView toolbar_title = findViewById(R.id.toolbar_title);
            toolbar_title.setText(intent.getStringExtra("title"));
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginType.equalsIgnoreCase("Logistics")){
//                    startActivity(new Intent(getApplicationContext(), LogisticsActivity.class));
                    onBackPressed();
                }else{
                    onBackPressed();
//                    startActivity(new Intent(getApplicationContext(), FinanceActivity.class));
//                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


class VerifiedAdapter extends RecyclerView.Adapter<VerifiedAdapter.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
//    DMS.CheckingInterface itemClick;
    Shared_Common_Pref shared_common_pref;

    String Amount = "0";
    String Stockist_Name = "";
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
            Amount = "0";
            if(jsonObject.has("Order_Value") && !jsonObject.getString("Order_Value").equals("") )
                Amount = Constant.roundTwoDecimals(jsonObject.getDouble("Order_Value"));
            holder.orderValue.setText(Amount);

            if(!jsonObject.isNull("Stockist_Name"))
                Stockist_Name = String.valueOf(jsonObject.get("Stockist_Name"));
            else
                Stockist_Name = "";

            String UTRNumber = String.valueOf(jsonObject.get("UTRNumber"));
            String Imgurl = String.valueOf(jsonObject.get("Imgurl"));
            String Payment_Option=String.valueOf(jsonObject.get("Payment_Option"));
            String Payment_Mode=String.valueOf(jsonObject.get("Payment_Mode"));

            if(!jsonObject.isNull("ERP_Code") && !jsonObject.getString("ERP_Code").equals("")){
                Stockist_Name = Stockist_Name+" - "+ jsonObject.getString("ERP_Code");
            }


            holder.orderID.setText(OrderID);
            holder.orderDate.setText(PayDt);
            holder.orderDistributor.setText(Stockist_Name);
            holder.txtPaymentOption.setText("Payment Type : "+ Payment_Option);
            if(Payment_Option.equals("Offline")){
                holder.txtPaymentMode.setVisibility(View.VISIBLE);
                holder.txtPaymentMode.setText("Payment Mode : "+ Payment_Mode);
            }else {
                holder.txtPaymentMode.setVisibility(View.GONE);
                holder.txtPaymentMode.setText("");
            }

        /*    String invoiceAmt = "0";
            if(!jsonObject.isNull("Invoice_Amount")){
                invoiceAmt = jsonObject.getString("Invoice_Amount");
            }
            holder.tv_invoice_amount.setText(invoiceAmt);*/

            String paidAmt = "0";
            if(jsonObject.has("Amount") && !jsonObject.getString("Amount").equals(""))
                paidAmt = Constant.roundTwoDecimals(jsonObject.getDouble("Amount"));

            holder.tv_paid_amount.setText(paidAmt);


            String LoginType = shared_common_pref.getvalue("Login_details");
            Log.v("LoginType", LoginType);
            if (LoginType.equalsIgnoreCase("Logistics")) {

                holder.martl_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent Details = new Intent(context, DispatchEditActivtity.class);
                        Details.putExtra("title", "DISPATCH EDIT");
                        Details.putExtra("OrderId", OrderID);
                        Details.putExtra("editMode", 1);
                        context.startActivity(Details);
                    }
                });
            }
            /*else {

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
                        MIntent.putExtra("editMode", 0);
                        context.startActivity(MIntent);
                    }
                });

            }*/

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
        TextView tv_paid_amount;
        //        tv_invoice_amount,
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
            tv_paid_amount=itemView.findViewById(R.id.tv_paid_amount);
//            tv_invoice_amount=itemView.findViewById(R.id.tv_invoice_amount);
        }
    }


}
