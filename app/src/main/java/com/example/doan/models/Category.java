package com.example.doan.models;

public class Category {
    private int id;
    private String name;

    private int imageResId;
    private String description;

    // Constructor mặc định
    public Category() {
    }

    // Constructor với đầy đủ tham số
    public Category(int id, String name, int imageResId, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
    }

    // Getters và setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}