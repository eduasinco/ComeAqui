package com.example.eduardorodriguez.comeaqui.chat.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.MessageObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PutAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.DateFragment;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.hdodenhof.circleimageview.CircleImageView;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class ConversationActivity extends AppCompatActivity {

    ChatObject chat;
    User chattingWith;

    MessageObject lastMessage = null;
    String lastMessageDate = "";


    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private ImageView btnEnviar;
    private View backView;
    private AdapterMensajes adapter;
    WebSocketClient mWebSocketClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        fotoPerfil = findViewById(R.id.dinner_image);
        nombre = findViewById(R.id.nombre);
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        backView = findViewById(R.id.back_arrow);

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
            chattingWith = USER.id == (chat.users.get(0).id) ? chat.users.get(1) : chat.users.get(0);

            nombre.setText(chattingWith.first_name + " " + chattingWith.last_name);
            Glide.with(this).load(chattingWith.profile_photo).into(fotoPerfil);
            fotoPerfil.setOnClickListener(v -> goToProfileView(chattingWith));

            getChatMessages();
        }

        btnEnviar.setScaleX(0);
        btnEnviar.setVisibility(View.GONE);
        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtMensaje.getText().toString().trim().length() > 0){
                    btnEnviar.setVisibility(View.VISIBLE);
                    btnEnviar.animate().scaleX(1).setDuration(200);
                } else {
                    btnEnviar.animate().scaleX(0).setDuration(200).withEndAction(() -> btnEnviar.setVisibility(View.GONE));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rvMensajes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    hideKeyboard();
                }
            }
        });

        backView.setOnClickListener(v -> finish());
        start();
        btnEnviar.setOnClickListener(view -> {
            mWebSocketClient.send("{ \"message\": \"" + txtMensaje.getText().toString() + "\"," +
                    "\"command\": \"new_message\"," +
                    "\"from\": \"" + USER.id + "\"," +
                    "\"to\": \"" + chattingWith.id + "\"," +
                    "\"chatId\": \"" + chat.id + "\"}"
            );
            txtMensaje.setText("");
        });

    }

    void goToProfileView(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("user", user);
        startActivity(k);
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getChatMessages(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/chat_detail/" + chat.id + "/");
        try {
            String response = process.execute().get();
            if (response != null) {
                for (JsonElement je: new JsonParser().parse(response).getAsJsonObject().get("message_set").getAsJsonArray()){
                    MessageObject currentMessage = new MessageObject(je.getAsJsonObject());

                    setMessageStatus(currentMessage);
                    adapter.addMensaje(currentMessage);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setMessageStatus(MessageObject currentMessage){

        String currentMessageDate = DateFragment.getDateInSimpleFormat(currentMessage.createdAt);
        if (!lastMessageDate.equals(currentMessageDate)){
            currentMessage.newDay = true;
        }
        lastMessageDate = currentMessageDate;

        if (lastMessage == null){
            currentMessage.topSpace = true;
        } else if (currentMessage.sender.id != lastMessage.sender.id){
            currentMessage.topSpace = true;
            lastMessage.lastInGroup = true;
        }

        if (USER.id == currentMessage.sender.id){
            currentMessage.isOwner = true;
        }

        lastMessage = currentMessage;
    }


    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
    }


    public void start(){
        try {
            String url = getResources().getString(R.string.server) + "/ws/chat/" + chat.id + "/";
            URI uri = new URI(url);
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Connection Established!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    runOnUiThread(() -> {
                        MessageObject brandNewMessage = new MessageObject(new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("message").getAsJsonObject());
                        setMessageStatus(brandNewMessage);
                        adapter.addMensaje(brandNewMessage);
                        if (brandNewMessage.sender.id != USER.id){
                            setMessagAsSeen(brandNewMessage.id);
                        }
                    });
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }
                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setMessagAsSeen(int messageId){
        PutAsyncTask resetPassword = new PutAsyncTask(getResources().getString(R.string.server) + "/mark_message_as_seen/" + messageId + "/");
        resetPassword.execute();
    }
}
