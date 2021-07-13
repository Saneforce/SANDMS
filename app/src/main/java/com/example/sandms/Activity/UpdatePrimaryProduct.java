package com.example.sandms.Activity;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.PrimaryProductViewModel;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class UpdatePrimaryProduct extends AppCompatActivity {
    TextView productName, productPrice, productQty, productTotal, productTax, productDis, productDisAmt, productTaxAmt, productUnit;
    LinearLayout linPLus, linMinus;
    EditText mProductCount;
    int ProductCount = 0;
    float subTotal = 0;
    PrimaryProductViewModel contactViewModel;
    PrimaryProduct task;
    String Scheme = "", ClickedData = "";
    Shared_Common_Pref shared_common_pref;
    Float tax, taxAmt, disPercent;
    JSONObject jsonArray;
    float finalPrice = 0, disValue = 0, minusCount = 0;
    int schemCount =0;
    Float   valueTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_primary_product);
        shared_common_pref = new Shared_Common_Pref(this);

        ClickedData = shared_common_pref.getvalue("task");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        task = gson.fromJson(ClickedData, PrimaryProduct.class);

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

        loadTask(task);

        ProductCount = Integer.parseInt(mProductCount.getText().toString());
        subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
        float subTT = subTotal + Float.parseFloat(productTaxAmt.getText().toString());
        productTotal.setText("" + subTT);
