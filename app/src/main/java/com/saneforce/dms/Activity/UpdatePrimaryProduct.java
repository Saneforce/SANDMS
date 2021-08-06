package com.saneforce.dms.Activity;

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

import androidx.appcompat.app.AppCompatActivity;


import com.saneforce.dms.Model.PrimaryProduct;

import com.saneforce.dms.R;
import com.saneforce.dms.Utils.Constants;
import com.saneforce.dms.Utils.PrimaryProductDatabase;
import com.saneforce.dms.Utils.PrimaryProductViewModel;
import com.saneforce.dms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

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
    String discountValue = "";

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

        Intent intent = getIntent();
        if(intent.hasCategory("Scheme"))
            Scheme = task.getSelectedScheme();

        if(intent.hasCategory("discount"))
            discountValue = task.getSelectedDisValue();

        loadTask(task);

        ProductCount = Integer.parseInt(mProductCount.getText().toString());
        subTotal = Float.parseFloat(productQty.getText().toString()) *
                Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
        Log.v("total", String.valueOf(subTotal));
        float subTT = subTotal + Float.parseFloat(productTaxAmt.getText().toString());
        productTotal.setText("" + subTT);

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
                Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
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
           // Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
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
                subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
                Log.v("PRODUCT_COUNT", String.valueOf(subTotal));
                //updateTask(task);
                tax = Float.valueOf(productTax.getText().toString());
                if (tax != 0.0) {
                    taxAmt = 100 + tax;
                    Log.v("Discounttax1", String.valueOf(taxAmt));
                    subTotal = (taxAmt * subTotal) / 100;
                    productTotal.setText("" + subTotal);
                    Log.e("Total_Distcount", "=" + subTotal);
                    Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
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
                    subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
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
                        Float calculate = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
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
                if(mProductCount.getText().toString().equals(""))
                    return;

                ProductCount = Integer.parseInt(mProductCount.getText().toString());

                if (mProductCount.getText().toString().equalsIgnoreCase("")) {
                    productQty.setText("0");
                    subTotal = 0;
                    productTotal.setText("" + subTotal);
                    productTaxAmt.setText("" + new DecimalFormat("##.##").format(subTotal));
                    //  updateTask(task);

                } else {
                    productQty.setText(mProductCount.getText().toString());
                    subTotal = Float.parseFloat(productQty.getText().toString()) * Float.parseFloat(productPrice.getText().toString().replaceAll("Rs:", ""));
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


                    if (!Scheme.equalsIgnoreCase("")) {
                        schemCount = Integer.parseInt(Scheme);
                        ProductCount = Integer.parseInt(mProductCount.getText().toString());
                        Log.e("prdcount1", String.valueOf(ProductCount));
                        Log.e("scheme2", String.valueOf(schemCount));
                        if (ProductCount == schemCount || ProductCount > schemCount||schemCount<ProductCount) {
                            disValue = Float.parseFloat(discountValue);
                            Log.v("+disvalueshow77", String.valueOf(disValue));
                            Log.v("getdis",task.getDiscount());
                            if( disValue!=0|| disValue!=0.0){
                           // if(!task.getSchemeProducts().getDiscountvalue().equals("0")||!task.getSchemeProducts().getDiscountvalue().equals("0.0")) {
                                productDis.setText(discountValue);
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
                            disValue = Float.valueOf(discountValue);
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
//                                disValue = Float.valueOf(discountValue);
//                                productDis.setText(discountValue);
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

                if(ProductCount>0)
                updateSchemeData(task.getSchemeProducts(), ProductCount, task);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateSchemeData(task.getSchemeProducts(), ProductCount, task);
    }

    private void loadTask(PrimaryProduct task) {
        productName.setText(task.getPname());
        double itemPrice = 0;
//        if(totalAmt==0)
        itemPrice = Double.parseDouble(task.getProduct_Cat_Code())*task.getProduct_Sale_Unit_Cn_Qty();

        productPrice.setText(Constants.roundTwoDecimals(itemPrice));
        productQty.setText(task.getTxtqty());
        mProductCount.setText(task.getQty());
        productTax.setText(task.getTax_Value());
        productDis.setText(discountValue);
        productUnit.setText(task.getProduct_Sale_Unit());
       if(task.getTax_amt().equals("")||task.getTax_amt().equals("0")){
           productTaxAmt.setText("0");
           task.setTax_amt("0");
       }else {
           productTaxAmt.setText(task.getTax_amt());
       }
        if(!discountValue.equalsIgnoreCase("")){
            productDis.setText(discountValue);
            disPercent = Float.valueOf(discountValue);
            if (disPercent != 0) {
                productDisAmt.setText(task.getDis_amt());
            } else {
                task.setDis_amt("0");
            //    productDisAmt.setText("0");
                productDisAmt.setText(task.getDis_amt());
            }
            disValue = Float.parseFloat(discountValue);
        }

        if(!Scheme.equalsIgnoreCase("")) {
            schemCount = Integer.parseInt(Scheme);
        }

        tax = Float.valueOf(task.getTax_Value());
//        Scheme = task.getSchemeProducts().getScheme();
    }

    public void UpdateProduct(View v) {
        updateSchemeData(task.getSchemeProducts(), ProductCount, task);
        finish();
    }

    private void updateTask(final PrimaryProduct task, float subTotal, Float valueTotal, float finalPrice) {
        class UpdateTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                task.setQty(mProductCount.getText().toString());
                task.setTxtqty(mProductCount.getText().toString());
                task.setSubtotal(String.valueOf(finalPrice));
                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(task.getPID(),
                                mProductCount.getText().toString(),
                                mProductCount.getText().toString(),
                                String.valueOf(subTotal),
                                task.getTax_Value(),
                                task.getDiscount(),
                                String.valueOf(valueTotal),
                                discountValue,
                                task.getSelectedScheme(),
                                task.getSelectedDisValue(),
                                task.getSelectedFree(),
                                task.getOff_Pro_code(),
                                task.getOff_Pro_name(),
                                task.getOff_Pro_Unit(),
                                task.getOff_disc_type(),
                                task.getOff_free_unit());
                return null;
            }
//String.valueOf(finalPrice)
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }


    PrimaryProduct.SchemeProducts selectedScheme = null;

    private void updateSchemeData(List<PrimaryProduct.SchemeProducts> schemeProducts, int qty, PrimaryProduct mContact) {
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
        subTotal = subTotal * mContact.getProduct_Sale_Unit_Cn_Qty();
*/


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

        String discountType = "";

        double discountValue = 0;
        double productAmt = 0;
        double schemeDisc = 0;
        String OffFreeUnit = "";

        if(selectedScheme != null){
//            workinglist.get(position).setSelectedScheme(selectedScheme.getScheme());
            task.setSelectedScheme(selectedScheme.getScheme());
            if(selectedScheme.getFree_Unit()!=null)
                OffFreeUnit = selectedScheme.getFree_Unit();

//            workinglist.get(position).setSelectedDisValue(selectedScheme.getDiscountvalue());
            task.setSelectedDisValue(selectedScheme.getDiscountvalue());

//            workinglist.get(position).setOff_Pro_code(selectedScheme.getProduct_Code());
            task.setOff_Pro_code(selectedScheme.getProduct_Code());

//            workinglist.get(position).setOff_Pro_name(selectedScheme.getProduct_Name());
            task.setOff_Pro_name(selectedScheme.getProduct_Name());

//            workinglist.get(position).setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            task.setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            discountType= selectedScheme.getDiscount_Type();


//            if(discountType.equals("Rs"))
//                holder.ll_disc.setVisibility(View.GONE);
//            else
//                holder.ll_disc.setVisibility(View.VISIBLE);

            String packageType = selectedScheme.getPackage();

            double freeQty = 0;
            double packageCalc = (tempQty/Double.parseDouble(selectedScheme.getScheme()));

            if(packageType.equals("Y"))
                packageCalc = (int)packageCalc;

            if(!selectedScheme.getFree().equals(""))
                freeQty = packageCalc * Integer.parseInt(selectedScheme.getFree());
            freeQty = (int) freeQty;
//            workinglist.get(position).setSelectedFree(String.valueOf(freeQty));
            task.setSelectedFree(String.valueOf((int) freeQty));

//            holder.tv_free_qty.setText(String.valueOf(freeQty));



            if(task.getProduct_Cat_Code()!=null && !task.getProduct_Cat_Code().equals(""))
                productAmt = Double.parseDouble(task.getProduct_Cat_Code());

            if(selectedScheme.getDiscountvalue()!=null && !selectedScheme.getDiscountvalue().equals(""))
                schemeDisc = Double.parseDouble(selectedScheme.getDiscountvalue());

            switch (discountType){
                case "%":
                    discountValue = (productAmt * (tempQty * product_Sale_Unit_Cn_Qty)) * (schemeDisc/100);
//                    holder.ll_disc.setVisibility(View.VISIBLE);
//                    holder.ProductDis.setText(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
//                    holder.ProductDisAmt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                    break;
                case "Rs":
                    discountValue = ((double) tempQty/Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
//                    holder.ProductDisAmt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
//                    holder.ll_disc.setVisibility(View.GONE);
                    break;
//                default:
            }

//            discountValue = discountValue*product_Sale_Unit_Cn_Qty;

            if(discountValue>0){
//                workinglist.get(position).setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
                task.setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));

//                workinglist.get(position).setDis_amt(Constants.roundTwoDecimals(discountValue));
                task.setDis_amt(Constants.roundTwoDecimals(discountValue));
                task.setSelectedDisValue(Constants.roundTwoDecimals(discountValue));
/*
                totalAmt = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) -discountValue;
                holder.ll_disc_reduction.setVisibility(View.VISIBLE);
                holder.tv_disc_amt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                holder.tv_disc_amt_total.setText(String.valueOf(Constants.roundTwoDecimals(totalAmt)));*/

            }else {
                task.setDiscount("0");
                task.setDis_amt("0");
            }

        }else {
//            holder.ll_disc.setVisibility(View.VISIBLE);
//            holder.tv_free_qty.setText("0");

//            viewHolder.tv_dis.setText(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
//            viewHolder.dis_amount.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
            task.setDiscount("0");
            task.setDis_amt("0");
            task.setSelectedFree("0");
            task.setSelectedScheme("");
            task.setSelectedDisValue("0");
            task.setOff_Pro_code("0");
            task.setOff_Pro_name("0");
            task.setOff_Pro_Unit("0");

        }

        task.setOff_free_unit(OffFreeUnit);

        double totalAmt = 0;
        double taxPercent = 0;
        double taxAmt = 0;

        try {
            totalAmt = Double.parseDouble(task.getProduct_Cat_Code()) * (qty *product_Sale_Unit_Cn_Qty);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            taxPercent = Double.parseDouble(task.getTax_Value());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double itemPrice = 0;
//        if(totalAmt==0)
            itemPrice = Double.parseDouble(task.getProduct_Cat_Code())*product_Sale_Unit_Cn_Qty;
//        else
//            itemPrice = totalAmt;

        productPrice.setText("Rs:" + Constants.roundTwoDecimals(itemPrice));
        productQty.setText(String.valueOf(qty));

//        holder.productItemTotal.setText(Constants.roundTwoDecimals(totalAmt));


//        holder.ProductTax.setText(String.valueOf(taxPercent));

        try {
            taxAmt =  (totalAmt- discountValue) * (taxPercent/100);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        holder.ProductTaxAmt.setText(Constants.roundTwoDecimals(taxAmt));
        productTotal.setText(Constants.roundTwoDecimals(((totalAmt - discountValue) + taxAmt)));
        subTotal = (float) totalAmt;
        valueTotal = (float) taxAmt;
        finalPrice = (float) ((totalAmt - discountValue) + taxAmt);

        updateTask(task, subTotal, valueTotal, finalPrice);
    }


}
