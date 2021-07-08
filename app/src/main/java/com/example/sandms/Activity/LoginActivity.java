package com.example.sandms.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Common_Class;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    String name, email;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    String Sf_Code, Division_Code, Cut_Off_Time, Sf_Name, SteCode,
            SfUsrNme, StckLstCde, StckLstMb, SpCode, SpNme, StckLstAdd, SpAddr, logintype;
    JSONObject jsonObject1;
    Shared_Common_Pref shared_common_pref;
    Button Login;
    TextInputEditText edtEmail, edtPass;
    Common_Class common_class;
    Gson gson;
    SharedPreferences UserDetails;
    public static final String MyPREFERENCES = "MyPrefs";

    //SignInButton mSignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initilaize();
        googleSignInitial();
        UserDetails = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        shared_common_pref = new Shared_Common_Pref(this);
        gson = new Gson();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calobj = Calendar.getInstance();
        String dateTime = "'" + df.format(calobj.getTime()) + "'";
        System.out.println("Date_and_Time" + dateTime);
        common_class = new Common_Class(this);
        Login = findViewById(R.id.custom_button);
        edtEmail = findViewById(R.id.email_edt);
        edtPass = findViewById(R.id.pass_edt);
        Login.setText("Login");
        Login.setTextColor(Color.WHITE);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NamePassword(edtEmail.getText().toString(), edtPass.getText().toString());
            }
        });
    }

    /*Initilaizing all view in this method */
    private void initilaize() {
        signInButton = findViewById(R.id.sign_in_button);
    }


    public void googleSignInitial() {
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();

            // gMailLogin("ekumar.san@gmail.com");
            gMailLogin(email);
            Log.e("Name", name);
            Log.e("EMAIL_VALUE", email);
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        } else {
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {

                        } else {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    /*GMAIL LOGIN*/

    public void gMailLogin(String emailString) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> gMail = apiInterface.Glogin(emailString);

        Log.v("DMS_REQUEST", gMail.request().toString());

        gMail.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.v("DMS_RESPONSE", response.body().toString());

                try {
                    jsonObject1 = new JSONObject(response.body().toString());
                    String san = jsonObject1.getString("success");
                    shared_common_pref.save("Login_Successfully", san);

                    Log.e("LoginResponse", response.body().toString());
                    JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Sf_Code = jsonObject.optString("Sf_Code");
                        Division_Code = jsonObject.optString("div_Code");
                        Cut_Off_Time = jsonObject.optString("Cut_Off_Time");
                        Sf_Name = jsonObject.optString("Sf_Name");
                        SteCode = jsonObject.optString("State_Code");
                        SfUsrNme = jsonObject.optString("Sf_UserName");
                        StckLstCde = jsonObject.optString("Stockist_Code");
                        StckLstMb = jsonObject.optString("Stockist_Mobile");
                        SpCode = jsonObject.optString("sup_code");
                        SpNme = jsonObject.optString("sup_name");
                        StckLstAdd = jsonObject.optString("Stockist_Address");
                        SpAddr = jsonObject.optString("sup_addr");

                    }


                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
            }
        });
    }


    /*User Name and Password*/
    public void NamePassword(String email, String Pass) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> gMail = apiInterface.userLogin(email, Pass);
        Log.v("LOGIN_USER", gMail.request().toString());
        gMail.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    jsonObject1 = new JSONObject(response.body().toString());
                    String san = jsonObject1.getString("success");
                    shared_common_pref.save("Login_Successfully", san);
                    logintype = jsonObject1.optString("logintype");
                    shared_common_pref.save("Login_details", logintype);
                    Log.v("FinanceFinanceFinance", logintype);
                    Log.e("LoginResponse", response.body().toString());
                    JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Sf_Code = jsonObject.optString("Sf_Code");
                        Division_Code = jsonObject.optString("div_Code");
                        Cut_Off_Time = jsonObject.optString("Cut_Off_Time");
                        Sf_Name = jsonObject.optString("Sf_Name");
                        SteCode = jsonObject.optString("State_Code");
                        SfUsrNme = jsonObject.optString("Sf_UserName");
                        StckLstCde = jsonObject.optString("Stockist_Code");
                        StckLstMb = jsonObject.optString("Stockist_Mobile");
                        SpCode = jsonObject.optString("sup_code");
                        SpNme = jsonObject.optString("sup_name");
                        StckLstAdd = jsonObject.optString("Stockist_Address");
                        SpAddr = jsonObject.optString("sup_addr");
                    }
                    if (logintype.equalsIgnoreCase("Finance")) {

                        Intent intent = new Intent(LoginActivity.this, FinanceActivity.class);
                        shared_common_pref.save(Shared_Common_Pref.Sf_Code, Sf_Code);
                        shared_common_pref.save(Shared_Common_Pref.Div_Code, Division_Code);
                        shared_common_pref.save(Shared_Common_Pref.Cut_Off_Time, Cut_Off_Time);
                        shared_common_pref.save(Shared_Common_Pref.Sf_Name, Sf_Name);
                        shared_common_pref.save(Shared_Common_Pref.Sf_UserName, SfUsrNme);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Code, StckLstCde);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Mobile, StckLstMb);
                        shared_common_pref.save(Shared_Common_Pref.sup_code, SpCode);
                        shared_common_pref.save(Shared_Common_Pref.sup_name, SpNme);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Address, StckLstAdd);
                        shared_common_pref.save(Shared_Common_Pref.sup_addr, SpAddr);
                        shared_common_pref.save(Shared_Common_Pref.State_Code, SteCode);

                        startActivity(intent);
                        finish();

                    } else if (logintype.equals("Logistics")) {
                        Intent intent = new Intent(LoginActivity.this, LogisticsActivity.class);
                        shared_common_pref.save(Shared_Common_Pref.Sf_Code, Sf_Code);
                        shared_common_pref.save(Shared_Common_Pref.Div_Code, Division_Code);
                        shared_common_pref.save(Shared_Common_Pref.Cut_Off_Time, Cut_Off_Time);
                        shared_common_pref.save(Shared_Common_Pref.Sf_Name, Sf_Name);
                        shared_common_pref.save(Shared_Common_Pref.Sf_UserName, SfUsrNme);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Code, StckLstCde);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Mobile, StckLstMb);
                        shared_common_pref.save(Shared_Common_Pref.sup_code, SpCode);
                        shared_common_pref.save(Shared_Common_Pref.sup_name, SpNme);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Address, StckLstAdd);
                        shared_common_pref.save(Shared_Common_Pref.sup_addr, SpAddr);
                        shared_common_pref.save(Shared_Common_Pref.State_Code, SteCode);

                        startActivity(intent);
                        finish
                                ();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                        shared_common_pref.save(Shared_Common_Pref.Sf_Code, Sf_Code);
                        shared_common_pref.save(Shared_Common_Pref.Div_Code, Division_Code);
                        shared_common_pref.save(Shared_Common_Pref.Cut_Off_Time, Cut_Off_Time);
                        shared_common_pref.save(Shared_Common_Pref.Sf_Name, Sf_Name);
                        shared_common_pref.save(Shared_Common_Pref.Sf_UserName, SfUsrNme);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Code, StckLstCde);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Mobile, StckLstMb);
                        shared_common_pref.save(Shared_Common_Pref.sup_code, SpCode);
                        shared_common_pref.save(Shared_Common_Pref.sup_name, SpNme);
                        shared_common_pref.save(Shared_Common_Pref.Stockist_Address, StckLstAdd);
                        shared_common_pref.save(Shared_Common_Pref.sup_addr, SpAddr);
                        shared_common_pref.save(Shared_Common_Pref.State_Code, SteCode);

                        startActivity(intent);
                        finish();
                        Toast.makeText(LoginActivity.this, jsonObject1.getString("Msm"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginActivity.this.finish();
        System.exit(0);
    }


}

