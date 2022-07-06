package com.saneforce.dms.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;
import com.saneforce.dms.adapter.PartialDispatchAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.model.DispatchModel;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.InputFilterMinMax;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatchEditActivtity extends AppCompatActivity {
    private static final String TAG = DispatchEditActivtity.class.getSimpleName();
    String OrderId;
    RecyclerView pendingRecycle;
    Shared_Common_Pref mShared_common_pref;
    Common_Class mCommon_class;
//    ArrayList<Object> listdata = new ArrayList<>();
    //    String jsonsds = "";
    JSONArray jsonArray = null;
    JSONObject jsonObject = null;
    ImageView imgBack;
    //    String OrderVal;
//    Integer newQty;
//    Integer OldCQtys;
//    Integer Rates;
//    String OldQty;
    ViewProductEdit priProdAdapter;

    int editMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_edit_activtity);
        OrderId = String.valueOf(getIntent().getSerializableExtra("OrderId"));
        pendingRecycle = (RecyclerView) findViewById(R.id.product_edit);
        mShared_common_pref = new Shared_Common_Pref(this);
        mCommon_class = new Common_Class(this);
        if (getIntent().hasExtra("editMode"))
            editMode = getIntent().getIntExtra("editMode", 0);
        getToolbar();

        ResponseDetails();
    }


    public void ResponseDetails() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.LogistData(OrderId);
//        listdata.clear();
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
                        jsonObject.put("postponed", "false");

//                        listdata.add(jsonArray.get(i));
                    }

                    pendingRecycle.setHasFixedSize(true);
                    pendingRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    pendingRecycle.setNestedScrollingEnabled(false);
                    priProdAdapter = new ViewProductEdit(DispatchEditActivtity.this, jsonArray, editMode);
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
        if(!Constant.isInternetAvailable(this)){
            Toast.makeText(this, "Please check the Internet connection", Toast.LENGTH_SHORT).show();
        }else if (!priProdAdapter.isValid())
            Toast.makeText(this, "Please add atleast 1 quantity of product", Toast.LENGTH_SHORT).show();
       /* else if (priProdAdapter.isAnyPostpondEdit()) {
            showPartialEditDialog(priProdAdapter.getPospondEditData());
        }*/
        else
