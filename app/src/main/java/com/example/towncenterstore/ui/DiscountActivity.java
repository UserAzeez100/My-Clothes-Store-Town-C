package com.example.towncenterstore.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.DiscountAdapter;
import com.example.towncenterstore.adapters.NotificationAdapter;
import com.example.towncenterstore.adapters.StyleAdapter;
import com.example.towncenterstore.databinding.ActivityDiscountBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.pojo.discounts.BaseResponseDiscounts;
import com.example.towncenterstore.pojo.discounts.DiscountProduct;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.product.style_product.BaseResponseStyle;
import com.example.towncenterstore.pojo.product.style_product.Style;
import com.example.towncenterstore.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DiscountActivity extends AppCompatActivity {

    ActivityDiscountBinding binding;
    public static Activity discountActivity;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token;
    DiscountAdapter discountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();


    }

    private void init(){
        discountActivity = this;
        getToken();
        hiddenContent();
        showDiscountsProducts(token);
        onClickBtnBack();

    }

    private void visibleContent() {
        binding.rvDiscount.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvDiscount.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void getToken() {
        sp = getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
    }

    private void showDiscountsProducts(String token) {
        Call<BaseResponseDiscounts<DiscountProduct>> call = RetrofitClient.getInstance().getRetrofitRequests().showDiscountsProducts(token);
        call.enqueue(new Callback<BaseResponseDiscounts<DiscountProduct>>() {
            @Override
            public void onResponse(Call<BaseResponseDiscounts<DiscountProduct>> call, Response<BaseResponseDiscounts<DiscountProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getProducts().isEmpty()){
                        setData(response);
                    }
                    visibleContent();
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(DiscountActivity.this,jsonObject.getString("message")+ "", Toast.LENGTH_SHORT).show();                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseDiscounts<DiscountProduct>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(DiscountActivity.this, t.getMessage()+"", Toast.LENGTH_SHORT).show();                    visibleContent();
                }
            }
        });

    }

    private void setData(Response<BaseResponseDiscounts<DiscountProduct>> response) {

        discountAdapter = new DiscountAdapter(DiscountActivity.this, new GoToProductDetails() {
            @Override
            public void setData(int position, int id) {
                saveInfoProduct(position,id);
                moveNextScreen(DiscountActivity.this, ProductDetailActivity.class,R.anim.slide_in_bottom,R.anim.slide_out_bottom);

            }
        });
        discountAdapter.setDiscountProductList(response.body().getProducts());
        binding.rvDiscount.setAdapter(discountAdapter);
        binding.rvDiscount.setLayoutManager(new LinearLayoutManager(DiscountActivity.this));

    }
    private void moveNextScreen(Context fromActivity, Class<?> toActivity, int firstAnim, int lastAnim){
        startActivity(new Intent(fromActivity,toActivity));
        overridePendingTransition(firstAnim, lastAnim);
    }

    private void saveInfoProduct(int position,int id){
        editor.putInt(Constants.PRODUCT_ID, id);
        editor.apply();
    }

    private void onClickBtnBack(){
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}