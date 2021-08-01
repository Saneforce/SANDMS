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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Activity.UpdatePrimaryProduct;
import com.example.sandms.Interface.DMS;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.Product_Array;
import com.example.sandms.R;
import com.example.sandms.Utils.AlertDialogBox;
import com.example.sandms.Utils.PrimaryProductDatabase;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomViewAdapter extends RecyclerView.Adapter<CustomViewAdapter.MyViewHolder> {
    String subtotal;

    Shared_Common_Pref mShared_common_pref;
    Float quntaity, price;
    private List<PrimaryProduct> mProduct_arrays = new ArrayList<>();
    DMS.viewProduct viewProd;
    Context context;
    AlertDialog.Builder builder;
    String productname;
    String catName;
    String catImg;
    String productID;
    String productcode;
    Float productqty;
    Float productRate;
    Product_Array newProductArray;
    ArrayList<PrimaryProduct> Product_Array_List;
    Shared_Common_Pref shared_common_pref;
    String GrandTotal;
    int gtotal;
    float sum = 0;
    TextView viewTotal;
    ArrayList<String> totalArray = new ArrayList<>();
    // viewProduct mProducrtDelete;

    public CustomViewAdapter(Context context ,String grandTotal,TextView viewTotal,DMS.viewProduct viewProd) {
        this.context = context;
        this.viewProd = viewProd;
        this.mProduct_arrays = mProduct_arrays;
        this.GrandTotal=grandTotal;
        this.viewTotal=viewTotal;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_item_viewcart, null, false);


        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PrimaryProduct mProductArray = mProduct_arrays.get(position);
        shared_common_pref = new Shared_Common_Pref(context);
        GrandTotal = shared_common_pref.getvalue("GrandTotal");
        viewTotal.setText(GrandTotal);
        Log.v("gdtot",GrandTotal.toString());
        String productid=mProductArray.getPID();
//        mShared_common_pref.clear_pref("SubTotal");
        //  mShared_common_pref.clear_pref("ItemTotal");
        holder.txtCatName.setText(mProductArray.getName());
        holder.txtPrice.setText(mProductArray.getProduct_Cat_Code());
        holder.txtQty.setText(mProductArray.getQty());
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
}
