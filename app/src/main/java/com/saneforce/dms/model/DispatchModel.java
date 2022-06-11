package com.saneforce.dms.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DispatchModel {

    @SerializedName("Slno")
    @Expose
    private String slno;
    @SerializedName("OrderID")
    @Expose
    private String orderID;
    @SerializedName("PCode")
    @Expose
    private String pCode;
    @SerializedName("Product_Name")
    @Expose
    private String productName;
    @SerializedName("OldCQty")
    @Expose
    private Integer oldCQty;
    @SerializedName("Oldvalue")
    @Expose
    private Object oldvalue;
    @SerializedName("Rate")
    @Expose
    private Double rate;
    @SerializedName("Cl_bal")
    @Expose
    private String clBal;
    @SerializedName("Unit")
    @Expose
    private String unit;
    @SerializedName("newCQty")
    @Expose
    private Integer newCQty;
    @SerializedName("Newvalue")
    @Expose
    private Integer newvalue;
    @SerializedName("Free")
    @Expose
    private Integer free;
    @SerializedName("discount_price")
    @Expose
    private Integer discountPrice;
    @SerializedName("SchemeAvail")
    @Expose
    private String schemeAvail;
    @SerializedName("Offer_Product_Unit")
    @Expose
    private String offerProductUnit;
    @SerializedName("Offer_ProductNm")
    @Expose
    private String offerProductNm;

    @SerializedName("total_ordered")
    @Expose
    private String total_ordered;

    @SerializedName("total_dipatched")
    @Expose
    private String total_dipatched;

    @SerializedName("total_pending")
    @Expose
    private String total_pending;

    public String getTotal_ordered() {
        return total_ordered;
    }

    public void setTotal_ordered(String total_ordered) {
        this.total_ordered = total_ordered;
    }

    public String getTotal_dipatched() {
        return total_dipatched;
    }

    public void setTotal_dipatched(String total_dipatched) {
        this.total_dipatched = total_dipatched;
    }

    public String getTotal_pending() {
        return total_pending;
    }

    public void setTotal_pending(String total_pending) {
        this.total_pending = total_pending;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPCode() {
        return pCode;
    }

    public void setPCode(String pCode) {
        this.pCode = pCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getOldCQty() {
        return oldCQty;
    }

    public void setOldCQty(Integer oldCQty) {
        this.oldCQty = oldCQty;
    }

    public Object getOldvalue() {
        return oldvalue;
    }

    public void setOldvalue(Object oldvalue) {
        this.oldvalue = oldvalue;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getClBal() {
        return clBal;
    }

    public void setClBal(String clBal) {
        this.clBal = clBal;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getNewCQty() {
        return newCQty;
    }

    public void setNewCQty(Integer newCQty) {
        this.newCQty = newCQty;
    }

    public Integer getNewvalue() {
        return newvalue;
    }

    public void setNewvalue(Integer newvalue) {
        this.newvalue = newvalue;
    }

    public Integer getFree() {
        return free;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getSchemeAvail() {
        return schemeAvail;
    }

    public void setSchemeAvail(String schemeAvail) {
        this.schemeAvail = schemeAvail;
    }

    public String getOfferProductUnit() {
        return offerProductUnit;
    }

    public void setOfferProductUnit(String offerProductUnit) {
        this.offerProductUnit = offerProductUnit;
    }

    public String getOfferProductNm() {
        return offerProductNm;
    }

    public void setOfferProductNm(String offerProductNm) {
        this.offerProductNm = offerProductNm;
    }

    @Override
    public String toString() {
        return "DispatchModel{" +
                "slno='" + slno + '\'' +
                ", orderID='" + orderID + '\'' +
                ", pCode='" + pCode + '\'' +
                ", productName='" + productName + '\'' +
                ", oldCQty=" + oldCQty +
                ", oldvalue=" + oldvalue +
                ", rate=" + rate +
                ", clBal='" + clBal + '\'' +
                ", unit='" + unit + '\'' +
                ", newCQty=" + newCQty +
                ", newvalue=" + newvalue +
                ", free=" + free +
                ", discountPrice=" + discountPrice +
                ", schemeAvail='" + schemeAvail + '\'' +
                ", offerProductUnit='" + offerProductUnit + '\'' +
                ", offerProductNm='" + offerProductNm + '\'' +
                '}';
    }
}