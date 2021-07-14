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
        Log.v("total", String.valueOf(subTotal));
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
//                Scheme = task.getSchemeProducts().getScheme();
//                String proCnt = String.valueOf(ProductCount);
//                Log.v("countcount_Click", String.valueOf(proCnt));
//                Log.v("countcount_Click_SCHEME", Scheme);
//                mProductCount.setText("" + ProductCount);
//                subTotal = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                Log.v("+++subtotal", String.valueOf(subTotal));
//                productItem.setText(mProductCount.getText().toString());
//
////                disValue = Float.valueOf(task.getSchemeProducts().getDiscountvalue());
////                Log.v("+++disvalue", String.valueOf(disValue));
//                if("".equals(task.getSchemeProducts().getDiscountvalue())||
//                        ("0").equals(task.getSchemeProducts().getDiscountvalue())
//                ){
//                    productDisAmt.setText("0");
//                    finalPrice=0;
//                }
//                productTotal.setText("" + subTotal);
//                if("".equals(task.getTax_Value())||
//                        ("0").equals(task.getTax_Value())){
//                    tax= Float.valueOf("0.0");
//
//                }else {
//                    tax = Float.valueOf(task.getTax_Value());
//
//                }
//                //tax = Float.valueOf(task.getTax_Value());
//                if (!task.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
//                    schemCount = Integer.parseInt(task.getSchemeProducts().getScheme());
//                    if (ProductCount == schemCount || ProductCount > schemCount) {
//
//                        disValue = Float.valueOf(task.getSchemeProducts().getDiscountvalue());
//                        Log.v("+disvalue", String.valueOf(disValue));
//                        if(disValue.equals("0")||disValue.equals("")){
//                            productDisAmt.setText("0");
//                            finalPrice=0;
//                        }else {
//                            productDis.setText(task.getSchemeProducts().getDiscountvalue());
//                            finalPrice = (subTotal * disValue) / 100;
//                            Log.v("+finalPrice*disvalue", String.valueOf(finalPrice));
//                            productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                        }
//                        if (tax != 0.0) {
//                            taxAmt = 100 + tax;
//                            Log.v("+TaxAMT", String.valueOf(taxAmt));
//                            subTotal=subTotal-finalPrice;
//                            Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                            Log.e("+Total_calculatecatcode", "=" + task.getProduct_Cat_Code());
//                            Log.e("+Total_product", "=" +mProductCount.getText().toString());
//                            Log.v("+Subtotafterdis", String.valueOf(subTotal));
//                            Log.v("+calcu",calculate.toString());
//                            valueTotal = subTotal/Float.parseFloat(mProductCount.getText().toString());
//                            subTotal = (taxAmt * subTotal) / 100;
//                            productTotal.setText("" + subTotal);
//                            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            Log.v("+Total", "=" + subTotal);
//                            productTotal.setText("" + subTotal);
//                            Log.v("+PRDTaxAMT", String.valueOf(valueTotal));
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal),
//                                    String.valueOf(valueTotal), String.valueOf(finalPrice));
//                            Log.v("ifupdatedval++",   String.valueOf(valueTotal)+String.valueOf(finalPrice));
//                        } else {
//
//                            if(task.getTax_Value().equals("")||task.getTax_Value().equals("0")){
//                                productTaxAmt.setText("0");
//                                valueTotal=Float.parseFloat("0.0");//new code added jul87
//
//                            }else {
//                                productTaxAmt.setText(task.getTax_amt());
//                                valueTotal=Float.parseFloat(task.getTax_amt());//new code added jul 8
//                            }
//                            //if no tax
//                            if(task.getDis_amt().equals("")||task.getDis_amt().equals("0")){
//                                productDisAmt.setText("0");
//                                finalPrice=Float.parseFloat("0.0");//new code added jly 8
//                            }else {
//                                productDisAmt.setText(task.getDis_amt());
//                                finalPrice=Float.parseFloat(task.getDis_amt());//new code added jul 8
//                            }
//                            //new code added-jul8
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal),
//                                    String.valueOf(valueTotal), String.valueOf(finalPrice));
//                            //   productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                        }
//
//                        Log.e("taxamt+", task.getTax_amt());
//                        //   updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), task.getTax_amt(), task.getDis_amt());
//
//                        Log.v("updatedval++",  task.getTax_amt()+ task.getDis_amt());
//                    } else {
//                        productDis.setText("0");
//                        productDisAmt.setText("0");
//
////new code tax start
//                        if (tax != 0.0) {
//                            taxAmt = 100 + tax;
//                            Log.v("++++TaxAMT1", String.valueOf(taxAmt));
//                            subTotal = (taxAmt * subTotal) / 100;
//                            productTotal.setText("" + subTotal);
//                            Log.e("+++++Total_Distcount1", "=" + subTotal);
//                            Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                            Log.e("+++++Total_calctcode1", "=" + task.getProduct_Cat_Code());
//                            Log.e("++++++Total_product1", "=" + mProductCount.getText().toString());
//                            valueTotal = subTotal - calculate;
//                            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            Log.v("+PRDTaxAMT1", String.valueOf(valueTotal));
//                        } else {
//                            productTaxAmt.setText(task.getTax_amt());
//                        }
//                        //new code tax stop
//                        prdDisAmt=task.getDis_amt();
//                        if(task.getDis_amt().equals("")||task.getDis_amt().equals("0")){
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), "0");
//                            Log.v("updatedval++else",   String.valueOf(valueTotal)+ prdDisAmt);
//
//                        }else{
//                            updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), task.getDis_amt());
//                            Log.v("updatedval++else",    String.valueOf(valueTotal)+ task.getDis_amt());
//                        }
//                    }
//
//                    //  updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
//                }else {//new start
//                    if (tax != 0.0) {
//                        taxAmt = 100 + tax;
//                        Log.v("q+TaxAMT1", String.valueOf(taxAmt));
//                        subTotal = (taxAmt * subTotal) / 100;
//                        productTotal.setText("" + subTotal);
//                        Log.e("q+Total_Distcount1", "=" + subTotal);
//                        Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                        Log.e("q+Total_calctcode1", "=" + task.getProduct_Cat_Code());
//                        Log.e("q+Total_product1", "=" + mProductCount.getText().toString());
//                        valueTotal = subTotal - calculate;
//                        productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                        Log.v("q+PRDTaxAMT1", String.valueOf(valueTotal));
//                    } else {
//                        if(task.getTax_amt().equals("")||task.getTax_amt().equals("0")){
//                            productTaxAmt.setText("0");
//                            valueTotal=Float.parseFloat("0.0");//new july 8
//                        }else {
//                            productTaxAmt.setText(task.getTax_amt());
//                            valueTotal=Float.parseFloat(task.getTax_amt());
//                        }
//                        //  productTaxAmt.setText(task.getTax_amt());
//                    }
//
//                    Log.e("5Taxamt",task.getTax_amt().toString());
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
//                Scheme = task.getSchemeProducts().getScheme();
//                String proCnt = String.valueOf(ProductCount);
//                Log.v("countcount_Click", String.valueOf(proCnt));
//                Log.v("countcount_Click_SCHEME", Scheme);
//
//
//                if (ProductCount >= 0) {
//                    String prdDisAmt;
//                    mProductCount.setText("" + ProductCount);
//                    subTotal = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                    Log.v("+++subtotal", String.valueOf(subTotal));
//                    productItem.setText(mProductCount.getText().toString());
//
////                disValue = Float.valueOf(task.getSchemeProducts().getDiscountvalue());
////                Log.v("+++disvalue", String.valueOf(disValue));
//                    if("".equals(task.getSchemeProducts().getDiscountvalue())||
//                            ("0").equals(task.getSchemeProducts().getDiscountvalue())
//                    ){
//                        productDisAmt.setText("0");
//                        finalPrice=0;
//                    }
//                    productTotal.setText("" + subTotal);
//                    tax = Float.valueOf(task.getTax_Value());
//                    if (!task.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
//                        schemCount = Integer.parseInt(task.getSchemeProducts().getScheme());
//                        if (ProductCount == schemCount || ProductCount > schemCount) {
//
//                            disValue = Float.valueOf(task.getSchemeProducts().getDiscountvalue());
//                            Log.v("+disvalue", String.valueOf(disValue));
//                            if(disValue.equals("0")||disValue.equals("")){
//                                productDisAmt.setText("0");
//                                finalPrice=0;
//                            }else {
//                                productDis.setText(task.getSchemeProducts().getDiscountvalue());
//                                finalPrice = (subTotal * disValue) / 100;
//                                Log.v("+finalPrice*disvalue", String.valueOf(finalPrice));
//                                productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                            }
//                            if (tax != 0.0) {
//                                taxAmt = 100 + tax;
//                                Log.v("+TaxAMT", String.valueOf(taxAmt));
//                                subTotal=subTotal-finalPrice;
//                                Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                                Log.e("+Total_calculatecatcode", "=" + task.getProduct_Cat_Code());
//                                Log.e("+Total_product", "=" +mProductCount.getText().toString());
//                                Log.v("+Subtotafterdis", String.valueOf(subTotal));
//                                Log.v("+calcu",calculate.toString());
//                                valueTotal = subTotal/Float.parseFloat(mProductCount.getText().toString());
//                                subTotal = (taxAmt * subTotal) / 100;
//                                productTotal.setText("" + subTotal);
//                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                                Log.v("+Total", "=" + subTotal);
//                                productTotal.setText("" + subTotal);
//                                Log.v("+PRDTaxAMT", String.valueOf(valueTotal));
//                                updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal),
//                                        String.valueOf(valueTotal), String.valueOf(finalPrice));
//                                Log.v("ifupdatedval++",   String.valueOf(valueTotal)+String.valueOf(finalPrice));
//                            } else {
//                                productTaxAmt.setText(task.getTax_amt());
//                                //if no tax
//                                if(task.getDis_amt().equals("")||task.getDis_amt().equals("0")){
//                                    productDisAmt.setText("0");
//                                }else {
//                                    productDisAmt.setText(task.getDis_amt());
//                                }
//
//                                //   productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                            }
//
//                            Log.e("taxamt+", task.getTax_amt());
//                            //   updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), task.getTax_amt(), task.getDis_amt());
//
//                            Log.v("updatedval++",  task.getTax_amt()+ task.getDis_amt());
//                        } else {
//                            productDis.setText("0");
//                            productDisAmt.setText("0");
//
////new code tax start
//                            if (tax != 0.0) {
//                                taxAmt = 100 + tax;
//                                Log.v("++++TaxAMT1", String.valueOf(taxAmt));
//                                subTotal = (taxAmt * subTotal) / 100;
//                                productTotal.setText("" + subTotal);
//                                Log.e("+++++Total_Distcount1", "=" + subTotal);
//                                Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                                Log.e("+++++Total_calctcode1", "=" + task.getProduct_Cat_Code());
//                                Log.e("++++++Total_product1", "=" + mProductCount.getText().toString());
//                                valueTotal = subTotal - calculate;
//                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                                Log.v("+PRDTaxAMT1", String.valueOf(valueTotal));
//                            } else {
//                                productTaxAmt.setText(task.getTax_amt());
//                            }
//                            //new code tax stop
//                            prdDisAmt=task.getDis_amt();
//                            if(task.getDis_amt().equals("")||task.getDis_amt().equals("0")){
//                                updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), "0");
//                                Log.v("updatedval++else",   String.valueOf(valueTotal)+ prdDisAmt);
//
//                            }else{
//                                updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), task.getDis_amt());
//                                Log.v("updatedval++else",    String.valueOf(valueTotal)+ task.getDis_amt());
//                            }
//                        }
//
//                        //  updateTask(mContact, mProductCount.getText().toString(), String.valueOf(subTotal), String.valueOf(valueTotal), String.valueOf(finalPrice));
//                    }else {//new start
//                        if (tax != 0.0) {
//                            taxAmt = 100 + tax;
//                            Log.v("q+TaxAMT1", String.valueOf(taxAmt));
//                            subTotal = (taxAmt * subTotal) / 100;
//                            productTotal.setText("" + subTotal);
//                            Log.e("q+Total_Distcount1", "=" + subTotal);
//                            Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
//                            Log.e("q+Total_calctcode1", "=" + task.getProduct_Cat_Code());
//                            Log.e("q+Total_product1", "=" + mProductCount.getText().toString());
//                            valueTotal = subTotal - calculate;
//                            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            Log.v("q+PRDTaxAMT1", String.valueOf(valueTotal));
//                        } else {
//                            productTaxAmt.setText(task.getTax_amt());
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
            productDis.setText(""+ disValue);
            subTotal = subTotal - finalPrice;
            Log.v("finalPriceAfterDis", String.valueOf(subTotal));
            subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
            productTotal.setText("" + subTotal);


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
                subTotal= Float.valueOf(new DecimalFormat("##.##").format(subTotal));
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
//                        valueTotal=Float.parseFloat(task.getTax_amt());//new code added jul 8
//                    }
                    //if no tax


