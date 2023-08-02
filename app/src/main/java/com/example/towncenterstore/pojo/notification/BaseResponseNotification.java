package com.example.towncenterstore.pojo.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponseNotification<Model> {
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("notifications")
    @Expose
    private List<Notification> notifications;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

}
