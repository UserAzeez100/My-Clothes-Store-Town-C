package com.example.towncenterstore.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.towncenterstore.databinding.ActivityHomeBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.R;
import com.example.towncenterstore.fragments.CartFragment;
import com.example.towncenterstore.fragments.FavouriteFragment;
import com.example.towncenterstore.fragments.HomeFragment;
import com.example.towncenterstore.fragments.OrdersFragment;
import com.example.towncenterstore.fragments.ProfileFragment;
import com.example.towncenterstore.listeners.OnClick;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity implements OnClick {
    ActivityHomeBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public static Activity homeActivity;
    public static MeowBottomNavigation bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();


    }

    public void initFragment() {
        homeActivity = this;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutContainer, HomeFragment.newInstance("", "")).commit();
    }

    private void init() {

        bottomNavigationView = binding.bottomNavigation;

        sp = getSharedPreferences(Constants.NAME_SHARED, MODE_PRIVATE);
        editor = sp.edit();

        initFragment();
        onClickBottomNavigation();
        fromProfileFragment();
        getLocation();
        toCartFragment();

    }

    public void onClickBottomNavigation() {

        binding.bottomNavigation.show(R.id.home, true);

        binding.bottomNavigation.add(new MeowBottomNavigation.Model(R.id.favourite, R.drawable.heart_icon2));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(R.id.profile, R.drawable.profle_person_icon));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(R.id.home, R.drawable.home_icon2));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(R.id.cart, R.drawable.buy_icon2));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(R.id.orders, R.drawable.order_icon2));


        binding.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayoutContainer, HomeFragment.newInstance("", "")).commit();

                        break;
                    case R.id.orders:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayoutContainer, OrdersFragment.newInstance("", "")).commit();
                        break;
                    case R.id.favourite:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayoutContainer, FavouriteFragment.newInstance("", "")).commit();
                        break;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayoutContainer, ProfileFragment.newInstance("", "")).commit();
                        break;
                    case R.id.cart:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayoutContainer, CartFragment.newInstance("", "", "")).commit();
                        break;
                }
                return null;
            }
        });

    }

    private void fromProfileFragment() {
        Intent intent1 = getIntent();
        if ((intent1.getStringExtra(Constants.NAME_ACTIVITY_INTENT_TO_HOME)) != null) {
            if ((intent1.getStringExtra(Constants.NAME_ACTIVITY_INTENT_TO_HOME)).equals("Profile_Fragment")) {
                binding.bottomNavigation.show(R.id.profile, true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutContainer, ProfileFragment.newInstance("", "")).commit();
            }
        }

    }

    private void toCartFragment() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra(Constants.CLICK_CART, false)) {
            binding.bottomNavigation.show(R.id.cart, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayoutContainer, CartFragment.newInstance("", "", "")).commit();

        }
    }

    private void getLocation() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra(Constants.STATUS_INTENT_LOCATION, false)) {
            if (!intent.getStringExtra(Constants.DETAILS_TITLE).isEmpty() && !intent.getStringExtra(Constants.MAP_LAT).isEmpty() && !intent.getStringExtra(Constants.MAP_LONG).isEmpty()) {
                binding.bottomNavigation.show(R.id.cart, true);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayoutContainer, CartFragment.newInstance(intent.getStringExtra(Constants.DETAILS_TITLE), intent.getStringExtra(Constants.MAP_LAT), intent.getStringExtra(Constants.MAP_LONG))).commit();

            }
        }


    }

    @Override
    public void onClick(String name) {

        switch (name) {
            case "search_box":
                moveNextScreen(HomeActivity.this, SearchActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "icon_notification":
                moveNextScreen(HomeActivity.this, NotificationActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "category":
                moveNextScreen(HomeActivity.this, StyleActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "location":
                moveNextScreen(HomeActivity.this, LocationActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "receipt":
                moveNextScreen(HomeActivity.this, ReceiptActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "edit_profile":
                moveNextScreen(HomeActivity.this, EditProfileActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
//            case "change_password":
//                startActivity(new Intent(HomeActivity.this, ChangePasswordActivity.class));
//                break;
            case "call_us":
                moveNextScreen(HomeActivity.this, ContactUsActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "notification":
                moveNextScreen(HomeActivity.this, NotificationActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "log_out_profile":
                moveNextScreen(HomeActivity.this, LoginActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                finish();
                break;
            case "sale_page":
                moveNextScreen(HomeActivity.this, DiscountActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "favourite_product":
                moveNextScreen(HomeActivity.this, ProductDetailActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                break;
            case "person_photo":
                startActivity(new Intent(HomeActivity.this, ShowProfileImageActivity.class));
                Animatoo.INSTANCE.animateSlideUp(HomeActivity.this);
                break;
        }
    }

    private void moveNextScreen(Context fromActivity, Class<?> toActivity, int firstAnim, int lastAnim) {
        startActivity(new Intent(fromActivity, toActivity));
        overridePendingTransition(firstAnim, lastAnim);
    }


}