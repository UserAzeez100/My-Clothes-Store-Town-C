package com.example.towncenterstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.NotificationItemBinding;
import com.example.towncenterstore.pojo.notification.Notification;
import com.example.towncenterstore.general.CornersTransformation;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notificationList;
    private Context context;

    public NotificationAdapter( Context context) {
        this.context = context;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationItemBinding binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String title = notificationList.get(position).getTitle();

        if(!notificationList.get(position).getImage().equals("")) {
            Glide.with(context).load(notificationList.get(position).getImage())
                    .apply(RequestOptions.bitmapTransform(new CornersTransformation(context, 30)))
                    .into(holder.photoNotification);
        }else {
            if (title.equals("في الطريق")){
                Glide.with(context).load(getContext().getDrawable(R.drawable.on_way_img))
                        .into(holder.photoNotification);
            } else if (title.equals("قيد الانتظار")) {
                Glide.with(context).load(getContext().getDrawable(R.drawable.pending_img))
                        .into(holder.photoNotification);
            }else{
                Glide.with(context).load(getContext().getDrawable(R.drawable.complete_img))
                        .into(holder.photoNotification);
            }

        }
        holder.titleNotification.setText(notificationList.get(position).getTitle());
        holder.descriptionNotification.setText(notificationList.get(position).getDescription());
        holder.dateNotification.setText(notificationList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public Context getContext() {
        return context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleNotification, descriptionNotification, dateNotification;
        ImageView photoNotification;
        CardView cardView;


        public ViewHolder(NotificationItemBinding binding) {
            super(binding.getRoot());

            photoNotification = binding.imgNotificationPhoto;
            titleNotification = binding.tvNotificationTitle;
            descriptionNotification = binding.tvNotificationDescription;
            dateNotification = binding.tvNotificationDate;
            cardView = binding.cardViewId;

        }

    }


}
