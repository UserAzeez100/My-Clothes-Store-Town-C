package com.example.towncenterstore.pojo.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("total_price")
    @Expose
    private String totalPrice;
    @SerializedName("discount")
    @Expose
    private Object discount;
    @SerializedName("final_price")
    @Expose
    private String finalPrice;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("products")
    @Expose
    private List<String> products;
    @SerializedName("count_products")
    @Expose
    private int countProducts;

    public Order(){}

    public Order(int id, String status, int userId, String datetime, String location, String totalPrice, Object discount, String finalPrice, String lng, String lat, String createdAt, String updatedAt, List<String> products, int countProducts) {
        this.id = id;
        this.status = status;
        this.userId = userId;
        this.datetime = datetime;
        this.location = location;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.finalPrice = finalPrice;
        this.lng = lng;
        this.lat = lat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.products = products;
        this.countProducts = countProducts;
    }

    public Order(int id, String status, String finalPrice, String updatedAt) {
        this.id = id;
        this.status = status;
        this.finalPrice = finalPrice;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Object getDiscount() {
        return discount;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public int getCountProducts() {
        return countProducts;
    }

    public void setCountProducts(int countProducts) {
        this.countProducts = countProducts;
    }
}
