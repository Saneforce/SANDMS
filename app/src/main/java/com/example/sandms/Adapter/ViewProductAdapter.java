package com.example.sandms.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

public class ViewProductAdapter  extends RecyclerView.Adapter<ViewProductAdapter.MyViewHolder> {

    Context context;
    JSONArray viewPro;

    public ViewProductAdapter(Context context, JSONArray viewPro) {
        this.context = context;
        this.viewPro = viewPro;
    }

    @NonNull
    @NotNull
    @Override
    public ViewProductAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_item_viewcart, null, false);

        return new ViewProductAdapter.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewProductAdapter.MyViewHolder holder, int position) {

    Log.v("JSON_ARRAY_VALUE",viewPro.toString());

     /*   PrimaryProduct mProductArray = mProduct_arrays.get(position);

        Log.v("DATA,djkdcd",mProductArray.toString());
        final int positions = holder.getAdapterPosition();


        holder.txtCatName.setText(mProductArray.getName());
        holder.txtPrice.setText("Rate : " + mProductArray.getProduct_Cat_Code());
        holder.txtQty.setText("Qty : " + mProductArray.getQty());

        // holder.totalAmount.setText("Total : " + mProduct_arrays.get(position).getQty() * mProduct_arrays.get(position).getProduct_Cat_Code());
        //   Picasso.with(context).load(mProductArray.getCatImage()).error(R.drawable.no_prod).into(holder.productImage);
        holder.editCount.setText("" + mProductArray.getQty());*/
    }

    @Override
    public int getItemCount() {
        return viewPro.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtQty;
        TextView txtPrice;
        TextView totalAmount;
        TextView txtCatName;
        ImageView productImage;
        TextView editCount;
        ImageView deleteProduct;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtQty = (TextView) itemView.findViewById(R.id.item_qty);
            txtPrice = (TextView) itemView.findViewById(R.id.item_price);
            txtCatName = (TextView) itemView.findViewById(R.id.item_product_name);
            totalAmount = (TextView) itemView.findViewById(R.id.total_amount);
            productImage = (ImageView) itemView.findViewById(R.id.image_product);
            editCount = (TextView) itemView.findViewById(R.id.edit_qty);
            deleteProduct = (ImageView) itemView.findViewById(R.id.delete_product);
        }
    }
}
