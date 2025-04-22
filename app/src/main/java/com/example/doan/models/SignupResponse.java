package com.example.doan.models;

public class SignupResponse {
    private String status;
    private String message;
    private User users;

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public User getUsers() { return users; }
    public void setUsers(User users) { this.users = users; }

    public static class User {
        private int id;
        private String name;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}