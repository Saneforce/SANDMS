

package com.saneforce.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.dms.model.OrderGroup;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.Constants;

import java.util.List;

public class OrderGroupAdapter extends RecyclerView.Adapter<OrderGroupAdapter.MyViewHolder> {
    Context context;
    List<OrderGroup> orderGroupList;
    public OrderGroupAdapter(Context context, List<OrderGroup> orderGroupList) {
        this.context = context;
        this.orderGroupList = orderGroupList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_order_group_fin, null, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.tv_order_id.setText(orderGroupList.get(position).getOrder_id());

/*

        double invoiceValue = 0;
        if(orderGroupList.get(position).getInvoice_value()!=null)
            invoiceValue = Double.parseDouble(orderGroupList.get(position).getInvoice_value());
        holder.tv_invoice_value.setText(Constants.roundTwoDecimals(invoiceValue));
*/


        double orderValue = 0;
        if(orderGroupList.get(position).getSales_value()!=null)
            orderValue = Double.parseDouble(orderGroupList.get(position).getSales_value());
        holder.tv_sales_value.setText(Constants.roundTwoDecimals(orderValue));


        double receivedValue = 0;
        if(orderGroupList.get(position).getReceived_amt()!=null)
            receivedValue = Double.parseDouble(orderGroupList.get(position).getReceived_amt());
        holder.tv_received_amt.setText(Constants.roundTwoDecimals(receivedValue));

    }

    @Override
    public int getItemCount() {
        return orderGroupList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_order;
        TextView tv_order_id;
        TextView tv_sales_value;
//        TextView tv_invoice_value;
        TextView tv_received_amt;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_order = itemView.findViewById(R.id.ll_order);
            tv_order_id = itemView.findViewById(R.id.tv_order_id);
            tv_sales_value = itemView.findViewById(R.id.tv_sales_value);
//            tv_invoice_value = itemView.findViewById(R.id.tv_invoice_value);
            tv_received_amt=itemView.findViewById(R.id.tv_received_amt);


        }
    }
}
