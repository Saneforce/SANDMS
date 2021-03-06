package com.saneforce.dms.adapter;

import static io.realm.Realm.getApplicationContext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.saneforce.dms.activity.UpdatePrimaryProduct;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.model.PrimaryProduct;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.AlertDialogBox;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.PrimaryProductDatabase;
import com.saneforce.dms.utils.Shared_Common_Pref;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomViewAdapter extends RecyclerView.Adapter<CustomViewAdapter.MyViewHolder> {
    String subtotal;

    Shared_Common_Pref mShared_common_pref;
//    Float quntaity, price;
    private List<PrimaryProduct> mProduct_arrays = new ArrayList<>();
    DMS.viewProduct viewProd;
    Context context;
//    AlertDialog.Builder builder;
//    String productname;
//    String catName;
//    String catImg;
//    String productID;
//    String productcode;
//    Float productqty;
//    Float productRate;
//    Product_Array newProductArray;
//    ArrayList<PrimaryProduct> Product_Array_List;
    Shared_Common_Pref shared_common_pref;
    String GrandTotal;
    int gtotal;
//    float sum = 0;
    TextView viewTotal;
//    ArrayList<String> totalArray = new ArrayList<>();
    // viewProduct mProducrtDelete;
    int orderType;
    public CustomViewAdapter(Context context ,String grandTotal,TextView viewTotal,int orderType, DMS.viewProduct viewProd) {
        this.context = context;
        this.viewProd = viewProd;
        this.GrandTotal=grandTotal;
        this.viewTotal=viewTotal;
        this.orderType=orderType;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_item_viewcart_new, null, false);


        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PrimaryProduct mProductArray = mProduct_arrays.get(position);
        shared_common_pref = new Shared_Common_Pref(context);
        String productid=mProductArray.getPID();
//        mShared_common_pref.clear_pref("SubTotal");
        //  mShared_common_pref.clear_pref("ItemTotal");
        holder.txtCatName.setText(mProductArray.getName());

        holder.child_product_price_total.setPaintFlags(holder.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtPrice.setText(mProductArray.getProduct_Cat_Code());
//        holder.txtQty.setText(mProductArray.getQty());
//        holder.editCount.setText("" + mProductArray.getQty());

//        float  qty, rate;
        float taxAmount;
//        qty = Float.parseFloat(mProductArray.getQty());
//        rate = Float.parseFloat(mProductArray.getProduct_Cat_Code());
        taxAmount=Float.parseFloat( mProduct_arrays.get(position).getTax_amt());
        //   taxAmount = Float.parseFloat(mProductArray.getTax_amt());
//        Log.v("taxcart", String.valueOf(taxAmount));
//        Log.v("discart",   mProduct_arrays.get(position).getDis_amt());
//        Log.v("totalcart",mProductArray.getSubtotal());

/*        if( mProduct_arrays.get(position).getDis_amt().equals("0")||mProduct_arrays.get(position).getDis_amt().equals("")){
            holder.dis_amount.setText("0");
        }else{
            holder.dis_amount.setText(mProduct_arrays.get(position).getDis_amt());
        }*/
        if( mProduct_arrays.get(position).getTax_amt().equals("0")||mProduct_arrays.get(position).getTax_amt().equals("")){
            holder.tax_amount.setText("Rs." +"0");
        }else {
            holder.tax_amount.setText("Rs." + new DecimalFormat("##.##").format(taxAmount));

        }

        holder.totalAmount.setText("Rs." + Constant.roundTwoDecimals(Double.parseDouble(mProductArray.getSubtotal())));

        holder.tv_free_qty.setText(String.valueOf(mProductArray.getSelectedFree()));

//        holder.item_amount.setText("Rs." + new DecimalFormat("##.##").format(total));

//        GrandTotal =  mShared_common_pref.getvalue("GrandTotal");
        holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox.showDialog(v.getContext(), "", "Do you Surely want to delete this order?",
                        "Yes", "NO", false, new DMS.AlertBox() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {
                                subtotal=mProduct_arrays.get(position).getSubtotal();
                                // subtotal=new DecimalFormat("##.##").format(subtotal);
                                //  GrandTotal=new DecimalFormat("##.##").format(GrandTotal);
                                //Double ss= Double.valueOf(NumberFormat.getNumberInstance(Locale.getDefault()).format(subtotal));
                                //  Double gg= Double.valueOf(NumberFormat.getNumberInstance(Locale.getDefault()).format(GrandTotal));
//Double gg=Double.parseDouble(GrandTotal)-Double.parseDouble(subtotal);
                                // viewTotal.setText(String.valueOf(gg));

                                // Log.v("prdid1", String.valueOf(gg));
                                mShared_common_pref = new Shared_Common_Pref(context);
                                mShared_common_pref.save("SubTotal", subtotal);
                                //  mShared_common_pref.save("ItemTotal", mProduct_arrays.get(position).getQty());
                                //  Log.v("prdid2",GrandTotal);
                                Log.v("prdid",productid);
                                // gtotal= GrandTotal-subtotal;
                                //  GrandTotal = String.valueOf(GrandTotal) - String.valueOf(subtotal);
                                //  gtotal= Integer.parseInt(GrandTotal)-Integer.parseInt(subtotal);
                                Log.v("prdid3", String.valueOf(gtotal));
                                // GrandTotal = GrandTotal - subtotal;
                                deleteTask(mProductArray,productid,subtotal);
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

            }
        });
        holder.editProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox.showDialog(v.getContext(), "", "Do you want to  edit this order?",
                        "Yes", "NO", false, new DMS.AlertBox() {
                            @Override
                            public void PositiveMethod(DialogInterface dialog, int id) {

                                Log.v("prdid",productid);

                                PrimaryProduct task = mProduct_arrays.get(position);
                                Log.v("PRODUCT_LIST", new Gson().toJson(task));
                                shared_common_pref.save("task", new Gson().toJson(task));
                                // mShared_common_pref.save("SubTotal", String.valueOf(subtotal));
                                Intent intent = new Intent(context, UpdatePrimaryProduct.class);
                                context.startActivity(intent);
                            }

                            @Override
                            public void NegativeMethod(DialogInterface dialog, int id) {
                            }
                        });

            }
        });

        updateSchemeData(mProductArray.getSchemeProducts(), mProductArray.getTxtqty().equals("") ? 0 : Integer.parseInt(mProductArray.getTxtqty()), holder, position, mProductArray);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mProduct_arrays.size();
    }

    public List<PrimaryProduct> getData() {
        return mProduct_arrays;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

//        TextView txtQty;
        TextView txtPrice;
        TextView totalAmount;
        TextView txtCatName;
        ImageView productImage;
//        TextView editCount;
//        TextView item_amount;
        TextView tax_amount;
//        TextView dis_amount;
        ImageView deleteProduct;
        ImageView editProduct;

        TextView tv_free_qty;
//        TextView tv_final_total_amt;
        TextView tv_dis;
        LinearLayout ll_disc;
        TextView item_tax;
        TextView child_pro_unit;
        TextView tv_free_unit;

        TextView tv_off_label;
        TextView ProductDisAmt;
        TextView tv_qty;
        TextView child_product_price_total;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            txtQty = (TextView) itemView.findViewById(R.id.item_qty);
            txtPrice = (TextView) itemView.findViewById(R.id.item_price);
            txtCatName = (TextView) itemView.findViewById(R.id.item_product_name);
            totalAmount = (TextView) itemView.findViewById(R.id.total_amount);
//            item_amount = (TextView) itemView.findViewById(R.id.item_amount);
            tax_amount = (TextView) itemView.findViewById(R.id.item_tax_amount);
//            dis_amount = (TextView) itemView.findViewById(R.id.item_dis_amount);
            productImage = (ImageView) itemView.findViewById(R.id.image_product);
//            editCount = (TextView) itemView.findViewById(R.id.edit_qty);
            deleteProduct = (ImageView) itemView.findViewById(R.id.delete_product);
            editProduct=(ImageView) itemView.findViewById(R.id.edit_product);
            tv_free_qty= itemView.findViewById(R.id.tv_free_qty);
//            tv_final_total_amt= itemView.findViewById(R.id.tv_final_total_amt);
            ll_disc= itemView.findViewById(R.id.ll_disc);
            tv_dis= itemView.findViewById(R.id.txt_dis);
            item_tax= itemView.findViewById(R.id.item_tax);
            child_pro_unit = itemView.findViewById(R.id.child_pro_unit);
            tv_free_unit = itemView.findViewById(R.id.tv_free_unit);
            tv_off_label = itemView.findViewById(R.id.tv_off_label);
            ProductDisAmt = itemView.findViewById(R.id.txt_dis_amt);
            child_product_price_total = itemView.findViewById(R.id.child_product_price_total);

            tv_qty = itemView.findViewById(R.id.tv_qty);

        }
    }


    public void filteredContact(List<PrimaryProduct> contacts) {
        this.mProduct_arrays = contacts;
        notifyDataSetChanged();
    }


    private void deleteTask(final PrimaryProduct task, String productID,String subtotal) {
        this.subtotal=subtotal;
        // this.GrandTotal=GrandTotal;

        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(task.getPID(),
                                "0",
                                "0");

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                try {
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(context,"Sucessfully  Product Deleted",Toast.LENGTH_SHORT).show();


                mShared_common_pref = new Shared_Common_Pref(context);
                mShared_common_pref.save("SubTotal",subtotal);
                // mShared_common_pref.save("GrandTotal", String.valueOf(gtotal));
//               String GrandTotal =  mShared_common_pref.getvalue("GrandTotal");

//                Gson gson = new Gson();
//               String jsonCars = gson.toJson(Product_Array_List);
//               Log.v("Category_Data", jsonCars);
//
//                Intent mIntent = new Intent(context, ViewCartActivity.class);
//                mIntent.putExtra("list_as_string",jsonCars);
//                mIntent.putExtra("GrandTotal", String.valueOf(gtotal));
//                mIntent.putExtra("SubTotal",subtotal);
//
//                context.startActivity(mIntent);
                //  context.startActivity(new Intent(context, ViewCartActivity.class));
            }
        }

        DeleteTask ut = new  DeleteTask();
        ut.execute();
    }

    PrimaryProduct.SchemeProducts selectedScheme = null;

    private void updateSchemeData(List<PrimaryProduct.SchemeProducts> schemeProducts, int qty, MyViewHolder viewHolder, int position, PrimaryProduct contact) {
        viewHolder.tv_qty.setText(String.valueOf(qty));

        viewHolder.child_pro_unit.setText(contact.getProduct_Sale_Unit());
        int product_Sale_Unit_Cn_Qty = 1;
        if(contact.getProduct_Sale_Unit_Cn_Qty()!=0)
            product_Sale_Unit_Cn_Qty= contact.getProduct_Sale_Unit_Cn_Qty();

        int tempQty = qty * product_Sale_Unit_Cn_Qty;
/*
        double value=
        subTotal = Double.parseDouble(contact.getProduct_Cat_Code()) * contact.getProduct_Sale_Unit_Cn_Qty();

        tax = Float.valueOf(contact.getTax_Value());

        contact.getTxtqty()
        valueTotal = subTotal*tax/100;
        subTotal = (taxAmt* subTotal) / 100;
        subTotal = subTotal * contact.getProduct_Sale_Unit_Cn_Qty();


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

        String discountType = "%";

        double discountValue = 0;
        double unitDiscountValue = 0;
        double productAmt = 0;
        double schemeDisc = 0;
        String OffFreeUnit = "";

        String packageType = "";

        double itemPrice = 0;
        String displayedDis = "0";


        if(contact.getProduct_Cat_Code()!=null && !contact.getProduct_Cat_Code().equals(""))
            productAmt = Double.parseDouble(contact.getProduct_Cat_Code());

        itemPrice = productAmt * product_Sale_Unit_Cn_Qty;

        if(selectedScheme != null){
            if(selectedScheme.getFree_Unit()!=null)
                OffFreeUnit = selectedScheme.getFree_Unit();

            mProduct_arrays.get(position).setSelectedScheme(selectedScheme.getScheme());
            contact.setSelectedScheme(selectedScheme.getScheme());


//            mProduct_arrays.get(position).setSelectedDisValue(String.valueOf(discountValue));
//            contact.setSelectedDisValue(String.valueOf(discountValue));


            mProduct_arrays.get(position).setOff_Pro_code(selectedScheme.getProduct_Code());
            contact.setOff_Pro_code(selectedScheme.getProduct_Code());

            mProduct_arrays.get(position).setOff_Pro_name(selectedScheme.getProduct_Name());
            contact.setOff_Pro_name(selectedScheme.getProduct_Name());

            mProduct_arrays.get(position).setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            contact.setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            if(!selectedScheme.getDiscount_Type().equals(""))
                discountType= selectedScheme.getDiscount_Type();

/*            if(discountType.equals("Rs"))
                viewHolder.ll_disc.setVisibility(View.GONE);
            else
                viewHolder.ll_disc.setVisibility(View.VISIBLE);*/

            packageType = selectedScheme.getPackage();

            double freeQty = 0;
            if(productAmt > 0){
                double packageCalc = (tempQty/Double.parseDouble(selectedScheme.getScheme()));

                if(packageType.equals("Y"))
                    packageCalc = (int)packageCalc;

                if(!selectedScheme.getFree().equals(""))
                    freeQty = packageCalc * Integer.parseInt(selectedScheme.getFree());
            }
//            freeQty = (int) freeQty;

            mProduct_arrays.get(position).setSelectedFree(String.valueOf((int) freeQty));
            contact.setSelectedFree(String.valueOf((int) freeQty));

            viewHolder.tv_free_qty.setText(String.valueOf((int) freeQty));

        }else {

//            viewHolder.ll_disc.setVisibility(View.VISIBLE);
        viewHolder.tv_free_qty.setText("0");

        viewHolder.tv_dis.setText(String.valueOf(Constant.roundTwoDecimals(schemeDisc)));
//            viewHolder.dis_amount.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
        mProduct_arrays.get(position).setDis_amt(Constant.roundTwoDecimals(discountValue));
        contact.setDis_amt(Constant.roundTwoDecimals(discountValue));

        mProduct_arrays.get(position).setSelectedDisValue(Constant.roundTwoDecimals(discountValue));
        contact.setSelectedDisValue(Constant.roundTwoDecimals(discountValue));


        mProduct_arrays.get(position).setSelectedScheme("");
        contact.setSelectedScheme("");

        mProduct_arrays.get(position).setOff_Pro_code("");
        contact.setOff_Pro_code("");

        mProduct_arrays.get(position).setOff_Pro_name("");
        contact.setOff_Pro_name("");

        mProduct_arrays.get(position).setOff_Pro_Unit("");
        contact.setOff_Pro_Unit("");

        mProduct_arrays.get(position).setSelectedFree("0");
        contact.setSelectedFree("0");
    }

        if(!contact.isEdited()) {
            if (selectedScheme != null) {

                if (selectedScheme.getDiscountvalue() != null && !selectedScheme.getDiscountvalue().equals(""))
                    schemeDisc = Double.parseDouble(selectedScheme.getDiscountvalue());

                switch (discountType) {
                    case "%":
                        discountValue = (productAmt * tempQty) * (schemeDisc / 100);
                        viewHolder.ll_disc.setVisibility(View.VISIBLE);
                        unitDiscountValue = (productAmt * product_Sale_Unit_Cn_Qty) * (schemeDisc / 100);

                        viewHolder.tv_dis.setText(String.valueOf(Constant.roundTwoDecimals(schemeDisc)));
//                    viewHolder.dis_amount.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
//                        displayedDis = selectedScheme.getDiscountvalue();
                        break;
                    case "Rs":
                        if (productAmt != 0) {
                            if (!packageType.equals("Y")) {
                                discountValue = ((double) tempQty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
                                unitDiscountValue = ((double) product_Sale_Unit_Cn_Qty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;

                            } else {
                                discountValue = (tempQty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
                                unitDiscountValue = ((int) product_Sale_Unit_Cn_Qty / Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;

                            }
//                        viewHolder.dis_amount.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
//                        viewHolder.ll_disc.setVisibility(View.GONE);
//                            displayedDis = String.valueOf(unitDiscountValue);
                            break;
                        }
                    default:
//                    viewHolder.dis_amount.setText("0");
                        viewHolder.tv_dis.setText("0");
                        discountValue = 0;
                        unitDiscountValue = Double.parseDouble(Constant.roundTwoDecimals(Double.parseDouble(contact.getProduct_Cat_Code()) * product_Sale_Unit_Cn_Qty));
                        displayedDis = "0";

                }

            }
            unitDiscountValue = Double.parseDouble(contact.getProduct_Cat_Code()) * product_Sale_Unit_Cn_Qty - unitDiscountValue;

        }else {
            discountType = "Rs";
            double editedDis = 0;
            if (contact.getEditedDiscount() != null && !contact.getEditedDiscount().equals(""))
                editedDis = Double.parseDouble(contact.getEditedDiscount());

            discountValue = editedDis * tempQty;

            if (contact.getEditedPrice() != null && !contact.getEditedPrice().equals(""))
                unitDiscountValue = Double.parseDouble(contact.getEditedPrice()) * product_Sale_Unit_Cn_Qty;

//                    holder.ll_disc.setVisibility(View.VISIBLE);
//            holder.ProductDis.setText(String.valueOf(Constant.roundTwoDecimals(schemeDisc)));
            schemeDisc = Constant.roundTwoDecimals1(((editedDis * product_Sale_Unit_Cn_Qty)/itemPrice) * 100);
//
//            mProduct_arrays.get(position).setSelectedDisValue(String.valueOf(discountValue));
//            contact.setSelectedDisValue(String.valueOf(discountValue));
//            displayedDis = Constant.roundTwoDecimals(itemPrice - (Double.parseDouble(contact.getEditedPrice()) * product_Sale_Unit_Cn_Qty));

        }

        if(!contact.isEdited() && discountType.equals("%"))
            viewHolder.tv_off_label.setText(discountType+" off");
        else
            viewHolder.tv_off_label.setText(" off");

//            discountValue = discountValue*product_Sale_Unit_Cn_Qty;

            if(!discountType.equals("") && discountValue>0){
                mProduct_arrays.get(position).setDiscount(String.valueOf(schemeDisc));
                contact.setDiscount(String.valueOf(schemeDisc));

                mProduct_arrays.get(position).setDis_amt(Constant.roundTwoDecimals(discountValue));
                contact.setDis_amt(Constant.roundTwoDecimals(discountValue));

                mProduct_arrays.get(position).setSelectedDisValue(Constant.roundTwoDecimals(discountValue));
                contact.setSelectedDisValue(Constant.roundTwoDecimals(discountValue));

/*
                totalAmt = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) -discountValue;
                viewHolder.ll_disc_reduction.setVisibility(View.VISIBLE);
                viewHolder.tv_disc_amt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                viewHolder.tv_disc_amt_total.setText(String.valueOf(Constants.roundTwoDecimals(totalAmt)));*/

            }else {
                mProduct_arrays.get(position).setDiscount(String.valueOf(schemeDisc));
                contact.setDiscount(String.valueOf(schemeDisc));

                mProduct_arrays.get(position).setDis_amt("0");
                mProduct_arrays.get(position).setSelectedDisValue("0");
                contact.setDis_amt("0");
                contact.setSelectedDisValue("0");

//                viewHolder.dis_amount.setText("0");
                viewHolder.tv_dis.setText("0");
            }


        viewHolder.tv_free_unit.setText(OffFreeUnit);
        contact.setOff_free_unit(OffFreeUnit);

        double totalAmt = 0;
        double taxPercent = 0;
        double taxAmt = 0;

        try {
            totalAmt = Double.parseDouble(contact.getProduct_Cat_Code()) * (qty *product_Sale_Unit_Cn_Qty);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            taxPercent = Double.parseDouble(contact.getTax_Value());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        viewHolder.txtPrice.setText("Rs: " + Constant.roundTwoDecimals(itemPrice));
        viewHolder.child_product_price_total.setText("Rs: " + Constant.roundTwoDecimals(productAmt *tempQty));
//        viewHolder.txtQty.setText(String.valueOf(qty));
//        viewHolder.item_amount.setText(Constants.roundTwoDecimals(totalAmt));
        if(!discountType.equals("%")){
            displayedDis = context.getResources().getString(R.string.rupee_symbol) +" "+ Constant.roundTwoDecimals(discountValue);
        }else
            displayedDis = Constant.roundTwoDecimals(schemeDisc);

        viewHolder.tv_dis.setText(displayedDis);

        /*double finalDiscountAmt = unitDiscountValue;

        if(unitDiscountValue ==0){
            finalDiscountAmt = itemPrice;
        }
        if(finalDiscountAmt<=0)
            finalDiscountAmt = 0;*/
        viewHolder.ProductDisAmt.setText(Constant.roundTwoDecimals((productAmt *tempQty) - discountValue));

        viewHolder.item_tax.setText(String.valueOf(taxPercent));

        try {
            taxAmt =  (totalAmt- discountValue) * (taxPercent/100);
            if(taxAmt<=0)
                taxAmt = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.tax_amount.setText(Constant.roundTwoDecimals(taxAmt));

        double finalTotal = Constant.roundTwoDecimals1(((totalAmt - discountValue) + taxAmt));
        if(finalTotal<=0)
            finalTotal = 0;

//        viewHolder.tv_final_total_amt.setText(Constants.roundTwoDecimals(((totalAmt - discountValue) + taxAmt)));
        viewHolder.totalAmount.setText("Rs." + finalTotal);

    }



}
