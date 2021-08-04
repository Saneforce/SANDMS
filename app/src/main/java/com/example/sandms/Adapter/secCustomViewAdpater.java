package com.example.sandms.Adapter;

import static io.realm.Realm.getApplicationContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.sandms.Activity.UpdatePrimaryProduct;
import com.example.sandms.Interface.DMS;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.Product_Array;
import com.example.sandms.Model.SecondaryProduct;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;
import com.example.sandms.Utils.Constants;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class secCustomViewAdpater extends RecyclerView.Adapter<secCustomViewAdpater.MyViewHolder> {

    private List<SecondaryProduct> mProduct_arrays = new ArrayList<>();
    Shared_Common_Pref mShared_common_pref;

    DMS.viewProduct viewProd;
    Context context;
    String GrandTotal;
    int gtotal;
    TextView viewTotal;
    String subtotal;
    public secCustomViewAdpater(Context context,String grandTotal,TextView viewTotal, DMS.viewProduct viewProd) {
        this.context = context;
        this.viewProd = viewProd;
//        this.mProduct_arrays = mProduct_arrays;

    }

    @NonNull
    @Override
    public secCustomViewAdpater.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_item_viewcart, null, false);


        return new secCustomViewAdpater.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(secCustomViewAdpater.MyViewHolder holder, int position) {

        SecondaryProduct mProductArray = mProduct_arrays.get(position);
        holder.txtCatName.setText(mProductArray.getName());
        holder.txtPrice.setText(mProductArray.getProduct_Cat_Code());
        holder.txtQty.setText(mProductArray.getQty());
        String productid=mProductArray.getPID();
        holder.editCount.setText("" + mProductArray.getQty());

        float total, qty, rate;
        float taxAmount;
        qty = Float.parseFloat(mProductArray.getQty());
        rate = Float.parseFloat(mProductArray.getProduct_Cat_Code());
        total = qty * rate;
        taxAmount=Float.parseFloat( mProduct_arrays.get(position).getTax_amt());
        //   taxAmount = Float.parseFloat(mProductArray.getTax_amt());
        Log.v("taxcart", String.valueOf(taxAmount));
        Log.v("discart",   mProduct_arrays.get(position).getDis_amt());
        Log.v("totalcart",mProductArray.getSubtotal());

        if( mProduct_arrays.get(position).getDis_amt().equals("0")||mProduct_arrays.get(position).getDis_amt().equals("")){
            holder.dis_amount.setText("Rs." +"0");
        }else{
            holder.dis_amount.setText("Rs." +  mProduct_arrays.get(position).getDis_amt());
        }
        if( mProduct_arrays.get(position).getTax_amt().equals("0")||mProduct_arrays.get(position).getTax_amt().equals("")){
            holder.tax_amount.setText("Rs." +"0");
        }else {
            holder.tax_amount.setText("Rs." + new DecimalFormat("##.##").format(taxAmount));

        }

        holder.totalAmount.setText("Rs." + mProductArray.getSubtotal());

        holder.tv_free_qty.setText(String.valueOf(mProductArray.getSelectedFree()));

        holder.item_amount.setText("Rs." + new DecimalFormat("##.##").format(total));

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

                                SecondaryProduct task = mProduct_arrays.get(position);
                                Log.v("PRODUCT_LIST", new Gson().toJson(task));
                                mShared_common_pref.save("task", new Gson().toJson(task));
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtQty;
        TextView txtPrice;
        TextView totalAmount;
        TextView txtCatName;
        ImageView productImage;
        TextView editCount;
        TextView item_amount;
        TextView tax_amount;
        TextView dis_amount;
        ImageView deleteProduct;
        ImageView editProduct;

        TextView tv_free_qty;
        TextView tv_final_total_amt;
        TextView tv_dis;
        LinearLayout ll_disc;
        TextView item_tax;
        TextView child_pro_unit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQty = (TextView) itemView.findViewById(R.id.item_qty);
            txtPrice = (TextView) itemView.findViewById(R.id.item_price);
            txtCatName = (TextView) itemView.findViewById(R.id.item_product_name);
            totalAmount = (TextView) itemView.findViewById(R.id.total_amount);
            item_amount = (TextView) itemView.findViewById(R.id.item_amount);
            tax_amount = (TextView) itemView.findViewById(R.id.item_tax_amount);
            dis_amount = (TextView) itemView.findViewById(R.id.item_dis_amount);
            productImage = (ImageView) itemView.findViewById(R.id.image_product);
            editCount = (TextView) itemView.findViewById(R.id.edit_qty);
            deleteProduct = (ImageView) itemView.findViewById(R.id.delete_product);
            editProduct=(ImageView) itemView.findViewById(R.id.edit_product);
            tv_free_qty= itemView.findViewById(R.id.tv_free_qty);
            tv_final_total_amt= itemView.findViewById(R.id.tv_final_total_amt);
            ll_disc= itemView.findViewById(R.id.ll_disc);
            tv_dis= itemView.findViewById(R.id.txt_dis);
            item_tax= itemView.findViewById(R.id.item_tax);
            child_pro_unit = itemView.findViewById(R.id.child_pro_unit);
        }
    }


    public void filteredContact(List<SecondaryProduct> contacts) {
        this.mProduct_arrays = contacts;
        notifyDataSetChanged();
    }


    private void deleteTask(final SecondaryProduct task, String productID,String subtotal) {
        this.subtotal=subtotal;
        // this.GrandTotal=GrandTotal;

        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                PrimaryProductDatabase.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .deleteById(productID);



                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
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

    SecondaryProduct.SchemeProducts selectedScheme = null;

    private void updateSchemeData(List<SecondaryProduct.SchemeProducts> schemeProducts, int qty, secCustomViewAdpater.MyViewHolder viewHolder, int position, SecondaryProduct contact) {
        viewHolder.child_pro_unit.setText(contact.getProduct_Sale_Unit());
        int product_Sale_Unit_Cn_Qty = 1;
        if(contact.getProduct_Sale_Unit_Cn_Qty()!=0)
            product_Sale_Unit_Cn_Qty= contact.getProduct_Sale_Unit_Cn_Qty();
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
        for(SecondaryProduct.SchemeProducts scheme : schemeProducts){
            if(!scheme.getScheme().equals("")) {
                int currentSchemeCount = Integer.parseInt(scheme.getScheme());
                if(previousSchemeCount <= currentSchemeCount &&  currentSchemeCount <= qty){
                    previousSchemeCount =currentSchemeCount;
                    selectedScheme = scheme;
                }
            }
        }

        String discountType = "";

        double discountValue = 0;
        double productAmt = 0;
        double schemeDisc = 0;
        if(selectedScheme != null){
            mProduct_arrays.get(position).setSelectedScheme(selectedScheme.getScheme());
            contact.setSelectedScheme(selectedScheme.getScheme());

            mProduct_arrays.get(position).setSelectedDisValue(selectedScheme.getDiscountvalue());
            contact.setSelectedDisValue(selectedScheme.getDiscountvalue());

            mProduct_arrays.get(position).setOff_Pro_code(selectedScheme.getProduct_Code());
            contact.setOff_Pro_code(selectedScheme.getProduct_Code());

            mProduct_arrays.get(position).setOff_Pro_name(selectedScheme.getProduct_Name());
            contact.setOff_Pro_name(selectedScheme.getProduct_Name());

            mProduct_arrays.get(position).setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            contact.setOff_Pro_Unit(selectedScheme.getScheme_Unit());
            discountType= selectedScheme.getDiscount_Type();


            if(discountType.equals("Rs"))
                viewHolder.ll_disc.setVisibility(View.GONE);
            else
                viewHolder.ll_disc.setVisibility(View.VISIBLE);

            String packageType = selectedScheme.getPackage();

            double freeQty = 0;
            double packageCalc = 0;
            switch (packageType){
                case "N":
                    packageCalc = (int)(qty/Integer.parseInt(selectedScheme.getScheme()));
                    break;
                case "Y":
                    packageCalc = (double) (qty/Integer.parseInt(selectedScheme.getScheme()));
                    break;
//                default:

            }
            if(!selectedScheme.getFree().equals(""))
                freeQty = packageCalc * Integer.parseInt(selectedScheme.getFree());

            mProduct_arrays.get(position).setSelectedFree(String.valueOf(freeQty));
            contact.setSelectedFree(String.valueOf(freeQty));

            viewHolder.tv_free_qty.setText(String.valueOf(freeQty));



            if(contact.getProduct_Cat_Code()!=null && !contact.getProduct_Cat_Code().equals(""))
                productAmt = Double.parseDouble(contact.getProduct_Cat_Code());

            if(selectedScheme.getDiscountvalue()!=null && !selectedScheme.getDiscountvalue().equals(""))
                schemeDisc = Double.parseDouble(selectedScheme.getDiscountvalue());

            switch (discountType){
                case "%":
                    discountValue = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) * (schemeDisc/100);
                    viewHolder.ll_disc.setVisibility(View.VISIBLE);


                    viewHolder.tv_dis.setText(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
                    viewHolder.dis_amount.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                    break;
                case "Rs":
                    discountValue = ((double) qty/Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
                    viewHolder.dis_amount.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                    viewHolder.ll_disc.setVisibility(View.GONE);
                    break;
//                default:
            }
//            discountValue = discountValue*product_Sale_Unit_Cn_Qty;


            if(discountValue>0){
                mProduct_arrays.get(position).setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
                contact.setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));

                mProduct_arrays.get(position).setDis_amt(Constants.roundTwoDecimals(discountValue));
                contact.setDis_amt(Constants.roundTwoDecimals(discountValue));
/*
                totalAmt = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) -discountValue;
                viewHolder.ll_disc_reduction.setVisibility(View.VISIBLE);
                viewHolder.tv_disc_amt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                viewHolder.tv_disc_amt_total.setText(String.valueOf(Constants.roundTwoDecimals(totalAmt)));*/

            }

        }else {
            viewHolder.ll_disc.setVisibility(View.VISIBLE);
            viewHolder.tv_free_qty.setText("0");
        }
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
        viewHolder.txtPrice.setText("Rs:" + Constants.roundTwoDecimals(Double.parseDouble(contact.getProduct_Cat_Code())));
        viewHolder.txtQty.setText(String.valueOf(qty *product_Sale_Unit_Cn_Qty));
        viewHolder.item_amount.setText(Constants.roundTwoDecimals(totalAmt));


        viewHolder.item_tax.setText(String.valueOf(taxPercent));

        try {
            taxAmt =  (totalAmt- discountValue) * (taxPercent/100);

        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.tax_amount.setText(Constants.roundTwoDecimals(taxAmt));
        viewHolder.tv_final_total_amt.setText(Constants.roundTwoDecimals(((totalAmt - discountValue) + taxAmt)));

    }


}