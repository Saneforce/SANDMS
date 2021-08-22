package com.saneforce.dms.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportModel {
    @SerializedName(value = "slno", alternate = {"rw"})
    @Expose
    private String slno;
    @SerializedName(value = "Order_No", alternate = {"orderId"})
    @Expose
    private String orderNo;
    @SerializedName(value = "stockist_Code", alternate = {"Stockist_Code"})
    @Expose
    private String stockistCode;
    @SerializedName("sf_code")
    @Expose
    private String sfCode;
    @SerializedName(value = "Order_Date", alternate = {"OrderDate"} )
    @Expose
    private String orderDate;

    @SerializedName("OrderType")
    @Expose
    private String order_type;

    @SerializedName("conv_qty")
    @Expose
    private String conv_qty;

    @SerializedName("Paymentflag")
    @Expose
    private int Paymentflag;

    @SerializedName("Dispatch_Flag")
    @Expose
    private int Dispatch_Flag;


    @SerializedName("ReportType")
    @Expose
    private String reportType;

    @SerializedName("RetName")
    @Expose
    private String retailerName;

    @SerializedName("Order_Value")
    @Expose
    private String orderValue;

    @SerializedName("taxVal")
    @Expose
    private String taxValue;

    @SerializedName(value = "Order_Status", alternate = {"Status"})
    @Expose
    private String orderStatus;
    @SerializedName("Order_Taken_By")
    @Expose
    private String orderTakenBy;

    @SerializedName("OrderVal")
    @Expose
    private String orderValueTotal;

    @SerializedName("tax")
    @Expose
    private String tax;



    @SerializedName("Stockist_Name")
    @Expose
    private String Customer_Name;

    @SerializedName("ERP_Code")
    @Expose
    private String Customer_Id;

    @SerializedName("Invoice_Amount")
    @Expose
    private String Received_Amt;


    @SerializedName("PaymentDate")
    @Expose
    private String Paid_Date;

    @SerializedName("PaymentMode")
    @Expose
    private String Payment_Mode;


    public String getPayment_Mode() {
        return Payment_Mode;
    }

    public void setPayment_Mode(String payment_Mode) {
        Payment_Mode = payment_Mode;
    }

    public String getCustomer_Id() {
        return Customer_Id;
    }

    public void setCustomer_Id(String customer_Id) {
        Customer_Id = customer_Id;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public String getReceived_Amt() {
        return Received_Amt;
    }

    public void setReceived_Amt(String received_Amt) {
        Received_Amt = received_Amt;
    }

    public String getPaid_Date() {
        return Paid_Date;
    }

    public void setPaid_Date(String paid_Date) {
        Paid_Date = paid_Date;
    }

    public String getConv_qty() {
        return conv_qty;
    }

    public void setConv_qty(String conv_qty) {
        this.conv_qty = conv_qty;
    }

    public int getPaymentflag() {
        return Paymentflag;
    }

    public void setPaymentflag(int paymentflag) {
        Paymentflag = paymentflag;
    }

    public int getDispatch_Flag() {
        return Dispatch_Flag;
    }

    public void setDispatch_Flag(int dispatch_Flag) {
        Dispatch_Flag = dispatch_Flag;
    }

    public String getUnit_qty() {
        return conv_qty;
    }

    public void setUnit_qty(String unit_qty) {
        this.conv_qty = unit_qty;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }


//    public String getPaymentOption() {
//        return paymentOption;
//    }
//
//    public void setPaymentOption(String paymentOption) {
//        this.paymentOption = paymentOption;
//    }

//    private String paymentOption;
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTakenBy() {
        return orderTakenBy;
    }

    public void setOrderTakenBy(String orderTakenBy) {
        this.orderTakenBy = orderTakenBy;
    }

    public String getOrderValueTotal() {
        return orderValueTotal;
    }

    public void setOrderValueTotal(String orderValueTotal) {
        this.orderValueTotal = orderValueTotal;
    }


    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStockistCode() {
        return stockistCode;
    }

    public void setStockistCode(String stockistCode) {
        this.stockistCode = stockistCode;
    }

    public String getSfCode() {
        return sfCode;
    }

    public void setSfCode(String sfCode) {
        this.sfCode = sfCode;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(String orderValue) {
        this.orderValue = orderValue;
    }

}
