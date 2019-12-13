package com.example.eduardorodriguez.comeaqui.chat;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.wait_frame, WaitFragment.newInstance())
                .commit();
        start();

        View backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> finish());
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
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_chats/").execute());
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
                if (response != null)
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
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/unread_messages/" + USER.id +  "/");
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


    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
