package com.example.doan.models;

public class SendResetLinkRequest {
    private String email;

    public SendResetLinkRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}