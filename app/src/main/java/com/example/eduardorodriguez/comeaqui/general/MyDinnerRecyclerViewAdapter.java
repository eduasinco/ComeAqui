package com.example.eduardorodriguez.comeaqui.general;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.general.DinnerFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class MyDinnerRecyclerViewAdapter extends RecyclerView.Adapter<MyDinnerRecyclerViewAdapter.ViewHolder> {

    private final List<OrderObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDinnerRecyclerViewAdapter(List<OrderObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dinner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.dinnerName.setText(holder.mItem.owner.first_name + ", " + holder.mItem.owner.last_name);

        if(!holder.mItem.owner.profile_photo.contains("no-image")) {
            Glide.with(holder.mView.getContext()).load(holder.mItem.owner.profile_photo).into(holder.dinnerImage);
        }

        if (holder.mItem.owner.id == USER.id){
            holder.dinnerChat.setVisibility(View.GONE);
        }

        holder.dinnerChat.setOnClickListener(v -> {
            if (null != mListener){
                mListener.onChatInteraction(holder.mItem);
            }
        });

        holder.mView.setOnClickListener(v -> {
            if (null != mListener){
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView dinnerName;
        public final ImageView dinnerChat;
        public final ImageView dinnerImage;
        public OrderObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dinnerName = view.findViewById(R.id.dinner_name);
            dinnerChat = view.findViewById(R.id.dinner_chat);
            dinnerImage = view.findViewById(R.id.dinner_image);
        }
    }
}
