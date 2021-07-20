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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
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
//HorizontalScrollView priCategoryRecycler;
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
    Button forward,backward;
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
    int mFirst=0, mLast=0;
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
//        hsv=findViewById(R.id
//                .horizontal_scrollview);
        forward=findViewById(R.id.forward);
        backward=findViewById(R.id.backward);

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
                Log.v("STRVALUE", STR);
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
        priProductRecycler.setLayoutManager
                (new LinearLayoutManager(getApplicationContext()));
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
                    Log.v("mPrimaryProductOrder11",new Gson().toJson(contacts));

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

      //  priCategoryRecycler.set
       priCategoryRecycler.setAdapter(priCateAdapter);
       // priCategoryRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        priCategoryRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llm = (LinearLayoutManager)     priCategoryRecycler.getLayoutManager();
                mLast = llm.findLastCompletelyVisibleItemPosition();
                mFirst = llm.findFirstCompletelyVisibleItemPosition();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager llm = (LinearLayoutManager)     priCategoryRecycler.getLayoutManager();
              //  llm.scrollToPositionWithOffset(mLast + 1, List.length());
                llm.scrollToPositionWithOffset(mLast + 2, priCateAdapter.jsonArray.length());
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager llm = (LinearLayoutManager)     priCategoryRecycler.getLayoutManager();
             //   llm.scrollToPositionWithOffset(mFirst - 1, List.length());
                llm.scrollToPositionWithOffset(mFirst - 2,priCateAdapter.jsonArray.length());
            }
        });
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
                Log.v("TotalSize", new Gson().toJson(contacts.size()));
                item_count.setText("Items :" + new Gson().toJson(contacts.size()));
                grandTotal.setText("" + sum);
                float sum = 0;
                        float tax=0;
                for (PrimaryProduct cars : contacts) {
                    sum = sum + Float.parseFloat(cars.getSubtotal());
                   // sum = sum +Float.parseFloat( cars.getTax_Value())+ Float.parseFloat(cars.getSubtotal())+ Float.parseFloat(cars.getDis_amt())+;
                    ;
             //  Log.v("taxamttotal_valbefore", String.valueOf(tax));
                   Log.v("Total_valuebefore", String.valueOf(sum));
                }
                grandTotal.setText("" + sum);
                mShared_common_pref.save("GrandTotal", String.valueOf(sum));
            }
        });

