package com.example.eduardorodriguez.comeaqui.chat.conversation;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.MessageObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.MessageFirebaseObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AdapterMensajes extends RecyclerView.Adapter<AdapterMensajes.ViewHolder>{

    private List<MessageFirebaseObject> listMensaje = new ArrayList<>();
    private Context c;

    public AdapterMensajes(Context c) {
        this.c = c;
    }

    public void addMensaje(MessageFirebaseObject m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }


    @Override
    public AdapterMensajes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_mensajes, parent, false);
        return new AdapterMensajes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = listMensaje.get(position);
        holder.messageView.setText(holder.mItem.message);

        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child("user_image/" + holder.mItem.sender);
        firebaseStorage.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.mView.getContext()).load(uri.toString()).into(holder.chattererImage);
        }).addOnFailureListener(exception -> {});
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
        public MessageFirebaseObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            username = view.findViewById(R.id.nombreMensaje);
            messageView = view.findViewById(R.id.mensajeMensaje);
            dateView = view.findViewById(R.id.horaMensaje);
            chattererImage = view.findViewById(R.id.fotoPerfilMensaje);
        }
    }
}