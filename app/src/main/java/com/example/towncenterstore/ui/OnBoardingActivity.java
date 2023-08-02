package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.towncenterstore.listeners.ClickBtnSkip;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.ViewPagerAdapter;
import com.example.towncenterstore.databinding.ActivityOnBoardingBinding;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;


public class OnBoardingActivity extends AppCompatActivity {
    ActivityOnBoardingBinding binding;

    ViewPagerAdapter adapter;
    String[] textView1;
    String[] textView2;
    int[] images;
    WormDotsIndicator dotsIndicator;




    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public static Activity onBoardingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onBoardingActivity = this;


        test();

    }

    public void test(){
        textView1 = new String[]{getString(R.string.on_bording_1),getString(R.string.on_bording_2), getString(R.string.on_bording_3)};

        textView2 = new String[]{getString(R.string.on_bording_down_1),getString(R.string.on_bording_down_2), getString(R.string.on_bording_down_3)};


        images = new int[]{R.drawable.on_bording_image1,R.drawable.on_bording_image2,R.drawable.onbording_iamge3};



        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(OnBoardingActivity.this, textView1, textView2,images,
                new ClickBtnSkip() {
                    @Override
                    public void clickBtnSkip(int position) {
                        if (position == 2) {
                            Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            binding.viewPager.setCurrentItem(position+1);
                        }
                    }
                });
        // Binds the Adapter to the ViewPager
        binding.viewPager.setAdapter(adapter);
        adapter.setDotsIndicator(dotsIndicator);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp = getSharedPreferences(Constants.NAME_SHARED, MODE_PRIVATE);
        editor = sp.edit();
        editor.putBoolean(Constants.IF_OPEN_ACTIVITY,true);
        editor.apply();
    }


}