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
import com.example.towncenterstore.databinding.ProductItemInSearchBinding;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.pojo.search.ProductSearch;
import com.example.towncenterstore.general.CornersTransformation;

import java.util.List;

public class ProductsSearchAdapter extends RecyclerView.Adapter<ProductsSearchAdapter.ViewHolder> {
    List<ProductSearch> products;
    Context context;
    GoToProductDetails goToProductDetails;

    public ProductsSearchAdapter(Context context, GoToProductDetails goToProductDetails) {
        this.context = context;
        this.goToProductDetails = goToProductDetails;
    }

    public List<ProductSearch> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSearch> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductsSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductItemInSearchBinding binding = ProductItemInSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductsSearchAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsSearchAdapter.ViewHolder holder, int position) {
        holder.name.setText(products.get(position).getName());
        Glide.with(context).load(products.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 60)))
                .into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProductDetails.setData(position, products.get(position).getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public ViewHolder(ProductItemInSearchBinding binding) {
            super(binding.getRoot());
            image = binding.imgCategoryPhoto;
            name = binding.tvCategoryName;
        }
    }
}
