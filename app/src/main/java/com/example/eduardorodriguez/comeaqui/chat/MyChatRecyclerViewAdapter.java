package com.example.eduardorodriguez.comeaqui.chat;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    ArrayList<ChatObject> mValues;
    StorageReference firebaseStorage;

    public MyChatRecyclerViewAdapter(ArrayList<ChatObject> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_element, parent, false);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        User chattingWith = USER.id == (holder.mItem.users.get(0).id) ? holder.mItem.users.get(1) : holder.mItem.users.get(0);

        holder.username.setText(chattingWith.first_name + " " + chattingWith.last_name);
        if (holder.mItem.last_message != null){
            holder.lastMessage.setText(holder.mItem.last_message.message);
        }
        holder.notChat.setVisibility(View.INVISIBLE);

        holder.mView.setOnClickListener(v -> {
            Intent conversation = new Intent(holder.mView.getContext(), ConversationActivity.class);
            conversation.putExtra("chatId", holder.mItem.id + "");
            holder.mView.getContext().startActivity(conversation);
        });

        if (holder.mItem.userUnseenCount.getOrDefault(USER.id, 0) > 0 ){
            holder.notChat.setVisibility(View.VISIBLE);
            holder.notChat.setText("" + holder.mItem.userUnseenCount.get(USER.id));
        }
        if (!chattingWith.profile_photo.contains("no-image"))
            Glide.with(holder.mView.getContext()).load(chattingWith.profile_photo).into(holder.chattererImage);

        holder.dateView.setText(DateFormatting.hYesterdayWeekDay(holder.mItem.last_message.createdAt));
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView username;
        public final TextView lastMessage;
        public final TextView notChat;
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
            notChat = view.findViewById(R.id.notChat);
        }
    }
}
