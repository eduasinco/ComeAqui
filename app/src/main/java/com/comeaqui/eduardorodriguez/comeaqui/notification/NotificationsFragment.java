package com.comeaqui.eduardorodriguez.comeaqui.notification;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.MainActivity;
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
import java.util.LinkedHashMap;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.comeaqui.eduardorodriguez.comeaqui.App.MAX_CONNECTIONS_TRIES;
import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;

public class NotificationsFragment extends Fragment {

    ArrayList<NotificationObject> data = new ArrayList<>();
    static MyNotificationsRecyclerViewAdapter notificationAdapter;

    RecyclerView recyclerView;
    SwipeController swipeController = null;
    WebSocketClient mWebSocketClient;
    LinearLayout noNotifications;
    ProgressBar loadingProgress;

    static NotificationsFragment f;


    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public NotificationsFragment() {
    }

    public void makeList(JsonArray jsonArray){
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
        notificationAdapter.addData(data);
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
        noNotifications = view.findViewById(R.id.no_notifications);
        loadingProgress = view.findViewById(R.id.loading_progress);

        notificationAdapter = new MyNotificationsRecyclerViewAdapter(getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(notificationAdapter);

        getData();
        setupRecyclerView();
        start();
        return view;
    }

    private void setupRecyclerView() {
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isRecyclerScrollable(RecyclerView recyclerView) {
                return recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth() || recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRecyclerScrollable(recyclerView)){
                    if (!recyclerView.canScrollVertically(1)) {
                        loadMoreData();
                    }
                }
            }
        });
    }

    int page = 1;
    void getData(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        page = 1;
        data = new ArrayList<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_notifications/" + page + "/").execute());
    }
    void loadMoreData(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_notifications/" + page + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            loadingProgress.setVisibility(View.VISIBLE);
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
                makeList(new JsonParser().parse(response).getAsJsonArray());
                page++;
            }
            loadingProgress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }

    }

    int tries;
    Handler handler = new Handler();
    ArrayList<Toast> toasts = new ArrayList<>();
    private void start(){
        try {
            if (null != handler){
                handler.removeCallbacksAndMessages(null);
            }
            if (null != mWebSocketClient){
                mWebSocketClient.close();
            }

            tries++;
            Toast t = Toast.makeText(getContext(), "Connecting...", Toast.LENGTH_SHORT);
            t.show();
            toasts.add(t);
            URI uri = new URI(getResources().getString(R.string.async_server) + "/ws/notifications/" + USER.id +  "/");
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    for (Toast t: toasts){ t.cancel();}
                    tries = 0;
                    // getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show());
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
                    if (null != handler && tries < MAX_CONNECTIONS_TRIES) {
                        handler.postDelayed(() -> start(), 1000);
                    }
                    // getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Closed", Toast.LENGTH_SHORT).show());
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
        if (null != mWebSocketClient) {
            mWebSocketClient.close();
        }
        if (null != handler){
            handler.removeCallbacksAndMessages(null);
            handler = null;
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
