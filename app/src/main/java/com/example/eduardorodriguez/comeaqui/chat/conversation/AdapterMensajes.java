package com.example.eduardorodriguez.comeaqui.chat.conversation;

import android.content.Context;import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.DateBetweenMessages;
import com.example.eduardorodriguez.comeaqui.chat.MessageObject;
import com.example.eduardorodriguez.comeaqui.utilities.DateFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        holder.username.setText(holder.mItem.sender.username);
        holder.dateView.setText(DateFragment.getDateInFormat(holder.mItem.createdAt));

        if (holder.mItem instanceof DateBetweenMessages){
            holder.messageCard.setVisibility(View.GONE);
            TextView tv = new TextView(holder.mView.getContext());
            tv.setText(holder.mItem.message);
            tv.setGravity(Gravity.CENTER);
            // tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            return;
        }

        if (position < listMensaje.size() - 1) {
            MessageObject messageAfter = listMensaje.get(position + 1);
            if (MainActivity.user.id == (holder.mItem.sender.id)){
                holder.wholeMessage.setGravity(Gravity.RIGHT);
                holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message_send));
                if (!(holder.mItem.sender.id == messageAfter.sender.id)){
                    holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message_final_right));
                    holder.wholeMessage.setPadding(0, 0, 0, 50);
                }
            } else {
                holder.wholeMessage.setGravity(Gravity.LEFT);
                holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message));
                if (!(holder.mItem.sender.id == messageAfter.sender.id)){
                    holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message_final_left));
                    holder.wholeMessage.setPadding(0, 0, 0, 50);
                }
            }
        } else {
            if (MainActivity.user.id == (holder.mItem.sender.id)){
                holder.wholeMessage.setGravity(Gravity.RIGHT);
                holder.messageCard.setBackground(ContextCompat.getDrawable(holder.mView.getContext(), R.drawable.box_message_final_right));
            } else {
                holder.wholeMessage.setGravity(Gravity.LEFT);
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
        public final TextView username;
        public final TextView messageView;
        public final TextView dateView;
        public final ImageView chattererImage;
        public MessageObject mItem;
        public LinearLayout wholeMessage;
        public LinearLayout messageCard;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            wholeMessage = view.findViewById(R.id.view);
            username = view.findViewById(R.id.nombreMensaje);
            messageView = view.findViewById(R.id.mensajeMensaje);
            dateView = view.findViewById(R.id.horaMensaje);
            chattererImage = view.findViewById(R.id.fotoPerfilMensaje);
            messageCard = view.findViewById(R.id.message_card);
        }
    }
}