package com.saneforce.dms.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.Interface.DMS;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.Common_Class;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatchEditActivtity extends AppCompatActivity {
    String OrderId;
    RecyclerView pendingRecycle;
    Shared_Common_Pref mShared_common_pref;
    Common_Class mCommon_class;
    ArrayList<Object> listdata = new ArrayList<>();
    String jsonsds = "";
    JSONArray jsonArray = null;
    JSONObject jsonObject = null;
    ImageView imgBack;
    String OrderVal;
    Integer newQty;
    Integer OldCQtys;
    Integer Rates;
    String OldQty;
    ViewProductEdit priProdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_edit_activtity);
        OrderId = String.valueOf(getIntent().getSerializableExtra("OrderId"));
        pendingRecycle = (RecyclerView) findViewById(R.id.product_edit);
        mShared_common_pref = new Shared_Common_Pref(this);
        mCommon_class = new Common_Class(this);

       getToolbar();

        ResponseDetails();
    }


    public void ResponseDetails() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.LogistData(OrderId);
        listdata.clear();
        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JSONObject jsonObject1;
//                Shared_Common_Pref shared_common_pref;
                try {
                    jsonObject1 = new JSONObject(response.body().toString());

                    jsonArray = jsonObject1.optJSONArray("Order");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        jsonObject = jsonArray.getJSONObject(i);
                        listdata.add(jsonArray.get(i));
                    }

                    pendingRecycle.setHasFixedSize(true);
                    pendingRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    pendingRecycle.setNestedScrollingEnabled(false);
                    priProdAdapter = new ViewProductEdit(DispatchEditActivtity.this, jsonArray);
                    pendingRecycle.setAdapter(priProdAdapter);
                    mCommon_class.ProgressdialogShow(2, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mCommon_class.ProgressdialogShow(2, "");
                t.printStackTrace();
            }
        });


    }

    public void dipatchItem(View v) {
//        listdata = adap

       /* if(!priProdAdapter.isAnythingChanged())
            Toast.makeText(this, "Please do any changes", Toast.LENGTH_SHORT).show();
        else {*/
            Log.v("LIST_OF_DATAssssss", priProdAdapter.getUpdatedData().toString());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> ca = apiInterface.Dispatch(mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), priProdAdapter.getUpdatedData().toString());


            ca.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.v("RESPONSE_DISPACTCH", response.toString());
                    Toast.makeText(DispatchEditActivtity.this, "Dispatched Item Successfully", Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
//                            Intent Details = new Intent(DispatchEditActivtity.this, PaymentVerified.class);
//                            Details.putExtra("OrderId", OrderId);
//                            startActivity(Details);
                        }
                    }, 1000);


                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                }
            });

//        }

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
//                startActivity(new Intent(getApplicationContext(), PaymentVerified.class));
//                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

