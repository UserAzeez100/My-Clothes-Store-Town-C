package com.example.towncenterstore.pojo.product.color_products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseColorProduct<Model> {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("product_items")
    @Expose
    private Model productItems;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Model getProductItems() {
        return productItems;
    }

    public void setProductItems(Model productItems) {
        this.productItems = productItems;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}