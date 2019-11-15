package com.example.eduardorodriguez.comeaqui.order;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
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
import static com.example.eduardorodriguez.comeaqui.R.layout.fragment_pendingorders_list;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PendingOrdersFragment extends Fragment {

    RecyclerView recyclerView;

    SwipeRefreshLayout pullToRefresh;
    LinkedHashMap<Integer, OrderObject> data;
    MyPendingOrdersRecyclerViewAdapter orderAdapter;
    WebSocketClient mWebSocketClient;

    FrameLayout waitFrame;
    View view;

    boolean pending;
    private static final String PENDING = "pending";
    private OnListFragmentInteractionListener mListener;

    public PendingOrdersFragment() {}

    public static PendingOrdersFragment newInstance(boolean pending) {
        PendingOrdersFragment fragment = new PendingOrdersFragment();
        Bundle args = new Bundle();
        args.putBoolean(PENDING, pending);
        fragment.setArguments(args);
        return fragment;
    }

    public void makeList(JsonArray jsonArray){
        try {
            data = new LinkedHashMap<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                OrderObject oo = new OrderObject(jo);
                data.put(oo.id, oo);
            }
            orderAdapter = new MyPendingOrdersRecyclerViewAdapter(new ArrayList<>(data.values()), mListener);
            recyclerView.setAdapter(orderAdapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            pending = getArguments().getBoolean(PENDING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(fragment_pendingorders_list, container, false);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recycler);
        waitFrame = view.findViewById(R.id.wait_frame);

        getDataAndSet();
        if(pending)
            start();
        return view;
    }

    void getDataAndSet(){
        try {
            startWaitingFrame(true);
            new GetAsyncTask(getContext(),"GET", getResources().getString(R.string.server) + (pending ? "/my_pending_orders/" : "/my_past_orders/")){
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
                        OrderObject orderChanged = new OrderObject(new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("order_changed").getAsJsonObject());
                        data.get(orderChanged.id).seen = orderChanged.seen;
                        data.get(orderChanged.id).status = orderChanged.status;
                        orderAdapter.notifyDataSetChanged();
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
        mWebSocketClient.close();
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(OrderObject order);
    }
}
