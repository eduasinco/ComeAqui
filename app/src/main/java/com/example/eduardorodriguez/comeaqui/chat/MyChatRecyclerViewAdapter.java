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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedHashMap;

public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> {

    private LinkedHashMap<String, ChatFirebaseObject> mValues;
    private Object[] mValuesValues;
    private final OnListFragmentInteractionListener mListener;
    StorageReference firebaseStorage;

    public MyChatRecyclerViewAdapter(LinkedHashMap<String, ChatFirebaseObject> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void addChatObject(ChatFirebaseObject chat) {
        if (mValues == null)
            this.mValues = new LinkedHashMap<>();
        this.mValues.put(chat.id, chat);
        mValuesValues = mValues.values().toArray();
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat, parent, false);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = (ChatFirebaseObject) mValuesValues[position];

        FirebaseUser chattingWith = MainActivity.firebaseUser.id.equals(holder.mItem.user1.id) ? holder.mItem.user2 : holder.mItem.user1;

        holder.username.setText(chattingWith.username);
        holder.lastMessage.setText(holder.mItem.last_message);
        holder.dateView.setText("00/00/0000");
        holder.mView.setOnClickListener(v -> {
            Intent conversation = new Intent(holder.mView.getContext(), ConversationActivity.class);
            conversation.putExtra("chat", holder.mItem);
            holder.mView.getContext().startActivity(conversation);
        });

        firebaseStorage.child("user_image/" + chattingWith.id).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.mView.getContext()).load(uri.toString()).into(holder.chattererImage);
        }).addOnFailureListener(exception -> {});
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
