package com.saneforce.dms.model;

import java.util.ArrayList;
import java.util.List;

public class OutboxModel {
    private String name;
    private int  id, count;
    private List<OutboxItem> outboxItemList = new ArrayList<>();

    public OutboxModel(String name, int id, int count, List<OutboxItem> outboxItemList) {
        this.name = name;
        this.id = id;
        this.count = count;
        this.outboxItemList = outboxItemList;
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

    public List<OutboxItem> getOutboxItemList() {
        return outboxItemList;
    }

    private static class OutboxItem {
        private int slNo;
        private String date, orderId, orderValue, status, reason;

        public OutboxItem(int slNo, String date, String orderId, String orderValue, String status, String reason) {
            this.slNo = slNo;
            this.date = date;
            this.orderId = orderId;
            this.orderValue = orderValue;
            this.status = status;
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
