package com.example.towncenterstore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.databinding.ItemCartProductBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.ChangeTheQuantityOrDelete;
import com.example.towncenterstore.pojo.product.cart_product.ProductCart;
import com.example.towncenterstore.general.CornersTransformation;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
   private List<ProductCart> productCartArrayList;
    private Context context;
    private SharedPreferences sp;
    private int pos;
    private ChangeTheQuantityOrDelete changeTheQuantityOrDelete;

    public CartAdapter( Context context, ChangeTheQuantityOrDelete changeTheQuantityOrDelete) {
        this.context = context;
        this.changeTheQuantityOrDelete = changeTheQuantityOrDelete;
    }

    public List<ProductCart> getCartProductArrayList() {
        return productCartArrayList;
    }

    public void setCartProductArrayList(List<ProductCart> productCartArrayList) {
        this.productCartArrayList = productCartArrayList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ChangeTheQuantityOrDelete getChangeTheQuantity() {
        return changeTheQuantityOrDelete;
    }

    public void setChangeTheQuantity(ChangeTheQuantityOrDelete changeTheQuantityOrDelete) {
        this.changeTheQuantityOrDelete = changeTheQuantityOrDelete;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartProductBinding binding = ItemCartProductBinding.inflate(LayoutInflater.from(context), parent,
                false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        sp = getContext().getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        pos = position;

        String token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        holder.productName.setText(productCartArrayList.get(position).getName());
//        holder.productColor.setBackgroundColor(Color.parseColor(productCartArrayList.get(position).getColor()));
        holder.productSize.setText(productCartArrayList.get(position).getSize());
        double price = Double.parseDouble(productCartArrayList.get(position).getPrice());
        holder.productPrice.setText(price * productCartArrayList.get(position).getQuantity()+"₪");
        holder.productQuantity.setText(String.valueOf(productCartArrayList.get(position).getQuantity()));

        Drawable bc= holder.productColor.getBackground();
        bc.setColorFilter(Color.parseColor(productCartArrayList.get(position).getColor()), PorterDuff.Mode.ADD);


        Glide.with(context).load(productCartArrayList.get(position).getImage()).
        apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 15)))
                .into(holder.productImage);
        holder.increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTheQuantityOrDelete.change(Constants.INCREASE_QUANTITY, position, productCartArrayList.get(position).getId());
                Log.e("position",position+"");
                Log.e("pos",pos+"");
            }
        });

        holder.lowerQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productCartArrayList.get(position).getQuantity() > 1) {
                    Log.e("position",position+"");
                    Log.e("pos",pos+"");
                    changeTheQuantityOrDelete.change(Constants.LOWER_QUANTITY, position, productCartArrayList.get(position).getId());
                }else {
                    Toast.makeText(getContext(), "The quantity cannot be reduced less than 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("position",position+"");
                Log.e("pos",pos+"");
                changeTheQuantityOrDelete.change(Constants.DELETE_ITEM, position, productCartArrayList.get(position).getId());
//                deleteItem(productCartArrayList.get(position).getId(), token, holder);

            }
        });


    }

    @Override
    public int getItemCount() {
        return productCartArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productSize, productPrice, productQuantity;
        View productColor;
        ImageView increaseQuantity, lowerQuantity, deleteItem;
        CardView cardViewProduct;

        public ViewHolder(ItemCartProductBinding binding) {
            super(binding.getRoot());

            productImage = binding.imgProductsImage;
            productName = binding.tvProductName;
            productColor = binding.imageProductColor;
            productSize = binding.tvProductSizeValue;
            productPrice = binding.tvProductPrice;
            productQuantity = binding.tvItemQuantity;
            increaseQuantity = binding.increaseQuantity;
            lowerQuantity = binding.lowerQuantity;
            deleteItem = binding.imgDeleteItem;
            cardViewProduct = binding.cardViewProduct;


        }
    }



}