package com.example.towncenterstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.DiscountItemBinding;
import com.example.towncenterstore.databinding.NotificationItemBinding;
import com.example.towncenterstore.general.CornersTransformation;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.listeners.RecyclerViewOnItemClick;
import com.example.towncenterstore.pojo.discounts.DiscountProduct;

import java.util.ArrayList;
import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    private List<DiscountProduct> discountProductList;
    Context context;
    GoToProductDetails goToProductDetails;

    public DiscountAdapter(Context context, GoToProductDetails goToProductDetails) {
        this.context = context;
        this.goToProductDetails = goToProductDetails;
    }

    public List<DiscountProduct> getDiscountProductList() {
        return discountProductList;
    }

    public void setDiscountProductList(List<DiscountProduct> discountProductList) {
        this.discountProductList = discountProductList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public GoToProductDetails getGoToProductDetails() {
        return goToProductDetails;
    }

    public void setGoToProductDetails(GoToProductDetails goToProductDetails) {
        this.goToProductDetails = goToProductDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DiscountItemBinding binding = DiscountItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(discountProductList.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 30)))
                .into(holder.discountImage);
        holder.discountName.setText(discountProductList.get(position).getName());
        holder.discountBefore.setText(String.valueOf(discountProductList.get(position).getOriginalPrice()));
        holder.discountAfter.setText(discountProductList.get(position).getPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProductDetails.setData(position, discountProductList.get(position).getId());

            }
        });

    }

    @Override
    public int getItemCount() {
        return discountProductList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView discountImage;
        TextView discountName;
        TextView discountBefore;
        TextView discountAfter;


        public ViewHolder(DiscountItemBinding binding) {
            super(binding.getRoot());

            discountImage = itemView.findViewById(R.id.imgProductPhoto);
            discountName = itemView.findViewById(R.id.tvOfferName);
            discountBefore = itemView.findViewById(R.id.tvBeforePriceValue);
            discountAfter = itemView.findViewById(R.id.priceAfterValue);


        }
    }
}
