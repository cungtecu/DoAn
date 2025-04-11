package com.example.doan.models;

public class UserSummaryDTO {
    private Integer id;
    private String name;
    private Integer points;
    private String email;
    private String phone;

    public UserSummaryDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name; }
    public Integer getPoints() {
        return points; }
    public void setPoints(Integer points) { this.points = points; }
    public String getEmail() {
        return email; }
    public void setEmail(String email) {
        this.email = email; }


    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone; }
}


