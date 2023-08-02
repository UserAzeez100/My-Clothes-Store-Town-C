package com.example.towncenterstore.pojo.use_point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponseUsePoint<Model> {
    @SerializedName("orders")
    @Expose
    private List<Model> orders;
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("final_price")
    @Expose
    private int finalPrice;
    @SerializedName("discount")
    @Expose
    private int discount;

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;


    public List<Model> getOrders() {
        return orders;
    }

    public void setOrders(List<Model> orders) {
        this.orders = orders;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
