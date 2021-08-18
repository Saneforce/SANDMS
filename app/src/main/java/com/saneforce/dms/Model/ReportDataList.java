package com.saneforce.dms.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ReportDataList {
    @SerializedName("Data")
    @Expose
    private List<ReportModel> data = null;

    public List<ReportModel> getData() {
        return data;
    }

    public void setData(List<ReportModel> data) {
        this.data = data;
    }

    @SerializedName("Payment Done")
    @Expose
    private String paymentDone;
    @SerializedName("Payment Pending")
    @Expose
    private String paymentPending;
    @SerializedName("Order Dispatched")
    @Expose
    private String orderDispatched;
    public String getOrderDispatched() {
        return orderDispatched;
    }

    public void setOrderDispatched(String orderDispatched) {
        this.orderDispatched = orderDispatched;
    }
    @SerializedName("Payment Verified")
    @Expose
    private String paymentVerified;
    @SerializedName("Credit Verified")
    @Expose
    private String creditVerified;

    public String getCreditRaised() {
        return creditRaised;
    }

    public void setCreditRaised(String creditRaised) {
        this.creditRaised = creditRaised;
    }

    public String getCreditDispatched() {
        return creditDispatched;
    }

    public void setCreditDispatched(String creditDispatched) {
        this.creditDispatched = creditDispatched;
    }

    @SerializedName("Credit Raised")
    @Expose
    private String creditRaised;
    @SerializedName("Credit Dispatched")
    @Expose
    private String creditDispatched;

    @SerializedName("Dispatch Pending")
    @Expose
    private String dispatchPending;

    public String getDispatchPending() {
        return dispatchPending;
    }

    public void setDispatchPending(String dispatchPending) {
        this.dispatchPending = dispatchPending;
    }

    public String getPaymentVerified() {
        return paymentVerified;
    }

    public void setPaymentVerified(String paymentVerified) {
        this.paymentVerified = paymentVerified;
    }

    public String getCreditVerified() {
        return creditVerified;
    }

    public void setCreditVerified(String creditVerified) {
        this.creditVerified = creditVerified;
    }

    public String getPaymentDone() {
        return paymentDone;
    }

    public void setPaymentDone(String paymentDone) {
        this.paymentDone = paymentDone;
    }

    public String getPaymentPending() {
        return paymentPending;
    }

    public void setPaymentPending(String paymentPending) {
        this.paymentPending = paymentPending;
    }


}
