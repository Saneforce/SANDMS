package com.example.sandms.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.JsonObject;

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
    ArrayList<Object> listdata = new ArrayList<Object>();
    String jsonsds = "";
    JSONArray jsonArray = null;
    JSONObject jsonObject = null;
    ImageView imgBack;
    String OrderVal;
    Integer newQty;
    Integer OldCQtys;
    Integer Rates;
        String OldQty;
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
                Shared_Common_Pref shared_common_pref;
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
                    ViewProductEdit priProdAdapter = new ViewProductEdit(DispatchEditActivtity.this, jsonArray, new DMS.DisptachEditing() {
                        @Override
                        public void onClickParentInter(String position, String Slno, String PCode,
                                                       String OrderId, String ProductName, String OldCQty,
                                                       String Newvalue, String Oldvalue, String Rate,
                                                       String Cl_bal, String Unit, String newCQty) {


                            try {
                                newQty = Integer.valueOf(newCQty);
                                OldCQtys = Integer.valueOf(OldCQty);
                                Integer Rates = Integer.valueOf(Rate);
                                Integer NewOrder = newQty - OldCQtys;
                                Integer NewValue = NewOrder * Rates;
                                Integer OldValues = OldCQtys * Rates;
                                if (OldCQtys > newQty) {
                                    Toast.makeText(DispatchEditActivtity.this,"Please select quantity equal or below value",Toast.LENGTH_SHORT).show();

                                } else {

//                                    OldQty=OQty;


                               for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                       if (Slno.equals(jsonObject.getString("Slno"))) {
                                           jsonObject.put("Slno", Slno);
                                           jsonObject.put("PCode", PCode);
                                           jsonObject.put("OrderId", OrderId);
                                           jsonObject.put("ProductName", ProductName);
                                           jsonObject.put("OldCQty", OldCQtys);
                                           jsonObject.put("Newvalue", Float.valueOf(NewValue));
                                           jsonObject.put("Oldvalue", Float.valueOf(OldValues));
                                           jsonObject.put("Rate", Rates);
                                           jsonObject.put("Cl_bal", Cl_bal);
                                           jsonObject.put("Unit", Unit);
                                           jsonObject.put("newCQty", NewOrder);
                                           listdata.set(Integer.parseInt(position), jsonObject.toString());
                                       }else {

                                   }



                               }
                                    Log.v("LIST_OF_DATA", String.valueOf(listdata.toString()));

                                    Log.v("JsONDATE", jsonArray.toString());
                             }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NumberFormatException EE){}
                            Log.v("LIST_OF_DATA", String.valueOf(listdata.toString()));

                            Log.v("JsONDATE", jsonArray.toString());

                        }
                    });
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

    public void dipatchItem(View v) {
        try {
            if (OldCQtys > newQty) {
                Toast.makeText(DispatchEditActivtity.this, "Please select quantity equal or below value", Toast.LENGTH_SHORT).show();

            }
        }catch(NullPointerException ee){

            Log.v("LIST_OF_DATAssssss", String.valueOf(listdata.toString()));
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<JsonObject> ca = apiInterface.Dispatch(mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), listdata.toString());
            Log.v("parametersDispatch", "data1" + mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code) + "data2" + String.valueOf(listdata.toString()));

            Log.v("REquest_DISPACTCH", ca.request().toString());
            ca.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.v("RESPONSE_DISPACTCH", response.toString());
                    Toast.makeText(DispatchEditActivtity.this, "Dispatched Item Successfully", Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent Details = new Intent(DispatchEditActivtity.this, PaymentVerified.class);
                            Details.putExtra("OrderId", OrderId);
                            startActivity(Details);
                        }
                    }, 1000);


                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
            Log.v("Product_Request", ca.request().toString());
        }
        Log.v("LIST_OF_DATAssssss", String.valueOf(listdata.toString()));
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<JsonObject> ca = apiInterface.Dispatch(mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code), listdata.toString());
                Log.v("parametersDispatch", "data1" + mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code) + "data2" + String.valueOf(listdata.toString()));

                Log.v("REquest_DISPACTCH", ca.request().toString());
                ca.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.v("RESPONSE_DISPACTCH", response.toString());
                        Toast.makeText(DispatchEditActivtity.this, "Dispatched Item Successfully", Toast.LENGTH_SHORT).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent Details = new Intent(DispatchEditActivtity.this, PaymentVerified.class);
                                Details.putExtra("OrderId", OrderId);
                                startActivity(Details);
                            }
                        }, 1000);


                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
                Log.v("Product_Request", ca.request().toString());
            }

    public void getToolbar() {

        imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), PaymentVerified.class));
                finish();
            }
        });

    }

}

