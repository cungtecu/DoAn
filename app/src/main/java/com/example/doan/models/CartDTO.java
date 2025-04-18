package com.example.doan.models;

import org.threeten.bp.LocalDateTime;
import java.util.List;

public class CartDTO {
    private Integer id;
    private UserDto user;
    private List<CartItemDTO> cartItems;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<CartItemDTO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }

    public LocalDateTime getCreatAt() {
        return createdAt;
    }

    public void setCreatAt(LocalDateTime creatAt) {
        this.createdAt = creatAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}