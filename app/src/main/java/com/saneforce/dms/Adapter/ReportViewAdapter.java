package com.saneforce.dms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.saneforce.dms.Interface.DMS;
import com.saneforce.dms.Model.ReportModel;
import com.saneforce.dms.R;
import com.saneforce.dms.Utils.Shared_Common_Pref;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ReportViewAdapter extends RecyclerView.Adapter<ReportViewAdapter.MyViewHolder> {
    Context context;
    List<ReportModel> mDate;
    DMS.ViewReport mViewReport;
    String produtId, productDate,taxValue,tax;
    String OrderValue;
    String OrderTakenbyFilter;
    TextView textTotalValue;
    String orderType;
    Shared_Common_Pref shared_common_pref;

    public ReportViewAdapter(Context context, List<ReportModel> mDate, DMS.ViewReport mViewReport ,String ordertakenbyFilter ,TextView textTotalValue, String orderType) {
        this.context = context;
        this.mDate = mDate;
        this.mViewReport = mViewReport;
        this.OrderTakenbyFilter=ordertakenbyFilter;
        this.textTotalValue=textTotalValue;
        this.orderType=orderType;
        shared_common_pref = new Shared_Common_Pref(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_report_list, null, false);
       /* listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewReport.reportCliick(produtId, productDate,OrderValue);
            }
        });*/
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            double totalvalue=0.0;
            String reportType=mDate.get(position).getReportType();

            String orderType = "F";
            if(mDate.get(position).getOrder_type().equals("1"))
                orderType = "P";

            if(this.orderType.equals("1")){
                holder.tv_order_type.setVisibility(View.GONE);
            }
            else{
                holder.tv_order_type.setText(orderType);
                holder.tv_order_type.setVisibility(View.VISIBLE);
            }


            if (OrderTakenbyFilter.equals(mDate.get(position).getOrderStatus()) ||
                    OrderTakenbyFilter.contains(mDate.get(position).getOrderStatus())) {
                holder.txtsNo.setText(mDate.get(position).getSlno());
                holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
                holder.txtOrderID.setText(mDate.get(position).getOrderNo());
                holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());
                holder.txtOrderTakenBy.setText("Taken By:" + mDate.get(position).getOrderTakenBy());
//        if(mDate.get(position).getOrderStatus().equalsIgnoreCase())

                double totalfin =totalvalue+ Float.parseFloat(mDate.get(position).getOrderValue());
//textTotalValue.setText(String.valueOf(totalfin));
                if(reportType.equals("Secondary")) {
                    holder.txtRetailerName.setVisibility(View.VISIBLE);
                    holder.txtRetailerName.setText("Retailer Name:" + mDate.get(position).getRetailerName());
                }

                float total = Float.parseFloat(mDate.get(position).getOrderValue());

                BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
                double totalroundoff= bd.doubleValue();
                holder.txtValue.setText(String.valueOf( totalroundoff));
     /*   holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
            }
        });
*/

            } else if (OrderTakenbyFilter.equals("All")) {
                holder.txtsNo.setText(mDate.get(position).getSlno());
                holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
                holder.txtOrderID.setText(mDate.get(position).getOrderNo());
                holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());
                holder.txtOrderTakenBy.setText("Taken By:" + mDate.get(position).getOrderTakenBy());
                double totalfin =totalvalue+ Float.parseFloat(mDate.get(position).getOrderValue());
                // textTotalValue.setText(String.valueOf(totalfin));



                if(reportType.equals("Secondary")) {
                    holder.txtRetailerName.setVisibility(View.VISIBLE);
                    holder.txtRetailerName.setText("Retailer Name:" + mDate.get(position).getRetailerName());
                }

                float total = Float.parseFloat(mDate.get(position).getOrderValue());

                BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
                float totalroundoff= (float) bd.doubleValue();
                holder.txtValue.setText(String.valueOf(totalroundoff));


            } else {
                holder.linearLayout.setVisibility(View.GONE);
                holder.linearLayoutTakenby.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            holder.txtsNo.setText(mDate.get(position).getSlno());
            holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
            holder.txtOrderID.setText(mDate.get(position).getOrderNo());
            holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());
            holder.txtOrderTakenBy.setText("Taken By:" + mDate.get(position).getOrderTakenBy());
            float total = Float.parseFloat(mDate.get(position).getOrderValue());

            BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
            double totalroundoff= bd.doubleValue();
            holder.txtValue.setText(""+ totalroundoff);
//    float total = Float.parseFloat(mDate.get(position).getOrderValue());
//    holder.txtValue.setText(new DecimalFormat("##.##").format(total));
            String reportType=mDate.get(position).getReportType();
            if(reportType.equals("Secondary")) {
                holder.txtRetailerName.setVisibility(View.VISIBLE);
                holder.txtRetailerName.setText("Retailer Name:" + mDate.get(position).getRetailerName());
            }
            //holder.txtPaymentOption.setText("Payment Type:"+mDate.get(position).getPaymentOption());
        }
    }

    @Override
    public int getItemCount() {
        return mDate.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtsNo,txtOrderDate,txtOrderID,txtValue,txtOrderStatus,txtOrderTakenBy,txtRetailerName,tv_order_type;
        LinearLayout linearLayout,linearLayoutTakenby;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtsNo = (TextView) itemView.findViewById(R.id.txt_serial);
            txtOrderID = (TextView) itemView.findViewById(R.id.txt_order);
            txtOrderDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtValue = (TextView) itemView.findViewById(R.id.txt_total);
            txtOrderStatus=itemView.findViewById(R.id.txt_status);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.row_report);
            linearLayoutTakenby= itemView.findViewById(R.id.row_reporttakenby);
            txtOrderTakenBy=itemView.findViewById(R.id.txt_ordertaken);
            txtRetailerName=itemView.findViewById(R.id.txt_reatiler);
            tv_order_type=itemView.findViewById(R.id.tv_order_type);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String editOrder = "0";

                if(mDate.get(getAdapterPosition()).getSfCode()!=null && shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Code).equals(mDate.get(getAdapterPosition()).getSfCode())){
                    editOrder = "1";
                }else{
                    editOrder = "0";
                }
            mViewReport.reportCliick(mDate.get(getAdapterPosition()).getOrderNo(), mDate.get(getAdapterPosition()).getOrderDate(),mDate.get(getAdapterPosition()).getOrderValue(),mDate.get(getAdapterPosition()).getOrder_type(), editOrder);
        }
    }
}
