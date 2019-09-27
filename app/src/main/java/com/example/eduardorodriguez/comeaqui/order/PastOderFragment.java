package com.example.eduardorodriguez.comeaqui.order;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.R.layout.fragment_order_pending_past;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastOderFragment extends Fragment {

    SwipeRefreshLayout pullToRefresh;

    public static ArrayList<OrderObject> data;
    static OrderAdapter fa;
    static View view;

    boolean pending;

    public PastOderFragment() {
        // Required empty public constructor
    }

    public static void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new OrderObject(jo));
            }
            fa.addNewRow(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        view = inflater.inflate(fragment_order_pending_past, container, false);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        fa = new OrderAdapter(getActivity(), data);
        pending = getArguments().getBoolean("pending");

        ListView listView = view.findViewById(R.id.getfoodlist);
        listView.setAdapter(fa);

        getDataAndSet();
        pullToRefresh.setOnRefreshListener(() -> {
            getDataAndSet();
            pullToRefresh.setRefreshing(false);
        });

        start();
        return view;
    }

    void getDataAndSet(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + (pending ? "/my_pending_orders/" : "/my_past_orders/"));
        try {
            String response = process.execute().get();
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start(){
        try {
            URI uri = new URI(getActivity().getResources().getString(R.string.server) + "/ws/orders_data_changed/" + MainActivity.user.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Connection Established!", Toast.LENGTH_LONG).show();
                    });
                }
                @Override
                public void onMessage(String s) {
                    getActivity().runOnUiThread(() -> {
                        OrderObject orderChanged = new OrderObject(new JsonParser().parse(s).getAsJsonObject().get("order_changed").getAsJsonObject());
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
