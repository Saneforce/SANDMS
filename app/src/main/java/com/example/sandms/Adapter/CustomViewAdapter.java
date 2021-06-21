package com.example.sandms.Adapter;

import android.app.AlertDialog;
import android.content.Context;
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
import com.example.sandms.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomViewAdapter extends RecyclerView.Adapter<CustomViewAdapter.MyViewHolder> {


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
    float sum = 0;
    ArrayList<String> totalArray = new ArrayList<>();
    // viewProduct mProducrtDelete;

    public CustomViewAdapter(Context context, DMS.viewProduct viewProd) {
        this.context = context;
        this.viewProd = viewProd;
        this.mProduct_arrays = mProduct_arrays;

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
        holder.txtCatName.setText(mProductArray.getName());
        holder.txtPrice.setText(mProductArray.getProduct_Cat_Code());
        holder.txtQty.setText(mProductArray.getQty());
        holder.editCount.setText("" + mProductArray.getQty());
        holder.dis_amount.setText("Rs." + mProductArray.getDis_amt());

        float total, qty, rate;
        float taxAmount;
        qty = Float.parseFloat(mProductArray.getQty());
        rate = Float.parseFloat(mProductArray.getProduct_Cat_Code());
        total = qty * rate;
        taxAmount = Float.parseFloat(mProductArray.getTax_amt());
        holder.tax_amount.setText("Rs." + new DecimalFormat("##.##").format(taxAmount));
        holder.totalAmount.setText("Rs." + mProductArray.getSubtotal());
        holder.item_amount.setText("Rs." + new DecimalFormat("##.##").format(total));


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

        }
    }


    public void filteredContact(List<PrimaryProduct> contacts) {
        this.mProduct_arrays = contacts;
        notifyDataSetChanged();
    }
}