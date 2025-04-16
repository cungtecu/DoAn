package com.example.doan.models;

import java.math.BigDecimal;

public class Product {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private Category categories;
    private boolean isDeleted;

    public Product() {}

    public Product(Integer id, String name, String description, BigDecimal price, String image, Category categories, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categories = categories;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Category getCategories() { return categories; }
    public void setCategories(Category categories) { this.categories = categories; }
    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { this.isDeleted = deleted; }

    public int getCategoryId() {
        return categories != null ? categories.getId() : -1;
    }
}