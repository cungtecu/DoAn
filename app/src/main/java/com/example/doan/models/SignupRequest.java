package com.example.doan.models;

public class SignupRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;

    public SignupRequest(String name, String email, String phone, String password, String confirmPassword) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // Getter và Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    @Override
    public String toString() {
        return "SignupRequest{name='" + name + "', email='" + email + "', phone='" + phone + "', password='" + password + "'}";
    }
}