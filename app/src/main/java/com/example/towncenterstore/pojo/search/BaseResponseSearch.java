package com.example.towncenterstore.pojo.search;

import com.example.towncenterstore.pojo.product.Products;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseSearch<Model> {

    @SerializedName("products")
    @Expose
    private Model products;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;

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

    public Model getProducts() {
        return products;
    }

    public void setProducts(Model products) {
        this.products = products;
    }
}
