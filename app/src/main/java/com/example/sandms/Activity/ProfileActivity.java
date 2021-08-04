package com.example.sandms.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    EditText profileName, profilePlace;
    Shared_Common_Pref shared_common_pref;
    String sf_Code;
    JSONObject jsonObject1;
    JsonArray jsonArray;
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
        getProfileData(sf_Code);

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
                Log.v("DMS_RESPONSE", response.body().toString());

              try {
                   jsonObject1 = new JSONObject(response.body().toString());
                   Log.e("LoginResponse1",  jsonObject1.toString());
                   JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                   for (int i = 0; i < jsonArray.length(); i++) {
                       JSONObject jsonObject = jsonArray.getJSONObject(i);
                       String name=jsonObject.getString("Stockist_Name");
                       Log.e("LoginResponse", name.toString());
                       edtName.setText(name);
                       String email=jsonObject.getString("Stockist_Address1");
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

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Invalid profile", Toast.LENGTH_LONG).show();
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