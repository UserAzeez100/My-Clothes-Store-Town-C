package com.example.towncenterstore.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.ProductColorsAdapter;
import com.example.towncenterstore.adapters.SizeProductAdapter;
import com.example.towncenterstore.databinding.ActivityProductDetailBinding;
import com.example.towncenterstore.databinding.FragmentCartBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.AddToFavourite;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.listeners.RemoveFormFavourite;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.product.color_products.BaseResponseColorProduct;
import com.example.towncenterstore.pojo.product.color_products.ProductColors;
import com.example.towncenterstore.pojo.product.details_product.BaseResponseDetailsProduct;
import com.example.towncenterstore.pojo.product.details_product.ProductColor;
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

public class ProductDetailActivity extends AppCompatActivity {

    ActivityProductDetailBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token;
    int productId, productQuantity;
    ArrayList<String> sizeProductArrayList;
    SizeProductAdapter sizeProductAdapter;
    String productSize;
    int page = 1;
    int styleId;
    ProductColors productColors;
    List<ProductColors> colorsArrayList = new ArrayList<>();
    ProductColorsAdapter productColorsAdapter;
    public static Activity productDetailActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();


    }

    private void onClickLowerOrIncreaseQuantity() {
        binding.imgIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
            }
        });

        binding.imgLowerQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lowerQuantity();
            }
        });
    }

    private void init() {
        productDetailActivity = this;
        hiddenContent();
        getToken();
        onClickListeners();
        showProductDetails(productId, token);
        onChangeScroll();
        onClickProductImage();


    }

    private void onClickListeners() {
        onClickLowerOrIncreaseQuantity();
        onClickAddToCartBtn();
        onClickBtnBack();
        onClickCartImage();
    }

    private void onClickCartImage(){
        binding.cartButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this,HomeActivity.class);
                intent.putExtra(Constants.CLICK_CART,true);
                startActivity(intent);
                HomeActivity.homeActivity.finish();
                StyleActivity.styleActivity.finish();
                ProductColorsActivity.productColorsActivity.finish();
                finish();
            }
        });
    }


    private void getToken() {

        sp = getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
        if (sp.getInt(Constants.PRODUCT_ID, 0) > 0) {
            productId = sp.getInt(Constants.PRODUCT_ID, 0);
        }
        productQuantity = Integer.parseInt(binding.tvItemQuantity.getText().toString());
        if (sp.getInt(Constants.STYLE_ID, 0) > 0) {
            styleId = sp.getInt(Constants.STYLE_ID, 0);
        }

    }

    private void onClickProductImage() {
        binding.imgProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetailActivity.this, ShowProductImageActivity.class));
                Animatoo.INSTANCE.animateSlideUp(ProductDetailActivity.this);
            }
        });
    }


    private void showProductDetails(int productId, String token) {
        Call<BaseResponseDetailsProduct<ProductColors>> call = RetrofitClient.getInstance().getRetrofitRequests().showProductDetails(productId, token);
        call.enqueue(new Callback<BaseResponseDetailsProduct<ProductColors>>() {
            @Override
            public void onResponse(Call<BaseResponseDetailsProduct<ProductColors>> call, Response<BaseResponseDetailsProduct<ProductColors>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                            productColors = response.body().getProductItem();
                            showProductColors(page, styleId, token);
                            setInfoProduct(response);
                            visibleContent();

                    } else {
                        binding.progress.setVisibility(View.INVISIBLE);
                    }

                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ProductDetailActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseDetailsProduct<ProductColors>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductDetailActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setInfoProduct(Response<BaseResponseDetailsProduct<ProductColors>> response) {

        Glide.with(ProductDetailActivity.this).load(response.body().getProductItem().getImage()).apply(RequestOptions.circleCropTransform()).into(binding.imgProductImage);
        editor.putString(Constants.PRODUCT_IMAGE, response.body().getProductItem().getImage());
        editor.apply();

        binding.titleProductTv.setText(response.body().getProductItem().getName());
        binding.tvShowProductDescription.setText(response.body().getProductItem().getDescription());
        binding.productPriceTv.setText(response.body().getProductItem().getPrice()+"₪");

        if (response.body().getProductItem().getOriginalPrice() != null){
            binding.productBeforePriceTv.setText(response.body().getProductItem().getOriginalPrice()+"₪");
            binding.productBeforePriceTv.setVisibility(View.VISIBLE);
            binding.lineTv.setVisibility(View.VISIBLE);
        }




        sizeProductArrayList = (ArrayList<String>) response.body().getProductItem().getSize();
        sizeProductAdapter = new SizeProductAdapter(sizeProductArrayList, ProductDetailActivity.this, new OnClick() {
            @Override
            public void onClick(String name) {
                productSize = name;
            }
        });

        binding.rvSizes.setAdapter(sizeProductAdapter);
        binding.rvSizes.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, RecyclerView.HORIZONTAL, false));

    }

    private void visibleContent() {
        binding.nestedScrollView.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.nestedScrollView.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void increaseQuantity() {
        binding.tvItemQuantity.setText(String.valueOf(productQuantity + 1));
        productQuantity = Integer.parseInt(binding.tvItemQuantity.getText().toString());
    }

    private void lowerQuantity() {
        if (productQuantity == 1) {
            Toast.makeText(ProductDetailActivity.this, getResources().getString(R.string.the_quantity_cannot_be_reduced_less_than_1), Toast.LENGTH_SHORT).show();
        } else {
            binding.tvItemQuantity.setText(String.valueOf(productQuantity - 1));
            productQuantity = Integer.parseInt(binding.tvItemQuantity.getText().toString());
        }
    }

    private void addToCart(int productId, int quantity, String size, String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().addToCart(productId, quantity, size, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressBtnAdd.setVisibility(View.INVISIBLE);
                    binding.btnAddCart.setVisibility(View.VISIBLE);
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ProductDetailActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressBtnAdd.setVisibility(View.INVISIBLE);
                        binding.btnAddCart.setVisibility(View.VISIBLE);
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductDetailActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressBtnAdd.setVisibility(View.INVISIBLE);
                    binding.btnAddCart.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void onClickAddToCartBtn() {
        binding.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productSize != null) {
                    binding.btnAddCart.setVisibility(View.INVISIBLE);
                    binding.progressBtnAdd.setVisibility(View.VISIBLE);
                    addToCart(productId, productQuantity, productSize, token);
                } else {
                    Toast.makeText(ProductDetailActivity.this, getResources().getString(R.string.choose_the_size_product), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);


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
                        Toast.makeText(ProductDetailActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {

                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseColorProduct<Products<ProductColors>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductDetailActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });
    }

    private void setData(Response<BaseResponseColorProduct<Products<ProductColors>>> response) {

        if (response.body().getProductItems().getCurrentPage() == 1) {
            for (int i = 0; i < response.body().getProductItems().getData().size(); i++) {
                if (response.body().getProductItems().getData().get(i).getId() == productId) {
                    response.body().getProductItems().getData().remove(response.body().getProductItems().getData().get(i));

                }
            }
            colorsArrayList = response.body().getProductItems().getData();
            productColorsAdapter = new ProductColorsAdapter(ProductDetailActivity.this, new GoToProductDetails() {
                @Override
                public void setData(int position, int id) {
                    productId = id;
                    saveInfoProduct(position, id);
                    hiddenContent();
                    showProductDetails(productId, token);

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
        binding.rvProducts.setAdapter(productColorsAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductDetailActivity.this, RecyclerView.HORIZONTAL, false);
        binding.rvProducts.setLayoutManager(layoutManager);
    }


    private void saveInfoProduct(int position, int id) {
        editor.putInt(Constants.PRODUCT_ID, id);
        editor.apply();
    }

    private void addToFavourite(int productId, String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().addToFavourite(productId, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ProductDetailActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductDetailActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ProductDetailActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ProductDetailActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void onChangeScroll() {
        binding.nestedScrollView2.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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


//        private void testShare() {
//    binding.productImage.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//                try {
//                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
//                    String shareMessage= "\nLet me recommend you this application\n\n";
//                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                    startActivity(Intent.createChooser(shareIntent, "choose one"));
//                } catch(Exception e) {
//                    //e.toString();
//                }
//
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "post.getTitle()");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, "post.getContent()");
//
//            // Start the activity to share the post
//            startActivity(Intent.createChooser(shareIntent, "Share post"));
//        }
//    });
//    }

}