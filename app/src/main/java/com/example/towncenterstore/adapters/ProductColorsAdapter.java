package com.example.towncenterstore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ItemProductInStyleBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.AddToFavourite;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.listeners.RemoveFormFavourite;
import com.example.towncenterstore.pojo.product.Favourite_product.BaseResponseFavourite;
import com.example.towncenterstore.pojo.product.Favourite_product.ProductFavourite;
import com.example.towncenterstore.pojo.product.color_products.ProductColors;
import com.example.towncenterstore.general.CornersTransformation;
import com.example.towncenterstore.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductColorsAdapter extends RecyclerView.Adapter<ProductColorsAdapter.ViewHolder> {
    List<ProductColors> productColorsList;
    Context context;
    GoToProductDetails goToProductDetails;
    AddToFavourite addToFavourite;
    RemoveFormFavourite removeFormFavourite;
    SharedPreferences sp;
    String token;
    List<ProductFavourite> productFavourites = new ArrayList<>();


    public ProductColorsAdapter(Context context, GoToProductDetails goToProductDetails, AddToFavourite addToFavourite, RemoveFormFavourite removeFormFavourite) {
        this.context = context;
        this.goToProductDetails = goToProductDetails;
        this.addToFavourite = addToFavourite;
        this.removeFormFavourite = removeFormFavourite;
    }

    public List<ProductColors> getProductColorsList() {
        return productColorsList;
    }

    public void setProductColorsList(List<ProductColors> productColorsList) {
        this.productColorsList = productColorsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sp = getContext().getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        ItemProductInStyleBinding binding = ItemProductInStyleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setVisibility(View.INVISIBLE);
        showFavouriteProducts(token, holder, position);
        holder.productName.setText(productColorsList.get(position).getName());
        Glide.with(context).load(productColorsList.get(position).getImage()).
                apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 60))).
                into(holder.productImage);

        Drawable bc= holder.productColor.getBackground();
        bc.setColorFilter(Color.parseColor(productColorsList.get(position).getColor()), PorterDuff.Mode.ADD);

//        holder.productColor.setBackgroundColor(Color.parseColor(productColorsList.get(position).getColor()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProductDetails.setData(position, productColorsList.get(position).getId());
            }
        });

        holder.favouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (productColorsList.get(position).isActive()) {
                    holder.favouriteImage.setImageDrawable(getContext().getDrawable(R.drawable.heart3));
                    removeFormFavourite.removeFavourite(position);
                    productColorsList.get(position).setActive(false);
                } else {
                    holder.favouriteImage.setImageDrawable(getContext().getDrawable(R.drawable.heart3_fill));
                    addToFavourite.add(position);
                    productColorsList.get(position).setActive(true);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return productColorsList.size();
    }


    public Context getContext() {
        return context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage, shareImage, favouriteImage;
        TextView productName;
        View productColor;

        public ViewHolder(ItemProductInStyleBinding binding) {
            super(binding.getRoot());
            productName = binding.tvProductName;
            productImage = binding.imgProductPhoto;
            shareImage = binding.imgShareIcon;
            favouriteImage = binding.imgFavoriteIcon;
            productColor = binding.vProductColor;

        }
    }

    private void showFavouriteProducts(String token, @NonNull ViewHolder holder, int position) {

        Call<BaseResponseFavourite<ProductFavourite>> call = RetrofitClient.getInstance().getRetrofitRequests().showFavouriteProducts(token);
        call.enqueue(new Callback<BaseResponseFavourite<ProductFavourite>>() {
            @Override
            public void onResponse(Call<BaseResponseFavourite<ProductFavourite>> call, Response<BaseResponseFavourite<ProductFavourite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getProducts() != null) {
                        productFavourites = getData(response);
                        for (int i = 0; i < productFavourites.size(); i++) {
                            if (productFavourites.get(i).getProductId() == productColorsList.get(position).getId()) {
                                holder.favouriteImage.setImageDrawable(getContext().getDrawable(R.drawable.heart3_fill));
                                productColorsList.get(position).setActive(true);
                                holder.itemView.setVisibility(View.VISIBLE);
                                break;
                            } else {
                                holder.favouriteImage.setImageDrawable(getContext().getDrawable(R.drawable.heart3));
                                productColorsList.get(position).setActive(false);
                                holder.itemView.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        holder.favouriteImage.setImageDrawable(getContext().getDrawable(R.drawable.heart3));
                        productColorsList.get(position).setActive(false);
                        holder.itemView.setVisibility(View.VISIBLE);
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(getContext(), jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseFavourite<ProductFavourite>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private List<ProductFavourite> getData(Response<BaseResponseFavourite<ProductFavourite>> response) {

        if (response.body().getProducts() != null) {
            return productFavourites = response.body().getProducts();
        }
        return null;
    }
}


