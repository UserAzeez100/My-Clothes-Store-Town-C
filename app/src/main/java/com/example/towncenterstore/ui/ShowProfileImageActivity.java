package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.towncenterstore.databinding.ActivityShowProfileImageBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.OnClick;

public class ShowProfileImageActivity extends AppCompatActivity {
    ActivityShowProfileImageBinding binding;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowProfileImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }


    private void init() {
        getProductImage();
    }

    private void getProductImage() {
        sp = getSharedPreferences(Constants.NAME_SHARED, MODE_PRIVATE);
        if (!sp.getString(Constants.PERSON_PHOTO, "").isEmpty()) {
            Glide.with(ShowProfileImageActivity.this).load(sp.getString(Constants.PERSON_PHOTO, "")).into(binding.photoView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.INSTANCE.animateSlideDown(ShowProfileImageActivity.this);
        finish();
    }
}