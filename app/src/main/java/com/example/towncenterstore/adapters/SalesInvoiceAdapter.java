package com.example.towncenterstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.towncenterstore.databinding.SalesItemBinding;
import com.example.towncenterstore.pojo.orders.show_order.ShowOrder;

import java.util.List;


public class SalesInvoiceAdapter extends RecyclerView.Adapter<SalesInvoiceAdapter.ViewHolder> {

    List<ShowOrder> showOrderList;
    Context context;

    public SalesInvoiceAdapter(List<ShowOrder> showOrderList, Context context) {
        this.showOrderList = showOrderList;
        this.context = context;
    }

    public List<ShowOrder> getShowOrderList() {
        return showOrderList;
    }

    public void setShowOrderList(List<ShowOrder> showOrderList) {
        this.showOrderList = showOrderList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SalesItemBinding binding=SalesItemBinding.inflate(LayoutInflater.from(context)
                ,parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.productName.setText(showOrderList.get(position).getProductName());
        holder.productQuantity.setText(showOrderList.get(position).getQuantity()+"");
        holder.productTotalPrice.setText(showOrderList.get(position).getTotal() + "₪");
    }

    @Override
    public int getItemCount() {
        return showOrderList.size();
    }

    public Context getContext() {
        return context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView productName, productQuantity,productTotalPrice;



        public ViewHolder(@NonNull SalesItemBinding binding) {
            super(binding.getRoot());

            productName = binding.tvProductName;
            productQuantity = binding.tvProductQuantity;
            productTotalPrice = binding.tvProductPrice;


        }

    }


}
