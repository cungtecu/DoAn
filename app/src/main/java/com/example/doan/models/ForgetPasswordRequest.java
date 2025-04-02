package com.example.doan.models;

public class ForgetPasswordRequest {
    private String email;

    public ForgetPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
