package com.saneforce.dms.model;

import java.util.ArrayList;
import java.util.List;

public class OutboxModel {
    private String name;
    private int  id, count, value;
    private List<OutboxItem> outboxItemList = new ArrayList<>();



    public OutboxModel(String name, int id, int count, List<OutboxItem> outboxItemList) {
        this.name = name;
        this.id = id;
        this.count = count;
        this.value = value;
        this.outboxItemList = outboxItemList;
    }

    public OutboxModel() {

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public int getvalue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setOutboxItemList(List<OutboxItem> outboxItemList) {
        this.outboxItemList = outboxItemList;
    }

    public List<OutboxItem> getOutboxItemList() {
        return outboxItemList;
    }

    public static class OutboxItem {
        private int slNo;
        private String date, orderId, orderValue, status, reason;
        private String name;

        public OutboxItem(int slNo, String date, String orderId, String orderValue, String status, String reason) {
            this.slNo = slNo;
            this.date = date;
            this.orderId = orderId;
            this.orderValue = orderValue;
            this.status = status;
            this.reason = reason;
        }

        public OutboxItem() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSlNo(int slNo) {
            this.slNo = slNo;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public void setOrderValue(String orderValue) {
            this.orderValue = orderValue;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public int getSlNo() {
            return slNo;
        }

        public String getDate() {
            return date;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getOrderValue() {
            return orderValue;
        }

        public String getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }

        @Override
        public String toString() {
            return "OutboxItem{" +
                    "slNo=" + slNo +
                    ", date='" + date + '\'' +
                    ", orderId='" + orderId + '\'' +
                    ", orderValue='" + orderValue + '\'' +
                    ", status='" + status + '\'' +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }
}
