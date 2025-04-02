package com.example.doan.models;

public class SigninRequest {
    private String username;
    private String password;

    public SigninRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
