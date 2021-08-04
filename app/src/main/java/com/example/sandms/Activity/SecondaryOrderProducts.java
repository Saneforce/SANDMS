package com.example.sandms.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.sandms.Interface.DMS;
import com.example.sandms.Model.SecondaryProduct;
import com.example.sandms.Model.SecondaryProduct;
import com.example.sandms.Model.Product;
import com.example.sandms.Model.Product_Array;
import com.example.sandms.Model.SecondaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.Constants;
import com.example.sandms.Utils.CustomListViewDialog;
import com.example.sandms.Utils.SecondaryProductDatabase;
import com.example.sandms.Utils.SecondaryProductViewModel;
import com.example.sandms.Utils.SecondaryProductDatabase;
import com.example.sandms.Utils.SecondaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.example.sandms.sqlite.DBController;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.realm.Realm.getApplicationContext;

@SuppressWarnings("deprecation")
public class SecondaryOrderProducts extends AppCompatActivity {

    Gson gson;
    Shared_Common_Pref mShared_common_pref;
    RecyclerView priCategoryRecycler;
    RecyclerView priProductRecycler;
    SecCategoryAdapter priCateAdapter;
    secProductAdapter priProdAdapter;
    JSONArray jsonBrandCateg = null;
    JSONArray jsonBrandProduct = null;
    public String a;
    ArrayList<Product_Array> Product_Array_List;
    TextView text_checki;
    TextView grandTotal, item_count;
    float sum = 0;
    Product_Array product_array;
    ArrayList<String> list;
    /* Submit button */
    LinearLayout proceedCart;
    JSONObject person1;
    JSONObject PersonObjectArray;
    ArrayList<String> listV = new ArrayList<>();
    String JsonDatas;
    LinearLayout bottomLinear;
    Type listType;
    String ZeroPosId = "", ZeroPosNam = "", ZeroImg = "";
    List<Product> eventsArrayList;
    List<Product> Product_Modal;
    List<Product> Product_ModalSetAdapter;
    SecondaryProductViewModel mPrimaryProductViewModel;
    String productBarCode = "";
    EditText edt_serach;
    SecondaryProductDatabase primaryProductDatabase;
    List<SecondaryProduct> mPrimaryProduct = new ArrayList<>();
    SecondaryProductViewModel deleteViewModel;
    Common_Class mCommon_class;
    SearchView searchEdit;

    DBController dbController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_secondary_order_products);
        gson = new Gson();
        grandTotal = (TextView) findViewById(R.id.total_amount);
        item_count = (TextView) findViewById(R.id.item_count);
        mShared_common_pref = new Shared_Common_Pref(this);
        mCommon_class = new Common_Class(this);
        dbController = new DBController(this);
        primaryProductDatabase = Room.databaseBuilder(getApplicationContext(), SecondaryProductDatabase.class, "contact_datbase").fallbackToDestructiveMigration().build();
        searchEdit = findViewById(R.id.edt_serach_view);
        ImageView imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();

            }
        });

        proceedCart = (LinearLayout) findViewById(R.id.Linear_proceed_cart);
        bottomLinear = (LinearLayout) findViewById(R.id.bottom_linear);
        proceedCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String STR = grandTotal.getText().toString();
                Log.e("STRVALUE", STR);
                if (STR.equals("0") || STR.equals("0.0")) {
                    Toast.makeText(SecondaryOrderProducts.this, "Please choose to cart", Toast.LENGTH_SHORT).show();
                } else {
                    SaveDataValue();
                }
            }
        });

        text_checki = findViewById(R.id.text_checki);

        try {

            jsonBrandCateg = new JSONArray(dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_BRAND));
            jsonBrandProduct = new JSONArray(dbController.getResponseFromKey(DBController.SECONDARY_PRODUCT_DATA));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        priCategoryRecycler = findViewById(R.id.rec_checking);
        priCategoryRecycler.setHasFixedSize(true);
        priCategoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        priCategoryRecycler.setNestedScrollingEnabled(false);

        priProductRecycler = findViewById(R.id.rec_checking1);
        priProductRecycler.setHasFixedSize(true);
        priProductRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        priProductRecycler.setNestedScrollingEnabled(false);
        priProdAdapter = new secProductAdapter(this);
        priProductRecycler.setAdapter(priProdAdapter);
        mPrimaryProductViewModel = ViewModelProviders.of(this).get(SecondaryProductViewModel.class);
        mPrimaryProductViewModel.getAllData().observe(this, new Observer<List<SecondaryProduct>>() {
            @Override
            public void onChanged(List<SecondaryProduct> contacts) {
                priProdAdapter.setContact(contacts, "Nil");
            }
        });

        priCateAdapter = new SecCategoryAdapter(getApplicationContext(), jsonBrandCateg, jsonBrandProduct, new DMS.CheckingInterface() {

            @Override
            public void ProdcutDetails(String id, String name, String img) {

                productBarCode = id;

                loadFilteredTodos(productBarCode);

                text_checki.setVisibility(View.VISIBLE);
                text_checki.setText(name);
            }
        });
        priCategoryRecycler.setAdapter(priCateAdapter);

