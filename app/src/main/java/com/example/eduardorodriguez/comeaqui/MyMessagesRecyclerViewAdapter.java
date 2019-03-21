package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MessagesFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMessagesRecyclerViewAdapter extends RecyclerView.Adapter<MyMessagesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String[]> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMessagesRecyclerViewAdapter(ArrayList<String[]> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void updateData(ArrayList<String[]> data){
        this.mValues = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_messages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int pos = position;

        holder.fullNameView.setText(mValues.get(position)[0] + " " + mValues.get(position)[1]);
        holder.messageView.setText(mValues.get(position)[0] + " wants to try what you prepared!");
        holder.senderEmailView.setText(mValues.get(position)[2]);
        holder.creationDateView.setText(mValues.get(position)[4].substring(0, 10) + " " + mValues.get(position)[4].substring(11, 16));

        Glide.with(holder.mView.getContext()).load("http://127.0.0.1:8000/media/" + mValues.get(position)[3]).into(holder.senderImageView);
        holder.messageWholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderLook = new Intent(holder.mView.getContext(), MessageLookActivity.class);
                orderLook.putExtra("firstName", mValues.get(pos)[0]);
                orderLook.putExtra("lastName", mValues.get(pos)[1]);
                orderLook.putExtra("senderEmail", mValues.get(pos)[2]);
                orderLook.putExtra("senderImage", mValues.get(pos)[3]);
                orderLook.putExtra("creationDate", mValues.get(pos)[4]);
                orderLook.putExtra("id", mValues.get(pos)[5]);
                orderLook.putExtra("postPlateName", mValues.get(pos)[6]);
                orderLook.putExtra("postFoodPhoto", mValues.get(pos)[7]);
                orderLook.putExtra("postPrice", mValues.get(pos)[8]);
                orderLook.putExtra("postDescription", mValues.get(pos)[9]);
                orderLook.putExtra("post", mValues.get(pos)[10]);
                orderLook.putExtra("poster", mValues.get(pos)[11]);
                holder.mView.getContext().startActivity(orderLook);
            }
        });
    }

    @Override
    public int getItemCount() {

        return (mValues != null) ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ConstraintLayout messageWholeView;
        public final TextView fullNameView;
        public final TextView messageView;
        public final TextView creationDateView;
        public final TextView senderEmailView;
        public final ImageView senderImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            messageWholeView =  view.findViewById(R.id.message);
            fullNameView =  view.findViewById(R.id.fullName);
            messageView =  view.findViewById(R.id.messageText);
            creationDateView =  view.findViewById(R.id.creationDate);
            senderEmailView =  view.findViewById(R.id.senderEmail);
            senderImageView =  view.findViewById(R.id.senderImage);

        }

        @Override
        public String toString() {
            return "";
        }
    }
}
