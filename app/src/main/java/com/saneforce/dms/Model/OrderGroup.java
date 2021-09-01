package com.saneforce.dms.Model;

public class OrderGroup {
    String order_id, sales_value, invoice_value, received_amt;

    public OrderGroup(String tv_order_id, String tv_sales_value, String tv_invoice_value, String tv_received_amt) {
        this.order_id = tv_order_id;
        this.sales_value = tv_sales_value;
        this.invoice_value = tv_invoice_value;
        this.received_amt = tv_received_amt;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getSales_value() {
        return sales_value;
    }

    public String getInvoice_value() {
        return invoice_value;
    }

    public String getReceived_amt() {
        return received_amt;
    }

}
