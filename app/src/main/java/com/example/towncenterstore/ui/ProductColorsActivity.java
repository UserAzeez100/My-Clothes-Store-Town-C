package com.example.towncenterstore.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.ProductColorsAdapter;
import com.example.towncenterstore.databinding.ActivityProductColorsBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.AddToFavourite;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.listeners.RemoveFormFavourite;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.product.Favourite_product.BaseResponseFavourite;
import com.example.towncenterstore.pojo.product.Favourite_product.ProductFavourite;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.product.color_products.BaseResponseColorProduct;
import com.example.towncenterstore.pojo.product.color_products.ProductColors;
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


public class ProductColorsActivity extends AppCompatActivity {

    ActivityProductColorsBinding binding;
    List<ProductColors> colorsArrayList = new ArrayList<>();
    ProductColorsAdapter productColorsAdapter;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token, styleName;
    int styleId, productId;
    int page = 1;

    public static Activity productColorsActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductColorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();



    }


    private void init() {
        productColorsActivity = this;

        getToken();
        hiddenContent();
        showProductColors(page, styleId, token);
        onChangeScroll();
        onClickListeners();

    }

    private void onClickListeners(){
        onClickBtnBack();
    }

    private void showProductColors(int page, int styleId, String token) {
        Call<BaseResponseColorProduct<Products<ProductColors>>> call = RetrofitClient.getInstance().getRetrofitRequests().showProductColors(page, styleId, token);

        call.enqueue(new Callback<BaseResponseColorProduct<Products<ProductColors>>>() {
            @Override
            public void onResponse(Call<BaseResponseColorProduct<Products<ProductColors>>> call, Response<BaseResponseColorProduct<Products<ProductColors>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getProductItems() != null) {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        setData(response);
                        visibleContent();
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    }

                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ProductColorsActivity.this, jsonObject.getString("message")+"", Toast.LENGTH_SHORT).show();                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {

                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseColorProduct<Products<ProductColors>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductColorsActivity.this,  t.getMessage()+"", Toast.LENGTH_SHORT).show();                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });
    }

    private void setData(Response<BaseResponseColorProduct<Products<ProductColors>>> response) {

        if (response.body().getProductItems().getCurrentPage() == 1) {
            colorsArrayList = response.body().getProductItems().getData();
            productColorsAdapter = new ProductColorsAdapter(ProductColorsActivity.this, new GoToProductDetails() {
                @Override
                public void setData(int position, int id) {
                    saveInfoProduct(position,id);
                    moveNextScreen(ProductColorsActivity.this, ProductDetailActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                }
            }, new AddToFavourite() {
                @Override
                public void add(int position) {
                    productId = response.body().getProductItems().getData().get(position).getId();

                    addToFavourite(productId, token);

                }
            }, new RemoveFormFavourite() {
                @Override
                public void removeFavourite(int position) {
                    productId = response.body().getProductItems().getData().get(position).getId();
                    removeFromFavourite(productId, token);

                }
            }
            );

        } else {
            colorsArrayList.addAll(response.body().getProductItems().getData());

        }
        productColorsAdapter.setProductColorsList(colorsArrayList);
        binding.rvProductColors.setAdapter(productColorsAdapter);
        binding.rvProductColors.setLayoutManager(new GridLayoutManager(ProductColorsActivity.this,2));
    }

    private void saveInfoProduct(int position,int id){
        editor.putInt(Constants.PRODUCT_ID, id);
        editor.apply();
    }



    private void getToken() {

        sp = getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
        if (sp.getInt(Constants.STYLE_ID, 0) > 0) {
            styleId = sp.getInt(Constants.STYLE_ID, 0);
        }
        if (!sp.getString(Constants.STYLE_NAME, "").equals("")) {
            styleName = sp.getString(Constants.STYLE_NAME, "");
            binding.tvScreenTitle.setText(styleName);
        }

    }

    private void visibleContent() {
        binding.rvProductColors.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvProductColors.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void addToFavourite(int productId, String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().addToFavourite(productId, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductColorsActivity.this, response.body().getMessage()+"", Toast.LENGTH_SHORT).show();                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ProductColorsActivity.this, jsonObject.getString("message")+"", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductColorsActivity.this, t.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeFromFavourite(int productId, String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().removeFromFavourite(productId, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductColorsActivity.this, response.body().getMessage()+"", Toast.LENGTH_SHORT).show();                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ProductColorsActivity.this, jsonObject.getString("message")+"", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductColorsActivity.this, t.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void onChangeScroll() {
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    binding.progressLoadMore.setVisibility(View.VISIBLE);
                    showProductColors(page, styleId, token);
                }

            }
        });
    }

    private void onClickBtnBack() {
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

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
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

    }
}