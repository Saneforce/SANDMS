package com.saneforce.dms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.saneforce.dms.R;
import com.saneforce.dms.model.OutboxModel;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.List;

public class OutboxAdapter extends RecyclerView.Adapter<OutboxAdapter.MyViewHolder> {
    private List<OutboxModel> outboxItemList;
    Context context;
    JSONArray viewPro;
    OutboxModel bm;

    public OutboxAdapter(List<OutboxModel>outboxModelList,Context context, JSONArray viewPro) {
        this.context = context;
        this.viewPro = viewPro;
        this.outboxItemList = outboxModelList;
    }


    @NonNull
    @NotNull
    @Override
    public OutboxAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_outbox_, null, false);
        return new OutboxAdapter.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OutboxAdapter.MyViewHolder holder, int position) {
        bm = outboxItemList.get(position);
        holder.tv_orderId.setText(String.valueOf(bm.getId()));
        holder.tv_product_name.setText(String.valueOf(bm.getName()));
        holder.tv_order_value.setText(String.valueOf(bm.getvalue()));

}

    @Override
    public int getItemCount() {
        return viewPro.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_orderId,tv_product_name,tv_order_value;



        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tv_orderId=itemView.findViewById(R.id.tv_orderId);
            tv_product_name=itemView.findViewById(R.id.tv_product_name);
            tv_order_value=itemView.findViewById(R.id.tv_order_value);

        }
    }
}
