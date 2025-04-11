package com.example.doan.models;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String image;
    private Category categories;
    private boolean isDeleted;
    private int stock;

    public Product() {}

    public Product(int id, String name, String description, double price, String image, Category categories, boolean isDeleted, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categories = categories;
        this.isDeleted = isDeleted;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Category getCategories() { return categories; }
    public void setCategories(Category categories) { this.categories = categories; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getCategoryId() {
        return categories != null ? categories.getId() : -1;
    }
}
