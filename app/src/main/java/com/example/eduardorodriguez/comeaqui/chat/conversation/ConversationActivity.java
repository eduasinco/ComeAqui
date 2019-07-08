package com.example.eduardorodriguez.comeaqui.chat.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.MessageObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.ChatFirebaseObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.FirebaseUser;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.MessageFirebaseObject;
import com.example.eduardorodriguez.comeaqui.profile.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.firebase.database.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.concurrent.ExecutionException;

public class ConversationActivity extends AppCompatActivity {

    ChatFirebaseObject chat;
    FirebaseUser chattingWith;

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private ImageButton btnEnviarFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        fotoPerfil = findViewById(R.id.fotoPerfil);
        nombre = findViewById(R.id.nombre);
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviarFoto = findViewById(R.id.btnEnviarFoto);

        adapter = new AdapterMensajes(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) {
            chat = (ChatFirebaseObject) b.get("chat");
            chattingWith = (FirebaseUser) b.get("chatting_with");

            Glide.with(this).load(chattingWith.profile_photo).into(fotoPerfil);
            nombre.setText(chattingWith.first_name + " " + chattingWith.last_name);
            getChatFirebaseMessages(chat.id);
        }

        btnEnviar.setOnClickListener(view -> {
            PostAsyncTask emitMessage = new PostAsyncTask(getResources().getString(R.string.server) + "/create_message/");
            emitMessage.execute(
                    new String[]{"message", txtMensaje.getText().toString()},
                    new String[]{"chat_id", chat + ""}
            );

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");
            MessageFirebaseObject message = new MessageFirebaseObject();
            message.message = txtMensaje.getText().toString();
            message.chat = chat.id;
            message.sender = "user1";
            DatabaseReference newRef = reference.child("messages");
            newRef.setValue(message);
            txtMensaje.setText("");
        });

    }

    private void getChatMessages(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/chat_detail/" + chat + "/");
        try {
            String response = process.execute().get();
            if (response != null)
                for (JsonElement je: new JsonParser().parse(response).getAsJsonObject().get("message_set").getAsJsonArray()){
                    // adapter.addMensaje(new MessageObject(je.getAsJsonObject()));
                }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getChatFirebaseMessages(String chat){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");
        reference
                .orderByChild("chat")
                .equalTo(chat)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        MessageFirebaseObject message = dataSnapshot.getValue(MessageFirebaseObject.class);
                        adapter.addMensaje(message);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
    }
}
