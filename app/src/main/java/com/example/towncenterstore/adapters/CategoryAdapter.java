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
import com.example.towncenterstore.databinding.CategoryItemBinding;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.pojo.category.Category;
import com.example.towncenterstore.general.CornersTransformation;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    List<Category> categories = new ArrayList<>();
    Context context;
    OnClickItemCategoryOrProduct clickItemCategoryOrProduct;


    public CategoryAdapter( Context context, OnClickItemCategoryOrProduct clickItemCategoryOrProduct) {
        this.context = context;
        this.clickItemCategoryOrProduct = clickItemCategoryOrProduct;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public OnClickItemCategoryOrProduct getClickItemCategoryOrProduct() {
        return clickItemCategoryOrProduct;
    }

    public void setClickItemCategoryOrProduct(OnClickItemCategoryOrProduct clickItemCategoryOrProduct) {
        this.clickItemCategoryOrProduct = clickItemCategoryOrProduct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(categories.get(position).getName());
        Glide.with(context).load(categories.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 60)))
                .into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickItemCategoryOrProduct.onClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public ViewHolder(CategoryItemBinding binding) {
            super(binding.getRoot());
            image = binding.imgCategoryPhoto;
            name = binding.tvCategoryName;
        }
    }
}