class ViewProductEdit extends RecyclerView.Adapter<ViewProductEdit.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
    Shared_Common_Pref shared_common_pref;
    Integer value2 = 0;
    JSONObject jsonvalue = null;
    JSONObject jsonObject = null;
    DMS.DisptachEditing itemClick;

    public ViewProductEdit(Context context, JSONArray jsonArray, DMS.DisptachEditing itemClick) {
        this.context = context;
        this.jsonArray = jsonArray;
        shared_common_pref = new Shared_Common_Pref(context);
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewProductEdit.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dispatch_items, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* itemClick.onClickParentInter("", "", "", "", "",
                        "", "", "", "", "", "", "","");*/
            }
        });


        return new ViewProductEdit.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProductEdit.MyViewHolder holder, int position) {
        String OCQty;

        Log.v("JSON________ARRAY", jsonArray.toString());
        try {
            jsonObject = (JSONObject) jsonArray.get(position);

            String Slno = String.valueOf(jsonObject.get("Slno"));
            String orderID = String.valueOf(jsonObject.get("OrderID"));
            String Product_Name = String.valueOf(jsonObject.get("Product_Name"));
            String PCode = String.valueOf(jsonObject.get("PCode"));
            String CQty = String.valueOf(jsonObject.get("OldCQty"));
            String value = String.valueOf(jsonObject.get("Oldvalue"));
            String Rate = String.valueOf(jsonObject.get("Rate"));
            String Cl_bal = String.valueOf(jsonObject.get("Cl_bal"));
            String Unit = String.valueOf(jsonObject.get("Unit"));
            String nCQty= String.valueOf(jsonObject.get("newCQty"));
            String NValue= String.valueOf(jsonObject.get("Newvalue"));
            Log.v("oldvalue",value);

            Log.v("oldvqty",CQty);
            Log.v("newqty",nCQty);


            holder.orderID.setText("" + String.valueOf(jsonObject.get("Product_Name")));
            holder.orderValue.setText("" + String.valueOf(jsonObject.get("OldCQty")));
            holder.orderDistributor.setText("" + String.valueOf(jsonObject.get("Unit")));
            Log.v("JSONDETIAILS", String.valueOf(jsonObject.get("Product_Name")));
           // holder.orderValue.setFilters(new InputFilter[]{new InputFilterMinMax("1", CQty)});
            OCQty = String.valueOf(jsonObject.get("OldCQty"));

            holder.orderValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.e("ordervalue",holder.orderValue.getText().toString());
                    Log.e("textchange",s.toString());
                    Log.e("orderval",CQty);
                  String OrderVal=holder.orderValue.getText().toString();


                    if (holder.orderValue.getText().toString().equals("") || holder.orderValue.getText()==null||holder.orderValue.getText().equals("0")||
                            holder.orderValue.getText().toString().equalsIgnoreCase("null")) {//||holder.orderValue.getText().equals("0")
                        holder.orderValue.setText("" + 0);
                       /* itemClick.onClickParentInter(String.valueOf(position), Slno, PCode, orderID,
                                Product_Name, holder.orderValue.getText().toString(), "", "",
                                Rate, Cl_bal, Unit, CQty,OCQty);*/
                    } else {
                        /*itemClick.onClickParentInter(String.valueOf(position), Slno, PCode, orderID,
                                Product_Name, holder.orderValue.getText().toString(),
                              "", "",
                                Rate, Cl_bal, Unit,
                                CQty,OCQty);*/
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

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
        TextView orderID, orderDate, orderDistributor, orderStatus;
        EditText orderValue;
        CardView martl_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.child_product_name);
            orderDistributor = itemView.findViewById(R.id.child_pro_unit);
            orderValue = itemView.findViewById(R.id.text_view_count);
        }
    }
}


