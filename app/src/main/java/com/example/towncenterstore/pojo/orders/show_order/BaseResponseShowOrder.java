package com.example.towncenterstore.pojo.orders.show_order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseShowOrder<Model> {

    @SerializedName("orders")
    @Expose
    private Model orders;
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("discount")
    @Expose
    private int discount;
    @SerializedName("final_price")
    @Expose
    private int finalPrice;
    @SerializedName("date")
    @Expose
    private String date;

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Model getOrders() {
        return orders;
    }

    public void setOrders(Model orders) {
        this.orders = orders;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
