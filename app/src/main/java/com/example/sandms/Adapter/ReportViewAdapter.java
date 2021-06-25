package com.example.sandms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Interface.DMS;

import com.example.sandms.Model.ReportModel;
import com.example.sandms.R;

import java.text.DecimalFormat;
import java.util.List;

public class ReportViewAdapter extends RecyclerView.Adapter<ReportViewAdapter.MyViewHolder> {
    Context context;
    List<ReportModel> mDate;
    DMS.ViewReport mViewReport;
    String produtId, productDate,taxValue,tax;
    String OrderValue;

    public ReportViewAdapter(Context context, List<ReportModel> mDate, DMS.ViewReport mViewReport   ) {
        this.context = context;
        this.mDate = mDate;
        this.mViewReport = mViewReport;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_report_list, null, false);
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewReport.reportCliick(produtId, productDate,OrderValue);
            }
        });
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.txtsNo.setText(mDate.get(position).getSlno());
        holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
        holder.txtOrderID.setText(mDate.get(position).getOrderNo());

        float total = Float.parseFloat(mDate.get(position).getOrderValue());
        holder.txtValue.setText(new DecimalFormat("##.##").format(total));
     /*   holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return mDate.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtsNo,txtOrderDate,txtOrderID,txtValue;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtsNo = (TextView) itemView.findViewById(R.id.txt_serial);
            txtOrderID = (TextView) itemView.findViewById(R.id.txt_order);
            txtOrderDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtValue = (TextView) itemView.findViewById(R.id.txt_total);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.row_report);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mViewReport.reportCliick(mDate.get(getAdapterPosition()).getOrderNo(), mDate.get(getAdapterPosition()).getOrderDate(),mDate.get(getAdapterPosition()).getOrderValue());


        }
    }
}
