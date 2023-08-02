package com.example.towncenterstore.pojo.product.cart_product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseCart<Model> {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("cart")
    @Expose
    private Model products;
    @SerializedName("object")
    @Expose
    private Model object;
    @SerializedName("total")
    @Expose
    private int total;


    public Model getProducts() {
        return products;
    }

    public void setProducts(Model products) {
        this.products = products;
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

    public Model getObject() {
        return object;
    }

    public void setObject(Model object) {
        this.object = object;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
