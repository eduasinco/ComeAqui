package com.example.eduardorodriguez.comeaqui.chat;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.ChatFirebaseObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.firebase.database.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends Fragment{

    LinkedHashMap<Integer, ChatObject> data;
    MyChatRecyclerViewAdapter adapter;

    RecyclerView recyclerView;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatFragment newInstance(int columnCount) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        getChatsAndSet();
        start();
        return view;
    }


    public void makeList(JsonArray jsonArray){
        try {
            data = new LinkedHashMap<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                ChatObject chat = new ChatObject(jo);
                chat.unread_count = getChatUnreadCount(chat.id);
                data.put(chat.id, chat);
            }
            adapter = new MyChatRecyclerViewAdapter(new ArrayList<>(data.values()), mListener);
            recyclerView.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void getMyChatsFromFirebase(){
        DatabaseReference userChats = FirebaseDatabase.getInstance().getReference("userChats");
        userChats
            .child(MainActivity.firebaseUser.id)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        DatabaseReference chats = FirebaseDatabase.getInstance().getReference("chats");
                        chats.child(postSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ChatFirebaseObject chat = dataSnapshot.getValue(ChatFirebaseObject.class);
                                if (chat != null) {
                                    chat.id = postSnapshot.getValue().toString();
                                    //adapter.addChatObject(chat);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

    }


    void getChatsAndSet(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_chats/");
        try {
            String response = process.execute().get();
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    int getChatUnreadCount(int chatId){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/chat_unread_count/" + chatId + "/");
        try {
            String response = process.execute().get();
            if (response != null)
                return new JsonParser().parse(response).getAsJsonObject().get("count").getAsInt();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void start(){
        try {
            URI uri = new URI(getActivity().getResources().getString(R.string.server) + "/ws/unread_messages/" + USER.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
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
                        int count = jo.get("chat_unread_messages").getAsInt();
                        data.get(chatObject.id).unread_count = count;
                        data.get(chatObject.id).last_message = chatObject.last_message;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ChatObject item);
    }
}
