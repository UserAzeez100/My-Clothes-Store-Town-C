package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;

    Animation bottom_animation, middleAnimation, top_animation;
    private static final int SPLASH_TIME_OUT = 3000;
    SharedPreferences sp;
    public static Activity splashActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
       getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navigationAppBar));
        setContentView(binding.getRoot());

        splashActivity = this;



//        Window window = splashActivity.getWindow();
//
//// clear FLAG_TRANSLUCENT_STATUS flag:
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//// finally change the color
//        window.setStatusBarColor(ContextCompat.getColor(splashActivity,R.color.o_base_color_app));

        init();
        //Splash Screen Code to call new Activity after some time
        goToNextActivity();


    }

    public void goToNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intentType = getIntent();
                if (isConnectedToInternet()) {
                    // user is connected to the internet
                    if (intentType.getStringExtra("type")!= null){
                        Log.e("intentType",intentType.getStringExtra("type")+"");
                        if (intentType.getStringExtra("type").equals("database")){
                            Intent intent = new Intent(SplashActivity.this, NotificationActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (intentType.getStringExtra("type").equals("status")){
                            Intent intent = new Intent(SplashActivity.this, ReceiptActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        if (sp.getBoolean(Constants.IF_OPEN_ACTIVITY, false)) {
                            if (sp.getBoolean(Constants.STATUS_REMEMBER, false)) {
                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Intent intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }


                }else {
                    // user is not connected to the internet
                    startActivity(new Intent(SplashActivity.this,NoInternetConnectionActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);


    }

    public void init() {
        sp = getSharedPreferences(Constants.NAME_SHARED, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.clear();
//        editor.apply();


        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.midel_animation);
        top_animation = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        binding.imgLogoApp.setAnimation(middleAnimation);

    }

    boolean isConnectedToInternet() {

        ConnectivityManager cm =
                (ConnectivityManager)SplashActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}