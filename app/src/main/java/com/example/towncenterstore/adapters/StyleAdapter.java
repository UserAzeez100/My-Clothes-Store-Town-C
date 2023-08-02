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
import com.example.towncenterstore.databinding.ItemStyleBinding;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.pojo.product.style_product.Style;
import com.example.towncenterstore.general.CornersTransformation;

import java.util.List;

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.ViewHolder> {
    private List<Style> styleArrayList;
    private Context context;
    private OnClickItemCategoryOrProduct clickItemCategoryOrProduct;


    public StyleAdapter(Context context, OnClickItemCategoryOrProduct clickItemCategoryOrProduct) {
        this.context = context;
        this.clickItemCategoryOrProduct = clickItemCategoryOrProduct;
    }

    public List<Style> getStyleArrayList() {
        return styleArrayList;
    }

    public void setStyleArrayList(List<Style> styleArrayList) {
        this.styleArrayList = styleArrayList;
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
        ItemStyleBinding binding = ItemStyleBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        Glide.with(context).load(styleArrayList.get(position).getImage())
                .apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 50)))
                .into(holder.styleImage);
        holder.styleName.setText(styleArrayList.get(position).getName());
        holder.styleDescription.setText(styleArrayList.get(position).getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickItemCategoryOrProduct.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return styleArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView styleImage;
        TextView styleName;
        TextView styleDescription;


        public ViewHolder(ItemStyleBinding binding) {
            super(binding.getRoot());

            styleImage = binding.imgStylePhoto;
            styleName = binding.imgStyleName;
            styleDescription = binding.tvStyleDescription;

        }
    }
}
