package com.saneforce.dms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.R;
import com.saneforce.dms.sqlite.DBController;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
//    EditText profileName, profilePlace;
    Shared_Common_Pref shared_common_pref;
    String sf_Code;
    JSONObject jsonObject1;
//    JsonArray jsonArray;
    ImageView imagView;
    DBController dbController;
    TextInputEditText edtName, edtAddress, edtMobile, edtStoclistName, edtGst, edtEmail;
//    edtDesignation,
    TextView toolbarTitle;
    TextInputEditText tie_erp_code, tie_sales_team_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        shared_common_pref = new Shared_Common_Pref(this);
        sf_Code = shared_common_pref.getvalue(Shared_Common_Pref.Sf_Code);
        toolbarTitle=findViewById(R.id.toolbar_title);
        toolbarTitle.setText("PROFILE");

        edtName = findViewById(R.id.name_edt);
        edtAddress = findViewById(R.id.add_edt);
        edtMobile = findViewById(R.id.mob_edt);
//        edtDesignation = findViewById(R.id.desg_edt);
        edtStoclistName = findViewById(R.id.stocknam_edt);
        edtGst = findViewById(R.id.gst_edt);
        edtEmail = findViewById(R.id.ema_edt);
        tie_erp_code = findViewById(R.id.tie_erp_code);
        tie_sales_team_name = findViewById(R.id.tie_sales_team_name);

        dbController = new DBController(this);
        if(!dbController.getResponseFromKey(DBController.PROFILE_DATA).equals("")){
            processResponse(dbController.getResponseFromKey(DBController.PROFILE_DATA));
        }
        else {
            getProfileData();
        }

        Button btnUpdate = findViewById(R.id.btn_update);
        LinearLayout llProfile = findViewById(R.id.ll_profile);
        if(ApiClient.APP_TYPE == 1){
            btnUpdate.setVisibility(View.GONE);
            llProfile.setVisibility(View.VISIBLE);
        }
        else {
            llProfile.setVisibility(View.GONE);
          /*  btnUpdate.setVisibility(View.VISIBLE);
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(edtName.getText().toString().equals(""))
                        Toast.makeText(ProfileActivity.this, "Please enter valid Name", Toast.LENGTH_SHORT).show();
                    else if(edtAddress.getText().toString().equals(""))
                        Toast.makeText(ProfileActivity.this, "Please enter valid Address", Toast.LENGTH_SHORT).show();
                    else if(edtMobile.getText().toString().equals(""))
                        Toast.makeText(ProfileActivity.this, "Please enter valid Mobile Number", Toast.LENGTH_SHORT).show();
                    else {
                        try {
                            updateProfile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });*/
        }



        imagView = findViewById(R.id.toolbar_back);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
            }
        });
    }

    private void updateProfile() {
        JSONObject js = new JSONObject();
        try {
            js.put("Stockist_Name", edtName.getText().toString());
            js.put("Stockist_Address", edtAddress.getText().toString());
            js.put("Stockist_ContactPerson", edtStoclistName.getText().toString());
            js.put("Email", edtEmail.getText().toString());
            js.put("Stockist_Mobile", edtMobile.getText().toString());
            js.put("gstn", edtGst.getText().toString());

        }catch (Exception e){
            e.printStackTrace();
        }
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.updateProfile(sf_Code, js.toString());
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String res = response.body().toString();
                Log.v("DMS_RESPONSE", res);

                try {
                    jsonObject1 = new JSONObject(res);
                    Log.e("LoginResponse1",  jsonObject1.toString());
                    JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("Stockist_Name");
                        Log.e("LoginResponse", name.toString());
                        edtName.setText(name);
                        String email = "";
                        if(jsonObject.has("Email"))
                        email=jsonObject.getString("Email");
                        edtEmail.setText(email);

                        String stocklistname=jsonObject.getString("Stockist_ContactPerson");
                        edtStoclistName.setText(stocklistname);
//                        String designation=jsonObject.getString("Stockist_Designation");
//                        edtDesignation.setText(designation);
                        String address=jsonObject.getString("Stockist_Address");
                        edtAddress.setText(address);
                        String mobile=jsonObject.getString("Stockist_Mobile");
                        edtMobile.setText(mobile);
                        String gst=jsonObject.getString("gstn");
                        edtGst.setText(gst);

                        String erpCode = "";
                        if(jsonObject.has("ERP_Code"))
                            erpCode=jsonObject.getString("ERP_Code");
                        tie_erp_code.setText(erpCode);

                        String salesTeamName = "";
                        if(jsonObject.has("FieldPerson"))
                            salesTeamName=jsonObject.getString("FieldPerson");
                        tie_sales_team_name.setText(salesTeamName);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getProfileData() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProfile(sf_Code);
        Log.v("DMS_REQUEST", call.request().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String res = response.body().toString();
                Log.v("DMS_RESPONSE", res);

              processResponse(res);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processResponse(String res) {
        try {
            jsonObject1 = new JSONObject(res);
            Log.e("LoginResponse1",  jsonObject1.toString());
            JSONArray jsonArray = jsonObject1.optJSONArray("Data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name=jsonObject.getString("Stockist_Name");
                Log.e("LoginResponse", name.toString());
                edtName.setText(name);
                String email=jsonObject.getString("Email");
                if(email!=null && !email.equals("") && !email.equals("null"))
                    edtEmail.setText(email);
                String stocklistname=jsonObject.getString("Stockist_ContactPerson");
                edtStoclistName.setText(stocklistname);
//                       String designation=jsonObject.getString("Stockist_Designation");
//                       edtDesignation.setText(designation);
                String address=jsonObject.getString("Stockist_Address");
                edtAddress.setText(address);
                String mobile=jsonObject.getString("Stockist_Mobile");
                edtMobile.setText(mobile);
                String gst=jsonObject.getString("gstn");
                edtGst.setText(gst);

                String erpCode = "";
                if(jsonObject.has("ERP_Code"))
                    erpCode=jsonObject.getString("ERP_Code");
                tie_erp_code.setText(erpCode);

                String salesTeamName = "";
                if(jsonObject.has("FieldPerson"))
                    salesTeamName=jsonObject.getString("FieldPerson");
                tie_sales_team_name.setText(salesTeamName);

                try {
                    shared_common_pref.save(Shared_Common_Pref.USER_NAME, name);
                    shared_common_pref.save(Shared_Common_Pref.USER_EMAIL, email);
                    shared_common_pref.save(Shared_Common_Pref.USER_PHONE, mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Company(View v) {
        Intent intent =new Intent(getApplicationContext(), CompanyProfile.class);
        intent.putExtra("fileName", "CompanyProfile.html");
        startActivity(intent);
    }
    public void PrivacyPolicy(View v) {
        Intent intent =new Intent(getApplicationContext(), CompanyProfile.class);
        intent.putExtra("fileName", "PrivacyPolicy.html");
        startActivity(intent);
    }

}