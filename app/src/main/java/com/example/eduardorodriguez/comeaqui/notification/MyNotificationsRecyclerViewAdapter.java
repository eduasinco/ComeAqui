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
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.utilities.DateFragment;
import com.example.eduardorodriguez.comeaqui.R;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationsRecyclerViewAdapter.ViewHolder> {

    public  List<OrderObject> mValues;
    Context context;

    public MyNotificationsRecyclerViewAdapter(Context context, ArrayList<OrderObject> data){
        this.context = context;
        this.mValues = data;
    }

    public void addNewRow(ArrayList<OrderObject> data){
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
        holder.usernameView.setText(holder.mItem.owner.email);
        holder.notificationView.setText(holder.mItem.owner.first_name + " " + holder.mItem.owner.last_name + " quiere probar tu plato!");
        holder.status.setText(holder.mItem.status);
        holder.date.setText(DateFragment.getDateInSimpleFormat(holder.mItem.createdAt));
        Glide.with(holder.mView.getContext()).load(holder.mItem.owner.profile_photo).into(holder.senderImageView);

        if (!holder.mItem.seenPoster){
            holder.status.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_notification_status_changed));
            holder.status.setTypeface(null, Typeface.BOLD);
            holder.status.setTextColor(Color.WHITE);
        } else if (holder.mItem.status.equals("CONFIRMED")){
            holder.status.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.success));
        } else if (holder.mItem.status.equals("CANCELED")){
            holder.status.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.canceled));
        } else {
            holder.status.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorPrimary));
        }


        holder.mView.setOnClickListener(v -> {
            Intent notification = new Intent(context, NotificationLookActivity.class);
            notification.putExtra("object", holder.mItem);
            context.startActivity(notification);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView usernameView;
        public final TextView notificationView;
        public final TextView status;
        public final TextView date;
        public final ImageView senderImageView;
        public OrderObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            usernameView = view.findViewById(R.id.username);
            notificationView = view.findViewById(R.id.notification);
            senderImageView = view.findViewById(R.id.dinner_image);
            status = view.findViewById(R.id.status);
            date = view.findViewById(R.id.date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + usernameView.getText() + "'";
        }
    }
}
