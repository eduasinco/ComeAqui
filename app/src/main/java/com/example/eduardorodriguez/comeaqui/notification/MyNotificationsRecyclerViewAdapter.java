package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.review.food_review_look.FoodPostReviewLookActivity;
import com.example.eduardorodriguez.comeaqui.utilities.DateFragment;
import com.example.eduardorodriguez.comeaqui.R;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationsRecyclerViewAdapter.ViewHolder> {

    public  List<NotificationObject> mValues;
    Context context;

    public MyNotificationsRecyclerViewAdapter(Context context, ArrayList<NotificationObject> data){
        this.context = context;
        this.mValues = data;
    }

    public void addNewRow(ArrayList<NotificationObject> data){
        this.mValues = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_notifications_element, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.usernameView.setText(holder.mItem.from_user.username);
        holder.notificationView.setText(holder.mItem.title);
        holder.date.setText(DateFragment.getDateInSimpleFormat(holder.mItem.createdAt));
        if (!holder.mItem.from_user.profile_photo.contains("no-image"))
            Glide.with(holder.mView.getContext()).load(holder.mItem.from_user.profile_photo).into(holder.senderImageView);

        switch (holder.mItem.type){
            case "ORDER":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.orderfill));
                holder.mView.setOnClickListener(v -> {
                    Intent notification = new Intent(context, NotificationLookActivity.class);
                    notification.putExtra("orderId", holder.mItem.type_id);
                    context.startActivity(notification);
                });
                break;
            case "REVIEW":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.profilefill));
                holder.mView.setOnClickListener(v -> {
                    Intent notification = new Intent(context, FoodPostReviewLookActivity.class);
                    notification.putExtra("foodPostId", holder.mItem.type_id);
                    context.startActivity(notification);
                });
                break;
            case "REVIEW_REPLY":
                holder.typeImage.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.profilefill));
                holder.mView.setOnClickListener(v -> {
                    Intent notification = new Intent(context, FoodPostReviewLookActivity.class);
                    notification.putExtra("foodPostId", holder.mItem.type_id);
                    context.startActivity(notification);
                });
                break;
            case "INFO":
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView usernameView;
        public final TextView notificationView;
        public final ImageView typeImage;
        public final TextView date;
        public final ImageView senderImageView;
        public NotificationObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            usernameView = view.findViewById(R.id.username);
            notificationView = view.findViewById(R.id.notification);
            senderImageView = view.findViewById(R.id.dinner_image);
            typeImage = view.findViewById(R.id.type_image);
            date = view.findViewById(R.id.date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + usernameView.getText() + "'";
        }
    }
}
