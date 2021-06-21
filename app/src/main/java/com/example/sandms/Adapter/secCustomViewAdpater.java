package com.example.sandms.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Interface.DMS;
import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.Product_Array;
import com.example.sandms.Model.SecondaryProduct;
import com.example.sandms.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class secCustomViewAdpater extends RecyclerView.Adapter<secCustomViewAdpater.MyViewHolder> {

    private List<SecondaryProduct> mProduct_arrays = new ArrayList<>();
    DMS.viewProduct viewProd;
    Context context;


    public secCustomViewAdpater(Context context, DMS.viewProduct viewProd) {
        this.context = context;
        this.viewProd = viewProd;
        this.mProduct_arrays = mProduct_arrays;

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
        holder.editCount.setText("" + mProductArray.getQty());

        float total, qty, rate;
        float taxAmount;
        qty = Float.parseFloat(mProductArray.getQty());
        rate = Float.parseFloat(mProductArray.getProduct_Cat_Code());
        total = qty * rate;
        taxAmount = Float.parseFloat(mProductArray.getTax_amt());
        holder.tax_amount.setText("Rs." + new DecimalFormat("##.##").format(taxAmount));
        holder.totalAmount.setText("Rs." + mProductArray.getSubtotal());
        holder.item_amount.setText("Rs." + total);

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
        ImageView deleteProduct;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQty = (TextView) itemView.findViewById(R.id.item_qty);
            txtPrice = (TextView) itemView.findViewById(R.id.item_price);
            txtCatName = (TextView) itemView.findViewById(R.id.item_product_name);
            totalAmount = (TextView) itemView.findViewById(R.id.total_amount);
            item_amount = (TextView) itemView.findViewById(R.id.item_amount);
            tax_amount = (TextView) itemView.findViewById(R.id.item_tax_amount);
            productImage = (ImageView) itemView.findViewById(R.id.image_product);
            editCount = (TextView) itemView.findViewById(R.id.edit_qty);
            deleteProduct = (ImageView) itemView.findViewById(R.id.delete_product);

        }
    }


    public void filteredContact(List<SecondaryProduct> contacts) {
        this.mProduct_arrays = contacts;
        notifyDataSetChanged();
    }
}