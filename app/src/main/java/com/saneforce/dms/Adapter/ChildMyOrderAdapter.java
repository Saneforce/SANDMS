package com.saneforce.dms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.saneforce.dms.Model.ChildMyOrderModel;
import com.saneforce.dms.R;


import java.util.List;

public class ChildMyOrderAdapter extends RecyclerView.Adapter<ChildMyOrderAdapter.SubMenuViewHolder> {

    private List<ChildMyOrderModel> menus;
    private Context context;


    public ChildMyOrderAdapter(List<ChildMyOrderModel> menus, Context context) {
        this.menus = menus;
        this.context = context;
    }

    @Override
    public SubMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_child_myorder_item, parent, false);
        return new SubMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubMenuViewHolder holder, int position) {
        ChildMyOrderModel menu = menus.get(position);
        holder.produtName.setText(menu.getName());
        //holder.productPrice.setText(String.valueOf(menu.getPrice()));
        holder.productQty.setText(menu.getType());
        //holder.productTotal.setText((int) (menu.getPrice() * menu.getPrice()));
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public class SubMenuViewHolder extends RecyclerView.ViewHolder {

        private TextView produtName, productPrice, productQty, productTotal;


        public SubMenuViewHolder(@NonNull View itemView) {
            super(itemView);

            produtName = itemView.findViewById(R.id.txt_product_name);
            //productPrice = itemView.findViewById(R.id.txt_product_price);
            productQty = itemView.findViewById(R.id.txt_qty_count);
            //productTotal = itemView.findViewById(R.id.txt_product_amount);

        }
    }
}
