package com.example.doan.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SystemConfig {
    @SerializedName("id")
    private Integer id;

    @SerializedName("fromValuePrice")
    private BigDecimal fromValuePrice;

    @SerializedName("toValuePoint")
    private BigDecimal toValuePoint;

    @SerializedName("fromDate")
    private LocalDateTime fromDate;

    @SerializedName("thruDate")
    private LocalDateTime thruDate;

    @SerializedName("currency")
    private Currencies currency;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public BigDecimal getFromValuePrice() { return fromValuePrice; }
    public void setFromValuePrice(BigDecimal fromValuePrice) { this.fromValuePrice = fromValuePrice; }
    public BigDecimal getToValuePoint() { return toValuePoint; }
    public void setToValuePoint(BigDecimal toValuePoint) { this.toValuePoint = toValuePoint; }
    public LocalDateTime getFromDate() { return fromDate; }
    public void setFromDate(LocalDateTime fromDate) { this.fromDate = fromDate; }
    public LocalDateTime getThruDate() { return thruDate; }
    public void setThruDate(LocalDateTime thruDate) { this.thruDate = thruDate; }
    public Currencies getCurrency() { return currency; }
    public void setCurrency(Currencies currency) { this.currency = currency; }
}