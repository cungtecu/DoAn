package com.example.doan.models;

public class CartCheckoutRequest {
    private String paymentMethod;
    private String deliveryTime; // Thêm field mới

    public CartCheckoutRequest() {
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}