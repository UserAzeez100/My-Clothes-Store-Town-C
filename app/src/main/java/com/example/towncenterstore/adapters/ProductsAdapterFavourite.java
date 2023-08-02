package com.example.towncenterstore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.databinding.ItemFavouriteProductBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.product.Favourite_product.ProductFavourite;
import com.example.towncenterstore.general.CornersTransformation;
import com.example.towncenterstore.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdapterFavourite extends RecyclerView.Adapter<ProductsAdapterFavourite.ViewHolder> {
    List<ProductFavourite> productsFavouriteList;
    Context context;
    OnClick onClick;
    SharedPreferences sp;
    String token;
    GoToProductDetails goToProductDetails;


    public ProductsAdapterFavourite(Context context, OnClick onClick, GoToProductDetails goToProductDetails) {
        this.context = context;
        this.onClick = onClick;
        this.goToProductDetails = goToProductDetails;
    }

    public List<ProductFavourite> getProductsFavouriteList() {
        return productsFavouriteList;
    }

    public void setProductsFavouriteList(List<ProductFavourite> productsFavouriteList) {
        this.productsFavouriteList = productsFavouriteList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public OnClick getOnClick() {
        return onClick;
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public ProductsAdapterFavourite.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavouriteProductBinding binding = ItemFavouriteProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new ProductsAdapterFavourite.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapterFavourite.ViewHolder holder, int position) {
        sp = getContext().getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        holder.name.setText(productsFavouriteList.get(position).getName());
        Glide.with(context).load(productsFavouriteList.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 15)))
                .into(holder.image);
        holder.date.setText(productsFavouriteList.get(position).getDate());
        holder.price.setText(productsFavouriteList.get(position).getPrice());

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromFavourite(productsFavouriteList.get(position).getProductId(), token, holder, position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProductDetails.setData(position, productsFavouriteList.get(position).getProductId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return productsFavouriteList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, deleteIcon;
        TextView name, date, price;
        ConstraintLayout constraintLayout;

        public ViewHolder(ItemFavouriteProductBinding binding) {
            super(binding.getRoot());
            image = binding.imgProductPhoto;
            name = binding.tvProductName;
            date = binding.tvProductDate;
            price = binding.tvProductPriceValue;
            deleteIcon = binding.imgDeleteItem;
            constraintLayout = binding.getRoot();
        }
    }

    public Context getContext() {
        return context;
    }

    private void removeFromFavourite(int productId, String token, @NonNull ProductsAdapterFavourite.ViewHolder holder, int position) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().removeFromFavourite(productId, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    holder.constraintLayout.setVisibility(View.INVISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(holder.constraintLayout.getContext(), android.R.anim.slide_in_left);
                    holder.constraintLayout.setAnimation(animation);
                    onClick.onClick(Constants.DELETE_FAVOURITE);
                    notifyDataSetChanged();
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);


                    } catch (JSONException | IOException e) {
                        Log.e("onResponsecatch", "onResponse");

                        throw new RuntimeException(e);

                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                if (t.getMessage() != null) {
////                    Snackbar.make(binding.getRoot(), t.getMessage(), Snackbar.LENGTH_LONG).show();
//                }
            }
        });

    }


}
