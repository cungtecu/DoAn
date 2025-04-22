package com.example.doan.models;

import org.threeten.bp.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private double totalPrice; // Chuyển BigDecimal thành double cho client
    private String status;
    private LocalDateTime createdAt; // LocalDateTime có thể cần xử lý đặc biệt nếu API trả về dạng chuỗi
    private List<OrderDetailResponse> orderDetails;
    private List<PaymentsDTO> payments;

    // Getters và Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderDetailResponse> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailResponse> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<PaymentsDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentsDTO> payments) {
        this.payments = payments;
    }
}