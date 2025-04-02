package com.example.doan.models;

public class User {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String password;

    // Constructor cũ với 2 tham số (name và email)
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Constructor mới với 4 tham số (name, email, phone, password)
    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
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

    // Setter methods (nếu cần thiết)
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
}
