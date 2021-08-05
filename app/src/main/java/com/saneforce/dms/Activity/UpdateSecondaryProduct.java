package com.saneforce.dms.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.saneforce.dms.Model.PrimaryProduct;
import com.saneforce.dms.Model.SecondaryProduct;

import com.saneforce.dms.R;
import com.saneforce.dms.Utils.SecondaryProductDatabase;
import com.saneforce.dms.Utils.SecondaryProductViewModel;
import com.saneforce.dms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.List;

public class UpdateSecondaryProduct extends AppCompatActivity {
    TextView productName, productPrice, productQty, productTotal, productTax, productDis, productDisAmt, productTaxAmt,productUnit;
    LinearLayout linPLus, linMinus;
    EditText mProductCount;
    int ProductCount = 0;
    float subTotal = 0;
    SecondaryProductViewModel contactViewModel;
    SecondaryProduct task;
    String Scheme = "", FilteredData = "",ClickedData="";
    Shared_Common_Pref shared_common_pref;
    int schemCount;
    Float tax, taxAmt, taxTotal;
    float disPercent = 0, plusCount = 0, minusCount = 0;
    float finalPrice = 0, disValue = 0;

    String discountValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_secondary_product);
        shared_common_pref = new Shared_Common_Pref(this);

        ClickedData = shared_common_pref.getvalue("task");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        task = gson.fromJson(ClickedData, SecondaryProduct.class);

        Log.v("CLICKED_DETAILS", new Gson().toJson(task));

        productName = findViewById(R.id.child_product_name);
        productPrice = findViewById(R.id.child_product_price);
        productQty = findViewById(R.id.product_item_qty);
        productTotal = findViewById(R.id.product_item_total);
        mProductCount = findViewById(R.id.edt_product_count_inc_dec);
        productUnit = findViewById(R.id.txt_product_unit);
        productTax = findViewById(R.id.txt_tax);
        productDis = findViewById(R.id.txt_dis);
        productTaxAmt = findViewById(R.id.txt_tax_amt);
        productDisAmt = findViewById(R.id.txt_dis_amt);

        linPLus = findViewById(R.id.image_plus);
        linMinus = findViewById(R.id.image_minus);
        Intent intent = getIntent();
        if(intent.hasCategory("Scheme"))
            Scheme = task.getSelectedScheme();

        if(intent.hasCategory("discount"))
            discountValue = task.getSelectedDisValue();

        loadTask(task);

        ProductCount = Integer.parseInt(mProductCount.getText().toString());
        subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
        float subTT = subTotal + Float.parseFloat(productTaxAmt.getText().toString());
        productTotal.setText("" + subTT);

        if (ProductCount == schemCount || ProductCount > schemCount) {
            finalPrice = (subTotal * disValue) / 100;
            Log.v("finalPrice", String.valueOf(finalPrice));
            productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
            subTotal = subTotal - finalPrice;
            Log.v("finalPriceAfterDis", String.valueOf(subTotal));
            productTotal.setText("" + subTotal);
            productDis.setText(""+ disValue);

        } else {
            productDis.setText("0");
            productDisAmt.setText("" + new DecimalFormat("##.##").format(0));
        }

        if (tax != 0.0) {
            taxAmt = 100 + tax;
            Log.v("Discount", String.valueOf(taxAmt));
            subTotal = (taxAmt * subTotal) / 100;
            productTotal.setText("" + subTotal);
            Log.e("Total_Distcount", "=" + subTotal);
            Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
            Float valueTotal = subTotal - calculate;
            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
        } else {
            productTaxAmt.setText(task.getTax_amt());
        }


        linPLus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("Prouct_Value", String.valueOf(ProductCount));
                ProductCount = Integer.parseInt(mProductCount.getText().toString());

                ProductCount = ProductCount + 1;
                mProductCount.setText("" + ProductCount);
                productQty.setText(mProductCount.getText().toString());
                subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                Log.v("PRODUCT_COUNT", String.valueOf(subTotal));
                //updateTask(task);

                Float tax, taxAmt, taxTotal;
                float edtCount = 0, plusCount = 0, minusCount = 0;

                tax = Float.valueOf(productTax.getText().toString());

                if (tax != 0.0) {
                    taxAmt = 100 + tax;
                    Log.v("Discount", String.valueOf(taxAmt));
                    subTotal = (taxAmt * subTotal) / 100;
                    productTotal.setText("" + subTotal);
                    Log.e("Total_Distcount", "=" + subTotal);
                    Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                    Float valueTotal = subTotal - calculate;
                    productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                }

            }
        });
        linMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductCount = Integer.parseInt(mProductCount.getText().toString());

                ProductCount = ProductCount - 1;
                if (ProductCount >= 0) {
                    mProductCount.setText("" + ProductCount);
                    productQty.setText(mProductCount.getText().toString());
                    subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                    productTotal.setText("" + subTotal);
                  //  updateTask(task);

                    Float tax, taxAmt, taxTotal;
                    float edtCount = 0, plusCount = 0, minusCount = 0;

                    if (mProductCount.getText().toString().equalsIgnoreCase(Scheme)) {
                        finalPrice = (subTotal * disValue) / 100;
                        Log.v("finalPrice", String.valueOf(finalPrice));
                        productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        subTotal = subTotal - finalPrice;
                        Log.v("finalPriceAfterDis", String.valueOf(subTotal));
                        productTotal.setText("" + subTotal);
                        productDis.setText(""+disValue);

                    } else {
                        productDis.setText("0");
                        productDisAmt.setText("" + new DecimalFormat("##.##").format(0));
                    }
                    tax = Float.valueOf(productTax.getText().toString());

                    if (tax != 0.0) {
                        taxAmt = 100 + tax;
                        Log.v("Discount", String.valueOf(taxAmt));
                        subTotal = (taxAmt * subTotal) / 100;
                        productTotal.setText("" + subTotal);
                        Log.e("Total_Distcount", "=" + subTotal);
                        Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                        Float valueTotal = subTotal - calculate;
                        productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                    }


                } else {
                    ProductCount = 0;
                    mProductCount.setText("" + 0);
                }
            }
        });
        mProductCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mProductCount.getText().toString().equalsIgnoreCase("")) {

                    productQty.setText("0");
                    subTotal = 0;
                    productTotal.setText("" + subTotal);
                    productTaxAmt.setText("" + new DecimalFormat("##.##").format(subTotal));
                  //  updateTask(task);

                } else {
                    productQty.setText(mProductCount.getText().toString());
                    subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                    productTotal.setText("" + subTotal);
                 //   updateTask(task);

                    Float tax, taxAmt, taxTotal;
                    float edtCount = 0, plusCount = 0, minusCount = 0;

                    tax = Float.valueOf(productTax.getText().toString());
                    ProductCount = Integer.parseInt(mProductCount.getText().toString());
                    if (ProductCount == schemCount || ProductCount > schemCount) {
                        finalPrice = (subTotal * disValue) / 100;
                        Log.v("finalPrice", String.valueOf(finalPrice));
                        productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        subTotal = subTotal - finalPrice;
                        Log.v("finalPriceAfterDis", String.valueOf(subTotal));
                        productTotal.setText("" + subTotal);
                        productDis.setText(""+disValue);

                    } else {
                        productDis.setText("0");
                        productDisAmt.setText("" + new DecimalFormat("##.##").format(0));
                    }


                    if (tax != 0.0) {
                        taxAmt = 100 + tax;
                        Log.v("Discount", String.valueOf(taxAmt));
                        subTotal = (taxAmt * subTotal) / 100;
                        productTotal.setText("" + subTotal);
                        Log.e("Total_Distcount", "=" + subTotal);
                        Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                        Float valueTotal = subTotal - calculate;
                        productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                    }else {
                        productTaxAmt.setText(task.getTax_amt());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadTask(SecondaryProduct task) {
        productName.setText(task.getPname());
        productPrice.setText(task.getProduct_Cat_Code());
        productQty.setText(task.getTxtqty());
        mProductCount.setText(task.getQty());
        productTax.setText(task.getTax_Value());
        productDis.setText(discountValue);
        productUnit.setText(task.getProduct_Sale_Unit());
        productTaxAmt.setText(task.getTax_amt());
        if(!discountValue.equalsIgnoreCase("")){
            productDis.setText(discountValue);
            disPercent = Float.valueOf(discountValue);
            if (disPercent != 0) {
                productDisAmt.setText(task.getDis_amt());
            } else {
                productDisAmt.setText("0");
            }
            disValue = Float.parseFloat(discountValue);
        }

        if(!Scheme.equalsIgnoreCase("")) {
            schemCount = Integer.parseInt(Scheme);
        }

        tax = Float.valueOf(task.getTax_Value());

    }

    public void UpdateProduct(View v) {
        updateTask(task);
        finish();
    }

    private void updateTask(final SecondaryProduct task) {
        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                task.setQty(mProductCount.getText().toString());
                task.setTxtqty(mProductCount.getText().toString());
                task.setSubtotal(String.valueOf(subTotal));
                SecondaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(task.getPID(),
                                productQty.getText().toString(),
                                productQty.getText().toString(),
                                String.valueOf(subTotal),
                                productTax.getText().toString(),
                                discountValue,
                                productTaxAmt.getText().toString(),
                                String.valueOf(finalPrice),
                                task.getSelectedDisValue(),
                                task.getSelectedFree(),
                                task.getSelectedFree(),
                                task.getOff_Pro_code(),task.getOff_Pro_name(), task.getOff_Pro_Unit());
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
