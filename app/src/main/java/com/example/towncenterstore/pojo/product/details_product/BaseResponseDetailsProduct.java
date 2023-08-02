package com.example.towncenterstore.pojo.product.details_product;

import com.bumptech.glide.load.model.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseDetailsProduct<Model> {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("product_item")
    @Expose
    private Model productItem;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Model getProductItem() {
        return productItem;
    }

    public void setProductItem(Model productItem) {
        this.productItem = productItem;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}