//            priProdAdapter.getUpdatedData(false);
            dispatchData(false);
    }

    /*public void FCMToken(){

    }
*/
    private void dispatchData(boolean isCreateNewOrder) {
        //            Log.v("LIST_OF_DATAssssss", priProdAdapter.getUpdatedData().toString());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.Dispatch(mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), priProdAdapter.getUpdatedData(isCreateNewOrder).toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String msg = "";

                if(jsonObject.get("success").getAsBoolean()){
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
                    msg = "Dispatched Item Successfully";

                }else{
                    msg = "Something went wrong, please try again";
                }
                if(jsonObject.has("msg"))
                    msg = jsonObject.get("msg").getAsString();

                Toast.makeText(DispatchEditActivtity.this, msg, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DispatchEditActivtity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void getToolbar() {
        Intent intent = getIntent();
        if (intent.hasExtra("title") && intent.getStringExtra("title") != null) {
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


    // Pass list of your model as arraylist
    private void showPartialEditDialog(ArrayList<DispatchModel> dispatchLIst) {
        final Dialog dialog = new Dialog(DispatchEditActivtity.this);
        dialog.setContentView(R.layout.dialog_partial_edit);
/*        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // this is optional
        }*/

        ListView listView = dialog.findViewById(R.id.lv_partial_dispatch);
//        TextView tv_title = dialog.findViewById(R.id.tv_title);
        TextView tv_new_order = dialog.findViewById(R.id.tv_new_order);
        TextView tv_discard = dialog.findViewById(R.id.tv_discard);

        ArrayAdapter<DispatchModel> arrayAdapter = new PartialDispatchAdapter(this, R.layout.item_partial_edit, dispatchLIst);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((adapterView, view, which, l) -> {
//            Log.d(TAG, "showPartialEditDialog: " + dispatchLIst.get(which));

        });
        tv_new_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dispatchData(true);
            }
        });
        tv_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dispatchData(false);
            }
        });
        dialog.show();
    }


    public class ViewProductEdit extends RecyclerView.Adapter<ViewProductEdit.MyViewHolder> {
        Context context;
        JSONArray jsonArray;
        Shared_Common_Pref shared_common_pref;
        Integer value2 = 0;
        JSONObject jsonvalue = null;
        JSONObject jsonObject = null;
        //    DMS.DisptachEditing itemClick;
        boolean isAllowedToChange = true;
        int editMode = 0;

        public ViewProductEdit(Context context, JSONArray jsonArray, int editMode) {
            this.context = context;
            this.jsonArray = jsonArray;
            shared_common_pref = new Shared_Common_Pref(context);
            this.editMode = editMode;
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

                holder.orderValue.setFilters(new InputFilter[]{new InputFilterMinMax("0", String.valueOf(oldValue))});
                holder.et_qty_value.setFilters(new InputFilter[]{new InputFilterMinMax("0", String.valueOf(oldValue))});
                String oldAmt = "";
                if (!jsonObject.has("tempOldCQty") || jsonObject.getString("tempOldCQty") == null || jsonObject.getString("tempOldCQty").equals("")) {
                    oldAmt = String.valueOf(oldValue);
                } else {
                    oldAmt = jsonObject.getString("tempOldCQty");
                }

                if (!jsonObject.isNull("SchemeAvail") && jsonObject.getString("SchemeAvail").equalsIgnoreCase("Yes")) {

                    holder.orderValue.setVisibility(View.GONE);
                    holder.linear_add.setVisibility(View.VISIBLE);
                    holder.et_qty_value.setText(oldAmt);

                    String free = "0";
                    if (jsonObject.has("Free"))
                        free = jsonObject.getString("Free");

                    if (!jsonObject.isNull("Offer_Product_Unit"))
                        free = free + " " + jsonObject.getString("Offer_Product_Unit");

                    holder.tv_free_value.setText(free);

                    String dis = "0";
                    if (jsonObject.has("discount_price"))
                        dis = jsonObject.getString("discount_price");

                    holder.tv_disc_amt_value.setText(dis);

                    if (editMode == 1) {
                        holder.martl_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (holder.text_checking.getText().toString().equalsIgnoreCase("Discard")) {
//                                    holder.ib_edit_qty.setVisibility(View.GONE);
                                    setQtyEdit(holder.et_qty_value,false, oldValue);
                                    holder.text_checking.setText("Discarded");
                                    holder.et_qty_value.setText(String.valueOf(oldValue));

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

                                } else {
                                    setQtyEdit(holder.et_qty_value,true, oldValue);
//                                    holder.ib_edit_qty.setVisibility(View.VISIBLE);
                                    holder.text_checking.setText("Discard");
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

                        setQtyEdit(holder.et_qty_value,false, oldValue);
                        /*holder.ib_edit_qty.setVisibility(View.VISIBLE);
                        holder.ib_edit_qty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                setQtyEdit(holder.et_qty_value, !holder.et_qty_value.getTag().equals("1"), oldValue);
                            }
                        });*/

                        holder.et_qty_value.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String OrderVal = s.toString();
                                long newValue = 0;
                                if (!s.toString().equals("")) {
                                    newValue = Long.parseLong(OrderVal);
                                    isAllowedToChange = newValue <= oldValue;
                           /* if(!isAllowedToChange){
                                Toast.makeText(context,"Please select quantity equal or below original value",Toast.LENGTH_SHORT).show();
                            }*/

                                } else
                                    isAllowedToChange = false;
                                if (isAllowedToChange) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());
                                        try {
                                            jsonObject.put("tempOldCQty", newValue); //new value 4
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            jsonObject.put("tempNewvalue", Constant.roundTwoDecimals((oldValue - newValue) * jsonObject.getDouble("Rate"))); //6 * Rates
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            jsonObject.put("tempOldvalue", Constant.roundTwoDecimals(newValue * jsonObject.getDouble("Rate")));//4 * Rates
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            jsonObject.put("tempnewCQty", oldValue - newValue); // 10-4 = 6
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        jsonArray.put(holder.getAdapterPosition(), jsonObject);
//                                        holder.et_qty_value.setSelection(holder.et_qty_value.getText().length());
                                        //                            notifyItemChanged(position);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                Log.d("afterTextChanged", "afterTextChanged: " + s);


                            }
                        });


                    }else {
                        setQtyEdit(holder.et_qty_value,false, oldValue);

//                        holder.ib_edit_qty.setVisibility(View.GONE);
                    }


                } else {
                    holder.orderValue.setVisibility(View.VISIBLE);
                    holder.linear_add.setVisibility(View.GONE);
                    holder.orderValue.setText(oldAmt);
                    setQtyEdit(holder.orderValue, false, oldValue);

//                    holder.orderValue.setEnabled(true);
//                    holder.orderValue.setClickable(true);

                    if (editMode == 1) {

                        holder.martl_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (holder.text_checking.getText().toString().equalsIgnoreCase("Discard")) {
//                                    holder.ib_edit_qty.setVisibility(View.GONE);
                                    setQtyEdit(holder.et_qty_value,false, oldValue);
                                    holder.text_checking.setText("Discarded");
                                    holder.et_qty_value.setText(String.valueOf(oldValue));

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

                                } else {
                                    setQtyEdit(holder.et_qty_value,true, oldValue);
//                                    holder.ib_edit_qty.setVisibility(View.VISIBLE);
                                    holder.text_checking.setText("Discard");
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


                        holder.orderValue.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String OrderVal = s.toString();
                                long newValue = 0;
                                if (!s.toString().equals("")) {
                                    newValue = Long.parseLong(OrderVal);
                                    isAllowedToChange = newValue <= oldValue;
                           /* if(!isAllowedToChange){
                                Toast.makeText(context,"Please select quantity equal or below original value",Toast.LENGTH_SHORT).show();
                            }*/

                                } else
                                    isAllowedToChange = false;
                                if (isAllowedToChange) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(holder.getAdapterPosition());
                                        try {
                                            jsonObject.put("tempOldCQty", newValue); //new value 4
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            jsonObject.put("tempNewvalue", Constant.roundTwoDecimals((oldValue - newValue) * jsonObject.getDouble("Rate"))); //6 * Rates
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            jsonObject.put("tempOldvalue", Constant.roundTwoDecimals(newValue * jsonObject.getDouble("Rate")));//4 * Rates
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            jsonObject.put("tempnewCQty", oldValue - newValue); // 10-4 = 6
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        jsonArray.put(holder.getAdapterPosition(), jsonObject);
//                                        holder.orderValue.setSelection(holder.orderValue.getText().length());
                                        //                            notifyItemChanged(position);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                Log.d("afterTextChanged", "afterTextChanged: " + s);
                            }
                        });

/*                        holder.ib_edit_qty.setVisibility(View.VISIBLE);
                        holder.ib_edit_qty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setQtyEdit(holder.orderValue, !holder.orderValue.getTag().equals("1"), oldValue);
                            }
                        });*/

                    } else {
//                        holder.ib_edit_qty.setVisibility(View.GONE);
                        holder.orderValue.setEnabled(false);
                        holder.orderValue.setClickable(false);
                    }

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
            TextView orderID, orderDistributor;
            TextView orderValue;
