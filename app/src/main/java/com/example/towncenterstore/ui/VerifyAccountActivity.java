package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.towncenterstore.databinding.ActivityVerifyAccountBinding;

public class VerifyAccountActivity extends AppCompatActivity {

    ActivityVerifyAccountBinding binding;
    public static Activity verifyAccountActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        verifyAccountActivity = this;

//
//        binding.backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(VerifyAccountActivity.this, LoginActivity.class);
//                startActivity(intent);
//
//            }
//        });

        binding.btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyAccountActivity.this, ChangePasswordActivity.class);
                startActivity(intent);

            }
        });

    }
}