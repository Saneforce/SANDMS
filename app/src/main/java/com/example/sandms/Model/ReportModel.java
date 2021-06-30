package com.example.sandms.Model;




import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportModel {
    @SerializedName("slno")
    @Expose
    private String slno;
    @SerializedName("Order_No")
    @Expose
    private String orderNo;
    @SerializedName("stockist_Code")
    @Expose
    private String stockistCode;
    @SerializedName("sf_code")
    @Expose
    private String sfCode;
    @SerializedName("Order_Date")
    @Expose
    private String orderDate;

    @SerializedName("Order_Value")
    @Expose
    private String orderValue;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @SerializedName("taxVal")
    @Expose
    private String taxValue;

    public String getOrderTakenBy() {
        return orderTakenBy;
    }

    public void setOrderTakenBy(String orderTakenBy) {
        this.orderTakenBy = orderTakenBy;
    }

    @SerializedName("Order_Status")
    @Expose
    private String orderStatus;
    @SerializedName("Order_Taken_By")
    @Expose
    private String orderTakenBy;

    public String getOrderValueTotal() {
        return orderValueTotal;
    }

    public void setOrderValueTotal(String orderValueTotal) {
        this.orderValueTotal = orderValueTotal;
    }

    @SerializedName("OrderVal")
    @Expose
    private String orderValueTotal;
    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    @SerializedName("tax")
    @Expose
    private String tax;

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
