package com.example.sandms.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Interface.DMS;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.PrimaryUom;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Model;
import com.example.sandms.Utils.CustomListViewDialog;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.PrimaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.realm.Realm.getApplicationContext;

public class PrimaryOrderList extends AppCompatActivity {
    Shared_Common_Pref mShared_common_pref;
    public   JSONObject jsonProductuom;
    RecyclerView priUnitRecycler;
    ArrayList<String> ProductNames = new ArrayList<>();
    TextView toolHeader;
    ImageView imgBack;
    private List<PrimaryUom> productList = new ArrayList<>();
    private  ProducAdapter mAdapter;
    String productid;
    PrimaryProduct pp;
    String ClickedData = "";
    String pos;
    EditText toolSearch;
    PrimaryProduct task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_order_list);
        getToolbar();
        mShared_common_pref = new Shared_Common_Pref(this);
        ClickedData = mShared_common_pref.getvalue("taskdata");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        task = gson.fromJson(ClickedData, PrimaryProduct.class);
        priUnitRecycler = findViewById(R.id.rec_checking);
        productid=getIntent().getStringExtra("orderid");
        pos=getIntent().getStringExtra("pos");
        Log.e("LoginResponse199",  productid.toString());
        Log.e("Logi",  pos.toString());
//        priUnitRecycler.setHasFixedSize(true);
//        priUnitRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        priUnitRecycler.setNestedScrollingEnabled(false);

        getProductId();
    }

    public void getProductId() {
        //   this.sf_Code=sf_Code;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProductuom(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.v("DMS_RESPONSE", response.body().toString());

                try {
                    jsonProductuom = new JSONObject(response.body().toString());
                    Log.e("LoginResponse1", jsonProductuom.toString());
                    String js = jsonProductuom.getString(
                            "success");
                    Log.e("LoginResponse133w", js.toString());
                    JSONArray jss = jsonProductuom.getJSONArray(
                            "Data");
                    Log.e("LoginResponse133ws", jss.toString());

                    // JSONArray jsonArray = jsonProductuom.optJSONArray("Data");
                    //  Log.e("LoginResponse133",  jsonProductuom.toString());
                    for (int i = 0; i < jss.length(); i++) {
                        JSONObject jsonObject = jss.optJSONObject(i);
                        String name = jsonObject.getString("name");
                        Log.v("LoginResponse1nn", name);
                        String productCode = jsonObject.getString("Product_Code");
                        Log.v("LoginResponse1nnq", productCode);
                        String conqty = jsonObject.getString("ConQty");
                        Log.v("LoginResponse1nnq", conqty);
                        if (productid.equals(productCode) || productid.contains(productCode)) {
                            PrimaryUom pp = new PrimaryUom(name, productCode, conqty);
                            productList.add(pp);
                        }
//                         priProdAdapter = new ProductsAdapter(PrimaryOrderList.this, name,productCode,conqty);
//                         priUnitRecycler.setAdapter(priProdAdapter);


                    }
                    mAdapter = new ProducAdapter(getApplicationContext(), productList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    priUnitRecycler.setLayoutManager(mLayoutManager);
                    priUnitRecycler.setItemAnimator(new DefaultItemAnimator());
                    priUnitRecycler.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(PrimaryOrderList.this, "Invalid products", Toast.LENGTH_LONG).show();
            }
        });

    }
        /*Toolbar*/
        public void getToolbar() {
//            TextView toolbartitle=findViewById(R.id.toolbar_title);
//            toolbartitle.setText("Product Unit Selection");

            imgBack = (ImageView) findViewById(R.id.toolbar_back);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
/*
                    AlertDialogBox.showDialog(PrimaryOrderList.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
                        @Override
                        public void PositiveMethod(DialogInterface dialog, int id) {
                            onBackPressed();
                        }

                        @Override
                        public void NegativeMethod(DialogInterface dialog, int id) {
                        }
                    });
*/
                }
            });
            toolHeader = (TextView) findViewById(R.id.toolbar_title);
            toolHeader.setText("Select UOM");

            toolSearch = (EditText) findViewById(R.id.toolbar_search);
            toolSearch.setVisibility(View.GONE);

        }


    public class ProducAdapter extends RecyclerView.Adapter<ProducAdapter.MyViewHolder> {
            private List<PrimaryUom> productList;
        Context ct;
        Double subtot,subtotal;
        int cqty;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, year, productvalue;
            LinearLayout image_down;
            ConstraintLayout row_category;
            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.child_product_name);
                productvalue = (TextView) view.findViewById(R.id.child_pro_unit);
                image_down=view.findViewById(R.id.image_down);
                row_category=view.findViewById(R.id.row_category);

            }
        }
        public ProducAdapter (Context ct,List<PrimaryUom> productList) {
            this.productList= productList;
            this.ct=ct;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_rows, parent, false);
            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PrimaryUom productlist = productList.get(position);
            holder.title.setText(productlist.getName());
            holder.productvalue.setText(productlist.getConQty());
            subtotal= Double.valueOf(task.getSubtotal());
            Log.e("subbbb", String.valueOf(subtot));
           cqty= Integer.parseInt(productlist.getConQty());

            holder.productvalue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateTask(task,productList.get(position).getName(),productList.get(position).getConQty(),subtotal);
                    finish();
                }
            });
            holder.row_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask(task,productList.get(position).getName(),productList.get(position).getConQty(),subtotal);
                finish();
            }
        });
    }

        @Override
        public int getItemCount() {
            return productList.size();
        }
    }

    private void updateTask(final PrimaryProduct task,final String PRODUCTNAME,final String cnqty, Double subtotal) {

        class UpdateTask extends AsyncTask<Void, Void, Void> {


            @Override
            protected Void doInBackground(Void... voids) {
                task.setProduct_Sale_Unit(PRODUCTNAME);
//                task.setQty(mProductCount.getText().toString());
//                task.setTxtqty(mProductCount.getText().toString());
             //   task.setSubtotal();
                Log.e("subt",subtotal.toString());
               //    task.setSubtotal(String.valueOf(subtotal));
               task.setProduct_Sale_Unit_Cn_Qty(Integer.parseInt(cnqty));
                Log.e("subtcnqty", String.valueOf(cnqty));
                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .updateDATA(task.getPID(),
                               PRODUCTNAME,
                                Integer.parseInt(cnqty),
                                String.valueOf(subtotal)
                        );
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}