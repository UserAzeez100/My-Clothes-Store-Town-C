package com.example.towncenterstore.pojo.orders.show_order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShowOrder {
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("total")
    @Expose
    private int total;

    public ShowOrder(String productName, int quantity, String price, int total) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
