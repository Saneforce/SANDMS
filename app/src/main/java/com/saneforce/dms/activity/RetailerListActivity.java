package com.saneforce.dms.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.R;
import com.saneforce.dms.adapter.DataAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.AlertDialogBox;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.PrimaryProductViewModel;
import com.saneforce.dms.utils.Shared_Common_Pref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetailerListActivity extends AppCompatActivity {

    public Context context;
    public Activity activity;
    EditText searchView;
    RecyclerView recyclerView;
    List<Common_Model> RetailerType = new ArrayList<>();
    private RecyclerView.LayoutManager mlayoutManager;
    public DataAdapter da;
    public Button no;
    Common_Model mCommon_model_spinner;
    DBController dbController;
    Shared_Common_Pref shared_common_pref;
    String sfCode = "";
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_retailer_list);
        no = (Button) findViewById(R.id.no);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        shared_common_pref = new Shared_Common_Pref(this);
        sfCode = shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code);
      //  no.setOnClickListener((View.OnClickListener) this);
        dbController = new DBController(this);
        RetailerType();
        da = new DataAdapter(RetailerType, this);
       //
        //
        //
        // recyclerView.setAdapter(da);
       // da.notifyDataSetChanged();

        ImageView imageView=findViewById(R.id.toolbar_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                da.getFilter().filter(s);
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //performSearch();
                    return true;
                }

                return false;
            }
        });
    }


    private void showExitDialog() {
        AlertDialogBox.showDialog(RetailerListActivity.this, "", "Do you want to exit?", "Yes", "NO", false, new DMS.AlertBox() {
            @Override
            public void PositiveMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }

            @Override
            public void NegativeMethod(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
    }

    private void performSearch() {
        searchView.clearFocus();
        InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
    public void RetailerType() {
        String RetailerDetails = "{\"tableName\":\"vwDoctor_Master_APP\",\"coloumns\":\"[\\\"doctor_code as id\\\", \\\"doctor_name as name\\\",\\\"town_code\\\",\\\"town_name\\\",\\\"lat\\\",\\\"long\\\",\\\"addrs\\\",\\\"ListedDr_Address1\\\",\\\"ListedDr_Sl_No\\\",\\\"Mobile_Number\\\",\\\"Doc_cat_code\\\",\\\"ContactPersion\\\",\\\"Doc_Special_Code\\\",\\\"Slan_Name\\\"]\",\"where\":\"[\\\"isnull(Doctor_Active_flag,0)=0\\\"]\",\"orderBy\":\"[\\\"name asc\\\"]\",\"desig\":\"mgr\"}";
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getRetName(shared_common_pref.getvalue(Shared_Common_Pref.Div_Code), sfCode, sfCode, shared_common_pref.getvalue(Shared_Common_Pref.State_Code), RetailerDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject JsonObject = response.body();
                try {
                    JsonArray jsonArray = JsonObject.getAsJsonArray("Data");
//                    shared_common_pref.save(Shared_Common_Pref.YET_TO_SYN, false);

                    processRetailerList(jsonArray);

                } catch (Exception io) {
                    io.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("LeaveTypeList", "Error");
                t.printStackTrace();
            }
        });
    }

    private void processRetailerList(JsonArray jsonArray) {
        if(jsonArray.size()>0)
            RetailerType.clear();
        try {
            for (int a = 0; a < jsonArray.size(); a++) {
                JsonObject jsonObject = (JsonObject) jsonArray.get(a);
                String id = jsonObject.get("id").getAsString();
                String name = jsonObject.get("name").getAsString();
                String townName = jsonObject.get("ListedDr_Address1").getAsString();
                String phone = jsonObject.get("Mobile_Number").getAsString();
                String scheme = "";
                if(jsonObject.has("Slan_Name"))
                    scheme = jsonObject.get("Slan_Name").getAsString();
                mCommon_model_spinner = new Common_Model(name, id, scheme, townName, phone);
                RetailerType.add(mCommon_model_spinner);
                recyclerView.setAdapter(da);
            }

            dbController.updateDataResponse(DBController.RETAILER_LIST, new Gson().toJson(jsonArray));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}