package com.example.doan.admin;

public class Admin_User_Class {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private int points;
    private String role;

    // Constructor đầy đủ
    public Admin_User_Class(int id, String name, String email, String phone, String password, int points, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.points = points;
        this.role = role;
    }

    // Constructor không có id (dùng khi thêm mới người dùng)
    public Admin_User_Class(String name, String email, String phone, String password, int points, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.points = points;
        this.role = role;
    }

    // Getter và Setter
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