//        CardView martl_view;


            LinearLayout linear_add;
            TextView tv_free_value, tv_disc_amt_value;

            MaterialCardView martl_view;
            TextView text_checking;
            TextView et_qty_value;
//            ImageButton ib_edit_qty;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                orderID = itemView.findViewById(R.id.child_product_name);
                orderDistributor = itemView.findViewById(R.id.child_pro_unit);
                orderValue = itemView.findViewById(R.id.text_view_count);
                linear_add = itemView.findViewById(R.id.linear_add);
                et_qty_value = itemView.findViewById(R.id.et_qty_value);
                tv_free_value = itemView.findViewById(R.id.tv_free_value);
                tv_disc_amt_value = itemView.findViewById(R.id.tv_disc_amt_value);
                martl_view = itemView.findViewById(R.id.martl_view);
                text_checking = itemView.findViewById(R.id.text_checking);
//                ib_edit_qty = itemView.findViewById(R.id.ib_edit_qty);
            }
        }

        public boolean isValid() {
            int postponedCount = 0;
            int qtyCount = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("SchemeAvail").equalsIgnoreCase("Yes") && !jsonObject.isNull("postponed") && jsonObject.getString("postponed").equals("true"))
                        ++postponedCount;
                    else {
//                        && !jsonObject.isNull("tempnewCQty") && !jsonObject.getString("tempnewCQty").equals("")
                        if(qtyCount ==0 && !jsonObject.isNull("OldCQty") && !jsonObject.getString("OldCQty").equals(""))
                                qtyCount +=  (jsonObject.getInt("OldCQty"));
//                        -jsonObject.getInt("tempnewCQty")
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return qtyCount > 0 && postponedCount != jsonArray.length();
        }

        public boolean isAnyPostpondEdit() {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("SchemeAvail").equalsIgnoreCase("Yes") && (jsonObject.isNull("postponed") || jsonObject.getString("postponed").equals("false")) &&
                        !jsonObject.isNull("tempOldCQty") && !jsonObject.isNull("OldCQty") &&
                        !jsonObject.getString("tempOldCQty").equals( jsonObject.getString("OldCQty")))
                        return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }

        public ArrayList<DispatchModel> getPospondEditData() {
            ArrayList<DispatchModel> dispatchModelArrayList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("SchemeAvail").equalsIgnoreCase("Yes") && (jsonObject.isNull("postponed") || jsonObject.getString("postponed").equals("false"))
                            && !jsonObject.isNull("tempOldCQty") &&
                            !jsonObject.getString("tempOldCQty").equals(jsonObject.getString("OldCQty")) ){
                        DispatchModel dispatchModel = new DispatchModel();
                        dispatchModel.setProductName(jsonObject.getString("Product_Name"));
//                                        OldCQty
//                                        tempOldCQty
//                                        tempnewCQty

                        dispatchModel.setTotal_ordered(jsonObject.getString("OldCQty"));
                        dispatchModel.setTotal_dipatched(jsonObject.getString("tempOldCQty"));
                        String pendingCount = jsonObject.getString("tempnewCQty");
                        if(!jsonObject.isNull("Free") && !jsonObject.getString("Free").equals("")  && !jsonObject.getString("Free").equals("0"))
                            pendingCount = pendingCount +" + "+ jsonObject.getString("Free");
                        dispatchModel.setTotal_pending(pendingCount);
                        dispatchModelArrayList.add(dispatchModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return dispatchModelArrayList;
        }


        public JSONArray getUpdatedData(boolean isCreateNewOrder) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("SchemeAvail").equalsIgnoreCase("Yes") &&
                            (!isCreateNewOrder || (jsonObject.isNull("tempOldCQty") || (!jsonObject.isNull("OldCQty") &&
                                    jsonObject.getString("tempOldCQty").equals(jsonObject.getString("OldCQty"))))))   {

                        if (!jsonObject.isNull("Oldvalue"))
                            jsonObject.put("Newvalue", jsonObject.getDouble("Oldvalue"));

                        if (!jsonObject.isNull("OldCQty"))
                            jsonObject.put("newCQty", jsonObject.getLong("OldCQty"));

                        jsonObject.put("OldCQty", 0);

                        jsonObject.put("Oldvalue", 0);

                    } else if(!jsonObject.isNull("tempOldCQty") && !jsonObject.getString("tempOldCQty").equals(jsonObject.getString("OldCQty"))){
                        if (!jsonObject.isNull("tempOldCQty"))
                            jsonObject.put("OldCQty", jsonObject.getLong("tempOldCQty"));

                        if (!jsonObject.isNull("tempNewvalue"))
                            jsonObject.put("Newvalue", jsonObject.getDouble("tempNewvalue"));

                        if (!jsonObject.isNull("tempOldvalue"))
                            jsonObject.put("Oldvalue", jsonObject.getDouble("tempOldvalue"));

                        if (!jsonObject.isNull("tempnewCQty"))
                            jsonObject.put("newCQty", jsonObject.getLong("tempnewCQty"));
                    }

                    jsonArray.put(i, jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Log.d(TAG, "getUpdatedData: server response => "+ jsonArray);
            return jsonArray;
        }

    }

    private void setQtyEdit(TextView et_qty_value, boolean isActive, long oldValue) {
//        et_qty_value.setEnabled(isActive);
//        et_qty_value.setClickable(isActive);
//        et_qty_value.setFocusable(isActive);
//        et_qty_value.setFocusableInTouchMode(isActive);

        if(isActive){
//            et_qty_value.setBackground(getDrawable(R.drawable.oval_background));
            et_qty_value.setTag("1");
        }
        else{
//            et_qty_value.setBackground(null);
            et_qty_value.setTag("0");
            et_qty_value.setText(String.valueOf(oldValue));

        }

    }


}
