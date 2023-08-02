package com.example.towncenterstore.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.adapters.NotificationAdapter;
import com.example.towncenterstore.databinding.ActivityNotificationBinding;
import com.example.towncenterstore.pojo.notification.BaseResponseNotification;
import com.example.towncenterstore.pojo.notification.Notification;
import com.example.towncenterstore.pojo.notification.Notifications;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    NotificationAdapter notificationAdapter;

    SharedPreferences sp;
    String token;

    ActivityNotificationBinding binding;

    public static Activity notificationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init() {
        notificationActivity = this;
        getToken();
        hiddenContent();
        showNotifications(token);
        onClickListeners();
    }

    private void visibleContent() {
        binding.rvNotifications.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvNotifications.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void getToken() {
        sp = getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
    }

    private void showNotifications(String token) {
        Call<BaseResponseNotification<Notification>> call = RetrofitClient.getInstance().getRetrofitRequests().showNotifications(token);
        call.enqueue(new Callback<BaseResponseNotification<Notification>>() {
            @Override
            public void onResponse(Call<BaseResponseNotification<Notification>> call, Response<BaseResponseNotification<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getNotifications() != null) {
                        setData(response);
                        visibleContent();
                    } else {
                        binding.noNotificationsLinearLayout.setVisibility(View.VISIBLE);
                        visibleContent();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(NotificationActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseNotification<Notification>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(NotificationActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    visibleContent();
                }
            }
        });

    }

    private void setData(Response<BaseResponseNotification<Notification>> response) {

        notificationAdapter = new NotificationAdapter(NotificationActivity.this);
        notificationAdapter.setNotificationList(response.body().getNotifications());
        binding.rvNotifications.setAdapter(notificationAdapter);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
    }


    private void onClickListeners() {
        onClickBtnBack();
    }

    private void onClickBtnBack() {
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        finish();
    }
}