package com.comeaqui.eduardorodriguez.comeaqui.notification;

import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
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

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class NotificationsFragment extends Fragment {

    ArrayList<NotificationObject> data;
    static MyNotificationsRecyclerViewAdapter notificationAdapter;

    RecyclerView recyclerView;
    SwipeController swipeController = null;
    FrameLayout waitFrame;
    WebSocketClient mWebSocketClient;
    LinearLayout noNotifications;

    static NotificationsFragment f;


    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public NotificationsFragment() {
    }

    public void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                NotificationObject oo = new NotificationObject(jo);
                data.add(oo);
            }
            if (data.size() > 0){
                noNotifications.setVisibility(View.GONE);
            } else {
                noNotifications.setVisibility(View.VISIBLE);
            }
            notificationAdapter = new MyNotificationsRecyclerViewAdapter(getContext(), data);
            recyclerView.setAdapter(notificationAdapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        f = this;
        recyclerView = view.findViewById(R.id.recycler);
        waitFrame = view.findViewById(R.id.wait_frame);
        noNotifications = view.findViewById(R.id.no_notifications);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.wait_frame, WaitFragment.newInstance())
                .commit();

        getData();
        setupRecyclerView();
        start();
        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setAdapter(notificationAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(notificationAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                //confirmOrder(notificationAdapter.mValues.get(position), false, getContext());
            }

            @Override
            public void onLeftClicked(int position) {
                //confirmOrder(notificationAdapter.mValues.get(position), true, getContext());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    void getData(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_notifications/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        GetAsyncTask(String uri){
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
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
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
            URI uri = new URI(getActivity().getResources().getString(R.string.async_server) + "/ws/notifications/" + USER.id +  "/");
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
                        NotificationObject notificationObject = new NotificationObject(new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("notification_added").getAsJsonObject());
                        data.add(0, notificationObject);
                        notificationAdapter.notifyDataSetChanged();
                        noNotifications.setVisibility(View.GONE);
                    });
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "No connection", Toast.LENGTH_LONG).show();
                    });
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
        mWebSocketClient.close();
    }
    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}