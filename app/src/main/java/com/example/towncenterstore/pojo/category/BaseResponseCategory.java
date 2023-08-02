package com.example.towncenterstore.pojo.category;

import com.example.towncenterstore.pojo.authentication_profile.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseCategory<Model> {
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("categories")
    @Expose
    private Model categories;
    @SerializedName("notifications")
    @Expose
    private int notifications;
    @SerializedName("orders_count")
    @Expose
    private int ordersCount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Model getCategories() {
        return categories;
    }

    public void setCategories(Model categories) {
        this.categories = categories;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }
}
