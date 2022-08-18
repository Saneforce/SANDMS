package com.saneforce.dms.adapter;

import static com.billdesk.utils.PaymentLibConstants.v;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.saneforce.dms.activity.SecondaryReportTab;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.model.ReportModel;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.Constant;
import com.saneforce.dms.utils.Shared_Common_Pref;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ReportViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ReportModel> mDate;
    DMS.ViewReport mViewReport;
//    String produtId, productDate,taxValue,tax;
//    String OrderValue;
    String OrderTakenbyFilter;
    TextView textTotalValue;
    String reportType;
    Shared_Common_Pref shared_common_pref;

    int viewType  = 1;
    int slno = 1;


    public void setOrderTakenbyFilter(String orderTakenbyFilter) {
        OrderTakenbyFilter = orderTakenbyFilter;
    }

    public void setTextTotalValue(TextView textTotalValue) {
        this.textTotalValue = textTotalValue;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public ReportViewAdapter(Context context, List<ReportModel> mDate, DMS.ViewReport mViewReport , String ordertakenbyFilter , TextView textTotalValue, String reportType, int viewType) {
        this.context = context;
        this.mDate = mDate;
        this.mViewReport = mViewReport;
        this.OrderTakenbyFilter=ordertakenbyFilter;
        this.textTotalValue=textTotalValue;
        this.reportType = reportType;
        this.viewType=viewType;
        shared_common_pref = new Shared_Common_Pref(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if (this.viewType ==1) {
            listItem = layoutInflater.inflate(R.layout.row_report_list, null, false);
            return new MyViewHolder(listItem);
        } else {
            listItem = layoutInflater.inflate(R.layout.item_finance_report, null, false);
            return new MyViewHolderFinance(listItem);
        }
       /* listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewReport.reportCliick(produtId, productDate,OrderValue);
            }
        });*/

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {

        if(holder1 instanceof MyViewHolder){
            MyViewHolder holder = (MyViewHolder) holder1;
        try {
            double totalvalue=0.0;
            String reportType=mDate.get(position).getReportType();

            String orderType = "";
            if(mDate.get(position).getOrder_type()!=null){
                if(mDate.get(position).getOrder_type().equals("1"))
                    orderType = "P";
                else if(mDate.get(position).getOrder_type().equals("0"))
                    orderType = "F";
            }

            if(this.reportType.equals("1") || orderType.equals("")){
                holder.tv_order_type.setVisibility(View.GONE);
            }
            else{
                holder.tv_order_type.setText(orderType);
                holder.tv_order_type.setVisibility(View.VISIBLE);
            }


            if (OrderTakenbyFilter.equals(mDate.get(position).getOrderStatus()) ||
                    OrderTakenbyFilter.contains(mDate.get(position).getOrderStatus())) {
                holder.txtsNo.setText(String.valueOf((this.slno++)));
                holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
                holder.txtOrderID.setText(mDate.get(position).getOrderNo());
                holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());

                String orderTakenBy = mDate.get(position).getOrderTakenBy();

                if(mDate.get(position).getERP_Code()!=null && !mDate.get(position).getERP_Code().equals(""))
                    orderTakenBy = orderTakenBy + " - " + mDate.get(position).getERP_Code();

                holder.txtOrderTakenBy.setText("Taken By: " + orderTakenBy);
//        if(mDate.get(position).getOrderStatus().equalsIgnoreCase())

//                double totalfin =totalvalue+ Float.parseFloat(mDate.get(position).getOrderValue());
//textTotalValue.setText(String.valueOf(totalfin));
                if(reportType.equals("Secondary")) {
                    holder.txtRetailerName.setVisibility(View.VISIBLE);
                    holder.txtRetailerName.setText("Retailer Name: " + mDate.get(position).getRetailerName());
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
                holder.txtsNo.setText(String.valueOf((this.slno++)));
                holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
                holder.txtOrderID.setText(mDate.get(position).getOrderNo());
                holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());

                String orderTakenBy = mDate.get(position).getOrderTakenBy();

                if(mDate.get(position).getERP_Code()!=null && !mDate.get(position).getERP_Code().equals(""))
                    orderTakenBy = orderTakenBy + " - " + mDate.get(position).getERP_Code();

                holder.txtOrderTakenBy.setText("Taken By: " + orderTakenBy);

//                double totalfin =totalvalue+ Float.parseFloat(mDate.get(position).getOrderValue());
                // textTotalValue.setText(String.valueOf(totalfin));



                if(reportType.equals("Secondary")) {
                    holder.txtRetailerName.setVisibility(View.VISIBLE);
                    holder.txtRetailerName.setText("Retailer Name: " + mDate.get(position).getRetailerName());
                }

                float total = Float.parseFloat(mDate.get(position).getOrderValue());

                BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
                float totalroundoff= (float) bd.doubleValue();
                holder.txtValue.setText(String.valueOf(totalroundoff));


            } else {
                holder.imageButton.setVisibility(View.GONE);
                holder.linearLayout.setVisibility(View.GONE);
                holder.linearLayoutTakenby.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            holder.txtsNo.setText(String.valueOf((this.slno++)));
            holder.txtOrderDate.setText(mDate.get(position).getOrderDate());
            holder.txtOrderID.setText(mDate.get(position).getOrderNo());
            holder.txtOrderStatus.setText(mDate.get(position).getOrderStatus());

            String orderTakenBy = mDate.get(position).getOrderTakenBy();

            if(mDate.get(position).getERP_Code()!=null && !mDate.get(position).getERP_Code().equals(""))
                orderTakenBy = orderTakenBy + " - " + mDate.get(position).getERP_Code();

            holder.txtOrderTakenBy.setText("Taken By: " + orderTakenBy);

            float total = Float.parseFloat(mDate.get(position).getOrderValue());

            BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
            double totalroundoff= bd.doubleValue();


            holder.txtValue.setText(""+ totalroundoff);
//    float total = Float.parseFloat(mDate.get(position).getOrderValue());
//    holder.txtValue.setText(new DecimalFormat("##.##").format(total));
            String reportType=mDate.get(position).getReportType();
            if(reportType.equals("Secondary")) {
                holder.txtRetailerName.setVisibility(View.VISIBLE);
                holder.txtRetailerName.setText("Retailer Name: " + mDate.get(position).getRetailerName());
            }
            //holder.txtPaymentOption.setText("Payment Type:"+mDate.get(position).getPaymentOption());
        }
        }else if(holder1 instanceof MyViewHolderFinance){
            MyViewHolderFinance holderFinance = (MyViewHolderFinance) holder1;

            if (OrderTakenbyFilter.equalsIgnoreCase(mDate.get(position).getOrderStatus()) || OrderTakenbyFilter.equals("All")){
                holderFinance.rl_root.setVisibility(View.VISIBLE);
                holderFinance.cv_root.setVisibility(View.VISIBLE);
            /*if(mDate.get(position).getOrderStatus().equals("Payment Verified")){
                holderFinance.cv_root.setVisibility(View.VISIBLE);
            }
            else{
                holderFinance.cv_root.setVisibility(View.GONE);
            }*/
//            holderFinance.cv_root.setText();

            holderFinance.tv_customer_name.setText(mDate.get(position).getCustomer_Name());
            holderFinance.tv_payment_status.setText(mDate.get(position).getOrderStatus());
            holderFinance.tv_order_id.setText(mDate.get(position).getOrderNo());
            double orderValue = 0;
            if(mDate.get(position).getOrderValue()!=null)
                orderValue = Double.parseDouble(mDate.get(position).getOrderValue());

            holderFinance.tv_sales_value.setText(Constant.roundTwoDecimals(orderValue));

            double receivedValue = 0;
            if(mDate.get(position).getReceived_Amt()!=null)
                receivedValue = Double.parseDouble(mDate.get(position).getReceived_Amt());

            holderFinance.tv_received_amt.setText(Constant.roundTwoDecimals(receivedValue));

            holderFinance.tv_ordered_date.setText(mDate.get(position).getOrderDate());
            holderFinance.tv_date_paid.setText(mDate.get(position).getPaid_Date());
            holderFinance.tv_payment_mode.setText(mDate.get(position).getPayment_Mode());
            holderFinance.tv_cutomer_id.setText(mDate.get(position).getERP_Code());


/*
            OrderGroupAdapter mAdapterDayReportList = new OrderGroupAdapter(context, mDate.get(position).getSubOrderGroup());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holderFinance.rv_sub_orders.setLayoutManager(mLayoutManager);
            holderFinance.rv_sub_orders.setAdapter(mAdapterDayReportList);*/
            }else {
                holderFinance.rl_root.setVisibility(View.GONE);
                holderFinance.cv_root.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mDate.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //implements View.OnClickListener
        TextView txtsNo, txtOrderDate, txtOrderID, txtValue, txtOrderType,txtOrderStatus, txtOrderTakenBy, txtRetailerName, tv_order_type;
        LinearLayout linearLayout, linearLayoutTakenby;
        ImageButton imageButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtsNo = (TextView) itemView.findViewById(R.id.txt_serial);
            txtOrderID = (TextView) itemView.findViewById(R.id.txt_order);
            txtOrderDate = (TextView) itemView.findViewById(R.id.txt_date);
            //txtOrderType = (TextView) itemView.findViewById(R.id.);
            txtValue = (TextView) itemView.findViewById(R.id.txt_total);
            txtOrderStatus = itemView.findViewById(R.id.txt_status);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.row_report);

            linearLayoutTakenby = itemView.findViewById(R.id.row_reporttakenby);
            txtOrderTakenBy = itemView.findViewById(R.id.txt_ordertaken);
            txtRetailerName = itemView.findViewById(R.id.txt_reatiler);
            tv_order_type = itemView.findViewById(R.id.tv_order_type);
            imageButton = itemView.findViewById(R.id.ib_forward);


            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    {
                        String editOrder = "0";

                        try {
                            if (mDate.get(getAdapterPosition()).getSfCode() != null && shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Code).equals(mDate.get(getAdapterPosition()).getSfCode())) {
                                editOrder = "1";
                            } else {
                                editOrder = "0";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mViewReport.reportCliick(mDate.get(getAdapterPosition()).getOrderNo(), mDate.get(getAdapterPosition()).getOrderDate(), mDate.get(getAdapterPosition()).getOrderValue(),
                                mDate.get(getAdapterPosition()).getOrder_type(), editOrder, mDate.get(getAdapterPosition()).getPaymentflag(), mDate.get(getAdapterPosition()).getDispatch_Flag(),
                                mDate.get(getAdapterPosition()).getPaid_Date(), mDate.get(getAdapterPosition()).getPayment_Mode(), mDate.get(getAdapterPosition()).getPaymentTypeName(), mDate.get(getAdapterPosition()).getUTR(),
                                mDate.get(getAdapterPosition()).getAttachment());

                    }
                }
            });
        }
    }
    public static class MyViewHolderFinance extends RecyclerView.ViewHolder {

        CardView cv_root;
        TextView tv_customer_name, tv_payment_status, tv_ordered_date, tv_date_paid, tv_payment_mode, tv_cutomer_id;
        TextView tv_order_id, tv_sales_value, tv_received_amt;
//        RecyclerView rv_sub_orders;
        RelativeLayout rl_root;
        public MyViewHolderFinance(@NonNull View itemView) {
            super(itemView);


            cv_root=itemView.findViewById(R.id.cv_root);
            tv_customer_name=itemView.findViewById(R.id.tv_customer_name);
            tv_payment_status=itemView.findViewById(R.id.tv_payment_status);
            tv_order_id=itemView.findViewById(R.id.tv_order_id);
            tv_sales_value=itemView.findViewById(R.id.tv_sales_value);
            tv_received_amt=itemView.findViewById(R.id.tv_received_amt);
            tv_ordered_date=itemView.findViewById(R.id.tv_ordered_date);
            tv_date_paid=itemView.findViewById(R.id.tv_date_paid);
            tv_payment_mode=itemView.findViewById(R.id.tv_payment_mode);
            tv_cutomer_id=itemView.findViewById(R.id.tv_cutomer_id);
            rl_root=itemView.findViewById(R.id.rl_root);

//            rv_sub_orders=itemView.findViewById(R.id.rv_sub_orders);


        }

   /*     @Override
        public void onClick(View v) {
            String editOrder = "0";

                if(mDate.get(getAdapterPosition()).getSfCode()!=null && shared_common_pref.getvalue1(Shared_Common_Pref.Sf_Code).equals(mDate.get(getAdapterPosition()).getSfCode())){
                    editOrder = "1";
                }else{
                    editOrder = "0";
                }
            mViewReport.reportCliick(mDate.get(getAdapterPosition()).getOrderNo(), mDate.get(getAdapterPosition()).getOrderDate(),mDate.get(getAdapterPosition()).getOrderValue(),mDate.get(getAdapterPosition()).getOrder_type(), editOrder,mDate.get(getAdapterPosition()).getPaymentflag(),mDate.get(getAdapterPosition()).getDispatch_Flag());
        }*/
    }
}
