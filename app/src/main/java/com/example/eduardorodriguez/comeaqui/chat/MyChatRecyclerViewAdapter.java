package com.example.eduardorodriguez.comeaqui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import com.example.eduardorodriguez.comeaqui.chat.ChatFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.profile.User;

import java.util.ArrayList;
import java.util.List;

public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private List<ChatObject> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyChatRecyclerViewAdapter(List<ChatObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void addData(ArrayList<ChatObject> data){
        this.mValues = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        for (User user: holder.mItem.users){
            if (user.id != MainActivity.user.id){
                holder.username.setText(user.email);
            }
        }
        holder.lastMessage.setText(holder.mItem.lastMessage.message);
        holder.dateView.setText(holder.mItem.lastMessage.createdAt);
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView username;
        public final TextView lastMessage;
        public final TextView dateView;
        public final ImageView chattererImage;
        public ChatObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            username = view.findViewById(R.id.username);
            lastMessage = view.findViewById(R.id.last_message);
            dateView = view.findViewById(R.id.date);
            chattererImage = view.findViewById(R.id.receiver_image);
        }
    }
}
