package com.example.doan.models;

public class OrderConfirmRequest {
    private String orderId;
    private String paymentMethod;
    private int usedDrips;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getUsedDrips() {
        return usedDrips;
    }

    public void setUsedDrips(int usedDrips) {
        this.usedDrips = usedDrips;
    }
}