package com.example.doan.models;

public class Category {
    private Integer id;
    private String name;
    private String image; // Đồng bộ với Products
    private String description;

    public Category() {}

    public Category(Integer id, String name, String image, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImage() { return image; } // Đổi từ getImageUrl
    public void setImage(String image) { this.image = image; } // Đổi từ setImageUrl
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}