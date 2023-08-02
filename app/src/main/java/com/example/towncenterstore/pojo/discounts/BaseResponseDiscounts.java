package com.example.towncenterstore.pojo.discounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponseDiscounts<Model> {
    @SerializedName("products")
    @Expose
    private List<Model> products;

    public List<Model> getProducts() {
        return products;
    }

    public void setProducts(List<Model> products) {
        this.products = products;
    }
}
