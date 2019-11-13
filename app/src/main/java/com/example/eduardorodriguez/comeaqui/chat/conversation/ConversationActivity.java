package com.example.eduardorodriguez.comeaqui.chat.conversation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.MessageObject;
import com.example.eduardorodriguez.comeaqui.login_and_register.LoginOrRegisterActivity;
import com.example.eduardorodriguez.comeaqui.login_and_register.register.RegisterActivity;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PutAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.DateFormatting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
        backView = findViewById(R.id.back);

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
        SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);

        if (pref.getBoolean("signed_in", false)) {
            USER = new User(new JsonParser().parse(pref.getString("user", "")).getAsJsonArray().get(0).getAsJsonObject());
        } else {
            Intent a = new Intent(this, LoginOrRegisterActivity.class);
            startActivity(a);
        }

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) {
            String chatId = b.getString("chatId");
            getChatMessages(chatId);
            start(chatId);
        }

        btnEnviar.setScaleX(0);
        btnEnviar.setVisibility(View.GONE);
        txtMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
        btnEnviar.setOnClickListener(view -> {
            sendMessage(validJsonString(txtMensaje.getText().toString()));
        });

    }
    private void sendMessage(String message){
        try{
            mWebSocketClient.send("{ \"message\": \"" + message + "\"," +
                    "\"command\": \"new_message\"," +
                    "\"from\": \"" + USER.id + "\"," +
                    "\"to\": \"" + chattingWith.id + "\"," +
                    "\"chatId\": \"" + chat.id + "\"}"
            );
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "The message was not able to be delivered", Toast.LENGTH_LONG).show();
        }
        txtMensaje.setText("");
    }


    private void setChat(){
        chattingWith = USER.id == (chat.users.get(0).id) ? chat.users.get(1) : chat.users.get(0);
        nombre.setText(chattingWith.first_name + " " + chattingWith.last_name);

        if (!chattingWith.profile_photo.contains("no-image"))
            Glide.with(this).load(chattingWith.profile_photo).into(fotoPerfil);
        fotoPerfil.setOnClickListener(v -> goToProfileView(chattingWith));
    }

    String validJsonString(String str){
        String regex = "\n";
        return str.replaceAll(regex, "\\\\n");
    }

    void goToProfileView(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getChatMessages(String chatId){
        try {
            new GetAsyncTask(this,"GET", getResources().getString(R.string.server) + "/chat_detail/" + chatId + "/"){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null) {
                        JsonObject chatJson = new JsonParser().parse(response).getAsJsonObject();
                        for (JsonElement je: chatJson.get("message_set").getAsJsonArray()){
                            MessageObject currentMessage = new MessageObject(je.getAsJsonObject());

                            setMessageStatus(currentMessage);
                            adapter.addMensaje(currentMessage);
                        }
                        chat = new ChatObject(chatJson);
                        setChat();
                    }
                    super.onPostExecute(response);
                }
            }.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setMessageStatus(MessageObject currentMessage){

        String currentMessageDate = DateFormatting.todayYesterdayWeekDay(currentMessage.createdAt);
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


    public void start(String chatId){
        try {
            String url = getResources().getString(R.string.server) + "/ws/chat/" + chatId + "/";
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
        PutAsyncTask resetPassword = new PutAsyncTask(this,getResources().getString(R.string.server) + "/mark_message_as_seen/" + messageId + "/");
        resetPassword.execute();
    }
}
