package com.example.towncenterstore.pojo.slider;

import android.graphics.drawable.Drawable;

public class SliderData {

    private Drawable imgUrl;


    public SliderData(Drawable imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Drawable getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(Drawable imgUrl) {
        this.imgUrl = imgUrl;
    }
}
