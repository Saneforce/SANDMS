

package com.saneforce.dms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.saneforce.dms.R;
import com.saneforce.dms.Utils.Constants;

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
            JSONObject jsonObjec = jsonArray.getJSONObject(position);
            holder.txtName.setText(jsonObjec.getString("Product_Name"));
            holder.txtQty.setText(jsonObjec.getString("CQty"));
            holder.txtRate.setText(Constants.roundTwoDecimals(jsonObjec.getDouble("Rate")));


            String taxRate = "0";
            if(jsonObjec.has("taxRate") && !jsonObjec.getString("taxRate").equals(""))
                taxRate =Constants.roundTwoDecimals(jsonObjec.getDouble("taxRate"));
            holder.txtTax.setText(taxRate);

            String taxVal = "0";
            if(jsonObjec.has("taxval") && !jsonObjec.getString("taxval").equals("") )
                taxVal =Constants.roundTwoDecimals(jsonObjec.getDouble("taxval"));
            holder.txtTotal.setText(taxVal);

            String disPrice = "0";
            if(jsonObjec.has("discount_price")  && !jsonObjec.getString("discount_price").equals("") )
                disPrice =Constants.roundTwoDecimals(jsonObjec.getDouble("discount_price"));

            holder.txtDis.setText(disPrice);

            String free = "0";
            if(jsonObjec.has("Free")  && !jsonObjec.getString("Free").equals(""))
                free =Constants.roundTwoDecimals(jsonObjec.getDouble("Free"));

            String free_unit = "";
            if(jsonObjec.has("Offer_Product_Unit")  && !jsonObjec.getString("Offer_Product_Unit").equals(""))
                free_unit =jsonObjec.getString("Offer_Product_Unit");

            holder.txt_free.setText(free+ " "+ free_unit);
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

        TextView txtName,txtQty,txtRate,txtTotal,txtTax,txtDis,txt_free ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtQty = (TextView) itemView.findViewById(R.id.txt_qty);
            txtRate = (TextView) itemView.findViewById(R.id.txt_rate);
            txtTotal = (TextView) itemView.findViewById(R.id.txt_total);
            txtTax=itemView.findViewById(R.id.txt_tax);
            txtDis=itemView.findViewById(R.id.txt_discount);
            txt_free=itemView.findViewById(R.id.txt_free);


        }
    }
}