/*

        edt_serach = findViewById(R.id.edt_serach);
        edt_serach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                priProdAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        searchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.onActionViewExpanded();
            }
        });
        searchEdit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                priProdAdapter.getFilter().filter(newText);
                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFirstItem();
            }
        }, 2000);



    }
    private void loadFirstItem() {
        if(jsonBrandCateg.length()>0){
            try {
                JSONObject jsonObject = jsonBrandCateg.getJSONObject(0);
                String productId = jsonObject.getString("id");
                String productName = jsonObject.getString("name");
//                String productImage = jsonObject.getString("Cat_Image");
                // getProductId();
                productBarCode = productId;
                loadFilteredTodos(productBarCode);

                text_checki.setVisibility(View.VISIBLE);
                text_checki.setText(productName);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else
            Toast.makeText(this, "Empty Data", Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onResume() {
        super.onResume();

        if (productBarCode.equalsIgnoreCase("")) {
            mPrimaryProductViewModel = ViewModelProviders.of(this).get(SecondaryProductViewModel.class);
            mPrimaryProductViewModel.getAllData().observe(this, new Observer<List<SecondaryProduct>>() {
                @Override
                public void onChanged(List<SecondaryProduct> contacts) {
                    priProdAdapter.setContact(contacts, "Nil");
                    mPrimaryProduct = contacts;

                }
            });
        } else {

            loadFilteredTodos(productBarCode);
        }
        SecondaryProductViewModel contactViewModels = ViewModelProviders.of(this).get(SecondaryProductViewModel.class);
        contactViewModels.getFilterDatas().observe(SecondaryOrderProducts.this, new Observer<List<SecondaryProduct>>() {
            @Override
            public void onChanged(List<SecondaryProduct> contacts) {
                Log.e("TotalSize", new Gson().toJson(contacts.size()));
                item_count.setText("Items :" + new Gson().toJson(contacts.size()));

                float sum = 0;
                for (SecondaryProduct cars : contacts) {
                    sum = sum + Float.parseFloat(cars.getSubtotal());

                    Log.e("Total_value", String.valueOf(sum));
                }
                grandTotal.setText("" + sum);
                mShared_common_pref.save("GrandTotal", String.valueOf(sum));
                mShared_common_pref.save("SubTotal", String.valueOf("0.0"));

            }
        });

        try {
            if(priProdAdapter!=null)
                priProdAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveDataValue() {

        mShared_common_pref.save("GrandTotal", grandTotal.getText().toString());
        mShared_common_pref.save("SubTotal", String.valueOf("0.0"));

        Gson gson = new Gson();
        String jsonCars = gson.toJson(Product_Array_List);
        Log.e("Category_Data", jsonCars);
        Intent mIntent = new Intent(SecondaryOrderProducts.this, ViewSecCartActivity.class);
        mIntent.putExtra("list_as_string", jsonCars);
        mIntent.putExtra("GrandTotal", grandTotal.getText().toString());
        startActivity(mIntent);
    }


    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void showExitDialog() {
        AlertDialogBox.showDialog(SecondaryOrderProducts.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {
                if (mPrimaryProduct.size() != 0) {
                    mCommon_class.ProgressdialogShow(1, "");
                    deleteViewModel = ViewModelProviders.of(SecondaryOrderProducts.this).get(SecondaryProductViewModel.class);
                    deleteViewModel.getAllData().observe(SecondaryOrderProducts.this, new Observer<List<SecondaryProduct>>() {
                        @Override
                        public void onChanged(List<SecondaryProduct> contacts) {
                            deleteViewModel.delete(contacts);
//                                    startActivity(new Intent(SecondaryOrderProducts.this, DashBoardActivity.class));
                            finish();
                            Log.v("mPrimaryProduct_123456", String.valueOf(contacts.size()));
                        }
                    });

                }else{
                    finish();
                    startActivity(new Intent(SecondaryOrderProducts.this, DashBoardActivity.class));
                }
            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private void loadFilteredTodos(String category) {

        Log.v("AsynTASk_VALUE", category);
        new AsyncTask<String, Void, List<SecondaryProduct>>() {
            @Override
            protected List<SecondaryProduct> doInBackground(String... params) {
                return primaryProductDatabase.getAppDatabase().contactDao().fetchTodoListByCategory(params[0]);
            }

            @Override
            protected void onPostExecute(List<SecondaryProduct> todoList) {
                priProdAdapter.setContact(todoList,category);
                mPrimaryProduct = todoList;

                Log.v("AsynTASk_VALUE", new Gson().toJson(todoList));
            }
        }.execute(category);

    }


}

class SecCategoryAdapter extends RecyclerView.Adapter<SecCategoryAdapter.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
    JSONArray jsonArray1;
    ArrayList<HashMap<String, String>> contactList;
    DMS.CheckingInterface itemClick;
    String id = "", name = "", Image = "";
    String productImage = "";
    public SecCategoryAdapter(Context context, JSONArray jsonArray, JSONArray jsonArray1, DMS.CheckingInterface itemClick) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.jsonArray1 = jsonArray1;
        contactList = new ArrayList<>();
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public SecCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_checking_apater, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.ProdcutDetails(id, name, Image);
            }
        });
        view.setClickable(false);
        return new SecCategoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecCategoryAdapter.MyViewHolder holder, int position) {
        try {
            JSONObject jsFuel = null;
            jsFuel = (JSONObject) jsonArray.get(position);

            holder.mText.setText("" + jsFuel.getString("name"));
            String productId = jsFuel.getString("id");
            String productName = jsFuel.getString("name");
            productImage = "";
            if(jsFuel.has("Cat_Image"))
                productImage = jsFuel.getString("Cat_Image");

            holder.martl_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemClick.ProdcutDetails(productId, productName, productImage);
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
        TextView mText;
        MaterialCardView martl_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.text_checking);
            martl_view = itemView.findViewById(R.id.martl_view);
        }

    }

}

class secProductAdapter extends RecyclerView.Adapter<secProductAdapter.ContactHolder> implements Filterable {

    private List<SecondaryProduct> exampleList = new ArrayList<>();
    private List<SecondaryProduct> workinglist = new ArrayList<>();

    private Integer count = 0;
    private Context mCtx;
    private int ProductCount = 0;
    int schemCount;
    Float tax, taxAmt, taxTotal;
    Float valueTotal = 0f;
    Float subTotal = 0f;
    float edtCount = 0, plusCount = 0, minusCount = 0;
    Shared_Common_Pref shared_common_pref;
//    String Scheme = "";
    Float disAmt, disValue;
    float finalPrice,disFinalPrice;

    public secProductAdapter(Context mCtx) {
        this.mCtx = mCtx;
        shared_common_pref = new Shared_Common_Pref(mCtx);
    }


    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subProdcutChildName, subProdcutChildRate, productItem,
                productItemTotal, ProductTax, ProductDis, ProductTaxAmt, ProductDisAmt,ProductUnit;

        LinearLayout linPLus, linMinus;
        TextView mProductCount;
        float subTotal = 0;
//        PrimaryProductViewModel contactViewModel;
//        PrimaryProduct task;
//        String FilteredData = "";
        LinearLayout image_dropdown;
        CustomListViewDialog customDialog;

        LinearLayout ll_free_qty;
        LinearLayout ll_disc;
        TextView tv_free_qty;
//        LinearLayout ll_disc_reduction;
//        TextView tv_disc_amt;
//        TextView tv_disc_amt_total;
        TextView tv_final_total_amt;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            image_dropdown=itemView.findViewById(R.id.image_down);
            subProdcutChildName = itemView.findViewById(R.id.child_product_name);
            subProdcutChildRate = itemView.findViewById(R.id.child_product_price);
            productItem = itemView.findViewById(R.id.product_item_qty);
            productItemTotal = itemView.findViewById(R.id.product_item_total);
            ProductTax = itemView.findViewById(R.id.txt_tax);
            ProductDis = itemView.findViewById(R.id.txt_dis);
            ProductTaxAmt = itemView.findViewById(R.id.txt_tax_amt);
            ProductDisAmt = itemView.findViewById(R.id.txt_dis_amt);
            ProductUnit = itemView.findViewById(R.id.child_pro_unit);
            linPLus = itemView.findViewById(R.id.image_plus);
            mProductCount = itemView.findViewById(R.id.text_view_count);
            linMinus = itemView.findViewById(R.id.image_minus);
            ll_free_qty = itemView.findViewById(R.id.ll_free_qty);
            tv_free_qty = itemView.findViewById(R.id.tv_free_qty);
            ll_disc = itemView.findViewById(R.id.ll_disc);
//            ll_disc_reduction = itemView.findViewById(R.id.ll_disc_reduction);
//            tv_disc_amt = itemView.findViewById(R.id.tv_disc_amt);
//            tv_disc_amt_total = itemView.findViewById(R.id.tv_disc_amt_total);
            tv_final_total_amt = itemView.findViewById(R.id.tv_final_total_amt);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            SecondaryProduct task = workinglist.get(getAdapterPosition());
            shared_common_pref.save("task", new Gson().toJson(task));
            Intent intent = new Intent(mCtx, UpdateSecondaryProduct.class);
            mCtx.startActivity(intent);
        }
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_details, parent, false);
        return new ContactHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull secProductAdapter.ContactHolder holder, int position) {
        SecondaryProduct mContact = workinglist.get(position);
        holder.subProdcutChildName.setText(mContact.getPname());
        holder.subProdcutChildRate.setText("Rs:" + mContact.getProduct_Cat_Code());
        holder.ProductTax.setText(mContact.getTax_Value());
        holder.ProductDis.setText(mContact.getDiscount());
        holder.ProductTaxAmt.setText(mContact.getTax_amt());
    // holder.ProductDisAmt.setText(mContact.getDiscount());
        holder.ProductUnit.setText(mContact.getProduct_Sale_Unit());
        String orderid=mContact.getUID();
        holder.image_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SecondaryProduct task = workinglist.get(position);
                Log.v("PRODUCTttt_LIST", new Gson().toJson(task));
                shared_common_pref.save("taskdata", new Gson().toJson(task));
                Intent aa=new Intent(mCtx,PrimaryOrderList.class);
                aa.putExtra("orderid",orderid);
                aa.putExtra("pos",workinglist.get(position).getProduct_Sale_Unit());
                aa.putExtra("uomList",workinglist.get(position).getUOMList());
                //   aa.putExtra("productunit",workinglist.get(position).getProduct_Sale_Unit());
                mCtx.startActivity(aa);
            }
        });



        tax = Float.valueOf(mContact.getTax_Value());
        if (mContact.getTxtqty().equalsIgnoreCase("")) {
            holder.productItem.setText("0");
            holder.mProductCount.setText("0");
        } else {
            holder.productItem.setText(mContact.getTxtqty());
            holder.mProductCount.setText(mContact.getTxtqty());
            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());


            float pc = Float.parseFloat((mContact.getTxtqty()));
            if(selectedScheme!=null && !selectedScheme.getScheme().equalsIgnoreCase("")) {
                if (pc == schemCount || pc > schemCount) {
                    disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                    holder.ProductDis.setText(selectedScheme.getDiscountvalue());
                    finalPrice = (subTotal * disValue) / 100;
                    Log.v("finalPrice", String.valueOf(finalPrice));
                    holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                    subTotal = subTotal - finalPrice;
                    Log.v("finalPriceAfterDis", String.valueOf(subTotal));
                    holder.productItemTotal.setText("" + subTotal);
                } else {
                    holder.ProductDis.setText("0");
                    holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(0));
                }
            }
            if (tax != 0.0) {
                taxAmt = 100 + tax;
                Log.v("Discount", String.valueOf(taxAmt));
                subTotal = (taxAmt * subTotal) / 100;
                //  holder.productItemTotal.setText("" + subTotal);
                Log.e("Total_Distcount", "=" + subTotal);
                Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                valueTotal = subTotal - calculate;

                holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
            } else {
                holder.ProductTaxAmt.setText(mContact.getTax_amt());
            }
            holder.productItemTotal.setText("" + subTotal);
            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice),
                    mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                    mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit());

        }

        holder.linPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = count + 1;

                Log.v("countcount_Click", String.valueOf(count));


                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());
                ProductCount = ProductCount + 1;
//                Scheme = mContact.getSchemeProducts().getScheme();
                String proCnt = String.valueOf(ProductCount);

//                Log.v("countcount_Click", String.valueOf(proCnt));
//                Log.v("countcount_Click_SCHEME", Scheme);

                holder.mProductCount.setText("" + ProductCount);
                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                Log.v("PRODUCT_COUNT", String.valueOf(subTotal));
                holder.productItem.setText(holder.mProductCount.getText().toString());
                holder.productItemTotal.setText("" + subTotal);
                tax = Float.valueOf(mContact.getTax_Value());
                if (selectedScheme!=null && !selectedScheme.getScheme().equalsIgnoreCase("")) {
                    schemCount = Integer.parseInt(selectedScheme.getScheme());
                    if (ProductCount == schemCount || ProductCount > schemCount) {
                        disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                        holder.ProductDis.setText(selectedScheme.getDiscountvalue());
                        finalPrice = (subTotal * disValue) / 100;
                        Log.v("finalPrice", String.valueOf(finalPrice));
                        holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        subTotal = subTotal - finalPrice;
                        Log.v("finalPriceAfterDis", String.valueOf(subTotal));
                        holder.productItemTotal.setText("" + subTotal);

                    } else {
                        holder.ProductDis.setText("0");
                        holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(0));
                    }
                }



                if (tax != 0.0) {
                    taxAmt = 100 + tax;
                    Log.v("Discount", String.valueOf(taxAmt));
                    subTotal = (taxAmt * subTotal) / 100;
                    holder.productItemTotal.setText("" + subTotal);
                    Log.e("Total_Distcount", "=" + subTotal);
                    Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                    valueTotal = subTotal - calculate;
                    holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                }else{
                    holder.ProductTaxAmt.setText(mContact.getTax_amt());
                }

                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice),
                        mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                        mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit());
                updateSchemeData(mContact.getSchemeProducts(), ProductCount, mContact, holder, position, mContact);

            }
        });


        holder.linMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());

                ProductCount = ProductCount - 1;

//                Scheme = mContact.getSchemeProducts().getScheme();
                String proCnt = String.valueOf(ProductCount);
                Log.v("countcount_Click", String.valueOf(proCnt));
//                Log.v("countcount_Click_SCHEME", Scheme);

                if (ProductCount >= 0) {
                    //  mProductCount.setText("" + ProductCount);
                    holder.mProductCount.setText("" + ProductCount);
                    subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                    holder.productItem.setText(holder.mProductCount.getText().toString());
                    holder.productItemTotal.setText("" + subTotal);
                    tax = Float.valueOf(mContact.getTax_Value());
                    if (selectedScheme!=null && !selectedScheme.getScheme().equalsIgnoreCase("")) {
                        schemCount = Integer.parseInt(selectedScheme.getScheme());
                        if (ProductCount == schemCount || ProductCount > schemCount) {
                            disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                            holder.ProductDis.setText(selectedScheme.getDiscountvalue());
                            finalPrice = (subTotal * disValue) / 100;
                            Log.v("finalPrice", String.valueOf(finalPrice));
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            subTotal = subTotal - finalPrice;
                            Log.v("finalPriceAfterDis", String.valueOf(subTotal));
                            holder.productItemTotal.setText("" + subTotal);

                        } else {
                            holder.ProductDis.setText("0");
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(0));
                        }
                    }
                    if (tax != 0.0) {
                        taxAmt = 100 + tax;
                        Log.v("Discount", String.valueOf(taxAmt));
                        subTotal = (taxAmt * subTotal) / 100;
                        holder.productItemTotal.setText("" + subTotal);
                        Log.e("Total_Distcount", "=" + subTotal);
                        Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                        valueTotal = subTotal - calculate;
                        holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                    }else{
                        holder.ProductTaxAmt.setText(mContact.getTax_amt());
                    }

                    updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice),
                            mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                            mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit());
                    updateSchemeData(mContact.getSchemeProducts(), ProductCount, mContact, holder, position, mContact);

                } else {
                    ProductCount = 0;
                    holder.mProductCount.setText("" + 0);
                }

            }
        });
        updateSchemeData(mContact.getSchemeProducts(), mContact.getTxtqty().equals("") ? 0 : Integer.parseInt(mContact.getTxtqty()) , mContact, holder, position, mContact);

    }

    SecondaryProduct.SchemeProducts selectedScheme = null;

    private void updateSchemeData(List<SecondaryProduct.SchemeProducts> schemeProducts, int qty, SecondaryProduct
 mContact, secProductAdapter.ContactHolder holder, int position, SecondaryProduct contact) {
        int product_Sale_Unit_Cn_Qty = 1;
        if(mContact.getProduct_Sale_Unit_Cn_Qty()!=0)
            product_Sale_Unit_Cn_Qty= mContact.getProduct_Sale_Unit_Cn_Qty();
/*
        double value=
        subTotal = Double.parseDouble(mContact.getProduct_Cat_Code()) * mContact.getProduct_Sale_Unit_Cn_Qty();

        tax = Float.valueOf(mContact.getTax_Value());

        mContact.getTxtqty()
        valueTotal = subTotal*tax/100;
        subTotal = (taxAmt* subTotal) / 100;
        subTotal = subTotal * mContact.getProduct_Sale_Unit_Cn_Qty();


*/


        selectedScheme = null;
        int previousSchemeCount = 0;
        for(SecondaryProduct.SchemeProducts scheme : schemeProducts){
            if(!scheme.getScheme().equals("")) {
                int currentSchemeCount = Integer.parseInt(scheme.getScheme());
                if(previousSchemeCount <= currentSchemeCount &&  currentSchemeCount <= qty){
                    previousSchemeCount =currentSchemeCount;
                    selectedScheme = scheme;
                }
            }
        }

        String discountType = "";

        double discountValue = 0;
        double productAmt = 0;
        double schemeDisc = 0;
        if(selectedScheme != null){
            workinglist.get(position).setSelectedScheme(selectedScheme.getScheme());
            contact.setSelectedScheme(selectedScheme.getScheme());

            workinglist.get(position).setSelectedDisValue(selectedScheme.getDiscountvalue());
            contact.setSelectedDisValue(selectedScheme.getDiscountvalue());

            workinglist.get(position).setOff_Pro_code(selectedScheme.getProduct_Code());
            contact.setOff_Pro_code(selectedScheme.getProduct_Code());

            workinglist.get(position).setOff_Pro_name(selectedScheme.getProduct_Name());
            contact.setOff_Pro_name(selectedScheme.getProduct_Name());

            workinglist.get(position).setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            contact.setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            discountType= selectedScheme.getDiscount_Type();


            if(discountType.equals("Rs"))
                holder.ll_disc.setVisibility(View.GONE);
            else
                holder.ll_disc.setVisibility(View.VISIBLE);

            String packageType = selectedScheme.getPackage();

            double freeQty = 0;
            double packageCalc = 0;
            switch (packageType){
                case "N":
                    packageCalc = (int)(qty/Integer.parseInt(selectedScheme.getScheme()));
                    break;
                case "Y":
                    packageCalc = (double) (qty/Integer.parseInt(selectedScheme.getScheme()));
                    break;
//                default:

            }
            if(!selectedScheme.getFree().equals(""))
                freeQty = packageCalc * Integer.parseInt(selectedScheme.getFree());

            workinglist.get(position).setSelectedFree(String.valueOf(freeQty));
            contact.setSelectedFree(String.valueOf(freeQty));

            holder.tv_free_qty.setText(String.valueOf(freeQty));



            if(mContact.getProduct_Cat_Code()!=null && !mContact.getProduct_Cat_Code().equals(""))
                productAmt = Double.parseDouble(mContact.getProduct_Cat_Code());

            if(selectedScheme.getDiscountvalue()!=null && !selectedScheme.getDiscountvalue().equals(""))
                schemeDisc = Double.parseDouble(selectedScheme.getDiscountvalue());

            switch (discountType){
                case "%":
                    discountValue = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) * (schemeDisc/100);
                    holder.ll_disc.setVisibility(View.VISIBLE);
                    holder.ProductDis.setText(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
                    holder.ProductDisAmt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                    break;
                case "Rs":
                    discountValue = ((double) qty/Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
                    holder.ProductDisAmt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                    holder.ll_disc.setVisibility(View.GONE);
                    break;
//                default:
            }
//            discountValue = discountValue*product_Sale_Unit_Cn_Qty;


            if(discountValue>0){
                workinglist.get(position).setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
                contact.setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));

                workinglist.get(position).setDis_amt(Constants.roundTwoDecimals(discountValue));
                contact.setDis_amt(Constants.roundTwoDecimals(discountValue));
/*
                totalAmt = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) -discountValue;
                holder.ll_disc_reduction.setVisibility(View.VISIBLE);
                holder.tv_disc_amt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                holder.tv_disc_amt_total.setText(String.valueOf(Constants.roundTwoDecimals(totalAmt)));*/

            }

        }else {
            holder.ll_disc.setVisibility(View.VISIBLE);
            holder.tv_free_qty.setText("0");
        }
        double totalAmt = 0;
        double taxPercent = 0;
        double taxAmt = 0;

        try {
            totalAmt = Double.parseDouble(mContact.getProduct_Cat_Code()) * (qty *product_Sale_Unit_Cn_Qty);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            taxPercent = Double.parseDouble(mContact.getTax_Value());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        holder.subProdcutChildRate.setText("Rs:" + Constants.roundTwoDecimals(Double.parseDouble(mContact.getProduct_Cat_Code())));
        holder.productItem.setText(String.valueOf(qty *product_Sale_Unit_Cn_Qty));
        holder.productItemTotal.setText(Constants.roundTwoDecimals(totalAmt));


        holder.ProductTax.setText(String.valueOf(taxPercent));

        try {
            taxAmt =  (totalAmt- discountValue) * (taxPercent/100);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.ProductTaxAmt.setText(Constants.roundTwoDecimals(taxAmt));
        holder.tv_final_total_amt.setText(Constants.roundTwoDecimals(((totalAmt - discountValue) + taxAmt)));

    }



    private void updateTask(final SecondaryProduct task, String Qty, String subTotal, String taxAmt, String disAmt,
                            String selectedScheme, String selectedDisValue, String selectedFree,
                            String Off_Pro_code, String Off_Pro_name, String Off_Pro_Unit) {
        class UpdateTask extends AsyncTask<Void, Void, Void> {


            @Override
            protected Void doInBackground(Void... voids) {
                task.setQty(Qty);
                task.setTxtqty(Qty);
                task.setSubtotal(subTotal);
                SecondaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(task.getPID(),
                                Qty, Qty,
                                String.valueOf(subTotal),
                                task.getTax_Value(),
                                task.getDiscount(),
                                taxAmt,
                                disAmt,
                                selectedScheme,
                                selectedDisValue,
                                selectedFree,
                                Off_Pro_code,
                                Off_Pro_name,
                                Off_Pro_Unit);
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
    public int getItemCount() {
        return workinglist.size();
    }

    @Override
    public Filter getFilter() {
        return SearchFilter;
    }

    private Filter SearchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SecondaryProduct> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SecondaryProduct item : exampleList) {
                    if (item.getPname().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }else if(item.getProduct_Sale_Unit().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            workinglist.clear();
            workinglist.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public void setContact(List<SecondaryProduct> contacts, String code) {
        if (!code.equalsIgnoreCase("Nil")) {
            this.exampleList.clear();
            this.exampleList.addAll(contacts);
            this.workinglist = contacts;
            notifyDataSetChanged();
        }
    }

}