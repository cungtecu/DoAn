package com.example.doan.models;

public class Product {
    private int image; // ID ảnh sản phẩm (R.drawable.xxx)
    private String name; // Tên sản phẩm
    private String price; // Giá sản phẩm

    // Constructor
    public Product(int image, String name, String price) {
        this.image = image;
        this.name = name;
        this.price = price;
    }

    // Getter methods
    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
