package com.example.towncenterstore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.towncenterstore.databinding.ItemSizeProductBinding;
import com.example.towncenterstore.databinding.ItemStyleBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.R;
import com.example.towncenterstore.listeners.RecyclerViewOnItemClick;

import java.util.ArrayList;
import java.util.List;

public class SizeProductAdapter extends RecyclerView.Adapter<SizeProductAdapter.ViewHolder> {

    private List<String> sizeProductArrayList;
    private Context context;
    OnClick onClick;


    int selectedPosition = -1;

    public SizeProductAdapter(List<String> sizeProductArrayList, Context context, OnClick onClick) {
        this.sizeProductArrayList = sizeProductArrayList;
        this.context = context;
        this.onClick = onClick;
        notifyDataSetChanged();
    }

    public List<String> getSizeProductArrayList() {
        return sizeProductArrayList;
    }

    public void setSizeProductArrayList(List<String> sizeProductArrayList) {
        this.sizeProductArrayList = sizeProductArrayList;
    }

    public Context getContext() {
        return context;
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
    public SizeProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSizeProductBinding binding = ItemSizeProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull SizeProductAdapter.ViewHolder holder, int position) {

        if (selectedPosition == -1){
            Log.e("selectedPosition == position","selectedPosition == positio-1");
            holder.sizeProduct.setBackground(getContext().getDrawable(R.drawable.item_size_shape));
//            holder.sizeProduct.setTextColor(getContext().getColor(R.color.red));
        }else {
            if (selectedPosition == position){
                Log.e("selectedPosition == position","selectedPosition == position if");
                holder.sizeProduct.setBackground(getContext().getDrawable(R.drawable.item_size_fill_shape));
//                holder.sizeProduct.setTextColor(getContext().getColor(R.color.white));
            }else {
                Log.e("selectedPosition == position","selectedPosition == position else");
                holder.sizeProduct.setBackground(getContext().getDrawable(R.drawable.item_size_shape));
                holder.sizeProduct.setTextColor(getContext().getColor(R.color.o_base_color_app));
            }
        }
        holder.sizeProduct.setText(sizeProductArrayList.get(position).toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick(sizeProductArrayList.get(position).toString());
                holder.sizeProduct.setBackground(getContext().getDrawable(R.drawable.item_size_fill_shape));
                holder.sizeProduct.setTextColor(getContext().getColor(R.color.white));
                if (selectedPosition != position){
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return sizeProductArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView sizeProduct;

        public ViewHolder(ItemSizeProductBinding binding) {
            super(binding.getRoot());
            sizeProduct = binding.tvProductSize;
        }
    }

    public String getSelected(){
        if (selectedPosition != -1){
            return sizeProductArrayList.get(selectedPosition);
        }
        return null;
    }
}
