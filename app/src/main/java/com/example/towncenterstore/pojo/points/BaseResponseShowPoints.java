package com.example.towncenterstore.pojo.points;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseResponseShowPoints<Model> {
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("mypoints")
    @Expose
    private int myPoints;
    @SerializedName("points")
    @Expose
    private Model points;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getMyPoints() {
        return myPoints;
    }

    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    public Model getPoints() {
        return points;
    }

    public void setPoints(Model points) {
        this.points = points;
    }
}
