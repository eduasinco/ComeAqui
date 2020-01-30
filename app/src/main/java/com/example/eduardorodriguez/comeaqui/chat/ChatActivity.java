package com.example.eduardorodriguez.comeaqui.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.SearchFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class ChatActivity extends AppCompatActivity{

    ArrayList<ChatObject> data;
    MyChatRecyclerViewAdapter adapter;
    HashMap<Integer, ChatObject> chatObjectHashMap;
    WebSocketClient mWebSocketClient;

    RecyclerView recyclerView;
    FrameLayout waitFrame;
    LinearLayout noMessages;
    EditText searchBox;
    ImageButton deleteText;
    ImageView noListImage;
    TextView noListMessage;

    String query = "query=";
    ArrayList<AsyncTask> tasks = new ArrayList<>();


    @Override
    public void onResume() {
        super.onResume();
        getChatsAndSet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_chat);
        waitFrame = findViewById(R.id.wait_frame);
        noMessages = findViewById(R.id.no_messages);
        searchBox = findViewById(R.id.search_box);
        deleteText = findViewById(R.id.delete_text);
        noListImage = findViewById(R.id.no_list_image);
        noListMessage = findViewById(R.id.no_list_message);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.wait_frame, WaitFragment.newInstance())
                .commit();

        setSearchListener();
        start();

        deleteText.setOnClickListener(v -> searchBox.setText(""));
        View backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> finish());
    }

    static long last_text_edit = 0;
    void setSearchListener(){
        final long delay = 1000;
        final Handler handler = new Handler();
        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                getChatsAndSet();
            }
        };
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){}
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before, int count){
                handler.removeCallbacks(input_finish_checker);
            }
            @Override
            public void afterTextChanged ( final Editable s){
                if (s.length() > 0) {
                    deleteText.setVisibility(View.VISIBLE);
                    noListImage.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.not_found));
                    noListMessage.setText("No messages found");
                } else {
                    noListImage.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.no_messages));
                    noListMessage.setText("No messages");
                    deleteText.setVisibility(View.GONE);
                    hideKeyboard();
                }
                query = "query=" + s.toString();
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    public void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            chatObjectHashMap = new HashMap<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                ChatObject chat = new ChatObject(jo);
                data.add(chat);
                chatObjectHashMap.put(chat.id, chat);
            }
            if (data.size() > 0){
                noMessages.setVisibility(View.GONE);
            } else{
                noMessages.setVisibility(View.VISIBLE);
            }
            adapter = new MyChatRecyclerViewAdapter(data);
            recyclerView.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void getChatsAndSet(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_chats/" + query.toString() + "/").execute());
    }

    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            startWaitingFrame(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplication(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                makeList(new JsonParser().parse(response).getAsJsonArray());
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }

    }


    void startWaitingFrame(boolean start){
        if (start) {
            waitFrame.setVisibility(View.VISIBLE);
        } else {
            waitFrame.setVisibility(View.GONE);
        }
    }

    private void start(){
        try {
            URI uri = new URI(getResources().getString(R.string.async_server) + "/ws/unread_messages/" + USER.id +  "/");
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // getActivity().runOnUiThread(() -> {
                    //    Toast.makeText(getActivity(), "Connection Established!", Toast.LENGTH_LONG).show();
                    // });
                }
                @Override
                public void onMessage(String s) {
                   runOnUiThread(() -> {
                        JsonObject jo = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject();
                        ChatObject chatObject = new ChatObject(jo.get("chat").getAsJsonObject());
                        if (chatObjectHashMap.containsKey(chatObject.id)){
                            int index = data.indexOf(chatObjectHashMap.get(chatObject.id));
                            data.remove(index);
                            data.add(0, chatObject);
                            chatObjectHashMap.put(chatObject.id, chatObject);
                        } else {
                            data.add(0, chatObject);
                            chatObjectHashMap.put(chatObject.id , chatObject);
                        }
                        noMessages.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
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

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
