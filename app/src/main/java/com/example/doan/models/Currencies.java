package com.example.doan.models;

import com.google.gson.annotations.SerializedName;

public class Currencies {
    @SerializedName("id")
    private Integer id;

    @SerializedName("currencyCode")
    private String currencyCode;

    @SerializedName("currencyName")
    private String currencyName;

    public Currencies(Integer id, String currencyCode, String currencyName) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}