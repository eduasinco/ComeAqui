package com.example.eduardorodriguez.comeaqui.chat;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.ChatFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.ChatFirebaseObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.FirebaseUser;
import com.example.eduardorodriguez.comeaqui.profile.User;

import java.util.ArrayList;
import java.util.List;

public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private List<ChatFirebaseObject> mValues;
    private final OnListFragmentInteractionListener mListener;
    private FirebaseUser userToTalkTo;

    public MyChatRecyclerViewAdapter(List<ChatFirebaseObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void addData(ArrayList<ChatFirebaseObject> data){
        this.mValues = data;
        this.notifyDataSetChanged();
    }

    public void addChatObject(ChatFirebaseObject chat) {
        if (mValues == null)
            this.mValues = new ArrayList<>();
        this.mValues.add(chat);
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
        for (FirebaseUser user: holder.mItem.users.values()){
            if (!user.email.equals(MainActivity.user.email)){
                userToTalkTo = user;
                        holder.username.setText(user.email);
                Glide.with(holder.mView.getContext()).load(user.profile_photo).into(holder.chattererImage);
            }
        }
        holder.lastMessage.setText(holder.mItem.last_message);
        holder.dateView.setText("00/00/0000");
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                Intent conversation = new Intent(holder.mView.getContext(), ConversationActivity.class);
                conversation.putExtra("chat", holder.mItem);
                conversation.putExtra("chatting_with", userToTalkTo);
                holder.mView.getContext().startActivity(conversation);
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
        public ChatFirebaseObject mItem;

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