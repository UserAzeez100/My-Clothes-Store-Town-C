package com.example.towncenterstore.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.StyleAdapter;
import com.example.towncenterstore.databinding.ActivityStyleBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.product.style_product.BaseResponseStyle;
import com.example.towncenterstore.pojo.product.style_product.Style;
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


public class StyleActivity extends AppCompatActivity {
    ActivityStyleBinding binding;
    List<Style> styleArrayList = new ArrayList<>();
    StyleAdapter styleAdapter;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String token, categoryName;
    int categoryId;
    int page = 1;

    public static Activity styleActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStyleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init() {
        styleActivity = this;
        HomeActivity.homeActivity.finish();
        getToken();
        hiddenContent();
        showStyles(page, categoryId, token);
        onChangeScroll();
        onClickBtnBack();


    }

    private void showStyles(int page, int categoryId, String token) {
        Call<BaseResponseStyle<Products<Style>>> call = RetrofitClient.getInstance().getRetrofitRequests().showStyles(page, categoryId, token);
        call.enqueue(new Callback<BaseResponseStyle<Products<Style>>>() {
            @Override
            public void onResponse(Call<BaseResponseStyle<Products<Style>>> call, Response<BaseResponseStyle<Products<Style>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getProducts() != null) {
                        setData(response);
                    }
                    visibleContent();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);

                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(StyleActivity.this,jsonObject.getString("message")+ "", Toast.LENGTH_SHORT).show();                        visibleContent();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseStyle<Products<Style>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(StyleActivity.this, t.getMessage()+"", Toast.LENGTH_SHORT).show();                    visibleContent();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void setData(Response<BaseResponseStyle<Products<Style>>> response) {


        if (response.body().getProducts().getCurrentPage() == 1) {
            styleArrayList = response.body().getProducts().getData();
            styleAdapter = new StyleAdapter(StyleActivity.this, new OnClickItemCategoryOrProduct() {
                @Override
                public void onClick(int position) {
                    saveInfoStyle(response,position);
                    moveNextScreen(StyleActivity.this, ProductColorsActivity.class,R.anim.slide_in_bottom,R.anim.slide_out_bottom);

                }
            }
            );

        } else {
            styleArrayList.addAll(response.body().getProducts().getData());
        }
        styleAdapter.setStyleArrayList(styleArrayList);
        binding.rvStyles.setAdapter(styleAdapter);
        binding.rvStyles.setLayoutManager(new LinearLayoutManager(StyleActivity.this));


    }

    private void saveInfoStyle(Response<BaseResponseStyle<Products<Style>>> response,int position){
        editor.putInt(Constants.STYLE_ID, response.body().getProducts().getData().get(position).getId());
        editor.putString(Constants.STYLE_NAME, response.body().getProducts().getData().get(position).getName());
        editor.apply();
    }

    private void getToken() {

        sp = StyleActivity.this.getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
        if (sp.getInt(Constants.CATEGORY_ID, 0) > 0) {
            categoryId = sp.getInt(Constants.CATEGORY_ID, 0);
        }
        if (!sp.getString(Constants.CATEGORY_NAME, "").equals("")) {
            categoryName = sp.getString(Constants.CATEGORY_NAME, "");
            binding.tvScreenTitle.setText(categoryName);
        }

    }

    private void visibleContent() {
        binding.rvStyles.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvStyles.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void onChangeScroll(){
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    binding.progressLoadMore.setVisibility(View.VISIBLE);
                    showStyles(page, categoryId, token);
                }

            }
        });
    }

    private void onClickBtnBack(){
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNextScreen(StyleActivity.this, HomeActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                finish();
            }
        });
    }

    private void moveNextScreen(Context fromActivity, Class<?> toActivity, int firstAnim, int lastAnim) {
        startActivity(new Intent(fromActivity, toActivity));
        overridePendingTransition(firstAnim, lastAnim);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveNextScreen(StyleActivity.this, HomeActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        finish();
    }
}