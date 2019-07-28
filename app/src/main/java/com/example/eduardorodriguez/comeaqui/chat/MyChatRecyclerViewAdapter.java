package com.example.eduardorodriguez.comeaqui.chat;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.DateFragment;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.ChatFragment.OnListFragmentInteractionListener;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.profile.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedHashMap;

public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private LinkedHashMap<Integer, ChatObject> mValues;
    private Object[] mValuesValues;
    private final OnListFragmentInteractionListener mListener;
    StorageReference firebaseStorage;

    public MyChatRecyclerViewAdapter(LinkedHashMap<Integer, ChatObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void addChatObject(ChatObject chat) {
        if (mValues == null)
            this.mValues = new LinkedHashMap<>();
        this.mValues.put(chat.id, chat);
        mValuesValues = mValues.values().toArray();
        this.notifyDataSetChanged();
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
        holder.mItem = (ChatObject) mValuesValues[position];

        User chattingWith = MainActivity.user.id == (holder.mItem.users.get(0).id) ? holder.mItem.users.get(1) : holder.mItem.users.get(0);

        holder.username.setText(chattingWith.first_name + ", " + chattingWith.last_name);
        MessageObject lastMessage = holder.mItem.messages.get(holder.mItem.messages.size() - 1);
        holder.lastMessage.setText(lastMessage.message);
        holder.mView.setOnClickListener(v -> {
            Intent conversation = new Intent(holder.mView.getContext(), ConversationActivity.class);
            conversation.putExtra("chat", holder.mItem);
            holder.mView.getContext().startActivity(conversation);
        });
        Glide.with(holder.mView.getContext()).load(chattingWith.profile_photo).into(holder.chattererImage);

        ((AppCompatActivity)holder.mView.getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.date, DateFragment.newInstance(holder.mItem.createdAt))
                .commit();
//        firebaseStorage.child("user_image/" + chattingWith.id).getDownloadUrl().addOnSuccessListener(uri -> {
//            Glide.with(holder.mView.getContext()).load(uri.toString()).into(holder.chattererImage);
//        }).addOnFailureListener(exception -> {});
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView username;
        public final TextView lastMessage;
        public final ImageView chattererImage;
        public ChatObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            username = view.findViewById(R.id.username);
            lastMessage = view.findViewById(R.id.last_message);
            chattererImage = view.findViewById(R.id.receiver_image);
        }
    }
}