class ViewProductEdit extends RecyclerView.Adapter<ViewProductEdit.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
    Shared_Common_Pref shared_common_pref;
    Integer value2 = 0;
    JSONObject jsonvalue = null;
    JSONObject jsonObject = null;
    //    DMS.DisptachEditing itemClick;
    boolean isAllowedToChange = true;

    public ViewProductEdit(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
        shared_common_pref = new Shared_Common_Pref(context);
//        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewProductEdit.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dispatch_items, parent, false);
/*        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* itemClick.onClickParentInter("", "", "", "", "",
                        "", "", "", "", "", "", "","");*//*
            }
        });*/


        return new ViewProductEdit.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProductEdit.MyViewHolder holder, int position) {
//        String OCQty;

//        Log.v("JSON________ARRAY", jsonArray.toString());
        try {

            jsonObject = (JSONObject) jsonArray.get(holder.getAdapterPosition());

//            String Slno = String.valueOf(jsonObject.get("Slno"));
//            String orderID = String.valueOf(jsonObject.get("OrderID"));
//            String Product_Name = String.valueOf(jsonObject.get("Product_Name"));
//            String PCode = String.valueOf(jsonObject.get("PCode"));
//            String CQty = String.valueOf(jsonObject.get("OldCQty"));
//            String value = String.valueOf(jsonObject.get("Oldvalue"));

//            String Rate = String.valueOf(jsonObject.get("Rate"));
//            String Cl_bal = String.valueOf(jsonObject.get("Cl_bal"));
//            String Unit = String.valueOf(jsonObject.get("Unit"));
//            String nCQty= String.valueOf(jsonObject.get("newCQty"));
//            String NValue= String.valueOf(jsonObject.get("Newvalue"));
//            Log.v("oldvalue",value);

//            Log.v("oldvqty",CQty);
//            Log.v("newqty",nCQty);
            long oldValue = jsonObject.getLong("OldCQty");


            holder.orderID.setText("" + jsonObject.get("Product_Name"));
            holder.orderDistributor.setText("" + jsonObject.get("Unit"));

           // holder.orderValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", CQty)});
//            holder.orderValue.setText(s.toString());
            isAllowedToChange = true;

            holder.orderValue.setFilters(new InputFilter[]{ new InputFilterMinMax("0", String.valueOf(oldValue))});
            String oldAmt = "";
            if(!jsonObject.has("tempOldCQty") || jsonObject.getString("tempOldCQty")==null || jsonObject.getString("tempOldCQty").equals("")){
                oldAmt =String.valueOf(oldValue);
            }else {
                oldAmt = jsonObject.getString("tempOldCQty");
            }

            if(!jsonObject.isNull("SchemeAvail") && jsonObject.getString("SchemeAvail").equalsIgnoreCase("Yes")){

                holder.orderValue.setVisibility(View.GONE);
                holder.linear_add.setVisibility(View.VISIBLE);
                holder.tv_qty_value.setText(oldAmt);

                String free = "0";
                if(jsonObject.has("Free"))
                    free = jsonObject.getString("Free");

                holder.tv_free_value.setText(free);

                String dis = "0";
                if(jsonObject.has("discount_price"))
                    dis = jsonObject.getString("discount_price");

                holder.tv_disc_amt_value.setText(dis);

                holder.martl_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if(holder.text_checking.getText().toString().equalsIgnoreCase("Postpone"))
                        {
                            holder.text_checking.setText("Postponed");
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());
                                jsonObject.put("postponed", "true");
                                jsonArray.put(holder.getAdapterPosition(), jsonObject);

                        } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            holder.text_checking.setTextColor(context.getResources().getColor(R.color.textColor));
                            holder.martl_view.setCardBackgroundColor(context.getResources().getColor(R.color.postponed));
                            holder.martl_view.setStrokeColor(context.getResources().getColor(R.color.postponed));

                        }else {
                            holder.text_checking.setText("Postpone");
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());
                                jsonObject.put("postponed", "false");
                                jsonArray.put(holder.getAdapterPosition(), jsonObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            holder.text_checking.setTextColor(context.getResources().getColor(R.color.black));
                            holder.martl_view.setCardBackgroundColor(context.getResources().getColor(R.color.textColor));
                            holder.martl_view.setStrokeColor(context.getResources().getColor(R.color.black));

                        }

                    }
                });

            }else {
                holder.orderValue.setVisibility(View.VISIBLE);
                holder.linear_add.setVisibility(View.GONE);
                holder.orderValue.setText(oldAmt);


                holder.orderValue.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String OrderVal=s.toString();
                        long  newValue = 0;
                        if(!s.toString().equals("")){
                            newValue = Long.parseLong(OrderVal);
                            isAllowedToChange = newValue < oldValue;
                           /* if(!isAllowedToChange){
                                Toast.makeText(context,"Please select quantity equal or below original value",Toast.LENGTH_SHORT).show();
                            }*/

                        }else
                            isAllowedToChange = false;
                        if(isAllowedToChange) {
                            try {

                                JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());
                                try {
                                    jsonObject.put("tempOldCQty", newValue); //new value 4
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    jsonObject.put("tempNewvalue", Constants.roundTwoDecimals((oldValue - newValue) * jsonObject.getDouble("Rate"))); //6 * Rates
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    jsonObject.put("tempOldvalue", Constants.roundTwoDecimals(newValue * jsonObject.getDouble("Rate")));//4 * Rates
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    jsonObject.put("tempnewCQty", oldValue - newValue); // 10-4 = 6
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                jsonArray.put(holder.getAdapterPosition(), jsonObject);
                                holder.orderValue.setSelection(holder.orderValue.getText().length());
    //                            notifyItemChanged(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("afterTextChanged", "afterTextChanged: "+ s);


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
        TextView orderID, orderDate, orderDistributor, orderStatus;
        EditText orderValue;
//        CardView martl_view;


        LinearLayout linear_add;
        TextView tv_qty_value, tv_free_value, tv_disc_amt_value;

        MaterialCardView martl_view;
        TextView text_checking;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.child_product_name);
            orderDistributor = itemView.findViewById(R.id.child_pro_unit);
            orderValue = itemView.findViewById(R.id.text_view_count);
            linear_add = itemView.findViewById(R.id.linear_add);
            tv_qty_value = itemView.findViewById(R.id.tv_qty_value);
            tv_free_value = itemView.findViewById(R.id.tv_free_value);
            tv_disc_amt_value = itemView.findViewById(R.id.tv_disc_amt_value);
            martl_view = itemView.findViewById(R.id.martl_view);
            text_checking = itemView.findViewById(R.id.text_checking);
        }
    }
    public boolean isAnythingChanged(){
        for(int i = 0; i< jsonArray.length(); i++){
            try {
                JSONObject jsonObject =jsonArray.getJSONObject(i);
                if(!jsonObject.isNull("tempOldCQty") || !jsonObject.isNull("tempNewvalue") ||
                !jsonObject.isNull("tempOldvalue") || !jsonObject.isNull("tempnewCQty")){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
    public JSONArray getUpdatedData(){
        for(int i = 0; i< jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(!jsonObject.isNull("postponed") && jsonObject.getString("postponed").equals("true")){

                    if(!jsonObject.isNull("Oldvalue"))
                        jsonObject.put("Newvalue", jsonObject.getDouble("Oldvalue"));

                    if(!jsonObject.isNull("OldCQty"))
                        jsonObject.put("newCQty", jsonObject.getLong("OldCQty"));

                    jsonObject.put("OldCQty", 0);

                    jsonObject.put("Oldvalue", 0);

                }else {
                    if(!jsonObject.isNull("tempOldCQty"))
                        jsonObject.put("OldCQty", jsonObject.getLong("tempOldCQty"));

                    if(!jsonObject.isNull("tempNewvalue"))
                        jsonObject.put("Newvalue", jsonObject.getDouble("tempNewvalue"));

                    if(!jsonObject.isNull("tempOldvalue"))
                        jsonObject.put("Oldvalue", jsonObject.getDouble("tempOldvalue"));

                    if(!jsonObject.isNull("tempnewCQty"))
                        jsonObject.put("newCQty", jsonObject.getLong("tempnewCQty"));
                }

                    jsonArray.put(i,jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return jsonArray;
    }
}


