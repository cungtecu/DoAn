package com.example.doan.models;

import java.math.BigDecimal;

public class PriceHistory {
    private Integer id;
    private Integer productId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String changedAt;

    public PriceHistory() {}

    public PriceHistory(Integer id, Integer productId, BigDecimal oldPrice, BigDecimal newPrice, String changedAt) {
        this.id = id;
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedAt = changedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(String changedAt) {
        this.changedAt = changedAt;
    }
}