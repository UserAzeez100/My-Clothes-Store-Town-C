package com.example.towncenterstore.pojo.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseOrders<Model> {
    @SerializedName("orders")
    @Expose
    private Model orders;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Model getOrders() {
        return orders;
    }

    public void setOrders(Model orders) {
        this.orders = orders;
    }
}
