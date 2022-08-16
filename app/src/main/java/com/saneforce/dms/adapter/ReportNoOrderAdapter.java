package com.saneforce.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.dms.R;
import com.saneforce.dms.activity.SecondaryReportTab;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.model.ReportModel;
import com.saneforce.dms.utils.Shared_Common_Pref;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ReportNoOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ReportModel> mDate;
    DMS.ViewReport mViewReport;
    String OrderTakenbyFilter;
    TextView textTotalValue;
    String reportType;
    Shared_Common_Pref shared_common_pref;

    int viewType=1;
    int slno=1;

    public ReportNoOrderAdapter(SecondaryReportTab context, FragmentManager childFragmentManager,int tabCount){
    }

    public void setOrderTakenbyFilter(String orderTakenbyFilter){
        OrderTakenbyFilter=orderTakenbyFilter;
    }
    public void setTextTotalValue(TextView textTotalValue){
        this.textTotalValue=textTotalValue;
    }
    public void setReportType(String reportType){
        this.reportType=reportType;
    }
    public void setViewType(int viewType){
        this.viewType=viewType;
    }

    public ReportNoOrderAdapter(Context context,List<ReportModel>mDate,DMS.ViewReport mViewReport,String orderTakenbyFilter,TextView textTotalValue,String reportType,int viewType){
        this.context=context;
        this.mDate=mDate;
        this.mViewReport=mViewReport;
        this.OrderTakenbyFilter=orderTakenbyFilter;
        this.textTotalValue=textTotalValue;
        this.reportType=reportType;
        this.viewType=viewType;
        shared_common_pref=new Shared_Common_Pref(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View listItem;
            listItem=layoutInflater.inflate(R.layout.row_report_list,null,false);
            return new ReportViewAdapter.MyViewHolderFinance(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        if(holder1 instanceof ReportViewAdapter.MyViewHolder){
            ReportViewAdapter.MyViewHolder holder=(ReportViewAdapter.MyViewHolder) holder1;
        try {
            double totalValue = 0.0;
            String reportType = mDate.get(position).getReportType();
            String orderType = "";
            if (mDate.get(position).getOrder_type() != null) {
                if (mDate.get(position).getOrder_type().equals("1"))
                    orderType = "P";
                else if (mDate.get(position).getOrder_type().equals("0"))
                    orderType = "F";
            }
            if(this.reportType.equals("1")||orderType.equals("")){
                holder.tv_order_type.setVisibility(View.GONE);
            }
            else{
                holder.tv_order_type.setText(orderType);
                holder.tv_order_type.setVisibility(View.VISIBLE);
            }

            if(OrderTakenbyFilter.equals(mDate.get(position).getOrderStatus())|| OrderTakenbyFilter.contains(mDate.get(position).getOrderStatus())){
                holder.txtsNo.setText(String.valueOf(this.slno++));
                holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
                holder.txtOrderID.setText(mDate.get(position).getOrderNo());
                holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());

                String orderTakenBy=mDate.get(position).getOrderTakenBy();

                if(mDate.get(position).getERP_Code()!=null && !mDate.get(position).getERP_Code().equals(""))
                    orderTakenBy=orderType+"-"+mDate.get(position).getERP_Code();

                holder.txtOrderTakenBy.setText("Taken By:"+orderTakenBy);
                if(reportType.equals("Secondary")){
                    holder.txtRetailerName.setVisibility(View.VISIBLE);
                    holder.txtRetailerName.setText("Retailer Name:"+mDate.get(position).getRetailerName());
                }
                float total=Float.parseFloat(mDate.get(position).getOrderValue());
                BigDecimal bd=new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
                double totalroundoff=bd.doubleValue();
                holder.txtValue.setText(String.valueOf(totalroundoff));
            }
            else if(OrderTakenbyFilter.equals("All")){
                holder.txtsNo.setText(String.valueOf(this.slno++));
                holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
                holder.txtOrderID.setText(mDate.get(position).getOrderNo());
                holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());

                String orderTakenBy=mDate.get(position).getOrderTakenBy();

                if(mDate.get(position).getERP_Code()!=null && !mDate.get(position).getERP_Code().equals(""))
                    orderTakenBy=orderType+"-"+mDate.get(position).getERP_Code();

                holder.txtOrderTakenBy.setText("Taken By:"+orderTakenBy);

                float total=Float.parseFloat(mDate.get(position).getOrderValue());

                BigDecimal bd=new BigDecimal(total).setScale(2,RoundingMode.HALF_UP);
                float totalroundoff=(float) bd.doubleValue();
                holder.txtValue.setText(String.valueOf(totalroundoff));
            }
            else {
                holder.imageButton.setVisibility(View.GONE);
                holder.linearLayout.setVisibility(View.GONE);
                holder.linearLayoutTakenby.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            holder.txtsNo.setText(String.valueOf(this.slno++));
            holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
            holder.txtOrderID.setText(mDate.get(position).getOrderNo());
            holder.txtOrderType.setText(mDate.get(position).getOrderStatus());

            String orderTakenBy = mDate.get(position).getOrderTakenBy();

            if(mDate.get(position).getERP_Code()!=null && !mDate.get(position).getERP_Code().equals(""))
                orderTakenBy = orderTakenBy + " - " + mDate.get(position).getERP_Code();

            holder.txtOrderTakenBy.setText("Taken By: " + orderTakenBy);

            float total = Float.parseFloat(mDate.get(position).getOrderValue());

            BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
            double totalroundoff= bd.doubleValue();


            holder.txtValue.setText(""+ totalroundoff);

            String reportType=mDate.get(position).getReportType();
            if(reportType.equals("Secondary")) {
                holder.txtRetailerName.setVisibility(View.VISIBLE);
                holder.txtRetailerName.setText("Retailer Name: " + mDate.get(position).getRetailerName());
            }

        }
        }

    }

    @Override
    public int getItemCount() {
        return mDate.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //implements View.OnClickListener
        TextView txtsNo, txtOrderDate, txtOrderID, txtOrderTakenBy, txtRetailerName, tv_order_type;
        LinearLayout linearLayout, linearLayoutTakenby;
        ImageButton imageButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtsNo = (TextView) itemView.findViewById(R.id.txt_serial);
            txtOrderID = (TextView) itemView.findViewById(R.id.txt_order);
            txtOrderDate = (TextView) itemView.findViewById(R.id.txt_date);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.row_report);
            linearLayoutTakenby = itemView.findViewById(R.id.row_reporttakenby);
            txtOrderTakenBy = itemView.findViewById(R.id.txt_ordertaken);
            txtRetailerName = itemView.findViewById(R.id.txt_reatiler);
            tv_order_type = itemView.findViewById(R.id.tv_order_type);
            imageButton = itemView.findViewById(R.id.ib_forward);
        }
    }
}
