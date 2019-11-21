package com.example.eduardorodriguez.comeaqui.chat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.ChatFirebaseObject;

import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.ErrorMessageFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.firebase.database.*;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class ChatFragment extends Fragment{

    ArrayList<ChatObject> data;
    MyChatRecyclerViewAdapter adapter;
    HashMap<Integer, ChatObject> chatObjectHashMap;
    WebSocketClient mWebSocketClient;

    RecyclerView recyclerView;
    FrameLayout waitFrame;
    LinearLayout noMessages;

    View view;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    private OnListFragmentInteractionListener mListener;

    public ChatFragment() {}
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getChatsAndSet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_chat);
        waitFrame = view.findViewById(R.id.wait_frame);
        noMessages = view.findViewById(R.id.no_messages);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.wait_frame, WaitFragment.newInstance())
                .commit();
        start();
        return view;
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
            adapter = new MyChatRecyclerViewAdapter(data, mListener);
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
                return ServerAPI.get(getContext(), this.uri);
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
            URI uri = new URI(getActivity().getResources().getString(R.string.server) + "/ws/unread_messages/" + USER.id +  "/");
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // getActivity().runOnUiThread(() -> {
                    //    Toast.makeText(getActivity(), "Connection Established!", Toast.LENGTH_LONG).show();
                    // });
                }
                @Override
                public void onMessage(String s) {
                    getActivity().runOnUiThread(() -> {
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ChatObject item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mWebSocketClient.close();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
