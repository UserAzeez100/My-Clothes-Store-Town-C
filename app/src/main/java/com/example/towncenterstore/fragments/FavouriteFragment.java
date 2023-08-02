package com.example.towncenterstore.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.FragmentFavouriteBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.adapters.ProductsAdapterFavourite;
import com.example.towncenterstore.listeners.RemoveFormFavourite;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.product.Favourite_product.BaseResponseFavourite;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.product.Favourite_product.ProductFavourite;
import com.example.towncenterstore.pojo.product.cart_product.BaseResponseCart;
import com.example.towncenterstore.pojo.product.cart_product.ProductCart;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.example.towncenterstore.ui.DiscountActivity;
import com.example.towncenterstore.ui.HomeActivity;
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


public class FavouriteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentFavouriteBinding binding;
    List<ProductFavourite> productsFavouriteList = new ArrayList<>();
    OnClick onClick;
    String token;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ProductsAdapterFavourite productsAdapterFavourite;
    Context mContext;


    private String mParam1;
    private String mParam2;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onClick = (OnClick) context;
        mContext = context;
    }

    public static FavouriteFragment newInstance(String param1, String param2) {
        FavouriteFragment fragment = new FavouriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            getToken();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);

        init();
        return binding.getRoot();
    }

    private void init() {

        hiddenContent();
        showFavouriteProducts(token);

    }

    private void visibleContent() {
        binding.rvFavouriteProducts.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvFavouriteProducts.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void getToken() {
        sp = mContext.getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
    }

    private void showFavouriteProducts(String token) {
        Call<BaseResponseFavourite<ProductFavourite>> call = RetrofitClient.getInstance().getRetrofitRequests().showFavouriteProducts(token);
        call.enqueue(new Callback<BaseResponseFavourite<ProductFavourite>>() {
            @Override
            public void onResponse(Call<BaseResponseFavourite<ProductFavourite>> call, Response<BaseResponseFavourite<ProductFavourite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getProducts() != null)  {
                        visibleContent();
                        setData(response);
                    } else {
                        binding.noFavoriteItemsLinearLayout.setVisibility(View.VISIBLE);
                        visibleContent();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message")+"", Toast.LENGTH_SHORT).show();                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseFavourite<ProductFavourite>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage()+"", Toast.LENGTH_SHORT).show();                    visibleContent();
                }
            }
        });

    }

    private void setData(Response<BaseResponseFavourite<ProductFavourite>> response) {

           productsFavouriteList = response.body().getProducts();
           productsAdapterFavourite = new ProductsAdapterFavourite(mContext, new OnClick() {
               @Override
               public void onClick(String name) {
                   if (name.equals(Constants.DELETE_FAVOURITE)) {
                       hiddenContent();
                       showFavouriteProducts(token);
                   }
               }
           }, new GoToProductDetails() {
               @Override
               public void setData(int position, int id) {
                   saveInfoProduct(position,id);
                   onClick.onClick("favourite_product");
               }
           });

        productsAdapterFavourite.setProductsFavouriteList(productsFavouriteList);
        binding.rvFavouriteProducts.setAdapter(productsAdapterFavourite);
        binding.rvFavouriteProducts.setLayoutManager(new LinearLayoutManager(mContext));
    }
    private void saveInfoProduct(int position,int id){
        editor.putInt(Constants.PRODUCT_ID, id);
        editor.apply();
    }



}