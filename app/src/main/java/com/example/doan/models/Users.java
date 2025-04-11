package com.example.doan.models;

public class Users {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private Integer points;
    private String role; // Đổi từ Integer sang String để khớp với enum Role trong Spring Boot

    // Constructor mặc định (yêu cầu bởi Gson)
    public Users() {}

    // Constructor với 2 tham số (name và email) - nếu cần dùng trong code khác
    public Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Constructor với 4 tham số (name, email, phone, password) - sửa lỗi và bỏ points/role
    public Users(String name, String email, String phone, String password, Integer points, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.points = points;
    }

    // Getter methods
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Integer getPoints() {
        return points;
    }

    public String getRole() { // Đổi từ Integer sang String
        return role;
    }

    // Setter methods
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setRole(String role) { // Đổi từ Integer sang String
        this.role = role;
    }
}