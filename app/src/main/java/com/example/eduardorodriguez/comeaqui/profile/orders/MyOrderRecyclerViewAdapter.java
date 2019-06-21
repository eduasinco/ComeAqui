package com.example.eduardorodriguez.comeaqui.profile.orders;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyOrderRecyclerViewAdapter extends RecyclerView.Adapter<MyOrderRecyclerViewAdapter.ViewHolder> {

    private ArrayList<OrderObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyOrderRecyclerViewAdapter(ArrayList<OrderObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void updateData(ArrayList<OrderObject> data){
        this.mValues = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OrderObject orderObject = mValues.get(position);

        holder.posterEmailView.setText(mValues.get(position).posterEmail);
        String status = mValues.get(position).orderStatus;
        switch (status){
            case "PENDING":
                holder.messageView.setText(status);
                holder.messageView.setTextColor(Color.parseColor("#FFC60000"));
                break;
            case "CONFIRMED":
                holder.messageView.setText(status);
                holder.messageView.setTextColor(Color.parseColor("#FF1EB600"));
                break;
            case "DELIVERED":
                holder.messageView.setText(status);
                holder.messageView.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.colorPrimary));
                break;

        }
        holder.foodNameView.setText(mValues.get(position).postPlateName);
        Glide.with(holder.mView.getContext()).load("http://127.0.0.1:8000/media/" + mValues.get(position).posterImage).into(holder.orderImageView);

        holder.listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderLook = new Intent(holder.mView.getContext(), OrderLookActivity.class);
                orderLook.putExtra("object", orderObject);
                holder.mView.getContext().startActivity(orderLook);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ConstraintLayout listItemView;
        public final TextView mIdView;
        public final TextView messageView;
        public final TextView foodNameView;
        public final TextView posterEmailView;
        public final ImageView orderImageView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            listItemView = view.findViewById(R.id.listItem);
            mIdView = view.findViewById(R.id.item_number);
            messageView = view.findViewById(R.id.orderMessage);
            foodNameView = view.findViewById(R.id.plateName);
            posterEmailView = view.findViewById(R.id.priceText);
            orderImageView = view.findViewById(R.id.orderImage);

        }
    }
}
