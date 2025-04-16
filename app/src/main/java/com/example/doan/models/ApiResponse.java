package com.example.doan.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    private String status;
    private String message;
    @SerializedName("users")
    private UserDto users; // Thêm trường users

    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDto getUsers() {
        return users;
    }

    public void setUsers(UserDto users) {
        this.users = users;
    }

    // Thêm class UserDto để parse trường users
    public static class UserDto {
        private Integer id;
        private String name;

        public UserDto(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}