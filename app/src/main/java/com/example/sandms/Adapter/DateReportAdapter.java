

package com.example.sandms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.R;
import com.example.sandms.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class DateReportAdapter extends RecyclerView.Adapter<DateReportAdapter.MyViewHolder> {
    Context context;
    JSONArray jsonArray;

    public DateReportAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_date_view_report, null, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            String taxRate;
            JSONObject jsonObjec = jsonArray.getJSONObject(position);
            holder.txtName.setText(jsonObjec.getString("Product_Name"));
            holder.txtQty.setText(jsonObjec.getString("CQty"));
            holder.txtRate.setText(jsonObjec.getString("Rate"));
            taxRate=jsonObjec.getString("taxRate");
            holder.txtTax.setText((taxRate));
            holder.txtTotal.setText(Constants.roundTwoDecimals(Double.parseDouble(jsonObjec.getString("taxval"))));
            holder.txtDis.setText(jsonObjec.getString("discount_price"));
            //  holder.txtTotal.setText(new DecimalFormat("##.##").format(totalValue));

            //NEW CODE
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName,txtQty,txtRate,txtTotal,txtTax,txtDis;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtQty = (TextView) itemView.findViewById(R.id.txt_qty);
            txtRate = (TextView) itemView.findViewById(R.id.txt_rate);
            txtTotal = (TextView) itemView.findViewById(R.id.txt_total);
            txtTax=itemView.findViewById(R.id.txt_tax);
            txtDis=itemView.findViewById(R.id.txt_discount);


        }
    }
}
