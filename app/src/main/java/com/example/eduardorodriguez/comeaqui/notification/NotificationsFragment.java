package com.example.eduardorodriguez.comeaqui.notification;

import android.graphics.Canvas;
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
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class NotificationsFragment extends Fragment {

    LinkedHashMap<Integer, NotificationObject> data;
    static MyNotificationsRecyclerViewAdapter notificationAdapter;

    RecyclerView recyclerView;
    SwipeController swipeController = null;
    FrameLayout waitFrame;

    static NotificationsFragment f;


    public NotificationsFragment() {
    }

    public void makeList(JsonArray jsonArray){
        try {
            data = new LinkedHashMap<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                NotificationObject oo = new NotificationObject(jo);
                data.put(oo.id, oo);
            }
            notificationAdapter = new MyNotificationsRecyclerViewAdapter(getContext(), new ArrayList<>(data.values()));
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

        getFragmentManager().beginTransaction()
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
        try {
            startWaitingFrame(true);
            new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_notifications/"){
                @Override
                protected void onPostExecute(String s) {
                    if (s != null)
                        makeList(new JsonParser().parse(s).getAsJsonArray());
                    startWaitingFrame(false);
                    super.onPostExecute(s);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(getContext(), "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(getContext(), "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitFrame.setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction()
                    .replace(R.id.wait_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitFrame.setVisibility(View.GONE);
        }
    }

    private void start(){
        try {
            URI uri = new URI(getActivity().getResources().getString(R.string.server) + "/ws/orders/" + USER.id +  "/");
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
//                        OrderObject orderChanged = new OrderObject(new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("order_changed").getAsJsonObject());
//                        data.get(orderChanged.id).seenPoster = orderChanged.seenPoster;
//                        data.get(orderChanged.id).status = orderChanged.status;
//                        notificationAdapter.notifyDataSetChanged();
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
}
