package com.example.towncenterstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ItemStatusOrderBinding;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.pojo.orders.Order;

import java.util.List;

public class ProductsAdapterOrders extends RecyclerView.Adapter<ProductsAdapterOrders.ViewHolder> {

    Context context;
    List<Order> orderList;
    OnClickItemCategoryOrProduct clickItemCategoryOrProduct;

    public ProductsAdapterOrders(Context context, OnClickItemCategoryOrProduct clickItemCategoryOrProduct) {
        this.context = context;
        this.clickItemCategoryOrProduct = clickItemCategoryOrProduct;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public OnClickItemCategoryOrProduct getClickItemCategoryOrProduct() {
        return clickItemCategoryOrProduct;
    }

    public void setClickItemCategoryOrProduct(OnClickItemCategoryOrProduct clickItemCategoryOrProduct) {
        this.clickItemCategoryOrProduct = clickItemCategoryOrProduct;
    }

    @NonNull
    @Override
    public ProductsAdapterOrders.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStatusOrderBinding binding = ItemStatusOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductsAdapterOrders.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapterOrders.ViewHolder holder, int position) {
        holder.totalPrice.setText(orderList.get(position).getTotalPrice()+"₪");
        holder.date.setText(orderList.get(position).getUpdatedAt());
        holder.id.setText("Order " + orderList.get(position).getId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickItemCategoryOrProduct.onClick(position);
            }
        });
        String status = orderList.get(position).getStatus();
        switch (status) {
            case "pending":
                holder.status.setBackground(getContext().getResources().getDrawable(R.drawable.shape_btn_pending_fill_orders));
                holder.status.setText(getContext().getText(R.string.pending));

                break;
            case "on_way":
                holder.status.setBackground(getContext().getResources().getDrawable(R.drawable.shape_btn_on_way_fill_orders));
                holder.status.setText(getContext().getText(R.string.on_the_way));

                break;
            case "complete":
                holder.status.setBackground(getContext().getResources().getDrawable(R.drawable.shape_btn_deleivered_fill_orders));
                holder.status.setText(getContext().getText(R.string.completed));
                break;

        }

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView status, date, id, totalPrice;

        public ViewHolder(ItemStatusOrderBinding binding) {
            super(binding.getRoot());
            id = binding.tvOrderId;
            totalPrice = binding.tvTotalPrice;
            date = binding.tvDateOrder;
            status = binding.tvStatusOrder;
        }
    }
}
