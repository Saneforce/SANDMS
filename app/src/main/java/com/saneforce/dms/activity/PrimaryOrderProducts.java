package com.saneforce.dms.activity;

import static io.realm.Realm.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.saneforce.dms.R;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.listener.PrimaryProducts;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.model.Product_Array;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.AlertDialogBox;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.CustomListViewDialog;
import com.saneforce.dms.utils.PrimaryProductDatabase;
import com.saneforce.dms.utils.PrimaryProductViewModel;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class PrimaryOrderProducts extends AppCompatActivity implements PrimaryProducts {//implements DMS.Master_Interface
    //HorizontalScrollView priCategoryRecycler;
    private static final String TAG = PrimaryOrderProducts.class.getSimpleName();
    public static final int ACTIVITY_REQUEST_CODE = 3;
    Gson gson;

    //   CustomListViewDialog customDialog;
    Shared_Common_Pref mShared_common_pref;
    RecyclerView priCategoryRecycler;
    RecyclerView priProductRecycler;
    CategoryAdapter priCateAdapter;
    ProductAdapter priProdAdapter;
    JSONArray jsonBrandCateg = null;
    JSONArray jsonBrandProduct = null;
    //    Common_Model mCommon_model_spinner;
    public JSONObject jsonProductuom;
    public String a;
    ArrayList<Product_Array> Product_Array_List;
    TextView text_checki;
    TextView grandTotal, item_count;
    //    float sum = 0;
    Button forward,backward;
    //    Product_Array product_array;
//    ArrayList<String> list;
    /* Submit button */
    LinearLayout proceedCart;
    //    JSONObject person1;
//    JSONObject PersonObjectArray;
//    ArrayList<String> listV = new ArrayList<>();
//    String JsonDatas;
    LinearLayout bottomLinear;
    //    Type listType;
//    String ZeroPosId = "", ZeroPosNam = "", ZeroImg = "";
//    List<Product> eventsArrayList;
//    List<Product> Product_Modal;
//    List<Product> Product_ModalSetAdapter;
//    PrimaryProductViewModel mPrimaryProductViewModel;
    String productBarCode = "a", productBarCodes = "";
    SearchView searchEdit;
    //    EditText edt_serach;
    PrimaryProductDatabase primaryProductDatabase;
    PrimaryProductViewModel deleteViewModel;
    Common_Class mCommon_class;
    //    int product_count = 0;
    List<PrimaryProduct> mPrimaryProduct = new ArrayList<>();
    String sPrimaryProd = "";
    int mFirst=0, mLast=0;
    List<Common_Model> productCodeOffileData = new ArrayList<>();

    DBController dbController;

    int orderType = 1;
    int PhoneOrderTypes = 4;

    int editMode = 0, categoryCode = -1;
    String orderVal = "0";
    String orderNo = "";
    boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_order_products);

        Log.v("Primary_order", "OnCreate");
        gson = new Gson();
        grandTotal = (TextView) findViewById(R.id.total_amount);
        item_count = (TextView) findViewById(R.id.item_count);
        mShared_common_pref = new Shared_Common_Pref(this);
        dbController = new DBController(this);
        searchEdit = findViewById(R.id.edt_serach_view);
        sPrimaryProd = dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_DATA);
        primaryProductDatabase = Room.databaseBuilder(getApplicationContext(), PrimaryProductDatabase.class, "contact_datbase").fallbackToDestructiveMigration().build();
        mCommon_class = new Common_Class(this);

        ImageView imagView = findViewById(R.id.toolbar_back);
        TextView toolHeader = findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        if(intent.hasExtra("order_type"))
            orderType = intent.getIntExtra("order_type", 1);

        if(intent.hasExtra("PhoneOrderTypes"))
            PhoneOrderTypes = intent.getIntExtra("PhoneOrderTypes", 4);

        if(intent.hasExtra("editMode"))
            editMode = intent.getIntExtra("editMode", 0);

        if(intent.hasExtra("categoryCode"))
            categoryCode = intent.getIntExtra("categoryCode", -1);

        if(intent.hasExtra("orderNo"))
            orderNo = intent.getStringExtra("orderNo");

        if(intent.hasExtra("orderVal")){
            orderVal = intent.getStringExtra("orderVal");
            grandTotal.setText(orderVal);
        }
        String title = "";

        if(editMode ==1)
            title = "Edit ";

        if(orderType ==2)
            title = title+"SECONDARY ORDER";
        else
            title = title+"PRIMARY ORDER";

        toolHeader.setText(title);

