package com.example.doan.models;

import org.threeten.bp.LocalDateTime;

public class CartItemDTO {
    private int id;
    private ProductResponseDTO product;
    private int quantity;
    private String size;

    private double price;

    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductResponseDTO getProduct() {
        return product;
    }

    public void setProduct(ProductResponseDTO product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


}