package com.example.doan.models;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    private Integer id;
    private String name;
    private String phone;
    private String email;

    // Các trường bổ sung nếu cần
    private Integer points;
    private String role;

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPoints() {
        return points;
    }

    public String getRole() {
        return role;
    }
}