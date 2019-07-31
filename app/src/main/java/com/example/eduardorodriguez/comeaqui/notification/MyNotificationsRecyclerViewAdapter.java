package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.DateFragment;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationsRecyclerViewAdapter extends RecyclerView.Adapter<MyNotificationsRecyclerViewAdapter.ViewHolder> {

    private  List<NotificationObject> mValues;
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
        holder.usernameView.setText(holder.mItem.sender.email);
        holder.notificationView.setText(holder.mItem.sender.first_name + " " + holder.mItem.sender.last_name + " quiere probar tu plato!");
        Glide.with(holder.mView.getContext()).load(holder.mItem.sender.profile_photo).into(holder.senderImageView);

        if (holder.mItem.order.status.equals("CONFIRMED")){
            holder.mView.setBackgroundColor(Color.parseColor("#FFD0FFD2"));
        } else if (holder.mItem.order.status.equals("CANCELED")) {
            holder.mView.setBackgroundColor(Color.parseColor("#FFD3D2"));
        }

        holder.mView.setOnClickListener(v -> {
            Intent notification = new Intent(context, NotificationLookActivity.class);
            notification.putExtra("object", holder.mItem);
            context.startActivity(notification);
        });

        ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.date, DateFragment.newInstance(holder.mItem.createdAt))
                .commit();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView usernameView;
        public final TextView notificationView;
        public final ImageView senderImageView;
        public NotificationObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            usernameView = view.findViewById(R.id.username);
            notificationView = view.findViewById(R.id.notification);
            senderImageView = view.findViewById(R.id.dinner_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + usernameView.getText() + "'";
        }
    }
}
