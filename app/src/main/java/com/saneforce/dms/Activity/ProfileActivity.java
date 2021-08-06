package com.saneforce.dms.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saneforce.dms.Interface.ApiInterface;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.ApiClient;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.Shared_Common_Pref;

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
    TextInputEditText edtName, edtAddress, edtMobile, edtDesignation, edtStoclistName, edtGst, edtEmail;
TextView toolbarTitle;
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
        edtDesignation = findViewById(R.id.desg_edt);
        edtStoclistName = findViewById(R.id.stocknam_edt);
        edtGst = findViewById(R.id.gst_edt);
        edtEmail = findViewById(R.id.ema_edt);
        Log.v("sfcode", sf_Code);
        if(Constants.isInternetAvailable(this))
            getProfileData(sf_Code);
        else{
            Toast.makeText(this, "Please check the internet connection", Toast.LENGTH_SHORT).show();
            finish();
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




    public void getProfileData(String sf_Code) {
    this.sf_Code=sf_Code;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getProfile(sf_Code);
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
                       String email=jsonObject.getString("Stockist_Address1");
                       if(email!=null && !email.equals("") && !email.equals("null"))
                       edtEmail.setText(email);
                       String stocklistname=jsonObject.getString("Stockist_ContactPerson");
                       edtStoclistName.setText(stocklistname);
                       String designation=jsonObject.getString("Stockist_Designation");
                       edtDesignation.setText(designation);
                       String address=jsonObject.getString("Stockist_Address");
                       edtAddress.setText(address);
                       String mobile=jsonObject.getString("Stockist_Mobile");
                       edtMobile.setText(mobile);
                       String gst=jsonObject.getString("gstn");
                       edtGst.setText(gst);

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