//new code add start
//
//        if (ProductCount == schemCount || ProductCount > schemCount) {
//            finalPrice = (subTotal * disValue) / 100;
//            Log.v("finalPrice", String.valueOf(finalPrice));
//            productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//            subTotal = subTotal - finalPrice;
//            Log.v("finalPriceAfterDis", String.valueOf(subTotal));
//            productTotal.setText("" + subTotal);
//            productDis.setText(""+ disValue);
//
//        } else {
//            productDis.setText("0");
//            productDisAmt.setText("" + new DecimalFormat("##.##").format(0));
//        }
//        if (tax != 0.0) {
//            taxAmt = 100 + tax;
//            Log.v("Discount", String.valueOf(taxAmt));
//            subTotal = (taxAmt * subTotal) / 100;
//            productTotal.setText("" + subTotal);
//            Log.e("Total_Distcount", "=" + subTotal);
//            Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
//            Float valueTotal = subTotal - calculate;
//            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//        } else {
//            productTaxAmt.setText(task.getTax_amt());
//        }
//        linPLus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v("Prouct_Value", String.valueOf(ProductCount));
//                ProductCount = Integer.parseInt(mProductCount.getText().toString());
//                ProductCount = ProductCount + 1;
//                mProductCount.setText("" + ProductCount);
//                productQty.setText(mProductCount.getText().toString());
//                subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
//                Log.v("PRODUCT_COUNT", String.valueOf(subTotal));
//                //updateTask(task);
//                tax = Float.valueOf(productTax.getText().toString());
//                if (tax != 0.0) {
//                    taxAmt = 100 + tax;
//                    Log.v("Discount", String.valueOf(taxAmt));
//                    subTotal = (taxAmt * subTotal) / 100;
//                    productTotal.setText("" + subTotal);
//                    Log.e("Total_Distcount", "=" + subTotal);
//                    Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
//                    Float valueTotal = subTotal - calculate;
//                    productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                } else {
//                    productTaxAmt.setText(task.getTax_amt());
//                }
//
//            }
//        });
//
//
//        linPLus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String prdDisAmt;
//                count = count + 1;
//                ProductCount = Integer.parseInt(mProductCount.getText().toString());
//                ProductCount = ProductCount + 1;
//                Scheme = mContact.getSchemeProducts().getScheme();
//                String proCnt = String.valueOf(ProductCount);
//                Log.v("countcount_Click", String.valueOf(proCnt));
//                Log.v("countcount_Click_SCHEME", Scheme);
//                mProductCount.setText("" + ProductCount);
//                subTotal = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                Log.v("+++subtotal", String.valueOf(subTotal));
//                productItem.setText(mProductCount.getText().toString());
//
////                disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
////                Log.v("+++disvalue", String.valueOf(disValue));
//                if("".equals(mContact.getSchemeProducts().getDiscountvalue())||
//                        ("0").equals(mContact.getSchemeProducts().getDiscountvalue())
//                ){
//                    ProductDisAmt.setText("0");
//                    finalPrice=0;
//                }
//                productItemTotal.setText("" + subTotal);
//                if("".equals(mContact.getTax_Value())||
//                        ("0").equals(mContact.getTax_Value())){
//                    tax= Float.valueOf("0.0");
//
//                }else {
//                    tax = Float.valueOf(mContact.getTax_Value());
//
//                }
//                //tax = Float.valueOf(mContact.getTax_Value());
//                if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
//                    schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
//                    if (ProductCount == schemCount || ProductCount > schemCount) {
//
//                        disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
//                        Log.v("+disvalue", String.valueOf(disValue));
//                        if(disValue.equals("0")||disValue.equals("")){
//                            ProductDisAmt.setText("0");
//                            finalPrice=0;
//                        }else {
//                            ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
//                            finalPrice = (subTotal * disValue) / 100;
//                            Log.v("+finalPrice*disvalue", String.valueOf(finalPrice));
//                            ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                        }
//                        if (tax != 0.0) {
//                            taxAmt = 100 + tax;
//                            Log.v("+TaxAMT", String.valueOf(taxAmt));
//                            subTotal=subTotal-finalPrice;
//                            Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                            Log.e("+Total_calculatecatcode", "=" + mContact.getProduct_Cat_Code());
//                            Log.e("+Total_product", "=" +mProductCount.getText().toString());
//                            Log.v("+Subtotafterdis", String.valueOf(subTotal));
//                            Log.v("+calcu",calculate.toString());
//                            valueTotal = subTotal/Float.parseFloat(mProductCount.getText().toString());
//                            subTotal = (taxAmt * subTotal) / 100;
//                            productItemTotal.setText("" + subTotal);
//                            ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            Log.v("+Total", "=" + subTotal);
//                            productItemTotal.setText("" + subTotal);
//                            Log.v("+PRDTaxAMT", String.valueOf(valueTotal));
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal),
//                                    String.valueOf(valueTotal), String.valueOf(finalPrice));
//                            Log.v("ifupdatedval++",   String.valueOf(valueTotal)+String.valueOf(finalPrice));
//                        } else {
//
//                            if(mContact.getTax_Value().equals("")||mContact.getTax_Value().equals("0")){
//                                ProductTaxAmt.setText("0");
//                                valueTotal=Float.parseFloat("0.0");//new code added jul87
//
//                            }else {
//                                ProductTaxAmt.setText(mContact.getTax_amt());
//                                valueTotal=Float.parseFloat(mContact.getTax_amt());//new code added jul 8
//                            }
//                            //if no tax
//                            if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
//                                ProductDisAmt.setText("0");
//                                finalPrice=Float.parseFloat("0.0");//new code added jly 8
//                            }else {
//                                ProductDisAmt.setText(mContact.getDis_amt());
//                                finalPrice=Float.parseFloat(mContact.getDis_amt());//new code added jul 8
//                            }
//                            //new code added-jul8
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal),
//                                    String.valueOf(valueTotal), String.valueOf(finalPrice));
//                            //   ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                        }
//
//                        Log.e("taxamt+", mContact.getTax_amt());
//                        //   updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), mContact.getTax_amt(), mContact.getDis_amt());
//
//                        Log.v("updatedval++",  mContact.getTax_amt()+ mContact.getDis_amt());
//                    } else {
//                        ProductDis.setText("0");
//                        ProductDisAmt.setText("0");
//
////new code tax start
//                        if (tax != 0.0) {
//                            taxAmt = 100 + tax;
//                            Log.v("++++TaxAMT1", String.valueOf(taxAmt));
//                            subTotal = (taxAmt * subTotal) / 100;
//                            productItemTotal.setText("" + subTotal);
//                            Log.e("+++++Total_Distcount1", "=" + subTotal);
//                            Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                            Log.e("+++++Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
//                            Log.e("++++++Total_product1", "=" + mProductCount.getText().toString());
//                            valueTotal = subTotal - calculate;
//                            ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            Log.v("+PRDTaxAMT1", String.valueOf(valueTotal));
//                        } else {
//                            ProductTaxAmt.setText(mContact.getTax_amt());
//                        }
//                        //new code tax stop
//                        prdDisAmt=mContact.getDis_amt();
//                        if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), "0");
//                            Log.v("updatedval++else",   String.valueOf(valueTotal)+ prdDisAmt);
//
//                        }else{
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), mContact.getDis_amt());
//                            Log.v("updatedval++else",    String.valueOf(valueTotal)+ mContact.getDis_amt());
//                        }
//                    }
//
//                    //  updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
//                }else {//new start
//                    if (tax != 0.0) {
//                        taxAmt = 100 + tax;
//                        Log.v("q+TaxAMT1", String.valueOf(taxAmt));
//                        subTotal = (taxAmt * subTotal) / 100;
//                        productItemTotal.setText("" + subTotal);
//                        Log.e("q+Total_Distcount1", "=" + subTotal);
//                        Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                        Log.e("q+Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
//                        Log.e("q+Total_product1", "=" + mProductCount.getText().toString());
//                        valueTotal = subTotal - calculate;
//                        ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                        Log.v("q+PRDTaxAMT1", String.valueOf(valueTotal));
//                    } else {
//                        if(mContact.getTax_amt().equals("")||mContact.getTax_amt().equals("0")){
//                            ProductTaxAmt.setText("0");
//                            valueTotal=Float.parseFloat("0.0");//new july 8
//                        }else {
//                            ProductTaxAmt.setText(mContact.getTax_amt());
//                            valueTotal=Float.parseFloat(mContact.getTax_amt());
//                        }
//                        //  ProductTaxAmt.setText(mContact.getTax_amt());
//                    }
//
//                    Log.e("5Taxamt",mContact.getTax_amt().toString());
//                    Log.e("55taxamt",valueTotal.toString());
//
//                    updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
//                }//new stop
//                //  updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
//            }
//        });
//
//        Log.v("PRODUCT_VALUE_TOTAL", String.valueOf(valueTotal));
//        linMinus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ProductCount = Integer.parseInt(mProductCount.getText().toString());
//                ProductCount = ProductCount - 1;
//                Scheme = mContact.getSchemeProducts().getScheme();
//                String proCnt = String.valueOf(ProductCount);
//                Log.v("countcount_Click", String.valueOf(proCnt));
//                Log.v("countcount_Click_SCHEME", Scheme);
//
//
//                if (ProductCount >= 0) {
//                    String prdDisAmt;
//                    mProductCount.setText("" + ProductCount);
//                    subTotal = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                    Log.v("+++subtotal", String.valueOf(subTotal));
//                    productItem.setText(mProductCount.getText().toString());
//
////                disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
////                Log.v("+++disvalue", String.valueOf(disValue));
//                    if("".equals(mContact.getSchemeProducts().getDiscountvalue())||
//                            ("0").equals(mContact.getSchemeProducts().getDiscountvalue())
//                    ){
//                        ProductDisAmt.setText("0");
//                        finalPrice=0;
//                    }
//                    productItemTotal.setText("" + subTotal);
//                    tax = Float.valueOf(mContact.getTax_Value());
//                    if (!mContact.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
//                        schemCount = Integer.parseInt(mContact.getSchemeProducts().getScheme());
//                        if (ProductCount == schemCount || ProductCount > schemCount) {
//
//                            disValue = Float.valueOf(mContact.getSchemeProducts().getDiscountvalue());
//                            Log.v("+disvalue", String.valueOf(disValue));
//                            if(disValue.equals("0")||disValue.equals("")){
//                                ProductDisAmt.setText("0");
//                                finalPrice=0;
//                            }else {
//                                ProductDis.setText(mContact.getSchemeProducts().getDiscountvalue());
//                                finalPrice = (subTotal * disValue) / 100;
//                                Log.v("+finalPrice*disvalue", String.valueOf(finalPrice));
//                                ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                            }
//                            if (tax != 0.0) {
//                                taxAmt = 100 + tax;
//                                Log.v("+TaxAMT", String.valueOf(taxAmt));
//                                subTotal=subTotal-finalPrice;
//                                Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                                Log.e("+Total_calculatecatcode", "=" + mContact.getProduct_Cat_Code());
//                                Log.e("+Total_product", "=" +mProductCount.getText().toString());
//                                Log.v("+Subtotafterdis", String.valueOf(subTotal));
//                                Log.v("+calcu",calculate.toString());
//                                valueTotal = subTotal/Float.parseFloat(mProductCount.getText().toString());
//                                subTotal = (taxAmt * subTotal) / 100;
//                                productItemTotal.setText("" + subTotal);
//                                ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                                Log.v("+Total", "=" + subTotal);
//                                productItemTotal.setText("" + subTotal);
//                                Log.v("+PRDTaxAMT", String.valueOf(valueTotal));
//                                updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal),
//                                        String.valueOf(valueTotal), String.valueOf(finalPrice));
//                                Log.v("ifupdatedval++",   String.valueOf(valueTotal)+String.valueOf(finalPrice));
//                            } else {
//                                ProductTaxAmt.setText(mContact.getTax_amt());
//                                //if no tax
//                                if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
//                                    ProductDisAmt.setText("0");
//                                }else {
//                                    ProductDisAmt.setText(mContact.getDis_amt());
//                                }
//
//                                //   ProductDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                            }
//
//                            Log.e("taxamt+", mContact.getTax_amt());
//                            //   updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), mContact.getTax_amt(), mContact.getDis_amt());
//
//                            Log.v("updatedval++",  mContact.getTax_amt()+ mContact.getDis_amt());
//                        } else {
//                            ProductDis.setText("0");
//                            ProductDisAmt.setText("0");
//
////new code tax start
//                            if (tax != 0.0) {
//                                taxAmt = 100 + tax;
//                                Log.v("++++TaxAMT1", String.valueOf(taxAmt));
//                                subTotal = (taxAmt * subTotal) / 100;
//                                productItemTotal.setText("" + subTotal);
//                                Log.e("+++++Total_Distcount1", "=" + subTotal);
//                                Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                                Log.e("+++++Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
//                                Log.e("++++++Total_product1", "=" + mProductCount.getText().toString());
//                                valueTotal = subTotal - calculate;
//                                ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                                Log.v("+PRDTaxAMT1", String.valueOf(valueTotal));
//                            } else {
//                                ProductTaxAmt.setText(mContact.getTax_amt());
//                            }
//                            //new code tax stop
//                            prdDisAmt=mContact.getDis_amt();
//                            if(mContact.getDis_amt().equals("")||mContact.getDis_amt().equals("0")){
//                                updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), "0");
//                                Log.v("updatedval++else",   String.valueOf(valueTotal)+ prdDisAmt);
//
//                            }else{
//                                updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), mContact.getDis_amt());
//                                Log.v("updatedval++else",    String.valueOf(valueTotal)+ mContact.getDis_amt());
//                            }
//                        }
//
//                        //  updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
//                    }else {//new start
//                        if (tax != 0.0) {
//                            taxAmt = 100 + tax;
//                            Log.v("q+TaxAMT1", String.valueOf(taxAmt));
//                            subTotal = (taxAmt * subTotal) / 100;
//                            productItemTotal.setText("" + subTotal);
//                            Log.e("q+Total_Distcount1", "=" + subTotal);
//                            Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(mContact.getProduct_Cat_Code());
//                            Log.e("q+Total_calctcode1", "=" + mContact.getProduct_Cat_Code());
//                            Log.e("q+Total_product1", "=" + mProductCount.getText().toString());
//                            valueTotal = subTotal - calculate;
//                            ProductTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            Log.v("q+PRDTaxAMT1", String.valueOf(valueTotal));
//                        } else {
//                            ProductTaxAmt.setText(mContact.getTax_amt());
//                        }
//
//                        updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
//                    }//new stop
//
//
//
//
//
//                }
//
//            }
//        });
        //new code stop

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

            Log.v("tax1Discount", String.valueOf(taxAmt));
            //subTotal = (taxAmt * subTotal) / 100;
           // productTotal.setText("" + subTotal);

            //new code jul9 start

            if(finalPrice!=0) {

                productTotal.setText("" + (taxAmt * subTotal) / 100);
               // subTotal = subTotal - finalPrice;//dis calculatihj july 9
                valueTotal = tax * subTotal / 100;
                Log.e("tota2222lyy", String.valueOf((taxAmt * subTotal) / 100));
                Log.e("taxcalyy", valueTotal.toString());
                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
             //   subTotal=subTotal
               // productTotal.setText("" + subTotal);
            }else{
                subTotal = (taxAmt * subTotal) / 100;
                Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                Log.e("calculate",calculate.toString());
                valueTotal = subTotal - calculate;
                Log.e("tax220", valueTotal.toString());
                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                productTotal.setText("" + subTotal);
            }


            Log.e("Tss11", "=" + subTotal);

            //new code jul 9 stop
            Log.e("Total_Distcount", "=" + subTotal);
            //below 2 line workin code commented.
           // Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
           // valueTotal = subTotal - calculate;
            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
        } else {
            task.setTax_amt("0");//new july 9
            Log.e("tax4",task.getTax_amt());
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
                tax = Float.valueOf(productTax.getText().toString());
                if (tax != 0.0) {
                    taxAmt = 100 + tax;
                    Log.v("Discounttax1", String.valueOf(taxAmt));
                    subTotal = (taxAmt * subTotal) / 100;
                    productTotal.setText("" + subTotal);
                    Log.e("Total_Distcount", "=" + subTotal);
                    Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                    Float valueTotal = subTotal - calculate;
                    productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                } else {
                    productTaxAmt.setText(task.getTax_amt());
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
                    // updateTask(task);

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
                    } else {
                        productTaxAmt.setText(task.getTax_amt());
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
                    Float valueTotal;
                    //new code july 9

                    tax = Float.valueOf(productTax.getText().toString());
                    Log.e("taxinitail", tax.toString());
                    ProductCount = Integer.parseInt(mProductCount.getText().toString());
                    Log.e("prdcount", String.valueOf(ProductCount));
                    Log.e("scheme", String.valueOf(schemCount));

                    if (tax.equals("") || tax.equals("0")) {
                        productTaxAmt.setText("0");
                        valueTotal = Float.parseFloat("0.0");//new code added jul87

                    }
//                    else {
//                        productTaxAmt.setText(PrimaryProduct.SchemeProducts..getTax_amt());
//                        valueTotal=Float.parseFloat(mContact.getTax_amt());//new code added jul 8
//                    }
                    //if no tax


//                    if(Float.parseFloat(disValue).e"")||disValue=="0.0")){
//                        productDisAmt.setText("0");
//                        finalPrice=Float.parseFloat("0.0");//new code added jly 8
//                    }
//                    else {
//                        ProductDisAmt.setText(mContact.getDis_amt());
//                        finalPrice=Float.parseFloat(mContact.getDis_amt());//new code added jul 8
//                    }
                    //new code july 9
                    if (ProductCount == schemCount || ProductCount > schemCount) {
                        finalPrice = (subTotal * disValue) / 100;
                        Log.v("finalPrice", String.valueOf(finalPrice));
                        productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                        productDis.setText("" + disValue);
                        //new code tax jyly 9
                        if (tax != 0.0) {
                            Log.e("initil ttt",tax.toString());
                            taxAmt = 100 + tax;
                            Log.v("tax3Discount", String.valueOf(taxAmt));
                           // subTotal = (taxAmt * subTotal) / 100;

                            Log.e("Tss", "=" + subTotal);

                            if(finalPrice!=0) {
                               // subTotal = subTotal - finalPrice;//dis calculatihj july 9
                                valueTotal = tax * subTotal / 100;
                                Log.e("tota2222l", String.valueOf(subTotal));
                                Log.e("taxcalyy", valueTotal.toString());
                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            }else{
                                subTotal = (taxAmt * subTotal) / 100;
                                Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                                Log.e("calculate",calculate.toString());
                                valueTotal = subTotal - calculate;
                                Log.e("tax220", valueTotal.toString());
                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            }




                           // subTotal = (taxAmt * subTotal) / 100;//original


                            //        productTotal.setText("" + subTotal);
                         //   Log.e("Total_Distcount", "=" + subTotal);
//                            Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
//                         Log.e("calculate",calculate.toString());
//                            valueTotal = subTotal - calculate;
                           // Log.e("tax220", valueTotal.toString());
                           // productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                        } else {
                            if (task.getTax_amt().equals("") || task.getTax_amt().equals("0")) {
                                task.setTax_amt("0");
                                productTaxAmt.setText(task.getTax_amt());
                                Log.e("gtax", task.getTax_amt().toString());
                            }
                            productTaxAmt.setText(task.getTax_amt());
                        }
                        //new code tax july 9
                       // subTotal = subTotal - finalPrice;
                        Log.v("finalPriceAfterDis33", String.valueOf(subTotal));
                        productTotal.setText("" + subTotal);


                    } else {
                        finalPrice = 0;
                        productDis.setText("0");
                        productDisAmt.setText("" + new DecimalFormat("##.##").format(0));
                        //   }
                        //new code start july 9
                        if (tax != 0.0) {
                            taxAmt = 100 + tax;
                            Log.v("tax3Discount", String.valueOf(taxAmt));
                            subTotal = (taxAmt * subTotal) / 100;
                            productTotal.setText("" + subTotal);
                            Log.e("Total_Distcount", "=" + subTotal);
                            Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
                            valueTotal = subTotal - calculate;
                            Log.e("tax220", valueTotal.toString());
                            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                        } else {
                            if (task.getTax_amt().equals("") || task.getTax_amt().equals("0")) {
                                task.setTax_amt("0");
                                productTaxAmt.setText(task.getTax_amt());
                                Log.e("gtax", task.getTax_amt().toString());
                            }
                            productTaxAmt.setText(task.getTax_amt());
                        }
                        //new code july 9 stop

                    }
                }
//new brace below  added july 9
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadTask(PrimaryProduct task) {
        productName.setText(task.getPname());
        productPrice.setText(task.getProduct_Cat_Code());
        productQty.setText(task.getTxtqty());
        mProductCount.setText(task.getQty());
        productTax.setText(task.getTax_Value());
        productDis.setText(task.getSchemeProducts().getDiscountvalue());
        productUnit.setText(task.getProduct_Sale_Unit());
       if(task.getTax_amt().equals("")||task.getTax_amt().equals("0")){
           productTaxAmt.setText("0");
           task.setTax_amt("0");
       }else {
           productTaxAmt.setText(task.getTax_amt());
       }
        if(!task.getSchemeProducts().getDiscountvalue().equalsIgnoreCase("")){
            productDis.setText(task.getSchemeProducts().getDiscountvalue());
            disPercent = Float.valueOf(task.getSchemeProducts().getDiscountvalue());
            if (disPercent != 0) {
                productDisAmt.setText(task.getDis_amt());
            } else {
                task.setDis_amt("0");
            //    productDisAmt.setText("0");
                productDisAmt.setText(task.getDis_amt());
            }
            disValue = Float.parseFloat(task.getSchemeProducts().getDiscountvalue());
        }

        if(!task.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
            schemCount = Integer.parseInt(task.getSchemeProducts().getScheme());
        }

        tax = Float.valueOf(task.getTax_Value());
        Scheme = task.getSchemeProducts().getScheme();

    }

    public void UpdateProduct(View v) {
        updateTask(task);
        finish();
    }

    private void updateTask(final PrimaryProduct task) {
        class UpdateTask extends AsyncTask<Void, Void, Void> {


            @Override
            protected Void doInBackground(Void... voids) {
                task.setQty(mProductCount.getText().toString());
                task.setTxtqty(mProductCount.getText().toString());
                task.setSubtotal(String.valueOf(subTotal));
                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(task.getPID(),
                                productQty.getText().toString(),
                                productQty.getText().toString(),
                                String.valueOf(subTotal),
                                productTax.getText().toString(),
                                task.getSchemeProducts().getDiscountvalue(),
                                productTaxAmt.getText().toString(),
                                String.valueOf(finalPrice));
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