//        sum= Float.parseFloat(getIntent().getStringExtra("GrandTotal"));
//        if(!"".equals(sum)|| !("0".equals(sum))||sum!=0){
//            grandTotal.setText("" + sum);
//        }

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
        Log.v("Category_Data", jsonCars);
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
           subTotal=Float.valueOf(new DecimalFormat("##.##").format(subTotal));
            Log.v("subtotalshow", subTotal.toString());
            //july11
            if ("".equals(mContact.getSchemeProducts().getDiscountvalue()) ||
                    ("0").equals(mContact.getSchemeProducts().getDiscountvalue())
            ) {
                holder.ProductDisAmt.setText("0");
                finalPrice = 0;
            }

            if ("".equals(mContact.getTax_Value()) ||
                    ("0").equals(mContact.getTax_Value())) {
                tax = Float.valueOf("0.0");

            } else {
                tax = Float.valueOf(mContact.getTax_Value());

            }
            //jul 11

            if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
                schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
                if (ProductCount == schemCount || ProductCount > schemCount) {
                    disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                    Log.v("+disvalueshow", String.valueOf(disValue));
                    if("".equals(mContact.getDis_amt())||
                            ("0").equals(mContact.getDis_amt())||("0.0").equals(mContact.getDis_amt())){

                   // if(disValue.equals("0")||disValue.equals("")){//july 14 commented

                        holder.ProductDisAmt.setText("0");
                        finalPrice=0;
                        holder.ProductDis.setText("0");
                        if(subTotal.equals("0")){
                            subTotal=Float.parseFloat("0.0");
                            holder.productItemTotal.setText("0.0");//new july10
                        }else {
                            holder.productItemTotal.setText("" + subTotal);//new july10
                        }
                    }else {
                        holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
                        finalPrice = (subTotal * disValue) / 100;
                        Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));
                        subTotal=subTotal-finalPrice;
                        subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                        Log.v("disamttotshow",String.valueOf(finalPrice));
                        holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        holder.productItemTotal.setText("" + subTotal);//new july 10
                    }
                    if (tax != 0.0) {
                        taxAmt = 100 + tax;
                        Log.v("+TaxAMTll", String.valueOf(taxAmt));
                        // subTotal=subTotal-finalPrice;
                        Log.v("disamt",String.valueOf(finalPrice));
                        Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                        Log.v("+Total_tcodellshow", "=" + mContact.getProduct_Cat_Code());
                        Log.v("+Total_productllshow", "=" +holder.mProductCount.getText().toString());
                        Log.v("+Subtotafterdisllshow", String.valueOf(subTotal));
                        //  Log.v("+calcull",calculate.toString());
                        // valueTotal = subTotal/Float.parseFloat(holder.mProductCount.getText().toString());//working code commented
                        valueTotal = subTotal*tax/100;
                        subTotal = (taxAmt* subTotal) / 100;
                        //   valueTotal = subTotal - calculate;//working code commented
                        holder.productItemTotal.setText("" + subTotal);//workig code commented
                        holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                        Log.v("+Totalshow", "=" + subTotal);
                        // holder.productItemTotal.setText("" + subTotal);
                        Log.v("+PRDTaxAMTshow", String.valueOf(valueTotal));
                        updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                String.valueOf(valueTotal), String.valueOf(finalPrice));
                        Log.v("ifupdatedval++show",   subTotal+""+String.valueOf(valueTotal)+String.valueOf(finalPrice));
                    } else {
                        if(mContact.getTax_Value().equals("")||mContact.getTax_Value().equals("0")){
                            holder.ProductTaxAmt.setText("0");
                            valueTotal=Float.parseFloat("0.0");//new code added jul87

                        }else {
                            holder.ProductTaxAmt.setText(mContact.getTax_amt());
                            valueTotal=Float.parseFloat(mContact.getTax_amt());//new code added jul 8
                        }

                        Log.e("cntup00show",subTotal+""+finalPrice+""+valueTotal);
                        updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                String.valueOf(valueTotal), String.valueOf(finalPrice));

                    }


                } else {
                    //july12
                    subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                    Log.v("22subtotalshow", subTotal.toString());
                    subTotal = Float.valueOf(new DecimalFormat("##.##").format( subTotal));
                    Log.v("22subtotalshow1", subTotal.toString());
                    disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                    Log.v("+0disvalueelseshow", String.valueOf(mContact.getDis_amt()));
                    if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0.0")||mContact.getDis_amt().equals("0")){
                   // if(("0").equals(disValue)||("").equals(disValue)){
                        holder.ProductDisAmt.setText("0");
                        finalPrice=0;
                        holder.ProductDis.setText("0");

                        if(subTotal.equals("0")){

                            subTotal=Float.parseFloat("0.0");
                            holder.productItemTotal.setText("0.0");//new july10
                        }else {
                            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());

                            holder.productItemTotal.setText("" + subTotal);//new july10
                        }
                    }else {
                        disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                        holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
                        finalPrice = (subTotal * disValue) / 100;
                        Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));

                        Log.v("disamttotshow",String.valueOf(finalPrice));
                        holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        subTotal=subTotal-finalPrice;
                        Log.v("22disamttotshow",String.valueOf(finalPrice));
                        subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                        holder.productItemTotal.setText("" + subTotal);//new july 10
                    }
                    //jul 12
//finalPrice=0;
//                    holder.ProductDis.setText("0");
//                   holder.ProductDisAmt.setText("0");

