package com.example.sandms.Activity;

import static com.example.sandms.Activity.ViewCartActivity.createProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrimaryOrderList extends AppCompatActivity {
    Shared_Common_Pref mShared_common_pref;
    public   JSONObject jsonProductuom;
    RecyclerView priUnitRecycler;
    ArrayList<String> ProductNames = new ArrayList<>();
    TextView toolHeader;
    ImageView imgBack;
    private  ProducAdapter mAdapter;
    String productid;
    PrimaryProduct pp;
    String ClickedData = "";
    String pos;
    EditText toolSearch;
    PrimaryProduct task;

    List<PrimaryProduct.UOMlist> uoMlistList = new ArrayList<>();

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
        Intent intent = getIntent();
        productid=getIntent().getStringExtra("orderid");
        pos=getIntent().getStringExtra("pos");
        uoMlistList.clear();
        if(intent.hasExtra("uomList"))
            uoMlistList.addAll((ArrayList<PrimaryProduct.UOMlist>) intent.getSerializableExtra("uomList"));

        Log.e("LoginResponse199", ""+ productid);
        Log.e("Logi",  ""+pos);
//        priUnitRecycler.setHasFixedSize(true);
//        priUnitRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        priUnitRecycler.setNestedScrollingEnabled(false);

        mAdapter = new ProducAdapter(getApplicationContext(), uoMlistList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        priUnitRecycler.setLayoutManager(mLayoutManager);
        priUnitRecycler.setItemAnimator(new DefaultItemAnimator());
        priUnitRecycler.setAdapter(mAdapter);

        if(uoMlistList.size()==0){
            getProductId();
        }
    }

    public void getProductId() {
        ProgressDialog progressDialog = createProgressDialog(this);
        //   this.sf_Code=sf_Code;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProductuom(mShared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.v("DMS_RESPONSE", response.body().toString());
                progressDialog.dismiss();
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
                        String id = "";
                        if(jsonObject.has("id"))
                        id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        Log.v("LoginResponse1nn", name);
                        String productCode = jsonObject.getString("Product_Code");
                        Log.v("LoginResponse1nnq", productCode);
                        String conqty = jsonObject.getString("ConQty");
                        Log.v("LoginResponse1nnq", conqty);
                        if (productid.equals(productCode) || productid.contains(productCode)) {
                            PrimaryProduct.UOMlist pp = new PrimaryProduct.UOMlist(id, name, productCode, conqty);
                            uoMlistList.add(pp);
                        }
//                         priProdAdapter = new ProductsAdapter(PrimaryOrderList.this, name,productCode,conqty);
//                         priUnitRecycler.setAdapter(priProdAdapter);

                        mAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
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

//            toolSearch = (EditText) findViewById(R.id.toolbar_search);
//            toolSearch.setVisibility(View.GONE);

        }


    public class ProducAdapter extends RecyclerView.Adapter<ProducAdapter.MyViewHolder> {
            private List<PrimaryProduct.UOMlist> productList;
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
        public ProducAdapter (Context ct,List<PrimaryProduct.UOMlist> productList) {
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
            PrimaryProduct.UOMlist productlist = productList.get(position);
            holder.title.setText(productlist.getName());
            holder.productvalue.setText(productlist.getConQty());
            subtotal= Double.valueOf(task.getSubtotal());
            Log.e("subbbb", String.valueOf(subtot));
            if(productlist.getConQty()!=null && !productlist.getConQty().equals(""))
                cqty= Integer.parseInt(productlist.getConQty());
            else
                cqty = 1;

            holder.productvalue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateTask(task,productList.get(position).getName(),productList.get(position).getConQty(),subtotal);
                    onBackPressed();
                }
            });
            holder.row_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask(task,productList.get(position).getName(),productList.get(position).getConQty(),subtotal);
                onBackPressed();
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
                int cnqtyInt =1;
                try {
                    cnqtyInt = Integer.parseInt(cnqty);
                    if(cnqtyInt ==0)
                        cnqtyInt =1;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    cnqtyInt =1;
                }
                task.setProduct_Sale_Unit_Cn_Qty(cnqtyInt);
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