//                    if(Float.parseFloat(disValue).e"")||disValue=="0.0")){
//                        productDisAmt.setText("0");
//                        finalPrice=Float.parseFloat("0.0");//new code added jly 8
//                    }
//                    else {
//                        productDisAmt.setText(task.getDis_amt());
//                        finalPrice=Float.parseFloat(task.getDis_amt());//new code added jul 8
//                    }
                    //jul 11

                    if (!task.getSchemeProducts().getScheme().equalsIgnoreCase("")) {
                        schemCount = Integer.parseInt(task.getSchemeProducts().getScheme());
                        ProductCount = Integer.parseInt(mProductCount.getText().toString());
                        Log.e("prdcount1", String.valueOf(ProductCount));
                        Log.e("scheme2", String.valueOf(schemCount));
                        if (ProductCount == schemCount || ProductCount > schemCount||schemCount<ProductCount) {
                            disValue = Float.parseFloat(task.getSchemeProducts().getDiscountvalue());
                            Log.v("+disvalueshow77", String.valueOf(disValue));
                            Log.v("getdis",task.getDiscount());
                            if( disValue!=0|| disValue!=0.0){
                           // if(!task.getSchemeProducts().getDiscountvalue().equals("0")||!task.getSchemeProducts().getDiscountvalue().equals("0.0")) {
                                productDis.setText(task.getSchemeProducts().getDiscountvalue());
                                finalPrice = (subTotal * disValue) / 100;
                                Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));
                                subTotal=subTotal-finalPrice;
                                Log.v("disamttotshow",String.valueOf(finalPrice));
                                productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
                                productTotal.setText("" +  new DecimalFormat("##.##").format(subTotal));//new july 10
                            }
                          else  if(task.getDiscount().equals("")||task.getDiscount().equals("0.0")||task.getDiscount().equals("0")){


                             // if(task.getDis_amt().equals("0")||task.getDis_amt().equals("")||task.getDis_amt().equals("0.0")){
                              task.setDis_amt("0.0");
                              task.setDiscount("0");
                              productDisAmt.setText("0");
                                  finalPrice=0;
                                  productDis.setText("0");
                                  if(subTotal == Float.parseFloat("0.0")||subTotal==Float.parseFloat("0")){
                                      subTotal=Float.parseFloat("0.0");
                                      productTotal.setText("0.0");//new july10
                                  }else {
                                      productTotal.setText("" + subTotal);//new july10
                                  }
                           //   }
                          }else{


                            }
                            if (tax != 0.0) {
                                taxAmt = 100 + tax;
                                Log.v("+TaxAMTll", String.valueOf(taxAmt));
                                // subTotal=subTotal-finalPrice;
                                Log.v("disamt",String.valueOf(finalPrice));
                                Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
                                Log.v("+Total_tcodellshow", "=" + task.getProduct_Cat_Code());
                                Log.v("+Total_productllshow", "=" +mProductCount.getText().toString());
                                Log.v("+Subtotafterdisllshow", String.valueOf(subTotal));
                                //  Log.v("+calcull",calculate.toString());
                                // valueTotal = subTotal/Float.parseFloat(mProductCount.getText().toString());//working code commented
                                valueTotal = subTotal*tax/100;
                                subTotal = (taxAmt* subTotal) / 100;
                                //   valueTotal = subTotal - calculate;//working code commented
                                productTotal.setText("" + subTotal);//workig code commented
                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                                Log.v("+Totalshow", "=" + subTotal);
                                // productItemTotal.setText("" + subTotal);
                                Log.v("+PRDTaxAMTshow", String.valueOf(valueTotal));

                                Log.v("ifupdatedval++show",   subTotal+""+String.valueOf(valueTotal)+String.valueOf(finalPrice));
                            } else {
                                if(task.getTax_Value().equals("")||task.getTax_Value().equals("0")){
                                    productTaxAmt.setText("0");
                                    valueTotal=Float.parseFloat("0.0");//new code added jul87

                                }else {
                                    productTaxAmt.setText(task.getTax_amt());
                                    valueTotal=Float.parseFloat(task.getTax_amt());//new code added jul 8
                                }

                                Log.e("cntup00show",subTotal+""+finalPrice+""+valueTotal);


                            }


                        } else {
                            //july12
                            disValue = Float.valueOf(task.getSchemeProducts().getDiscountvalue());
                            Log.v("+00disvalueelseshow", String.valueOf(task.getDiscount()));
                          //  if(task.getDiscount().equals("")||task.getDiscount().equals("0.0")||task.getDiscount().equals("0")){
                                // if(("0").equals(disValue)||("").equals(disValue)){
                                task.setDiscount("0.0");
                                task.setDis_amt("0.0");

                                productDisAmt.setText("0");
                                finalPrice=0;
                                productDis.setText("0");

                                if(subTotal==Float.parseFloat("0")||subTotal==Float.parseFloat("0.0")){

                                    subTotal=Float.parseFloat("0.0");
                                    productTotal.setText("0.0");//new july10
                                }else {
                                    subTotal = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());

                                    productTotal.setText("" + subTotal);//new july10
                                }
                         //   }else {
