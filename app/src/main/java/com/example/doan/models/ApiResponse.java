package com.example.doan.models;

import java.util.Map;

public class ApiResponse {
    private String status;
    private String message;
    private Map<String, Object> users;

    // Getter và Setter
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getUsers() { return users; }
    public void setUsers(Map<String, Object> users) { this.users = users; }
}