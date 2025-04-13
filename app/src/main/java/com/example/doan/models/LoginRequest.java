package com.example.doan.models;

public class LoginRequest {
    private String phone;
    private String password;

    // Constructor cho đăng nhập (chỉ cần phone và password)
    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    // Getters và setters
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

    @Override
    public String toString() {
        return "LoginRequest{" +
                "phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}