//                                disValue = Float.valueOf(task.getSchemeProducts().getDiscountvalue());
//                                productDis.setText(task.getSchemeProducts().getDiscountvalue());
//                                finalPrice = (subTotal * disValue) / 100;
//                                Log.v("+finalPrice*disvueshow", String.valueOf(finalPrice));
//                                subTotal=subTotal-finalPrice;
//                                Log.v("disamttotshow",String.valueOf(finalPrice));
//                                productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                                productTotal.setText("" + subTotal);//new july 10
                          //  }
                            //jul 12
//finalPrice=0;
//                    productDis.setText("0");
//                   productDisAmt.setText("0");

//new code tax start
                            if (tax != 0.0) {
                                taxAmt = 100 + tax;
                                Log.v("++++TaxAMT1", String.valueOf(taxAmt));
                                subTotal = (taxAmt * subTotal) / 100;
                                productTotal.setText("" + new DecimalFormat("##.##").format(subTotal));
                                Log.v("+++Total_Dist1show2", "=" + subTotal);
                                Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
                                Log.v("+++Total_calode1show2", "=" + task.getProduct_Cat_Code());
                                Log.v("++++Total_product1show2", "=" + mProductCount.getText().toString());
                                if(!("0").equals(disValue
                                )|| !(("").equals(disValue)))  {
                                    valueTotal =( subTotal-calculate)+finalPrice;
                                    Log.v("+PRDTaxAMTdisshow2", String.valueOf(finalPrice));
                                    Log.v("+PRDTaxAMT1+disshow2", String.valueOf(valueTotal));
                                }else{
                                    valueTotal = subTotal - calculate;
                                }
                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                                Log.v("+PRDTaxAMT1show2", String.valueOf(valueTotal));
                            } else {
                                valueTotal=Float.parseFloat(task.getTax_amt());
                                productTaxAmt.setText(task.getTax_amt());
                                subTotal = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
                                Log.v("+++subtotalwtaXshow2", String.valueOf(subTotal));
                                String subTotall= new DecimalFormat("##.##").format(subTotal);
                                productTotal.setText(String.valueOf(subTotall));
                            }


                        }


                    }








                    else {//new start
                        if (tax != 0.0) {
                            taxAmt = 100 + tax;
                            Log.v("q+TaxAMT1", String.valueOf(taxAmt));
                            subTotal = (taxAmt * subTotal) / 100;

                            // productItemTotal.setText("" + subTotal);
                            Log.v("q+Total_Distcount1", "=" + subTotal);
                            Float calculate = Float.parseFloat(mProductCount.getText().toString()) * Float.parseFloat(task.getProduct_Cat_Code());
                            Log.v("q+Total_calctcode1", "=" + task.getProduct_Cat_Code());
                            Log.v("q+Total_product1", "=" + mProductCount.getText().toString());
                            valueTotal = subTotal - calculate;
                            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
                            productTotal.setText("" + subTotal);
                            Log.v("q+PRDTaxAMT1", String.valueOf(valueTotal));
                        } else {
                            if(task.getTax_amt().equals("")||task.getTax_amt().equals("0")){
                                productTaxAmt.setText("0");
                                valueTotal=Float.parseFloat("0.0");//new july 8
                            }else {
                                productTaxAmt.setText(task.getTax_amt());
                                valueTotal=Float.parseFloat(task.getTax_amt());
                            }
                            productTotal.setText("" + subTotal);
                            //  productTaxAmt.setText(task.getTax_amt());
                        }

                        Log.v("5Taxamt",task.getTax_amt().toString());
                        Log.v("55taxamt",valueTotal.toString());
                        Log.v("55update",subTotal+""+valueTotal.toString()+finalPrice);
                         }//new stop
                }  //jul 11
                    
                    
                    
                    //new code july 9 start