//new code tax start
                    if (tax != 0.0) {
                        taxAmt = 100 + tax;
                        Log.v("++++TaxAMT1", String.valueOf(taxAmt));
                        subTotal = (taxAmt * subTotal) / 100;
                        holder.productItemTotal.setText("" + subTotal);
                        Log.v("+++Total_Dist1show2", "=" + subTotal);
                        Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                        Log.v("+++Total_calode1show2", "=" + mContact.getProduct_Cat_Code());
                        Log.v("++++Total_product1show2", "=" + holder.mProductCount.getText().toString());
                       if(!("0").equals(disValue
                       )|| !(("").equals(disValue)))  {
                           valueTotal =( subTotal-calculate)+finalPrice;
                           Log.v("+PRDTaxAMTdisshow2", String.valueOf(finalPrice));
                           Log.v("+PRDTaxAMT1+disshow2", String.valueOf(valueTotal));
                       }else{
                           valueTotal = subTotal - calculate;
                       }
                        holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                        Log.v("+PRDTaxAMT1show2", String.valueOf(valueTotal));
                    } else {
                        valueTotal=Float.parseFloat(mContact.getTax_amt());
                        holder.ProductTaxAmt.setText(mContact.getTax_amt());
                        if(String.valueOf(finalPrice)!="0" ||String.valueOf(finalPrice)!="0.0")//JUL14
                        {
                            holder.productItemTotal.setText(subTotal.toString());
                        }else {
                            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                            Log.v("+++subtotalwtaXshow2", String.valueOf(subTotal));
                            subTotal = Float.valueOf(new DecimalFormat("##.##").format( subTotal));
                            holder.productItemTotal.setText(subTotal.toString());
                        }
                    }
                    //new code tax stop
                 // String  prdDisAmt=mContact.getDis_amt();
                    if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
                        updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
                        Log.v("updatedval++else11",   subTotal+""+String.valueOf(valueTotal)+ finalPrice);

                    }else{
                        updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal),String.valueOf(finalPrice));
                        Log.v("updatshow++else22",    subTotal+""+String.valueOf(valueTotal)+ finalPrice);
                    }
                }


            }








            else {//new start
                if (tax != 0.0) {
                    taxAmt = 100 + tax;
                    Log.v("q+TaxAMT1", String.valueOf(taxAmt));
                    subTotal = (taxAmt * subTotal) / 100;

                    // holder.productItemTotal.setText("" + subTotal);
                    Log.v("q+Total_Distcount1", "=" + subTotal);
                    Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                    Log.v("q+Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
                    Log.v("q+Total_product1", "=" + holder.mProductCount.getText().toString());
                    valueTotal = subTotal - calculate;
                    holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                    holder.productItemTotal.setText("" + subTotal);
                    Log.v("q+PRDTaxAMT1", String.valueOf(valueTotal));
                } else {
                    if(mContact.getTax_amt().equals("")||mContact.getTax_amt().equals("0")){
                        holder.ProductTaxAmt.setText("0");
                        valueTotal=Float.parseFloat("0.0");//new july 8
                    }else {
                        holder.ProductTaxAmt.setText(mContact.getTax_amt());
                        valueTotal=Float.parseFloat(mContact.getTax_amt());
                    }
                    holder.productItemTotal.setText("" + subTotal);
                    //  holder.ProductTaxAmt.setText(mContact.getTax_amt());
                }

                Log.v("5Taxamt",mContact.getTax_amt().toString());
                Log.v("55taxamt",valueTotal.toString());
                Log.v("55update",subTotal+""+valueTotal.toString()+finalPrice);
                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
            }//new stop
        }  //jul 11



        Log.v("countcount", String.valueOf(count));
        holder.linPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prdDisAmt;
                count = count + 1;
                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());
                ProductCount = ProductCount + 1;
                Scheme = mContact.getSchemeProducts().getScheme();
                String proCnt = String.valueOf(ProductCount);
                Log.v("countcount_Click", String.valueOf(proCnt));
                Log.v("countcount_Click_SCHEME", Scheme);
                holder.mProductCount.setText("" + ProductCount);
                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                Log.v("+++subtotal", String.valueOf(subTotal));
                subTotal = Float.valueOf(new DecimalFormat("##.##").format( subTotal));
                holder.productItem.setText(holder.mProductCount.getText().toString());

