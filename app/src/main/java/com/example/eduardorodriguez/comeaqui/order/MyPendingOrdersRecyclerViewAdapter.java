package com.example.eduardorodriguez.comeaqui.order;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.PendingOrdersFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.utilities.DateFragment;

import java.util.ArrayList;
import java.util.List;


public class MyPendingOrdersRecyclerViewAdapter extends RecyclerView.Adapter<MyPendingOrdersRecyclerViewAdapter.ViewHolder> {

    private final List<OrderObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyPendingOrdersRecyclerViewAdapter(List<OrderObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.posterName.setText(holder.mItem.owner.first_name + " " + holder.mItem.owner.last_name);
        holder.posterUsername.setText(holder.mItem.owner.email);
        holder.postAddress.setText(holder.mItem.post.address);
        String priceTextE = "€" + holder.mItem.post.price + " - ";
        holder.price.setText(priceTextE);
        holder.orderStatus.setText(holder.mItem.status);
        holder.orderStatus.setBackgroundColor(Color.TRANSPARENT);

        if (!holder.mItem.seen){
            holder.orderStatus.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_notification_status_changed));
            holder.orderStatus.setTypeface(null, Typeface.BOLD);
            holder.orderStatus.setTextColor(Color.WHITE);
        } else if (holder.mItem.status.equals("CONFIRMED")){
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.success));
        } else if (holder.mItem.status.equals("CANCELED")){
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.canceled));
        } else {
            holder.orderStatus.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorPrimary));
        }

        holder.mView.setOnClickListener(v -> {
            Intent orderLook = new Intent(holder.mView.getContext(), OrderLookActivity.class);
            orderLook.putExtra("object", holder.mItem);
            boolean delete = false;
            if (holder.mItem.owner.id == MainActivity.user.id){
                delete = true;
            }
            orderLook.putExtra("delete", delete);
            holder.mView.getContext().startActivity(orderLook);
        });
        holder.dateView.setText(DateFragment.getDateInSimpleFormat(holder.mItem.createdAt));
    }

    @Override
    public int getItemCount() {
        return (mValues != null) ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView posterName;
        public final TextView price;
        public final TextView posterUsername;
        public final TextView postAddress;
        public final TextView orderStatus;
        public final TextView dateView;
        public OrderObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            posterName = view.findViewById(R.id.poster_name);
            price = view.findViewById(R.id.price);
            posterUsername = view.findViewById(R.id.poster_username);
            postAddress = view.findViewById(R.id.address);
            orderStatus = view.findViewById(R.id.order_status);
            dateView = view.findViewById(R.id.date);
        }
    }
}
