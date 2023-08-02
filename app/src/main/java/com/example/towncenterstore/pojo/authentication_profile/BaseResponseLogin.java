package com.example.towncenterstore.pojo.authentication_profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseLogin<Model> {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Model data;
    @SerializedName("user")
    @Expose
    private Model user;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Model getData() {
        return data;
    }

    public void setData(Model data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Model getUser() {
        return user;
    }

    public void setUser(Model user) {
        this.user = user;
    }
}