//        hsv=findViewById(R.id
//                .horizontal_scrollview);
        forward=findViewById(R.id.forward);
        backward=findViewById(R.id.backward);
        //  getProductId();
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("mPrimaryProduct", String.valueOf(mPrimaryProduct.size()));
                showExitDialog();

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
                    SaveDataValue(STR);
                }
            }
        });
        text_checki = findViewById(R.id.text_checki);
        try {
            jsonBrandCateg = new JSONArray(dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_BRAND));
            jsonBrandProduct = new JSONArray(dbController.getResponseFromKey(DBController.PRIMARY_PRODUCT_DATA));
            Log.v("JSON_Band_Product", jsonBrandProduct.toString());

            priCategoryRecycler = findViewById(R.id.rec_checking);
            priCategoryRecycler.setHasFixedSize(true);
            priCategoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            priCategoryRecycler.setNestedScrollingEnabled(false);
            priProductRecycler = findViewById(R.id.rec_checking1);
            priProductRecycler.setHasFixedSize(true);
            priProductRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            priProductRecycler.setNestedScrollingEnabled(false);
            priProdAdapter = new ProductAdapter(this, sPrimaryProd,jsonProductuom,productCodeOffileData, orderType);
            priProductRecycler.setAdapter(priProdAdapter);

            Log.v("productBarCodeproduct", productBarCode);

            priCateAdapter = new CategoryAdapter(getApplicationContext(),
                    jsonBrandCateg, jsonBrandProduct,jsonProductuom, new DMS.CheckingInterface1() {

                @Override
                public void ProdcutDetails(int position, String id, String name, String img) {
                    searchEdit.setQuery("", false);

                    // getProductId();
                    productBarCode = id;
                    loadFilteredTodos(productBarCode);


                    text_checki.setVisibility(View.VISIBLE);
                    text_checki.setText(name);

                    highlightPosition(position);
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


        } catch (JSONException e) {
            e.printStackTrace();
        }

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
//        edt_serach = findViewById(R.id.edt_serach);
       /* edt_serach.addTextChangedListener(new TextWatcher() {
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


        loadFirstItem();

       /* if (productBarCode.equalsIgnoreCase("a")) {
            mPrimaryProductViewModel = ViewModelProviders.of(this).get(PrimaryProductViewModel.class);
            mPrimaryProductViewModel.getAllData().observe(this, new Observer<List<PrimaryProduct>>() {
                @Override
                public void onChanged(List<PrimaryProduct> contacts) {
                    priProdAdapter.setContact(contacts, "Nil");
//                    Log.v("mPrimaryPr", String.valueOf(contacts.size()));
//                    Log.v("mPrimaryProductOrder11",new Gson().toJson(contacts));

                }
            });
        }*/



        PrimaryProductViewModel contactViewModels = ViewModelProviders.of(PrimaryOrderProducts.this).get(PrimaryProductViewModel.class);
        contactViewModels.getFilterDatas().observe(PrimaryOrderProducts.this, new Observer<List<PrimaryProduct>>() {
            @Override
            public void onChanged(List<PrimaryProduct> contacts) {
//                Log.v("TotalSize", new Gson().toJson(contacts.size()));
                item_count.setText("Items : " + new Gson().toJson(contacts.size()));
                /*float sum = 0;
//                        float tax=0;
                for (PrimaryProduct cars : contacts) {
                    try {
                        if(cars.getSubtotal()!=null && !cars.getSubtotal().equals(""))
                            sum = sum + Float.parseFloat(cars.getSubtotal());
                        // sum = sum +Float.parseFloat( cars.getTax_Value())+ Float.parseFloat(cars.getSubtotal())+ Float.parseFloat(cars.getDis_amt())+;
                        ;
                        //  Log.v("taxamttotal_valbefore", String.valueOf(tax));
//                        Log.v("Total_valuebefore", String.valueOf(sum));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
*/


                double itemTotal = 0;
                for (PrimaryProduct cars : contacts) {

                    double discountValue = 0;
                    double taxValue = 0;
                    double totalAmt = 0;
                    double productCode = 0;
                    double productQty = 0;
                    if(!cars.getSelectedDisValue().equals("") )
                        discountValue = Double.parseDouble(cars.getSelectedDisValue());
                    int unitQty = 1;
                    if(cars.getProduct_Sale_Unit_Cn_Qty()!=0)
                        unitQty = cars.getProduct_Sale_Unit_Cn_Qty();

                    if(cars.getProduct_Cat_Code()!=null && !cars.getProduct_Cat_Code().equals(""))
                        productCode = Double.parseDouble(cars.getProduct_Cat_Code());

                    if(cars.getQty()!=null && !cars.getQty().equals(""))
                        productQty = Double.parseDouble(cars.getQty());


                    totalAmt = productCode * (productQty * unitQty);
                    if(cars.getTax_amt()!=null && !cars.getTax_amt().equals(""))
                        taxValue = Double.parseDouble(cars.getTax_amt());

                    itemTotal = itemTotal + ((totalAmt- discountValue)+ taxValue);

//                    sum = sum + Float.parseFloat(cars.getSubtotal());
                    // sum = sum +Float.parseFloat( cars.getTax_Value())+ Float.parseFloat(cars.getSubtotal())+ Float.parseFloat(cars.getDis_amt())+;
                    ;
                    //  Log.v("taxamttotal_valbefore", String.valueOf(tax));
//                    Log.v("Total_foreviewcart", String.valueOf(itemTotal));
                }
                if(itemTotal<=0)
                    itemTotal = 0;

                grandTotal.setText("" + Constant.roundTwoDecimals(itemTotal));
                mShared_common_pref.save("GrandTotal", String.valueOf(itemTotal));
                mShared_common_pref.save("SubTotal", "0.0");
            }
        });

    }

    private void showExitDialog() {

        AlertDialogBox.showDialog(PrimaryOrderProducts.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {

                if (mPrimaryProduct.size() != 0) {
                    mCommon_class.ProgressdialogShow(2, "");
                    deleteViewModel = ViewModelProviders.of(PrimaryOrderProducts.this).get(PrimaryProductViewModel.class);
                    deleteViewModel.getAllData().observe(PrimaryOrderProducts.this, new Observer<List<PrimaryProduct>>() {
                        @Override
                        public void onChanged(List<PrimaryProduct> contacts) {
                            Log.v("mPrimaryProduct_123456", String.valueOf(contacts.size()));

                            deleteViewModel.delete(contacts);
//                                    startActivity(new Intent(PrimaryOrderProducts.this, DashBoardActivity.class));
                            completePreviousActivity(true);
                        }
                    });

                }else{
                    completePreviousActivity(true);
//                            startActivity(new Intent(PrimaryOrderProducts.this, DashBoardActivity.class));
                }

            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
    }

    private void loadFirstItem() {
        if(jsonBrandCateg!=null && jsonBrandCateg.length()>0){


            try {
                int position = 0;
                if(categoryCode != -1){
                    position = getCategoryPosition(categoryCode);

                    try {
                        LinearLayoutManager llm = (LinearLayoutManager) priCategoryRecycler.getLayoutManager();
                        llm.scrollToPositionWithOffset(position, priCateAdapter.jsonArray.length());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                JSONObject jsonObject = jsonBrandCateg.getJSONObject(position);
                String productId = jsonObject.getString("id");
                String productName = jsonObject.getString("name");
//                String productImage = jsonObject.getString("Cat_Image");
                // getProductId();
                productBarCode = productId;
                Log.d(TAG, "loadFirstItem: productBarCode "+ productBarCode);
                loadFilteredTodos(productBarCode);

                text_checki.setVisibility(View.VISIBLE);
                text_checki.setText(productName);
                highlightPosition(position);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, ""+ e.toString(), Toast.LENGTH_SHORT).show();

            }
        }else
            Toast.makeText(this, "Empty Data", Toast.LENGTH_SHORT).show();

    }

    private void highlightPosition(int position) {
        for(int i = 0; i< jsonBrandCateg.length(); i++){
            try {
                JSONObject jsonObject = jsonBrandCateg.getJSONObject(i);
                jsonObject.put("isSelected", false);
                jsonBrandCateg.put(i, jsonObject);
                priCateAdapter.notifyItemChanged(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        try {
            JSONObject jsonObject = jsonBrandCateg.getJSONObject(position);
            jsonObject.put("isSelected", true);
            jsonBrandCateg.put(position, jsonObject);
            priCateAdapter.notifyItemChanged(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private int getCategoryPosition(int categoryCode) {
        for(int i = 0; i< jsonBrandCateg.length(); i++){
            try {
                JSONObject jsonObject = jsonBrandCateg.getJSONObject(i);
                if(jsonObject.getInt("id") == categoryCode){
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return 0;

    }


    /*
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
                        Log.e("LoginResponse1",  jsonProductuom.toString());
                        String js=jsonProductuom.getString(
                                "success");
                        Log.e("LoginResponse133w",  js.toString());
                        JSONArray jss=jsonProductuom.getJSONArray(
                                "Data");
                        Log.e("LoginResponse133ws",  jss.toString());
    //                    String GS=new Gson().toJson(jss);
    //                  // JSONArray jsonArray = jsonProductuom.optJSONArray("Data");
    //                 Log.e("LoginResponse133",  GS.toString());
                        for (int i = 0; i < jss.length(); i++) {
                          JSONObject jsonObject = jss.optJSONObject(i);
                            String name=jsonObject.getString("name");
                            Log.v("LoginResponse1nn", name);
                            String productCode=jsonObject.getString("Product_Code");
                            Log.v("LoginResponse1nnq", productCode);
                            String conqty=jsonObject.getString("ConQty");
                            mCommon_model_spinner = new Common_Model(name, productCode,conqty, "flag");
                            productCodeOffileData.add(mCommon_model_spinner);
                            Log.v("daaa",productCodeOffileData.toString());
                            mShared_common_pref.save("PRODUCTCODE", productCodeOffileData.toString());
    //
    //                        customDialog = new CustomListViewDialog(PrimaryOrderProducts.this, productCodeOffileData, 12);
    //                        Window window = customDialog.getWindow();
    //                        window.setGravity(Gravity.CENTER);
    //                        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    //                        customDialog.show();

                        }

                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(PrimaryOrderProducts.this, "Invalid products", Toast.LENGTH_LONG).show();
                }
            });
        }
    */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Called");

        if(!isFirstTime)
            loadFilteredTodos(productBarCode);
        else
            isFirstTime = false;
  /*      Log.v("Primary_order", "onResume");
        if (productBarCode.equalsIgnoreCase("")) {
            mPrimaryProductViewModel = ViewModelProviders.of(this).get(PrimaryProductViewModel.class);
            mPrimaryProductViewModel.getAllData().observe(this, new Observer<List<PrimaryProduct>>() {
                @Override
                public void onChanged(List<PrimaryProduct> contacts) {
                    priProdAdapter.setContact(contacts, "Nil");
                    mPrimaryProduct = contacts;
//                    Log.v("mPrimaryProduct_123456", String.valueOf(contacts.size()));
//                    Log.v("mPrimaryProductOrder",new Gson().toJson(contacts));

                }
            });
        } else {

            loadFilteredTodos(productBarCode);
        }*/

//        sum= Float.parseFloat(getIntent().getStringExtra("GrandTotal"));
//        if(!"".equals(sum)|| !("0".equals(sum))||sum!=0){
//            grandTotal.setText("" + sum);
//        }

        try {
            if(priProdAdapter!=null)
                priProdAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            searchEdit.onActionViewCollapsed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Called ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if(requestCode == ACTIVITY_REQUEST_CODE) {

                boolean closeActivity = false;
                if(data!=null && data.hasExtra("closeActivity"))
                    closeActivity = data.getBooleanExtra("closeActivity", false);

                if(closeActivity){
                    if(orderType ==2){
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("closeActivity", closeActivity);
                        setResult(-1, resultIntent);
                    }

                    finish();
                }
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Called ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Called ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called ");
    }


    private void completePreviousActivity(boolean closeActivity) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("closeActivity", closeActivity);
        setResult(-1, resultIntent);
        finish();
    }

    public void SaveDataValue(String GrandTotal) {

        mShared_common_pref.save("GrandTotal", grandTotal.getText().toString());
        mShared_common_pref.save("SubTotal", String.valueOf("0.0"));
        Gson gson = new Gson();
        String jsonCars = gson.toJson(Product_Array_List);
//        Log.v("Category_Data", jsonCars);
        Intent mIntent = new Intent(PrimaryOrderProducts.this, ViewCartActivity.class);
        mIntent.putExtra("list_as_string", jsonCars);
        mIntent.putExtra("GrandTotal", grandTotal.getText().toString());
        mIntent.putExtra("order_type",orderType);
        mIntent.putExtra("PhoneOrderTypes",PhoneOrderTypes);
        mIntent.putExtra("orderNo",orderNo);
        startActivityForResult(mIntent, ACTIVITY_REQUEST_CODE);
//        startActivity(mIntent);
//        startActivityForResult(mIntent,66);
    }


    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadFilteredTodos(String category) {
        Log.d(TAG, "loadFilteredTodos: ");
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
                Log.d(TAG, "onPostExecute: ");

//                Log.v("calculate_value", new Gson().toJson(todoList.size()));
//                Log.v("mPrimaryProduct_123456", String.valueOf(todoList.size()));
            }
        }.execute(category);
    }

    @Override
    public void update(int value, int pos) {

    }



}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    Context context;
    JSONArray jsonArray;
    JSONArray jsonArray1;
    ArrayList<HashMap<String, String>> contactList;
    DMS.CheckingInterface1 itemClick;
    String id = "", name = "", Image = "";
    String productImage = "";
    JSONObject jsonProductuom;


    public CategoryAdapter(Context context, JSONArray jsonArray, JSONArray jsonArray1,JSONObject jsonProductuom, DMS.CheckingInterface1 itemClick) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.jsonArray1 = jsonArray1;
        this.jsonProductuom=jsonProductuom;
        contactList = new ArrayList<>();
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_checking_apater, parent, false);

/*        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.ProdcutDetails(id, name, Image);
            }
        });
        view.setClickable(false);*/
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
            productImage = "";
            if(jsFuel.has("Cat_Image"))
                productImage = jsFuel.getString("Cat_Image");

            if(jsFuel.has("isSelected") && jsFuel.getBoolean("isSelected")){
                holder.mText.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.martl_view.setCardBackgroundColor(context.getResources().getColor(R.color.textColor));
                holder.martl_view.setStrokeColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }else {
                holder.mText.setTextColor(context.getResources().getColor(R.color.textColor));
                holder.martl_view.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.martl_view.setStrokeColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }

            holder.martl_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.ProdcutDetails(position, productId, productName, productImage);
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

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ContactHolder>
        implements Filterable {
    private List<PrimaryProduct> exampleList = new ArrayList<>();
    private List<PrimaryProduct> workinglist = new ArrayList<>();
    int schemCount = 0;
    private Integer count = 0;
    private Context mCtx;
    private int ProductCount = 0;
    Float tax, taxAmt, disAmt, disValue;
    float finalPrice, disFinalPrice;
    Float valueTotal = 0f;
    Float subTotal = 0f;
//    Float dissubTotal = 0f;

    String sPrimaryProd;
    Shared_Common_Pref shared_common_pref;
    JSONObject jsonProductuom;
    List<Common_Model> productCodeOffileData;

//    Common_Model mCommon_model_spinner;
//    CustomListViewDialog customDialog;

    int orderType = 1;
    public ProductAdapter(Context mCtx, String sPrimaryProd,
                          JSONObject jsonProductuom,List<Common_Model>productCodeOffileData, int orderType) {
        this.mCtx = mCtx;
        this.sPrimaryProd = sPrimaryProd;
        this.jsonProductuom=jsonProductuom;
        this.productCodeOffileData=productCodeOffileData;
        shared_common_pref = new Shared_Common_Pref(mCtx);
        this.orderType = orderType;
    }





    class ContactHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,DMS.Master_Interface {


        //        Common_Model mCommon_model_spinner;
        TextView subProdcutChildName, subProdcutChildRate,
                productItemTotal, ProductTax, ProductDis, ProductTaxAmt,
                ProductDisAmt, ProductUnit;
        //        productItem,
        LinearLayout linPLus, linMinus, linInfo;
        TextView mProductCount;
        LinearLayout image_dropdown;
        CustomListViewDialog customDialog;

        LinearLayout ll_free_qty;
        LinearLayout ll_disc;
        TextView tv_free_qty;
        //        LinearLayout ll_disc_reduction;
//        TextView tv_disc_amt;
//        TextView tv_disc_amt_total;
        TextView tv_final_total_amt;
        TextView tv_free_unit;
        TextView tv_golden_scheme;
        //        TextView item_total_rupees;
        TextView tv_off_label;
        ImageButton ib_edit_price;
        TextView child_product_price_total;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            image_dropdown=itemView.findViewById(R.id.image_down);
            subProdcutChildName = itemView.findViewById(R.id.child_product_name);
            subProdcutChildRate = itemView.findViewById(R.id.child_product_price);
//            productItem = itemView.findViewById(R.id.product_item_qty); //
            productItemTotal = itemView.findViewById(R.id.product_item_total); //
            ProductTax = itemView.findViewById(R.id.txt_tax);
            ProductDis = itemView.findViewById(R.id.txt_dis);
            ProductTaxAmt = itemView.findViewById(R.id.txt_tax_amt);
            ProductUnit = itemView.findViewById(R.id.child_pro_unit);
            ProductDisAmt = itemView.findViewById(R.id.txt_dis_amt);
            linPLus = itemView.findViewById(R.id.image_plus);
            linInfo = itemView.findViewById(R.id.item_info);
            mProductCount = itemView.findViewById(R.id.text_view_count);
            linMinus = itemView.findViewById(R.id.image_minus);
            ll_free_qty = itemView.findViewById(R.id.ll_free_qty);
            tv_free_qty = itemView.findViewById(R.id.tv_free_qty);
            ll_disc = itemView.findViewById(R.id.ll_disc);
//            ll_disc_reduction = itemView.findViewById(R.id.ll_disc_reduction);
//            tv_disc_amt = itemView.findViewById(R.id.tv_disc_amt);
//            tv_disc_amt_total = itemView.findViewById(R.id.tv_disc_amt_total);
            tv_final_total_amt = itemView.findViewById(R.id.tv_final_total_amt);
            tv_free_unit = itemView.findViewById(R.id.tv_free_unit);
            tv_golden_scheme = itemView.findViewById(R.id.tv_golden_scheme);
            tv_off_label = itemView.findViewById(R.id.tv_off_label);
            ib_edit_price = itemView.findViewById(R.id.ib_edit_price);
            child_product_price_total = itemView.findViewById(R.id.child_product_price_total);
            itemView.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {
            PrimaryProduct task = workinglist.get(getAdapterPosition());
//            Log.v("PRODUCT_LIST", new Gson().toJson(task));
            shared_common_pref.save("task", new Gson().toJson(task));
            Intent intent = new Intent(mCtx, UpdatePrimaryProduct.class);
            intent.putExtra("order_type",orderType);
            mCtx.startActivity(intent);
        }

        public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
            customDialog.dismiss();
            if (type == 12) {
                ProductUnit.setText(myDataset.get(position).getName());
                //  orderTakenByFilter=myDataset.get(position).getName();
//                Log.e("order filter",myDataset.get(position).getName());
//                Log.e("order filter1",myDataset.get(position).getId());
//                Log.e("order filter2",myDataset.get(position).getAddress());
//                Log.e("order filter3",myDataset.get(position).getCheckouttime());
                //ViewDateReport(orderTakenByFilter);
                //  mArrayList.clear();

            }
        }
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_new, parent, false);
        return new ContactHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ContactHolder holder, int position) {

//        String productdata;
        PrimaryProduct mContact = workinglist.get(position);
//        String productCNQTY;

//        int product_Sale_Unit_Cn_Qty = 1;
//        if(mContact.getProduct_Sale_Unit_Cn_Qty()!=0)
//            product_Sale_Unit_Cn_Qty= mContact.getProduct_Sale_Unit_Cn_Qty();
//        Log.v("Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));

//        Log.v("PRODUCT_LIST_INNER", new Gson().toJson(mContact));
        holder.subProdcutChildName.setText(mContact.getPname());


        if(mContact.getGolden_scheme()!=null && mContact.getGolden_scheme().equals("1"))
            holder.tv_golden_scheme.setVisibility(View.VISIBLE);
        else
            holder.tv_golden_scheme.setVisibility(View.GONE);

        holder.child_product_price_total.setPaintFlags(holder.child_product_price_total.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if(orderType == 1)
            holder.ib_edit_price.setVisibility(View.GONE);
        else {

            holder.ib_edit_price.setVisibility(View.VISIBLE);

            holder.ib_edit_price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditPriceDialog(mContact, position, Double.parseDouble(holder.subProdcutChildRate.getText().toString().replaceAll("Rs:", "").trim()));
                }
            });

        }

        //productdata= shared_common_pref.getvalue("PRODUCTCODE");

       /* holder.linInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "Cool Buddyy !!!", Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.ProductUnit.setText(mContact.getProduct_Sale_Unit());
        String orderid=mContact.getUID();
        holder.image_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrimaryProduct task = workinglist.get(position);
//                Log.v("PRODUCTttt_LIST", new Gson().toJson(task));
                shared_common_pref.save("taskdata", new Gson().toJson(task));
                Intent aa=new Intent(mCtx,PrimaryOrderList.class);
                aa.putExtra("orderid",orderid);
                aa.putExtra("pos",workinglist.get(position).getProduct_Sale_Unit());
                aa.putExtra("uomList",workinglist.get(position).getUOMList());
                //   aa.putExtra("productunit",workinglist.get(position).getProduct_Sale_Unit());
                mCtx.startActivity(aa);
            }
        });

//        selectedScheme = mContact.getSelectedSchemeProduct();




        tax = Float.valueOf(mContact.getTax_Value());
        if (mContact.getTxtqty().equalsIgnoreCase("")) {
//            holder.productItem.setText("0");
            holder.mProductCount.setText("0");
            holder.ProductDis.setText("0");
        } else {
//            holder.productItem.setText(mContact.getTxtqty());
            holder.mProductCount.setText(mContact.getTxtqty());
            // subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) *
                    Float.parseFloat(mContact.getProduct_Cat_Code()
                    );
//            *Integer.parseInt(String.valueOf(mContact.getProduct_Sale_Unit_Cn_Qty()));
//            Log.v("1subtotalshow", String.valueOf(mContact.getProduct_Sale_Unit_Cn_Qty()));
            subTotal=Float.valueOf(new DecimalFormat("##.##").format(subTotal));
            Log.v("subtotalshow", subTotal.toString());
            //july11
            if (selectedScheme!=null && ("".equals(selectedScheme.getDiscountvalue()) ||
                    ("0").equals(selectedScheme.getDiscountvalue()))
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

        }  //jul 11




        updateSchemeData(mContact.getSchemeProducts(), mContact.getTxtqty().equals("") ? 0 : Integer.parseInt(mContact.getTxtqty()), mContact, holder, position);


//        Log.v("countcount", String.valueOf(count));
        holder.linPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int product_Sale_Unit_Cn_Qty=0;
                product_Sale_Unit_Cn_Qty=mContact.getProduct_Sale_Unit_Cn_Qty();
//                Log.v("+Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
//                String prdDisAmt;

                count = count + 1;
                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());
                ProductCount = ProductCount + 1;
//                Scheme = selectedScheme.getScheme();
                String proCnt = String.valueOf(ProductCount);
//                Log.v("countcount_Click", String.valueOf(proCnt));
//                Log.v("countcount_Click_SCHEME", Scheme);
                holder.mProductCount.setText("" + ProductCount);
                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) *
                        Float.parseFloat(mContact.getProduct_Cat_Code());
//                        *Integer.parseInt(String.valueOf(mContact.getProduct_Sale_Unit_Cn_Qty()));
//                Log.v("+++2subtotalshow", String.valueOf(mContact.getProduct_Sale_Unit_Cn_Qty()));

                Log.v("+++subtotal", String.valueOf(subTotal));
                subTotal = Float.valueOf(new DecimalFormat("##.##").format( subTotal));
//                holder.productItem.setText(holder.mProductCount.getText().toString());

//                disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
//                Log.v("+++disvalue", String.valueOf(disValue));
                if(selectedScheme!=null && ("".equals(selectedScheme.getDiscountvalue())||
                        ("0").equals(selectedScheme.getDiscountvalue()))) {
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
                if (selectedScheme!=null && !selectedScheme.getScheme().equals("")) {
                    schemCount = Integer.parseInt(selectedScheme.getScheme());
                    if (ProductCount == schemCount || ProductCount > schemCount) {

                        disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                        Log.v("+disvalue", String.valueOf(disValue));
                        if(disValue.equals("0")||disValue.equals("")){
                            holder.ProductDisAmt.setText("0");
                            finalPrice=0;

                            holder.ProductDis.setText("0");
                            if(subTotal.equals("0")){
                                holder.productItemTotal.setText("0.0");//new july10
                            }else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                            }
                        }else {
                            holder.ProductDis.setText(selectedScheme.getDiscountvalue());
                            finalPrice = (subTotal * disValue) / 100;
                            Log.v("+finalPrice*disvalue", String.valueOf(finalPrice));
                            subTotal=subTotal-finalPrice;
                            subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                            Log.v("disamttot",String.valueOf(finalPrice));
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            if(product_Sale_Unit_Cn_Qty!=0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("30Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                            }else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july 10
                            }
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

                            valueTotal = subTotal*tax/100;
                            subTotal = (taxAmt* subTotal) / 100;

                            //   valueTotal = subTotal - calculate;//working code commented
                            if(product_Sale_Unit_Cn_Qty!=0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("31Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                            }else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                            }//workig code commented
                            holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            Log.v("+Total", "=" + subTotal);

                            Log.v("+PRDTaxAMT", String.valueOf(valueTotal));
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                    String.valueOf(valueTotal), String.valueOf(finalPrice),
                                    mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                    mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
                            Log.v("ifupdatedval++",   subTotal+""+String.valueOf(valueTotal)+String.valueOf(finalPrice));
                        } else {

                            if(mContact.getTax_Value().equals("")||mContact.getTax_Value().equals("0")){
                                holder.ProductTaxAmt.setText("0");
                                valueTotal=Float.parseFloat("0.0");//new code added jul87

                            }else {
                                holder.ProductTaxAmt.setText(mContact.getTax_amt());
                                valueTotal=Float.parseFloat(mContact.getTax_amt());//new code added jul 8
                            }

                            Log.e("cntup00",subTotal+""+finalPrice+""+valueTotal);
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                    String.valueOf(valueTotal), String.valueOf(finalPrice),
                                    mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                    mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
                            //   holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        }
                        Log.v("taxamt+", valueTotal.toString());
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
                            if(product_Sale_Unit_Cn_Qty!=0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("34Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                            }else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                            }
                            Log.v("+++++Total_Distcount1", "=" + subTotal);
                            Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                            Log.v("+++++Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
                            Log.v("++++++Total_product1", "=" + holder.mProductCount.getText().toString());
                            valueTotal = subTotal - calculate;
                            holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            Log.v("+PRDTaxAMT1", String.valueOf(valueTotal));
                        } else {
                            holder.ProductTaxAmt.setText(mContact.getTax_amt());
                            subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) *
                                    Float.parseFloat(mContact.getProduct_Cat_Code())
                                    *product_Sale_Unit_Cn_Qty;
                            Log.v("++00subtotalshow", String.valueOf(mContact.getProduct_Sale_Unit_Cn_Qty()));

                            subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                            Log.v("+++subtotalwithoutaX", String.valueOf(subTotal));
                            if(product_Sale_Unit_Cn_Qty!=0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("35Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                            }else {
                                holder.productItemTotal.setText(Constant.roundTwoDecimals(subTotal));
                            }
                        }
                        //new code tax stop
//                        prdDisAmt=mContact.getDis_amt();
                        if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), "0",
                                    mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                    mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
//                            Log.v("updatedval++else11",   subTotal+""+String.valueOf(valueTotal)+ prdDisAmt);

                        }else{
                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), mContact.getDis_amt(),
                                    mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                    mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
                            Log.v("updatedval++else22",    String.valueOf(valueTotal)+ mContact.getDis_amt());
                        }
                    }

                }
                else {//new start
                    //  disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                    //new code jul12
                    if(selectedScheme!=null && selectedScheme.getDiscountvalue()!=null)
                        if( ("".equals(selectedScheme.getDiscountvalue())||
                                ("0").equals(selectedScheme.getDiscountvalue()))
                        ) {

                            holder.ProductDisAmt.setText("0");
                            finalPrice=0;
                            holder.ProductDis.setText("0");

                            if(subTotal.equals("0")){

                                subTotal=Float.parseFloat("0.0");
                                holder.productItemTotal.setText("0.0");//new july10
                            }else {
                                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                                if(product_Sale_Unit_Cn_Qty!=0) {
                                    subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                    Log.v("36Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                                }else {
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                                }
                            }
                        }
                        else {
                            disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                            holder.ProductDis.setText(selectedScheme.getDiscountvalue());
                            finalPrice = (subTotal * disValue) / 100;
                            Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));
                            subTotal=subTotal-finalPrice;
                            subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                            Log.v("disamttotshow",String.valueOf(finalPrice));
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            if(product_Sale_Unit_Cn_Qty!=0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("37Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                            }else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july 10
                            }
                        }//new code jul12
                    if (tax != 0.0) {
                        taxAmt = 100 + tax;
                        Log.v("q+TaxAMT1", String.valueOf(taxAmt));
                        subTotal = (taxAmt * subTotal) / 100;
//                        holder.productItemTotal.setText("" + new DecimalFormat("##.#").format(subTotal+
//                                (tax*(holder.mProductCount.getText().toString() ))/100));
//                        Log.v("q+Totaxtotal+", "=" + new DecimalFormat("##.#").format(subTotal+(tax*subTotal)/100)
//                        );
                        //holder.productItemTotal.setText("" + (taxAmt * subTotal) / 100);
                        holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
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
                        if(product_Sale_Unit_Cn_Qty!=0) {
                            subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                            Log.v("38Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                            holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                        }else {
                            holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                        }
                    }

                    Log.v("5Taxamt",mContact.getTax_amt().toString());
                    Log.v("55taxamt",valueTotal.toString());
                    Log.v("55update",subTotal+""+valueTotal.toString()+finalPrice);
                    updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice),
                            mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                            mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
                }
                updateSchemeData(mContact.getSchemeProducts(), ProductCount , mContact, holder, position);

            }
        });

//        Log.v("PRODUCT_VALUE_TOTAL", String.valueOf(valueTotal));
        holder.linMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String proCnt;
                ProductCount = Integer.parseInt(holder.mProductCount.getText().toString());
                ProductCount = ProductCount - 1;
                int product_Sale_Unit_Cn_Qty = 0;
                product_Sale_Unit_Cn_Qty = mContact.getProduct_Sale_Unit_Cn_Qty();
                Log.v("-saleunitcnqty", String.valueOf(product_Sale_Unit_Cn_Qty));
//                Scheme = selectedScheme.getScheme();
//                proCnt = String.valueOf(ProductCount);
//                Log.v("-countcount_Click", String.valueOf(proCnt));
//                Log.v("-countcount_ClSCHEME", Scheme);
                if (ProductCount >= 0) {
                    String prdDisAmt;
                    holder.mProductCount.setText("" + ProductCount);
                    subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) *
                            Float.parseFloat(mContact.getProduct_Cat_Code());
//                            *Integer.parseInt(String.valueOf(mContact.getProduct_Sale_Unit_Cn_Qty()));
//                    Log.v("--subtotalshow", String.valueOf(mContact.getProduct_Sale_Unit_Cn_Qty()));

                    Log.v("--subtotal", String.valueOf(subTotal));
//                    holder.productItem.setText(holder.mProductCount.getText().toString());

//                disValue = Float.valueOf(selectedScheme.getDiscountvalue());
//                Log.v("+++disvalue", String.valueOf(disValue));
                    if (selectedScheme==null || "".equals(selectedScheme.getDiscountvalue()) ||
                            ("0").equals(selectedScheme.getDiscountvalue())
                    ) {
                        holder.ProductDisAmt.setText("0");
                        finalPrice = 0;
                    }
                    //   holder.productItemTotal.setText("" + Constants.roundTwoDecimals(subTotal));//working code commented
                    if ("".equals(mContact.getTax_Value()) ||
                            ("0").equals(mContact.getTax_Value())) {
                        tax = Float.valueOf("0.0");
                        Log.v("tax", tax.toString());

                    } else {
                        tax = Float.valueOf(mContact.getTax_Value());
                        Log.v("tax", tax.toString());
                    }
                    //    holder.productItemTotal.setText("" + Constants.roundTwoDecimals(subTotal));//working code commented
                    // tax = Float.valueOf(mContact.getTax_Value());
                    if (selectedScheme!=null && !selectedScheme.getScheme().equals("")) {
                        //  schemCount = Integer.parseInt(selectedScheme.getScheme());
                        //jul 12
//                        Scheme = selectedScheme.getScheme();
                        String proCnt = String.valueOf(ProductCount);
                        Log.v("--prdcnt", proCnt);
//                        Log.v("-schemecnt", Scheme);
                        boolean isCountEqual = true;
                        try {
                            isCountEqual = Integer.parseInt(proCnt) == Integer.parseInt(selectedScheme.getScheme()) || Integer.parseInt(proCnt) > Integer.parseInt(selectedScheme.getScheme());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (isCountEqual) {
                            disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                            Log.v("-disvalue", String.valueOf(disValue));
                            if (disValue.equals("0") || disValue.equals("")) {
                                holder.ProductDisAmt.setText("0");
                                finalPrice = 0;

                                holder.ProductDis.setText("0");
                                if (subTotal.equals("0")) {
                                    holder.productItemTotal.setText("0.0");//new july10
                                } else {
                                    if (product_Sale_Unit_Cn_Qty != 0) {
                                        subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                        Log.v("40Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                        holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                                    } else {
                                        holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                                    }
                                }
                            } else {
                                holder.ProductDis.setText(selectedScheme.getDiscountvalue());
                                finalPrice = (subTotal * disValue) / 100;
                                Log.v("-finalPrice*disvalue", String.valueOf(finalPrice));
                                subTotal = subTotal - finalPrice;
                                subTotal = Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                                Log.v("-disamttot", String.valueOf(finalPrice));
                                holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                                if (product_Sale_Unit_Cn_Qty != 0) {
                                    subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                    Log.v("41Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                                } else {
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july 10
                                }
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
                                if (product_Sale_Unit_Cn_Qty != 0) {
                                    subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                    Log.v("42Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                                } else {
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//workig code commented
                                }
                                holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                                Log.v("-Total", "=" + subTotal);
                                // holder.productItemTotal.setText("" + subTotal);
                                Log.v("-PRDTaxAMT", String.valueOf(valueTotal));
                                updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                        String.valueOf(valueTotal), String.valueOf(finalPrice),
                                        mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                        mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
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
                                        String.valueOf(valueTotal), String.valueOf(finalPrice),
                                        mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                        mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
                                //   holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            }

                            Log.v("--taxamt+", valueTotal.toString());
                            //   updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), mContact.getTax_amt(), mContact.getDis_amt());

                            Log.v("--updatedval++", mContact.getTax_amt() + finalPrice);
                        } else {
                            //jul13
                            //july12
                            Log.e("disamt-", mContact.getDis_amt());

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
                                if (product_Sale_Unit_Cn_Qty != 0) {
                                    subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                    Log.v("44Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                                } else {
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                                }
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
                                subTotal = Float.valueOf(new DecimalFormat("##.##").format(subTotal));
                                if (product_Sale_Unit_Cn_Qty != 0) {
                                    subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                    Log.v("45Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                                } else {
                                    holder.productItemTotal.setText(new DecimalFormat("##.##").format(subTotal).toString());
                                }
                            }

                            updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                                    String.valueOf(valueTotal), String.valueOf(finalPrice),
                                    mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                    mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
                            Log.v("--updatedval++else11", subTotal + "" + String.valueOf(valueTotal) + finalPrice);
                        }

                    }
                    //
                    else {//new start
                        //  disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                        //new code jul12
                        if (selectedScheme == null || "".equals(selectedScheme.getDiscountvalue()) ||
                                ("0").equals(selectedScheme.getDiscountvalue())
                        ) {

                            holder.ProductDisAmt.setText("0");
                            finalPrice = 0;
                            holder.ProductDis.setText("0");

                            if (subTotal.equals("0")) {

                                subTotal = Float.parseFloat("0.0");
                                holder.productItemTotal.setText("0.0");//new july10
                            } else {
                                subTotal = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                                if (product_Sale_Unit_Cn_Qty != 0) {
                                    subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                    Log.v("46Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                                } else {
                                    holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july10
                                }
                            }
                        } else {
                            disValue = Float.valueOf(selectedScheme.getDiscountvalue());
                            holder.ProductDis.setText(selectedScheme.getDiscountvalue());
                            finalPrice = (subTotal * disValue) / 100;
                            Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));
                            subTotal = subTotal - finalPrice;
                            Log.v("disamttotshow", String.valueOf(finalPrice));
                            holder.ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                            if (product_Sale_Unit_Cn_Qty != 0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("47Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                            } else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));//new july 10
                            }
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
                            if (product_Sale_Unit_Cn_Qty != 0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("48Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                            } else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                            }
                            Log.v("-q+Total_Distcount1", "=" + subTotal);
                            Float calculate = Float.parseFloat(holder.mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
                            Log.v("-q+Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
                            Log.v("--q+Total_product1", "=" + holder.mProductCount.getText().toString());
                            valueTotal = subTotal - calculate;
                            holder.ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            Log.v("--q+PRDTaxAMT1", String.valueOf(valueTotal));
                        } else {
                            if (mContact.getTax_amt().equals("") || mContact.getTax_amt().equals("0")) {
                                holder.ProductTaxAmt.setText("0");
                                valueTotal = Float.parseFloat("0.0");//new july 8
                            } else {
                                holder.ProductTaxAmt.setText(mContact.getTax_amt());
                                valueTotal = Float.parseFloat(mContact.getTax_amt());
                            }
                            if (product_Sale_Unit_Cn_Qty != 0) {
                                subTotal = subTotal * product_Sale_Unit_Cn_Qty;
                                Log.v("49Sale_Unit_Cn_Qty", String.valueOf(product_Sale_Unit_Cn_Qty));
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                            } else {
                                holder.productItemTotal.setText("" + Constant.roundTwoDecimals(subTotal));
                            }
                        }

                        Log.v("-5Taxamt", mContact.getTax_amt().toString());
                        Log.v("-55taxamt", valueTotal.toString());
                        Log.v("-55update", subTotal + "" + valueTotal.toString() + finalPrice);
                        updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice),
                                mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree() ,
                                mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),mContact.getOff_disc_type(),mContact.getOff_free_unit());
                    }
                    updateSchemeData(mContact.getSchemeProducts(), ProductCount, mContact, holder, position);
                }

            }
        });




        /*if(mContact.getSchemeProducts().size()>0){
            holder.ll_free_qty.setVisibility(View.VISIBLE);
            holder.mProductCount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!editable.toString().equals("")){
                        int qty = Integer.parseInt(editable.toString());
//                    ll_free_qty
                        if(qty>0)
                            updateSchemeData(position, holder.tv_free_qty, mContact.getSchemeProducts(), qty, mContact, holder);

                    }
                }
            });
        }else {
            holder.ll_free_qty.setVisibility(View.GONE);
        }*/
    }
    double orgPrice = 0;
    double unitQty = 1;
    private void showEditPriceDialog(PrimaryProduct mContact, int position, double currentValue) {

        final Dialog dialog = new Dialog(mCtx);
        dialog.setContentView(R.layout.dialog_edit_price);


        EditText et_price = dialog.findViewById(R.id.et_price);
        TextView tv_new_order = dialog.findViewById(R.id.tv_new_order);


        if(mContact.getProduct_Cat_Code()!=null && !mContact.getProduct_Cat_Code().equals(""))
            orgPrice = Double.parseDouble(mContact.getProduct_Cat_Code());

        unitQty = mContact.getProduct_Sale_Unit_Cn_Qty();

        et_price.setText(String.valueOf(currentValue));
        tv_new_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                orgPrice = orgPrice * unitQty;
                double editedPrice = 0;
                if(!et_price.getText().toString().equals("")) {
                    editedPrice = Double.parseDouble(et_price.getText().toString());
                }

                if(orgPrice < editedPrice){
                    Toast.makeText(mCtx, "Please enter price less than the orignal price", Toast.LENGTH_SHORT).show();
                }else if(editedPrice> 0){
                    double discountValue = 0;
                    if(orgPrice > editedPrice) {
//                        discountType = "Rs";


                        discountValue = Constant.roundTwoDecimals1(orgPrice - editedPrice);

//                        schemeDis = Constant.roundTwoDecimals1(discountValue/originalPrice) * 100;
                        mContact.setEdited(true);
                        mContact.setEditedPrice(String.valueOf(editedPrice/unitQty));
                        mContact.setEditedDiscount(String.valueOf(discountValue /unitQty));
                    }else if(orgPrice == editedPrice) {
                        mContact.setEdited(false);
                        mContact.setEditedPrice("0");
                        mContact.setEditedDiscount("0");
                    }

                    dialog.dismiss();
                    updateScheme(mContact, position);

                }else {
                    Toast.makeText(mCtx, "Please enter valid unit price", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

    }

    PrimaryProduct.SchemeProducts selectedScheme = null;

    private void updateSchemeData(List<PrimaryProduct.SchemeProducts> schemeProducts, int qty, PrimaryProduct mContact, ContactHolder holder, int position) {
        int product_Sale_Unit_Cn_Qty = 1;
        if(mContact.getProduct_Sale_Unit_Cn_Qty()!=0)
            product_Sale_Unit_Cn_Qty= mContact.getProduct_Sale_Unit_Cn_Qty();
        int tempQty = qty * product_Sale_Unit_Cn_Qty;
/*
        double value=
        subTotal = Double.parseDouble(mContact.getProduct_Cat_Code()) * mContact.getProduct_Sale_Unit_Cn_Qty();

        tax = Float.valueOf(mContact.getTax_Value());

        mContact.getTxtqty()
        valueTotal = subTotal*tax/100;
        subTotal = (taxAmt* subTotal) / 100;
        subTotal = subTotal * mContact.getProduct_Sale_Unit_Cn_Qty();*/


        selectedScheme = null;
        int previousSchemeCount = 0;
        for(PrimaryProduct.SchemeProducts scheme : schemeProducts){
            if(!scheme.getScheme().equals("")) {
                int currentSchemeCount = Integer.parseInt(scheme.getScheme());
                if(previousSchemeCount <= currentSchemeCount &&  currentSchemeCount <= tempQty){
                    previousSchemeCount =currentSchemeCount;
                    selectedScheme = scheme;
                }
            }
        }

        String discountType = "%";

        double discountValue = 0;
        double unitDiscountValue = 0;
        double productAmt = 0;
        double schemeDisc = 0;
        String OffFreeUnit = "";

        String packageType = "";

        double itemPrice = 0;
        String displayedDis = "0";
        if(mContact.getProduct_Cat_Code()!=null && !mContact.getProduct_Cat_Code().equals(""))
            productAmt = Double.parseDouble(mContact.getProduct_Cat_Code());

        itemPrice = productAmt * product_Sale_Unit_Cn_Qty;

        if(selectedScheme != null){

            if(selectedScheme.getFree_Unit()!=null)
                OffFreeUnit = selectedScheme.getFree_Unit();

            workinglist.get(position).setSelectedScheme(selectedScheme.getScheme());
            mContact.setSelectedScheme(selectedScheme.getScheme());

            workinglist.get(position).setOff_Pro_code(selectedScheme.getProduct_Code());
            mContact.setOff_Pro_code(selectedScheme.getProduct_Code());

            workinglist.get(position).setOff_Pro_name(selectedScheme.getProduct_Name());
            mContact.setOff_Pro_name(selectedScheme.getProduct_Name());

            workinglist.get(position).setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            mContact.setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            if(!selectedScheme.getDiscount_Type().equals(""))
                discountType= selectedScheme.getDiscount_Type();



/*            if(discountType.equals("Rs"))
                holder.ll_disc.setVisibility(View.GONE);
            else
                holder.ll_disc.setVisibility(View.VISIBLE);*/

            packageType = selectedScheme.getPackage();

            double freeQty = 0;
            if(productAmt> 0){
                double packageCalc = (tempQty/Double.parseDouble(selectedScheme.getScheme()));

                if(packageType.equals("Y"))
                    packageCalc = (int)packageCalc;

/*            switch (packageType){
                case "N":
                    packageCalc = (int)(tempQty/Integer.parseInt(selectedScheme.getScheme()));
                    break;
                case "Y":
                    break;
//                default:

            }*/
                if(!selectedScheme.getFree().equals(""))
                    freeQty = packageCalc * Integer.parseInt(selectedScheme.getFree());
//            freeQty = (int) freeQty;
            }

            workinglist.get(position).setSelectedFree(String.valueOf((int) freeQty));
            mContact.setSelectedFree(String.valueOf((int) freeQty));

            holder.tv_free_qty.setText(String.valueOf((int) freeQty));

        }else {

//            holder.ll_disc.setVisibility(View.VISIBLE);
            holder.tv_free_qty.setText("0");

            workinglist.get(position).setDis_amt(Constant.roundTwoDecimals(discountValue));
            mContact.setDis_amt(Constant.roundTwoDecimals(discountValue));

            workinglist.get(position).setSelectedDisValue(Constant.roundTwoDecimals(discountValue));
            mContact.setSelectedDisValue(Constant.roundTwoDecimals(discountValue));


            workinglist.get(position).setSelectedScheme("");
            mContact.setSelectedScheme("");

            workinglist.get(position).setOff_Pro_code("");
            mContact.setOff_Pro_code("");

            workinglist.get(position).setOff_Pro_name("");
            mContact.setOff_Pro_name("");

            workinglist.get(position).setOff_Pro_Unit("");
            mContact.setOff_Pro_Unit("");

            workinglist.get(position).setSelectedFree("0");
            mContact.setSelectedFree("0");
        }


        if(!mContact.isEdited()) {
            if (selectedScheme != null){

                if (selectedScheme.getDiscountvalue() != null && !selectedScheme.getDiscountvalue().equals(""))
                    schemeDisc = Double.parseDouble(selectedScheme.getDiscountvalue());

                switch (discountType) {
                    case "%":
                        discountValue = (productAmt * tempQty) * (schemeDisc / 100);
                        unitDiscountValue = (productAmt * product_Sale_Unit_Cn_Qty) * (schemeDisc / 100);
//                    holder.ll_disc.setVisibility(View.VISIBLE);
                        holder.ProductDis.setText(String.valueOf(Constant.roundTwoDecimals(schemeDisc)));
//                        displayedDis = selectedScheme.getDiscountvalue();
                        break;
                    case "Rs":
                        if (productAmt != 0) {
                            if (!packageType.equals("Y")) {
                                discountValue = ((double) tempQty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
                                unitDiscountValue = ((double) product_Sale_Unit_Cn_Qty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;

                            } else {
                                int schInt = Integer.parseInt(selectedScheme.getScheme());
                                discountValue = (int)(tempQty / schInt) * schemeDisc;
                                unitDiscountValue = (int)(product_Sale_Unit_Cn_Qty / schInt) * schemeDisc;

                            }
//                        holder.ll_disc.setVisibility(View.GONE);
//                            displayedDis = String.valueOf(unitDiscountValue);
                            break;
                        }
                    default:
                        holder.ProductDis.setText("0");
                        discountValue = 0;
                        unitDiscountValue = Double.parseDouble(Constant.roundTwoDecimals(Double.parseDouble(mContact.getProduct_Cat_Code()) * product_Sale_Unit_Cn_Qty));
                        displayedDis = "0";
                }
            }
            unitDiscountValue = (Double.parseDouble(mContact.getProduct_Cat_Code())*product_Sale_Unit_Cn_Qty)-unitDiscountValue;

        }else {
            discountType = "Rs";
            double editedDis = 0;
            if(mContact.getEditedDiscount()!=null && !mContact.getEditedDiscount().equals(""))
                editedDis = Double.parseDouble(mContact.getEditedDiscount());

            discountValue =editedDis * tempQty;

            if(mContact.getEditedPrice()!=null && !mContact.getEditedPrice().equals(""))
                unitDiscountValue = Double.parseDouble(mContact.getEditedPrice()) * product_Sale_Unit_Cn_Qty;


            schemeDisc = Constant.roundTwoDecimals1(((editedDis * product_Sale_Unit_Cn_Qty)/itemPrice) * 100);
//                    holder.ll_disc.setVisibility(View.VISIBLE);
//            holder.ProductDis.setText(String.valueOf(Constant.roundTwoDecimals(schemeDisc)));
//            displayedDis = Constant.roundTwoDecimals(itemPrice - (Double.parseDouble(mContact.getEditedPrice()) * product_Sale_Unit_Cn_Qty));
        }

        if(!mContact.isEdited() && discountType.equals("%"))
            holder.tv_off_label.setText(discountType+" off");
        else
            holder.tv_off_label.setText(" off");

//            discountValue = discountValue*product_Sale_Unit_Cn_Qty;


        if(!discountType.equals("") && discountValue>0){
            workinglist.get(position).setDiscount(String.valueOf(schemeDisc));
            mContact.setDiscount(String.valueOf(schemeDisc));

            workinglist.get(position).setDis_amt(Constant.roundTwoDecimals(discountValue));
            mContact.setDis_amt(Constant.roundTwoDecimals(discountValue));

            workinglist.get(position).setSelectedDisValue(Constant.roundTwoDecimals(discountValue));
            mContact.setSelectedDisValue(Constant.roundTwoDecimals(discountValue));
/*
                totalAmt = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) -discountValue;
                holder.ll_disc_reduction.setVisibility(View.VISIBLE);
                holder.tv_disc_amt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                holder.tv_disc_amt_total.setText(String.valueOf(Constants.roundTwoDecimals(totalAmt)));*/

        }else {
            workinglist.get(position).setDiscount(String.valueOf(schemeDisc));
            mContact.setDiscount(String.valueOf(schemeDisc));

            workinglist.get(position).setDis_amt("0");
            workinglist.get(position).setSelectedDisValue("0");
            mContact.setDis_amt("0");
            mContact.setSelectedDisValue("0");
            holder.ProductDis.setText("0");

        }

        holder.tv_free_unit.setText(OffFreeUnit);
        mContact.setOff_free_unit(OffFreeUnit);

        double totalAmt = 0;
        double taxPercent = 0;
        double taxAmt = 0;

        try {
            totalAmt = Double.parseDouble(mContact.getProduct_Cat_Code()) * (qty * product_Sale_Unit_Cn_Qty);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            taxPercent = Double.parseDouble(mContact.getTax_Value());

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        holder.subProdcutChildRate.setText("Rs: " + Constant.roundTwoDecimals(itemPrice));
        holder.child_product_price_total.setText("Rs: " + Constant.roundTwoDecimals(productAmt *tempQty));

        if(!discountType.equals("%")){
            displayedDis = mCtx.getResources().getString(R.string.rupee_symbol) +" "+ Constant.roundTwoDecimals(discountValue);
        }else
            displayedDis = Constant.roundTwoDecimals(schemeDisc);

        holder.ProductDis.setText(displayedDis);

//        holder.productItem.setText(String.valueOf(qty));
//        holder.productItemTotal.setText(Constants.roundTwoDecimals(totalAmt));
       /* double finalDiscountAmt = unitDiscountValue;

        if(unitDiscountValue ==0){
            finalDiscountAmt = itemPrice;
        }
        if(finalDiscountAmt<=0)
            finalDiscountAmt = 0;*/
        holder.ProductDisAmt.setText(Constant.roundTwoDecimals((productAmt *tempQty) - discountValue));

        holder.ProductTax.setText(String.valueOf(taxPercent));

        try {
            taxAmt =  (totalAmt- discountValue) * (taxPercent/100);
            if(taxAmt<=0)
                taxAmt = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.ProductTaxAmt.setText(Constant.roundTwoDecimals(taxAmt));
        double finalTotal = Constant.roundTwoDecimals1(((totalAmt - discountValue) + taxAmt));
        if(finalTotal<=0)
            finalTotal = 0;

        holder.tv_final_total_amt.setText(String.valueOf(finalTotal));
        subTotal = (float) totalAmt;
        valueTotal = (float) taxAmt;
        finalPrice = (float) ((totalAmt - discountValue) + taxAmt);
        updateTask(mContact, holder.mProductCount.getText().toString(), String.valueOf(subTotal),
                String.valueOf(valueTotal), String.valueOf(finalPrice),
                mContact.getSelectedScheme(),mContact.getSelectedDisValue(),mContact.getSelectedFree(),
                mContact.getOff_Pro_code() ,mContact.getOff_Pro_name() ,mContact.getOff_Pro_Unit(),discountType, OffFreeUnit);
    }


    private void updateTask(final PrimaryProduct task, String Qty, String subTotal, String taxAmt, String finalTotal,
                            String selectedScheme, String selectedDisValue, String selectedFree,
                            String Off_Pro_code, String Off_Pro_name, String Off_Pro_Unit, String Off_disc_type, String Off_free_unit) {
        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                task.setQty(Qty);
                task.setTxtqty(Qty);
                task.setSubtotal(finalTotal);
                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(task.getPID(),
                                Qty, Qty,
                                String.valueOf(finalTotal),
                                task.getTax_Value(),
                                task.getDiscount(),
                                taxAmt,
                                selectedDisValue,
                                selectedScheme,
                                selectedDisValue,
                                selectedFree,
                                Off_Pro_code,
                                Off_Pro_name,
                                Off_Pro_Unit,
                                Off_disc_type,
                                Off_free_unit);
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


    /*public void getProductIds(TextView  productunit) {
        //   this.sf_Code=sf_Code;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProductuom(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code));
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.v("DMS_RESPONSE", response.body().toString());

                try {
                    jsonProductuom = new JSONObject(response.body().toString());
//                    Log.e("LoginResponse1",  jsonProductuom.toString());
                    String js=jsonProductuom.getString(
                            "success");
                    Log.e("LoginResponse133w",  js.toString());
                    JSONArray jss=jsonProductuom.getJSONArray(
                            "Data");
                    Log.e("LoginResponse133ws",  jss.toString());

                    for (int i = 0; i < jss.length(); i++) {
                        JSONObject jsonObject = jss.optJSONObject(i);
                        String name=jsonObject.getString("name");
                        Log.v("LoginResponse1nn", name);
                        String productCode=jsonObject.getString("Product_Code");
                        Log.v("LoginResponse1nnq", productCode);
                        String conqty=jsonObject.getString("ConQty");
                        mCommon_model_spinner = new Common_Model(name, productCode,conqty, "flag");
                        productCodeOffileData.add(mCommon_model_spinner);
                        Log.v("daaa",productCodeOffileData.toString());
                        //mShared_common_pref.save("PRODUCTCODE", productCodeOffileData.toString());
//
                        customDialog = new CustomListViewDialog(mCtx, productCodeOffileData,12,productunit);
                        Window window = customDialog.getWindow();
                        window.setGravity(Gravity.CENTER);
                        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        customDialog.show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(mCtx, "Invalid products", Toast.LENGTH_LONG).show();
            }
        });


    }
*/

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
//                    Log.v("salunit",item.getProduct_Sale_Unit());
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

    public void setContact(List<PrimaryProduct> contacts, String code) {
        if (!code.equalsIgnoreCase("Nil")) {
            this.exampleList.clear();
            this.workinglist.clear();
            this.exampleList.addAll(contacts);
            this.workinglist = contacts;
            ProductAdapter.this.notifyDataSetChanged();
            Log.d("ProductAdapt", "setContact: "+ workinglist);
            Log.d("ProductAdapt", "setContact: "+ exampleList);
        }
    }



    private void updateScheme(final PrimaryProduct task, int position) {
        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .updateEditDiscount(task.getPID(),task.isEdited(), task.getEditedDiscount(), task.getEditedPrice());

                workinglist.set(position, task);
                ProductAdapter.this.notifyItemChanged(position);

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



}
