package com.comeaqui.eduardorodriguez.comeaqui.order;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;

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

import static com.comeaqui.eduardorodriguez.comeaqui.App.USER;
import static com.comeaqui.eduardorodriguez.comeaqui.R.layout.fragment_pendingorders_list;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GuestingFragment extends Fragment {

    RecyclerView recyclerView;

    SwipeRefreshLayout pullToRefresh;
    LinkedHashMap<Integer, OrderObject> data = new LinkedHashMap<>();
    MyGuestingRecyclerViewAdapter orderAdapter;
    WebSocketClient mWebSocketClient;
    LinearLayout noNotifications;
    ProgressBar loadingProgress;
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
            orderAdapter.addData(new ArrayList<>(data.values()));
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
        noNotifications = view.findViewById(R.id.no_orders);
        loadingProgress = view.findViewById(R.id.loading_progress);

        orderAdapter = new MyGuestingRecyclerViewAdapter(new ArrayList<>(data.values()), mListener);
        recyclerView.setAdapter(orderAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreData();
                }
            }
        });
        start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataAndSet();
    }

    int page = 1;
    void getDataAndSet(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        page = 1;
        data = new LinkedHashMap<>();
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_guesting/" + page + "/").execute());
    }
    void loadMoreData(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/my_guesting/" + page + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
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
            if (response != null) {
                makeList(new JsonParser().parse(response).getAsJsonArray());
                page++;
            }
            loadingProgress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }

    }


    private void start(){
        try {
            URI uri = new URI(getActivity().getResources().getString(R.string.async_server) + "/ws/orders/" + USER.id +  "/");
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
