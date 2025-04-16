package com.example.doan.models;

public class User {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String gender;  // Thêm trường gender
    private String birthday;  // Thêm trường birthday
    private Integer points;  // Thêm trường points

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

    // Constructor đầy đủ với tất cả các tham số (bao gồm gender, birthday, points)
    public User(Integer id, String name, String email, String phone, String password, String gender, String birthday, Integer points) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.gender = gender;
        this.birthday = birthday;
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

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public Integer getPoints() {
        return points;
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

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}