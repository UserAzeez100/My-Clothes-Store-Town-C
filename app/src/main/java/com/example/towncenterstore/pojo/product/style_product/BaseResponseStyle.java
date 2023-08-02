package com.example.towncenterstore.pojo.product.style_product;

import android.view.Display;

import com.example.towncenterstore.pojo.product.Products;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseStyle<Model> {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("products")
    @Expose
    private Model products;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Model getProducts() {
        return products;
    }

    public void setProducts(Model products) {
        this.products = products;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
