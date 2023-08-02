package com.example.towncenterstore.pojo.product.Favourite_product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponseFavourite<Model> {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private boolean message;
    @SerializedName("products")
    @Expose
    private List<ProductFavourite> products;


    public List<ProductFavourite> getProducts() {
        return products;
    }

    public void setProducts(List<ProductFavourite> products) {
        this.products = products;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
