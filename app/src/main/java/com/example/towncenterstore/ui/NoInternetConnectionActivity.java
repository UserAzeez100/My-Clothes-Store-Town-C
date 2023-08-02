package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ActivityNoInternetConnectionBinding;

public class NoInternetConnectionActivity extends AppCompatActivity {

    ActivityNoInternetConnectionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoInternetConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       onClickBtnTryAgain();
    }


private void onClickBtnTryAgain(){
    binding.btnTryAgain.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isConnectedToInternet()){
                startActivity(new Intent(NoInternetConnectionActivity.this,SplashActivity.class));
                finish();
            }
        }
    });
}
   private boolean isConnectedToInternet() {

        ConnectivityManager cm =
                (ConnectivityManager)NoInternetConnectionActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}