//                disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
//                Log.v("+++disvalue", String.valueOf(disValue));
                if("".equals(mContact.getSchemeProducts().getDiscountvalue())||
                        ("0").equals(mContact.getSchemeProducts().getDiscountvalue())) {
                    holder.ProductDisAmt.setText("0");
                    finalPrice=0;
                }
             //   holder.productItemTotal.setText("" + subTotal);//working code commented
                if("".equals(mContact.getTax_Value())||
                        ("0").equals(mContact.getTax_Value())){
                    tax= Float.valueOf("0.0");
                    Log.v("tax",tax.toString());

                }else {
                    tax = Float.valueOf(mContact.getTax_Value());
                    Log.v("tax",tax.toString());
                }
                //tax = Float.valueOf(mContact.getTax_Value());
                if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
                    schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
                    if (ProductCount == schemCount || ProductCount > schemCount) {

                        disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                        Log.v("+disvalue", String.valueOf(disValue));
                        if(disValue.equals("0")||disValue.equals("")){
                            holder.ProductDisAmt.setText("0");
                            finalPrice=0;

                            holder.ProductDis.setText("0");
                            if(subTotal.equals("0")){
                                holder.productItemTotal.setText("0.0");//new july10
                            }else {
                                holder.productItemTotal.setText("" + subTotal);//new july10
                            }
                        }else {
                            holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
                            finalPrice = (subTotal * disValue) / 100;
                            Log.v("+finalPrice*disvalue", String.valueOf(finalPrice));

                            subTotal=subTotal-finalPrice;
                            subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                            Log.v("disamttot",String.valueOf(finalPrice));
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            holder.productItemTotal.setText("" + subTotal);//new july 10
                        }
                        if (tax != 0.0) {
                            taxAmt = 100 + tax;
                            Log.v("+TaxAMTll", String.valueOf(taxAmt));
                           // subTotal=subTotal-finalPrice;
                            Log.v("disamt",String.valueOf(finalPrice));
                            Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());

                            Log.v("+Total_tcodell", "=" + mContact.getProduct_Cat_Code());
                            Log.v("+Total_productll", "=" +holder.mProductCount.getText().toString());
                            Log.v("+Subtotafterdisll", String.valueOf(subTotal));
                          //  Log.v("+calcull",calculate.toString());
                           // valueTotal = subTotal/Float.parseFloat(holder.mProductCount.getText().toString());//working code commented

                            valueTotal = subTotal*tax/100;
                            subTotal = (taxAmt* subTotal) / 100;

                         //   valueTotal = subTotal - calculate;//working code commented
                             holder.productItemTotal.setText("" + subTotal);//workig code commented
                            holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            Log.v("+Total", "=" + subTotal);
                           // holder.productItemTotal.setText("" + subTotal);
                            Log.v("+PRDTaxAMT", String.valueOf(valueTotal));
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                    String.valueOf(valueTotal), String.valueOf(finalPrice));
                            Log.v("ifupdatedval++",   subTotal+""+String.valueOf(valueTotal)+String.valueOf(finalPrice));
                        } else {

                            if(mContact.getTax_Value().equals("")||mContact.getTax_Value().equals("0")){
                                holder.ProductTaxAmt.setText("0");
                                valueTotal=Float.parseFloat("0.0");//new code added jul87

                            }else {
                                holder.ProductTaxAmt.setText(mContact.getTax_amt());
                                valueTotal=Float.parseFloat(mContact.getTax_amt());//new code added jul 8
                            }
                            //if no tax
//                            if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
//                                holder.ProductDisAmt.setText("0");
//                                finalPrice=Float.parseFloat("0.0");//new code added jly 8
//                            }else {
//                                holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
//                                finalPrice = (subTotal * disValue) / 100;
//                                Log.v("+finalPrice*disvalue11", String.valueOf(finalPrice));
//                                holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                                subTotal=subTotal-finalPrice;
//                                holder.productItemTotal.setText("" + subTotal);
                               // holder.ProductDisAmt.setText(mContact.getDis_amt());
                               // finalPrice=Float.parseFloat(mContact.getDis_amt());//new code added jul 8
                       //     }
                            //new code added-jul8
                            Log.e("cntup00",subTotal+""+finalPrice+""+valueTotal);
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                    String.valueOf(valueTotal), String.valueOf(finalPrice));
                         //   holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        }

                      Log.v("taxamt+", valueTotal.toString());
                     //   updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), mContact.getTax_amt(), mContact.getDis_amt());

                        Log.v("updatedval++",  mContact.getTax_amt()+finalPrice);
                    }

                    else {
                        holder.ProductDis.setText("0");
                        holder.ProductDisAmt.setText("0");

//new code tax start
                        if (tax != 0.0) {
                            taxAmt = 100 + tax;
                            Log.v("++++TaxAMT1", String.valueOf(taxAmt));
                            subTotal = (taxAmt * subTotal) / 100;
                            holder.productItemTotal.setText("" + subTotal);
                            Log.v("+++++Total_Distcount1", "=" + subTotal);
                            Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                            Log.v("+++++Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
                            Log.v("++++++Total_product1", "=" + holder.mProductCount.getText().toString());
                            valueTotal = subTotal - calculate;
                            holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            Log.v("+PRDTaxAMT1", String.valueOf(valueTotal));
                        } else {
                            holder.ProductTaxAmt.setText(mContact.getTax_amt());
                            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                            subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                            Log.v("+++subtotalwithoutaX", String.valueOf(subTotal));
                            holder.productItemTotal.setText(subTotal.toString());
                        }
                        //new code tax stop
                        prdDisAmt=mContact.getDis_amt();
                        if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), "0");
                            Log.v("updatedval++else11",   subTotal+""+String.valueOf(valueTotal)+ prdDisAmt);

                        }else{
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), mContact.getDis_amt());
                            Log.v("updatedval++else22",    String.valueOf(valueTotal)+ mContact.getDis_amt());
                    }
                    }

                  //  updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
                }








                else {//new start
                  //  disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                    //new code jul12
                     if("".equals(mContact.getSchemeProducts().getDiscountvalue())||
                            ("0").equals(mContact.getSchemeProducts().getDiscountvalue())
                    ) {

                        holder.ProductDisAmt.setText("0");
                        finalPrice=0;
                        holder.ProductDis.setText("0");

                        if(subTotal.equals("0")){

                            subTotal=Float.parseFloat("0.0");
                            holder.productItemTotal.setText("0.0");//new july10
                        }else {
                            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());

                            holder.productItemTotal.setText("" + subTotal);//new july10
                        }
                    }
                     else {
                        disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                        holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
                        finalPrice = (subTotal * disValue) / 100;
                        Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));
                        subTotal=subTotal-finalPrice;
                        subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                        Log.v("disamttotshow",String.valueOf(finalPrice));
                        holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        holder.productItemTotal.setText("" + subTotal);//new july 10
                    }

                    //new code jul12

                    if (tax != 0.0) {
                        taxAmt = 100 + tax;
                        Log.v("q+TaxAMT1", String.valueOf(taxAmt));

                        subTotal = (taxAmt * subTotal) / 100;
//                        holder.productItemTotal.setText("" + new DecimalFormat("##.#").format(subTotal+
//                                (tax*(holder.mProductCount.getText().toString() ))/100));
//                        Log.v("q+Totaxtotal+", "=" + new DecimalFormat("##.#").format(subTotal+(tax*subTotal)/100)
//                        );
                        //holder.productItemTotal.setText("" + (taxAmt * subTotal) / 100);
                       holder.productItemTotal.setText("" + subTotal);
                        Log.v("q+Total_Distcount1", "=" + subTotal);
                        Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                        Log.v("q+Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
                        Log.v("q+Total_product1", "=" + holder.mProductCount.getText().toString());
                        valueTotal = subTotal - calculate;
                        holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                        Log.v("q+PRDTaxAMT1", String.valueOf(valueTotal));
                    }
                    else {
                        if(mContact.getTax_amt().equals("")||mContact.getTax_amt().equals("0")){
                            holder.ProductTaxAmt.setText("0");
                            valueTotal=Float.parseFloat("0.0");//new july 8
                        }else {
                            holder.ProductTaxAmt.setText(mContact.getTax_amt());
                            valueTotal=Float.parseFloat(mContact.getTax_amt());
                        }
                        holder.productItemTotal.setText("" + ( subTotal));
                    }

                    Log.v("5Taxamt",mContact.getTax_amt().toString());
                    Log.v("55taxamt",valueTotal.toString());
                    Log.v("55update",subTotal+""+valueTotal.toString()+finalPrice);
                    updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
                }//new stop
              //  updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
            }
        });

        Log.v("PRODUCT_VALUE_TOTAL", String.valueOf(valueTotal));
        holder.linMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proCnt;
                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());
                ProductCount = ProductCount - 1;
                Scheme = mContact.getSchemeProducts().getScheme();
                proCnt = String.valueOf(ProductCount);
                Log.v("-countcount_Click", String.valueOf(proCnt));
                Log.v("-countcount_ClSCHEME", Scheme);
                if (ProductCount >= 0) {
                   String prdDisAmt;
                    holder.mProductCount.setText("" + ProductCount);
                    subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                    Log.v("--subtotal", String.valueOf(subTotal));
                    holder.productItem.setText(holder.mProductCount.getText().toString());

//                disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
//                Log.v("+++disvalue", String.valueOf(disValue));
                    if("".equals(mContact.getSchemeProducts().getDiscountvalue())||
                            ("0").equals(mContact.getSchemeProducts().getDiscountvalue())
                    ) {
                        holder.ProductDisAmt.setText("0");
                        finalPrice=0;
                    }
                    //   holder.productItemTotal.setText("" + subTotal);//working code commented
                    if("".equals(mContact.getTax_Value())||
                            ("0").equals(mContact.getTax_Value())){
                        tax= Float.valueOf("0.0");
                        Log.v("tax",tax.toString());

                    }else {
                        tax = Float.valueOf(mContact.getTax_Value());
                        Log.v("tax",tax.toString());
                    }
                //    holder.productItemTotal.setText("" + subTotal);//working code commented
                   // tax = Float.valueOf(mContact.getTax_Value());
                    if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
                      //  schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
                        //jul 12
                        Scheme = mContact.getSchemeProducts().getScheme();
                        proCnt = String.valueOf(ProductCount);
                        Log.v("--prdcnt",proCnt);
                        Log.v("-schemecnt",Scheme);
                        if (Integer.parseInt(proCnt)== Integer.parseInt(Scheme) || Integer.parseInt(proCnt)>Integer.parseInt(Scheme)) {
                            disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                            Log.v("-disvalue", String.valueOf(disValue));
                            if (disValue.equals("0") || disValue.equals("")) {
                                holder.ProductDisAmt.setText("0");
                                finalPrice = 0;

                                holder.ProductDis.setText("0");
                                if (subTotal.equals("0")) {
                                    holder.productItemTotal.setText("0.0");//new july10
                                } else {
                                    holder.productItemTotal.setText("" + subTotal);//new july10
                                }
                            } else {
                                holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
                                finalPrice = (subTotal * disValue) / 100;
                                Log.v("-finalPrice*disvalue", String.valueOf(finalPrice));
                                subTotal = subTotal - finalPrice;
                                subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                                Log.v("-disamttot", String.valueOf(finalPrice));
                                holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                                holder.productItemTotal.setText("" + subTotal);//new july 10
                            }
                            if (tax != 0.0) {
                                taxAmt = 100 + tax;
                                Log.v("-TaxAMTll", String.valueOf(taxAmt));
                                // subTotal=subTotal-finalPrice;
                                Log.v("-disamt", String.valueOf(finalPrice));
                                Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());

                                Log.v("-Total_tcodell", "=" + mContact.getProduct_Cat_Code());
                                Log.v("-Total_productll", "=" + holder.mProductCount.getText().toString());
                                Log.v("-Subtotafterdisll", String.valueOf(subTotal));
                                //  Log.v("+calcull",calculate.toString());
                                // valueTotal = subTotal/Float.parseFloat(holder.mProductCount.getText().toString());//working code commented
                                valueTotal = subTotal * tax / 100;
                                subTotal = (taxAmt * subTotal) / 100;
                                //   valueTotal = subTotal - calculate;//working code commented
                                holder.productItemTotal.setText("" + subTotal);//workig code commented
                                holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                                Log.v("-Total", "=" + subTotal);
                                // holder.productItemTotal.setText("" + subTotal);
                                Log.v("-PRDTaxAMT", String.valueOf(valueTotal));
                                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                        String.valueOf(valueTotal), String.valueOf(finalPrice));
                                Log.v("--ifupdatedval++", subTotal + "" + String.valueOf(valueTotal) + String.valueOf(finalPrice));
                            } else {


                                if (mContact.getTax_Value().equals("") || mContact.getTax_Value().equals("0")) {
                                    holder.ProductTaxAmt.setText("0");
                                    valueTotal = Float.parseFloat("0.0");//new code added jul87

                                } else {
                                    holder.ProductTaxAmt.setText(mContact.getTax_amt());
                                    valueTotal = Float.parseFloat(mContact.getTax_amt());//new code added jul 8
                                }

                                Log.e("---cntup00", subTotal + "" + finalPrice + "" + valueTotal);
                                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                        String.valueOf(valueTotal), String.valueOf(finalPrice));
                                //   holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            }

                            Log.v("--taxamt+", valueTotal.toString());
                            //   updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), mContact.getTax_amt(), mContact.getDis_amt());

                            Log.v("--updatedval++", mContact.getTax_amt() + finalPrice);
                        } else {
                            //jul13
                            //july12
Log.e("disamt-",mContact.getDis_amt());
//                            if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0.0")||mContact.getDis_amt().equals("0")){
//                                // if(("0").equals(disValue)||("").equals(disValue)){
//                                holder.ProductDisAmt.setText("0");
//                                finalPrice=0;
//                                holder.ProductDis.setText("0");
//
//                                if(subTotal.equals("0")){
//
//                                    subTotal=Float.parseFloat("0.0");
//                                    holder.productItemTotal.setText("0.0");//new july10
//                                }else {
//                                    subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//
//                                    holder.productItemTotal.setText("" + subTotal);//new july10
//                                }
//                            }
//                            else {
//                                disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
//
//                                Log.v("-0disvalueelse", String.valueOf(mContact.getDis_amt()));
//                                holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
//                                finalPrice = (subTotal * disValue) / 100;
//                                Log.v("-finalPrice*disvues", String.valueOf(finalPrice));
//                                subTotal=subTotal-finalPrice;
//                                Log.v("-disamttot",String.valueOf(finalPrice));
//
//                                holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                                holder.productItemTotal.setText("" + subTotal);//new july 10
//                            }
                            //jul 12
                            //jul 13
                            //below 3 lines added juy 13
                              finalPrice = 0;
                             mContact.setDis_amt("0.0");
                             mContact.setDiscount("0.0");
                             holder.ProductDis.setText(mContact.getDiscount());
                            holder.ProductDisAmt.setText(mContact.getDis_amt());

//new code tax start
                            if (tax != 0.0) {
                                taxAmt = 100 + tax;
                                Log.v("--+TaxAMT1", String.valueOf(taxAmt));
                                subTotal = (taxAmt * subTotal) / 100;
                                holder.productItemTotal.setText("" + subTotal);
                                Log.v("--+Total_Distcount1", "=" + subTotal);
                                Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                                Log.v("--++Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
                                Log.v("--++Total_product1", "=" + holder.mProductCount.getText().toString());
                                valueTotal = subTotal - calculate;
                                holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                                Log.v("--PRDTaxAMT1", String.valueOf(valueTotal));
                            } else {
                                holder.ProductTaxAmt.setText(mContact.getTax_amt());
                                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                                Log.v("--+subtotalwithoutaX", String.valueOf(subTotal));
                                subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                                holder.productItemTotal.setText(new DecimalFormat("##.##").format(subTotal).toString());
                            }

                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                    String.valueOf(valueTotal), String.valueOf(finalPrice));
                            Log.v("--updatedval++else11", subTotal + "" + String.valueOf(valueTotal) + finalPrice);

                            //new code tax stop
//                            prdDisAmt = mContact.getDis_amt();
//                            if (mContact.getDis_amt().equals("") || mContact.getDis_amt().equals("0")) {
//                                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), "0");
//                                Log.v("--updatedval++else11", subTotal + "" + String.valueOf(valueTotal) + prdDisAmt);
//
//                            } else {
//                                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), mContact.getDis_amt());
//                                Log.v("--updatedval++else22", String.valueOf(valueTotal) + mContact.getDis_amt());
//                            }
                        }

                    }
                    //
                    else {//new start
                        //  disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                        //new code jul12
                        if("".equals(mContact.getSchemeProducts().getDiscountvalue())||
                                ("0").equals(mContact.getSchemeProducts().getDiscountvalue())
                        ) {

                            holder.ProductDisAmt.setText("0");
                            finalPrice=0;
                            holder.ProductDis.setText("0");

                            if(subTotal.equals("0")){

                                subTotal=Float.parseFloat("0.0");
                                holder.productItemTotal.setText("0.0");//new july10
                            }else {
                                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());

                                holder.productItemTotal.setText("" + subTotal);//new july10
                            }
                        }
                        else {
                            disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
                            holder.ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
                            finalPrice = (subTotal * disValue) / 100;
                            Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));
                            subTotal=subTotal-finalPrice;
                            Log.v("disamttotshow",String.valueOf(finalPrice));
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            holder.productItemTotal.setText("" + subTotal);//new july 10
                        }

                        //new code jul12

                        if (tax != 0.0) {
                            taxAmt = 100 + tax;
                            Log.v("-q+TaxAMT1", String.valueOf(taxAmt));

                            subTotal = (taxAmt * subTotal) / 100;
//                        holder.productItemTotal.setText("" + new DecimalFormat("##.#").format(subTotal+
//                                (tax*(holder.mProductCount.getText().toString() ))/100));
//                        Log.v("q+Totaxtotal+", "=" + new DecimalFormat("##.#").format(subTotal+(tax*subTotal)/100)
//                        );
                            //holder.productItemTotal.setText("" + (taxAmt * subTotal) / 100);
                            holder.productItemTotal.setText("" + subTotal);
                            Log.v("-q+Total_Distcount1", "=" + subTotal);
                            Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                            Log.v("-q+Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
                            Log.v("--q+Total_product1", "=" + holder.mProductCount.getText().toString());
                            valueTotal = subTotal - calculate;
                            holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            Log.v("--q+PRDTaxAMT1", String.valueOf(valueTotal));
                        }
                        else {
                            if(mContact.getTax_amt().equals("")||mContact.getTax_amt().equals("0")){
                                holder.ProductTaxAmt.setText("0");
                                valueTotal=Float.parseFloat("0.0");//new july 8
                            }else {
                                holder.ProductTaxAmt.setText(mContact.getTax_amt());
                                valueTotal=Float.parseFloat(mContact.getTax_amt());
                            }
                            holder.productItemTotal.setText("" + ( subTotal));
                        }

                        Log.v("-5Taxamt",mContact.getTax_amt().toString());
                        Log.v("-55taxamt",valueTotal.toString());
                        Log.v("-55update",subTotal+""+valueTotal.toString()+finalPrice);
                        updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
                    }




                }

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