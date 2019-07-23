package com.example.eduardorodriguez.comeaqui.chat.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import com.example.eduardorodriguez.comeaqui.chat.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.MessageObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.ChatFirebaseObject;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.FirebaseUser;
import com.example.eduardorodriguez.comeaqui.chat.firebase_objects.MessageFirebaseObject;
import com.example.eduardorodriguez.comeaqui.profile.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.*;
import okio.ByteString;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ConversationActivity extends AppCompatActivity {

    ChatObject chat;
    User chattingWith;

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private ImageView btnEnviar;
    private AdapterMensajes adapter;


    private Button start;
    private TextView output;
    private OkHttpClient client;

    WebSocket ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        fotoPerfil = findViewById(R.id.fotoPerfil);
        nombre = findViewById(R.id.nombre);
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

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
            chat = (ChatObject) b.get("chat");
            chattingWith = MainActivity.user.id == (chat.users.get(0).id) ? chat.users.get(1) : chat.users.get(0);

            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child("user_image/" + chattingWith.id);
            firebaseStorage.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this).load(uri.toString()).into(fotoPerfil);
            }).addOnFailureListener(exception -> {});
            nombre.setText(chattingWith.first_name + " " + chattingWith.last_name);
            getChatMessages();
        }

        btnEnviar.setOnClickListener(view -> {
            //createServerMessage();
            ws.send("{ \"message\": \"" + txtMensaje.getText().toString() + "\"," +
                    "\"command\": \"new_message\"," +
                    "\"from\": \"" + MainActivity.user.id + "\"," +
                    "\"chatId\": \"" + chat.id + "\"}"
            );
            txtMensaje.setText("");
        });
        client = new OkHttpClient();
        start();
    }

    private void getChatMessages(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/chat_detail/" + chat.id + "/");
        try {
            String response = process.execute().get();
            if (response != null)
                for (JsonElement je: new JsonParser().parse(response).getAsJsonObject().get("message_set").getAsJsonArray()){
                    adapter.addMensaje(new MessageObject(je.getAsJsonObject()));
                }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createServerMessage(){
        PostAsyncTask emitMessage = new PostAsyncTask(getResources().getString(R.string.server) + "/create_message/");
        emitMessage.execute(
                new String[]{"message", txtMensaje.getText().toString()},
                new String[]{"chat_id", chat.id + ""}
        );
    }

    private void createFirebaseMessage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");
        MessageFirebaseObject message = new MessageFirebaseObject();
        message.message = txtMensaje.getText().toString();
        //message.chat = chat.signature;
        message.sender = MainActivity.firebaseUser;
        DatabaseReference newRef = reference.push();
        newRef.setValue(message);

        DatabaseReference chats = FirebaseDatabase.getInstance().getReference("chats");
        //chats.child(chat.id).child("last_message").setValue(txtMensaje.getText().toString());
    }

    private void getChatFirebaseMessages(String chatSignature){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("messages");
        reference
                .orderByChild("chat")
                .equalTo(chatSignature)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        MessageFirebaseObject message = dataSnapshot.getValue(MessageFirebaseObject.class);
                        // adapter.addMensaje(message);
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
    }


    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        public ConversationActivity activity;
        public EchoWebSocketListener(ConversationActivity activity) {
            this.activity = activity;
        }
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            activity.runOnUiThread(() -> Toast.makeText(activity, "Connection Established!", Toast.LENGTH_LONG).show());
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output(text);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output(bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }

    private void start() {
        Request request = new Request.Builder().url("http://127.0.0.1:8000/ws/chat/example/")
                .build();
        EchoWebSocketListener listener = new EchoWebSocketListener(this);
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void output(final String txt) {
        runOnUiThread(() -> adapter.addMensaje(new MessageObject(new JsonParser().parse(txt).getAsJsonObject().get("message").getAsJsonObject())));
    }
}
