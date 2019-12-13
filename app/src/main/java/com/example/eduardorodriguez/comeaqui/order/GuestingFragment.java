package com.example.eduardorodriguez.comeaqui.order;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;

import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
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
import java.util.LinkedHashMap;

import static com.example.eduardorodriguez.comeaqui.App.USER;
import static com.example.eduardorodriguez.comeaqui.R.layout.fragment_pendingorders_list;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GuestingFragment extends Fragment {

    RecyclerView recyclerView;

    SwipeRefreshLayout pullToRefresh;
    LinkedHashMap<Integer, OrderObject> data;
    MyGuestingRecyclerViewAdapter orderAdapter;
    WebSocketClient mWebSocketClient;
    LinearLayout noNotifications;

    FrameLayout waitFrame;
    View view;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    private OnListFragmentInteractionListener mListener;

    public GuestingFragment() {}

    public static GuestingFragment newInstance() {
        GuestingFragment fragment = new GuestingFragment();
        Bundle args = new Bundle();
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
            if (data.size() > 0){
                noNotifications.setVisibility(View.GONE);
            } else {
                noNotifications.setVisibility(View.VISIBLE);
            }
            orderAdapter = new MyGuestingRecyclerViewAdapter(new ArrayList<>(data.values()), mListener);
            recyclerView.setAdapter(orderAdapter);
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
        super.onCreate(savedInstanceState);

        view = inflater.inflate(fragment_pendingorders_list, container, false);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recycler);
        waitFrame = view.findViewById(R.id.wait_frame);
        noNotifications = view.findViewById(R.id.no_orders);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.wait_frame, WaitFragment.newInstance())
                .commit();

        start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataAndSet();
    }

    void getDataAndSet(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_guesting/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
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
                        noNotifications.setVisibility(View.GONE);
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
        if (null != mWebSocketClient)
            mWebSocketClient.close();
    }
    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(OrderObject order);
    }
}
