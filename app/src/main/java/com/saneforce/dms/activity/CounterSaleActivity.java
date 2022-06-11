package com.saneforce.dms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.CustomListViewDialog;

import java.util.ArrayList;
import java.util.List;

public class
CounterSaleActivity extends AppCompatActivity implements DMS.Master_Interface {
    LinearLayout linBtm;
    Intent dashIntent;
    List<Common_Model> pymntCounter = new ArrayList<>();
    ArrayList<String> counterTypLst;
    Common_Model mCommon_model_spinner;
    CustomListViewDialog customDialog;
    TextView txtPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_sale);
        linBtm = findViewById(R.id.bottom_linear);
        txtPayment = findViewById(R.id.txt_payment);
    }

    public void NumberCheck(View v) {
        Toast.makeText(this, "No Details", Toast.LENGTH_SHORT).show();
    }

    public void CounterSaleOrders(View v) {
        dashIntent = new Intent(getApplicationContext(), PrimaryOrderProducts.class);
        dashIntent.putExtra("Mode", "1");
        startActivity(dashIntent);
    }

    public void PayClick(View v) {
        pymntCounter.clear();
        PaymentType();
    }


    public void PaymentType() {
        counterTypLst = new ArrayList<>();
        counterTypLst.add("Cash");
        counterTypLst.add("Cheque");
        counterTypLst.add("Credit Card");
        counterTypLst.add("Debit Card");
        counterTypLst.add("Upi");


        for (int i = 0; i < counterTypLst.size(); i++) {
            String id = String.valueOf(counterTypLst.get(i));
            String name = counterTypLst.get(i);
            mCommon_model_spinner = new Common_Model(id, name, "flag");
            pymntCounter.add(mCommon_model_spinner);
        }
        customDialog = new CustomListViewDialog(CounterSaleActivity.this, pymntCounter, 1);
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        customDialog.show();
    }

    @Override
    public void OnclickMasterType(List<Common_Model> myDataset, int position, int type) {
        customDialog.dismiss();
        if (type == 1) {
            txtPayment.setText(myDataset.get(position).getName());
        }
    }


    public void OnBackClick(View v) {
        CounterSaleActivity.super.onBackPressed();
    }

    @Override
    public void onBackPressed() {

    }
}