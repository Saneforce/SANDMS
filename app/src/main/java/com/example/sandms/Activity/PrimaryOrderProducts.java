package com.example.sandms.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.Product;
import com.example.sandms.Model.Product_Array;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.PrimaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
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
public class PrimaryOrderProducts extends AppCompatActivity {

    Gson gson;
    Shared_Common_Pref mShared_common_pref;
    RecyclerView priCategoryRecycler;
    RecyclerView priProductRecycler;
    CategoryAdapter priCateAdapter;
    ProductAdapter priProdAdapter;
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
    PrimaryProductViewModel mPrimaryProductViewModel;
    String productBarCode = "a", productBarCodes = "";
    SearchView searchEdit;
    EditText edt_serach;
    PrimaryProductDatabase primaryProductDatabase;
    PrimaryProductViewModel deleteViewModel;
    Common_Class mCommon_class;
    int product_count = 0;
    List<PrimaryProduct> mPrimaryProduct = new ArrayList<>();
    String sPrimaryProd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_order_products);

        Log.v("Primary_order", "OnCreate");

        gson = new Gson();
        grandTotal = (TextView) findViewById(R.id.total_amount);
        item_count = (TextView) findViewById(R.id.item_count);
        mShared_common_pref = new Shared_Common_Pref(this);
        searchEdit = findViewById(R.id.edt_serach_view);

        sPrimaryProd = mShared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Data);

        primaryProductDatabase = Room.databaseBuilder(getApplicationContext(), PrimaryProductDatabase.class, "contact_datbase").fallbackToDestructiveMigration().build();
        mCommon_class = new Common_Class(this);
        ImageView imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("mPrimaryProduct", String.valueOf(mPrimaryProduct.size()));

                AlertDialogBox.showDialog(PrimaryOrderProducts.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
                    @Override
                    public void PositiveMethod(DialogInterface dialog, int id) {

                        if (mPrimaryProduct.size() != 0) {
                            mCommon_class.ProgressdialogShow(1, "");
                            deleteViewModel = ViewModelProviders.of(PrimaryOrderProducts.this).get(PrimaryProductViewModel.class);
                            deleteViewModel.getAllData().observe(PrimaryOrderProducts.this, new Observer<List<PrimaryProduct>>() {
                                @Override
                                public void onChanged(List<PrimaryProduct> contacts) {
                                    deleteViewModel.delete(contacts);
                                    startActivity(new Intent(PrimaryOrderProducts.this, DashBoardActivity.class));
                                    finish();
                                    Log.v("mPrimaryProduct_123456", String.valueOf(contacts.size()));
                                }
                            });

                        }else{
                            startActivity(new Intent(PrimaryOrderProducts.this, DashBoardActivity.class));
                        }

                    }

                    @Override
                    public void NegativeMethod(DialogInterface dialog, int id) {
                    }
                });

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
                    Toast.makeText(PrimaryOrderProducts.this, "Please choose to cart", Toast.LENGTH_SHORT).show();
                } else {
                    SaveDataValue();
                }
            }
        });

        text_checki = findViewById(R.id.text_checki);

        try {

            jsonBrandCateg = new JSONArray(mShared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Brand));
            jsonBrandProduct = new JSONArray(mShared_common_pref.getvalue(Shared_Common_Pref.PriProduct_Data));
            Log.v("JSON_Band_Product", jsonBrandProduct.toString());
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
        priProdAdapter = new ProductAdapter(this, sPrimaryProd);
        priProductRecycler.setAdapter(priProdAdapter);

        if (productBarCode.equalsIgnoreCase("a")) {
            mPrimaryProductViewModel = ViewModelProviders.of(this).get(PrimaryProductViewModel.class);
            mPrimaryProductViewModel.getAllData().observe(this, new Observer<List<PrimaryProduct>>() {
                @Override
                public void onChanged(List<PrimaryProduct> contacts) {
                    priProdAdapter.setContact(contacts, "Nil");
                    Log.v("mPrimaryPr", String.valueOf(contacts.size()));
                    Log.v("mPrimaryProductOrder",new Gson().toJson(contacts));

                }
            });
        }
        Log.v("productBarCodeproduct", productBarCode);

        priCateAdapter = new CategoryAdapter(getApplicationContext(), jsonBrandCateg, jsonBrandProduct, new DMS.CheckingInterface() {

            @Override
            public void ProdcutDetails(String id, String name, String img) {
                searchEdit.setQuery("", false);

                productBarCode = id;
                loadFilteredTodos(productBarCode);

                text_checki.setVisibility(View.VISIBLE);
                text_checki.setText(name);
            }
        });
        priCategoryRecycler.setAdapter(priCateAdapter);

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


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Primary_order", "onResume");
        if (productBarCode.equalsIgnoreCase("")) {
            mPrimaryProductViewModel = ViewModelProviders.of(this).get(PrimaryProductViewModel.class);
            mPrimaryProductViewModel.getAllData().observe(this, new Observer<List<PrimaryProduct>>() {
                @Override
                public void onChanged(List<PrimaryProduct> contacts) {
                    priProdAdapter.setContact(contacts, "Nil");
                    mPrimaryProduct = contacts;
                    Log.v("mPrimaryProduct_123456", String.valueOf(contacts.size()));
                    Log.v("mPrimaryProductOrder",new Gson().toJson(contacts));

                }
            });
        } else {

            loadFilteredTodos(productBarCode);
        }
        PrimaryProductViewModel contactViewModels = ViewModelProviders.of(PrimaryOrderProducts.this).get(PrimaryProductViewModel.class);
        contactViewModels.getFilterDatas().observe(PrimaryOrderProducts.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {
                Log.e("TotalSize", new Gson().toJson(contacts.size()));
                item_count.setText("Items :" + new Gson().toJson(contacts.size()));

                float sum = 0;
                for (PrimaryProduct cars : contacts) {
                    sum = sum + Float.parseFloat(cars.getSubtotal());

                    Log.e("Total_value", String.valueOf(sum));
                }
                grandTotal.setText("" + sum);
                mShared_common_pref.save("GrandTotal", String.valueOf(sum));
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("Primary_order", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("Primary_order", "onStop");
    }

    public void SaveDataValue() {
        Gson gson = new Gson();
        String jsonCars = gson.toJson(Product_Array_List);
        Log.e("Category_Data", jsonCars);
        Intent mIntent = new Intent(PrimaryOrderProducts.this, ViewCartActivity.class);
        mIntent.putExtra("list_as_string", jsonCars);
        mIntent.putExtra("GrandTotal", grandTotal.getText().toString());
        startActivity(mIntent);
    }

    @Override
    public void onBackPressed() {
    }

    @SuppressLint("StaticFieldLeak")
    private void loadFilteredTodos(String category) {

        Log.v("AsynTASk_VALUE", category);
        new AsyncTask<String, Void, List<PrimaryProduct>>() {
            @Override
            protected List<PrimaryProduct> doInBackground(String... params) {
                return primaryProductDatabase.getAppDatabase().contactDao().fetchTodoListByCategory(params[0]);
            }

            @Override
            protected void onPostExecute(List<PrimaryProduct> todoList) {
                priProdAdapter.setContact(todoList, category);
                mPrimaryProduct = todoList;
                Log.v("calculate_value", new Gson().toJson(todoList.size()));
                Log.v("mPrimaryProduct_123456", String.valueOf(todoList.size()));
            }
        }.execute(category);
    }
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
    JSONArray jsonArray1;
    ArrayList<HashMap<String, String>> contactList;
    DMS.CheckingInterface itemClick;
    String id = "", name = "", Image = "";


    public CategoryAdapter(Context context, JSONArray jsonArray, JSONArray jsonArray1, DMS.CheckingInterface itemClick) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.jsonArray1 = jsonArray1;
        contactList = new ArrayList<>();
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_checking_apater, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.ProdcutDetails(id, name, Image);
            }
        });
        view.setClickable(false);
        return new CategoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        try {
            JSONObject jsFuel = null;
            jsFuel = (JSONObject) jsonArray.get(position);
            holder.mText.setText("" + jsFuel.getString("name"));
            String productId = jsFuel.getString("id");
            String productName = jsFuel.getString("name");
            String productImage = jsFuel.getString("Cat_Image");

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

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ContactHolder> implements Filterable {

    private List<PrimaryProduct> exampleList = new ArrayList<>();
    private List<PrimaryProduct> workinglist = new ArrayList<>();
    int schemCount = 0;
    private Integer count = 0;
    private Context mCtx;
    private int ProductCount = 0;

    Float tax, taxAmt, disAmt, disValue;
    float finalPrice, disFinalPrice;
    Float valueTotal = Float.valueOf(0);
    Float subTotal = Float.valueOf(0);
    Float dissubTotal = Float.valueOf(0);
    String Scheme = "";
    String sPrimaryProd;
    Shared_Common_Pref shared_common_pref;

    public ProductAdapter(Context mCtx, String sPrimaryProd) {
        this.mCtx = mCtx;
        this.sPrimaryProd = sPrimaryProd;
        shared_common_pref = new Shared_Common_Pref(mCtx);
    }

    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subProdcutChildName, subProdcutChildRate, productItem,
                productItemTotal, ProductTax, ProductDis, ProductTaxAmt,
                ProductDisAmt, ProductUnit;

        LinearLayout linPLus, linMinus, linInfo;
        TextView mProductCount;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            subProdcutChildName = itemView.findViewById(R.id.child_product_name);
            subProdcutChildRate = itemView.findViewById(R.id.child_product_price);
            productItem = itemView.findViewById(R.id.product_item_qty);
            productItemTotal = itemView.findViewById(R.id.product_item_total);
            ProductTax = itemView.findViewById(R.id.txt_tax);
            ProductDis = itemView.findViewById(R.id.txt_dis);
            ProductTaxAmt = itemView.findViewById(R.id.txt_tax_amt);
            ProductUnit = itemView.findViewById(R.id.child_pro_unit);
            ProductDisAmt = itemView.findViewById(R.id.txt_dis_amt);
            linPLus = itemView.findViewById(R.id.image_plus);
            linInfo = itemView.findViewById(R.id.item_info);
            mProductCount = itemView.findViewById(R.id.text_view_count);
            linMinus = itemView.findViewById(R.id.image_minus);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            PrimaryProduct task = workinglist.get(getAdapterPosition());
            Log.v("PRODUCT_LIST", new Gson().toJson(task));
            shared_common_pref.save("task", new Gson().toJson(task));
            Intent intent = new Intent(mCtx, UpdatePrimaryProduct.class);
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
    public void onBindViewHolder(@NonNull ProductAdapter.ContactHolder holder, int position) {
        PrimaryProduct mContact = workinglist.get(position);
        Log.v("PRODUCT_LIST_INNER", new Gson().toJson(mContact));
        holder.subProdcutChildName.setText(mContact.getPname());
        holder.subProdcutChildRate.setText("Rs:" + mContact.getProduct_Cat_Code());
        holder.ProductTax.setText(mContact.getTax_Value());
        holder.ProductTaxAmt.setText(mContact.getTax_amt());

        Log.v("DISCOUNT_RATE", mContact.getSchemeProducts().getScheme());
        holder.linInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "Cool Buddyy !!!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.ProductUnit.setText(mContact.getProduct_Sale_Unit());
        tax = Float.valueOf(mContact.getTax_Value());
        if (mContact.getTxtqty().equalsIgnoreCase("")) {
            holder.productItem.setText("0");
            holder.mProductCount.setText("0");
            holder.ProductDis.setText("0");
        } else {
            holder.productItem.setText(mContact.getTxtqty());
            holder.mProductCount.setText(mContact.getTxtqty());
            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());

            float pc = Float.parseFloat((mContact.getTxtqty()));
            if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
                schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
                if (pc == schemCount || pc > schemCount) {
                    disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                    holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
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
                // holder.productItemTotal.setText("" + subTotal);
                Log.e("Total_Distcount", "=" + subTotal);
                Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                valueTotal = subTotal - calculate;
                holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
            } else {
                holder.ProductTaxAmt.setText(mContact.getTax_amt());
            }
            holder.productItemTotal.setText("" + subTotal);
            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
        }

        Log.v("countcount", String.valueOf(count));
        holder.linPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count + 1;
                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());
                ProductCount = ProductCount + 1;
                Scheme = mContact.getSchemeProducts().getScheme();

                String proCnt = String.valueOf(ProductCount);
                Log.v("countcount_Click", String.valueOf(proCnt));
                Log.v("countcount_Click_SCHEME", Scheme);

                holder.mProductCount.setText("" + ProductCount);
                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                Log.v("PRODUCT_COUNT", String.valueOf(subTotal));
                holder.productItem.setText(holder.mProductCount.getText().toString());
                holder.productItemTotal.setText("" + subTotal);
                tax = Float.valueOf(mContact.getTax_Value());

                if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
                    schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
                    if (ProductCount == schemCount || ProductCount > schemCount) {
                        disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                        holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
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
                } else {
                    holder.ProductTaxAmt.setText(mContact.getTax_amt());
                }
                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
            }
        });

        Log.v("PRODUCT_VALUE_TOTAL", String.valueOf(valueTotal));

        holder.linMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());
                ProductCount = ProductCount - 1;

                Scheme = mContact.getSchemeProducts().getScheme();

                String proCnt = String.valueOf(ProductCount);
                Log.v("countcount_Click", String.valueOf(proCnt));
                Log.v("countcount_Click_SCHEME", Scheme);


                if (ProductCount >= 0) {
                    //  mProductCount.setText("" + ProductCount);
                    holder.mProductCount.setText("" + ProductCount);
                    subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                    holder.productItem.setText(holder.mProductCount.getText().toString());
                    holder.productItemTotal.setText("" + subTotal);
                    tax = Float.valueOf(mContact.getTax_Value());

                    if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
                        schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
                        if (ProductCount == schemCount || ProductCount > schemCount) {
                            disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                            holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
                            finalPrice = (subTotal * disValue) / 100;
                            Log.v("finalPrice", String.valueOf(finalPrice));
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            subTotal = subTotal - finalPrice;
                            Log.v("finalPriceAfterDis", String.valueOf(subTotal));
                            holder.productItemTotal.setText("" + subTotal);

                        }else {
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
                    } else {
                        holder.ProductTaxAmt.setText(mContact.getTax_amt());
                    }
                } else {
                    ProductCount = 0;
                    holder.mProductCount.setText("" + 0);
                }
                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
            }
        });
    }

    private void updateTask(final PrimaryProduct task, String Qty, String subTotal, String taxAmt, String disAmt) {
        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                task.setQty(Qty);
                task.setTxtqty(Qty);
                task.setSubtotal(subTotal);
                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(task.getPID(),
                                Qty, Qty,
                                String.valueOf(subTotal),
                                task.getTax_Value(),
                                task.getDiscount(),
                                taxAmt,
                                disAmt);
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
            List<PrimaryProduct> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (PrimaryProduct item : exampleList) {
                    if (item.getPname().toLowerCase().contains(filterPattern)) {
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

    public void setContact(List<PrimaryProduct> contacts, String code) {
        if (!code.equalsIgnoreCase("Nil")) {
            this.exampleList.clear();
            this.exampleList.addAll(contacts);
            this.workinglist = contacts;
            notifyDataSetChanged();
        }
    }

}