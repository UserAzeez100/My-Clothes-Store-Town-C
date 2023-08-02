package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ActivityShowProductImageBinding;
import com.example.towncenterstore.general.Constants;

public class ShowProductImageActivity extends AppCompatActivity {
ActivityShowProductImageBinding binding;
SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowProductImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();


    }

    private void init(){
        getProductImage();
    }

    private void getProductImage(){
        sp = getSharedPreferences(Constants.NAME_SHARED,MODE_PRIVATE);
        if (!sp.getString(Constants.PRODUCT_IMAGE,"").isEmpty()){
            Glide.with(ShowProductImageActivity.this).load(sp.getString(Constants.PRODUCT_IMAGE,"")).into(binding.photoView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateSlideDown(ShowProductImageActivity.this);
        finish();
    }
}