//                    if (ProductCount == schemCount || ProductCount > schemCount) {
//                        finalPrice = (subTotal * disValue) / 100;
//                        Log.v("finalPrice", String.valueOf(finalPrice));
//                        productDisAmt.setText("" + new DecimalFormat("##.##").format(finalPrice));
//                        productDis.setText("" + disValue);
//                        Log.e("taxx",tax.toString());
//                        //new code tax jyly 9
//                        if (tax != 0.0) {
//                            Log.e("initil ttt",tax.toString());
//                            taxAmt = 100 + tax;
//                            Log.v("tax3Discount", String.valueOf(taxAmt));
//                           // subTotal = (taxAmt * subTotal) / 100;
//
//                            Log.e("Tss", "=" + subTotal);
//
//                            if(finalPrice!=0) {
//                               // subTotal = subTotal - finalPrice;//dis calculatihj july 9
//                                valueTotal = tax * subTotal / 100;
//                                Log.e("tota2222l", String.valueOf(subTotal));
//                                Log.e("taxcalyy", valueTotal.toString());
//                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            }else{
//                                subTotal = (taxAmt * subTotal) / 100;
//                                Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
//                                Log.e("calculate",calculate.toString());
//                                valueTotal = subTotal - calculate;
//                                Log.e("tax220", valueTotal.toString());
//                                productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                            }
//
//
//
//
//                           // subTotal = (taxAmt * subTotal) / 100;//original
//
//
//                            //        productTotal.setText("" + subTotal);
//                         //   Log.e("Total_Distcount", "=" + subTotal);
////                            Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
////                         Log.e("calculate",calculate.toString());
////                            valueTotal = subTotal - calculate;
//                           // Log.e("tax220", valueTotal.toString());
//                           // productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                        } else {
//                            if (task.getTax_amt().equals("") || task.getTax_amt().equals("0")) {
//                                task.setTax_amt("0");
//                                productTaxAmt.setText(task.getTax_amt());
//                                Log.e("gtax", task.getTax_amt().toString());
//                            }
//                            productTaxAmt.setText(task.getTax_amt());
//                        }
//                        //new code tax july 9
//                       // subTotal = subTotal - finalPrice;
//                        Log.v("finalPriceAfterDis33", String.valueOf(subTotal));
//                        productTotal.setText("" + subTotal);
//
//
//                    } else {
//                        finalPrice = 0;
//                        productDis.setText("0");
//                        productDisAmt.setText("" + new DecimalFormat("##.##").format(0));
//                        //   }
//                        //new code start july 9
//                        if (tax != 0.0) {
//                            taxAmt = 100 + tax;
//                            Log.v("tax3Discount", String.valueOf(taxAmt));
//                            subTotal = (taxAmt * subTotal) / 100;
//                            productTotal.setText("" + subTotal);
//                            Log.e("Total_Distcount", "=" + subTotal);
//                            Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString());
//                            valueTotal = subTotal - calculate;
//                            Log.e("tax220", valueTotal.toString());
//                            productTaxAmt.setText("" + new DecimalFormat("##.##").format(valueTotal));
//                        } else {
//                            if (task.getTax_amt().equals("") || task.getTax_amt().equals("0")) {
//                                task.setTax_amt("0");
//                                productTaxAmt.setText(task.getTax_amt());
//                                Log.e("gtax", task.getTax_amt().toString());
//                            }
//                            productTaxAmt.setText(task.getTax_amt());
//                            productTotal.setText("" + subTotal);
//                        }
//                    }
                        //new code july 9 stop


            //    }//note for july 9 stop include this too
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
