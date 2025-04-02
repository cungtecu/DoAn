package com.example.doan;
public class EmailCheckResponse {
    private boolean exists;  // Trường để kiểm tra email có tồn tại không
    private String email;    // Trường email
    private String phone;    // Trường phone (nếu cần)

    // Getter cho email
    public String getEmail() {
        return email;
    }

    // Getter cho exists
    public boolean isExists() {
        return exists;
    }

    // Getter cho phone (nếu cần)
    public String getPhone() {
        return phone;
    }

    // Setter cho email (nếu cần)
    public void setEmail(String email) {
        this.email = email;
    }

    // Setter cho exists (nếu cần)
    public void setExists(boolean exists) {
        this.exists = exists;
    }

    // Setter cho phone (nếu cần)
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
