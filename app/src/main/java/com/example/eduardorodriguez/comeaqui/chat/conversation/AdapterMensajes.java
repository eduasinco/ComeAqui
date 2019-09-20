package com.example.eduardorodriguez.comeaqui.chat.conversation;

import android.content.Context;import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.MessageObject;
import com.example.eduardorodriguez.comeaqui.utilities.DateFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdapterMensajes extends RecyclerView.Adapter<AdapterMensajes.ViewHolder>{

    private List<MessageObject> listMensaje = new ArrayList<>();
    private Context c;
    StorageReference firebaseStorage;

    public AdapterMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje(MessageObject m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }


    @Override
    public AdapterMensajes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_mensajes, parent, false);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        return new AdapterMensajes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = listMensaje.get(position);
        holder.messageView.setText(holder.mItem.message);
        holder.dateView.setText(DateFragment.getDateForMessage(holder.mItem.createdAt));
        holder.datePopContainer.setVisibility(View.GONE);

        int paddingSides = holder.wholeMessage.getPaddingLeft();
        int topBottomPadding = holder.wholeMessage.getPaddingTop();
        holder.wholeMessage.setPadding(paddingSides, 0, paddingSides, 0);

        if (holder.mItem.newDay) {
            holder.datePop.setVisibility(View.VISIBLE);
            holder.datePopContainer.setVisibility(View.VISIBLE);
            holder.datePop.setText(DateFragment.getDateInForMessageConversation(holder.mItem.createdAt));
        } else if (holder.mItem.topSpace){
            holder.datePop.setText(DateFragment.getDateInForMessageConversation(holder.mItem.createdAt));
            holder.wholeMessage.setPadding(paddingSides, topBottomPadding * 2, paddingSides, topBottomPadding);
        }

        if (holder.mItem.isOwner){
            holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message_send));
            holder.wholeMessage.setGravity(Gravity.RIGHT);
            if (holder.mItem.lastInGroup){
                holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message_final_right));
            }
        } else {
            holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message));
            holder.wholeMessage.setGravity(Gravity.LEFT);
            if (holder.mItem.lastInGroup){
                holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message_final_left));
            }
        }

        Glide.with(holder.mView.getContext()).load(holder.mItem.sender.profile_photo).into(holder.chattererImage);
    }

    @Override
    public int getItemCount() {
        return listMensaje != null ? listMensaje.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView messageView;
        public final TextView dateView;
        public final TextView datePop;
        public final ImageView chattererImage;
        public MessageObject mItem;
        public LinearLayout wholeMessage;
        public LinearLayout messageCard;
        public ConstraintLayout datePopContainer;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            wholeMessage = view.findViewById(R.id.view);
            messageView = view.findViewById(R.id.mensajeMensaje);
            dateView = view.findViewById(R.id.horaMensaje);
            chattererImage = view.findViewById(R.id.fotoPerfilMensaje);
            messageCard = view.findViewById(R.id.message_card);

            datePop = view.findViewById(R.id.datePop);
            datePopContainer = view.findViewById(R.id.datePopContainer);
        }
    }
}