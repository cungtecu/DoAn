package com.example.doan.models;

public class CartItem {
    private int cartItemId;
    private int productId;
    private String productName;
    private String size;
    private int quantity;
    private double price;
    private String image;

    public CartItem(int cartItemId, int productId, String productName, String size, int quantity, double price, String image) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return image;
    }

    public void setImageUrl(String image) {
        this.image = image;
    }
}