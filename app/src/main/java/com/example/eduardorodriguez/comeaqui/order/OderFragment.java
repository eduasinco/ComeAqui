package com.example.eduardorodriguez.comeaqui.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.eduardorodriguez.comeaqui.OrderObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.AddFoodActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.R.layout.fragment_order;

/**
 * A simple {@link Fragment} subclass.
 */
public class OderFragment extends Fragment {

    SwipeRefreshLayout pullToRefresh;

    public static ArrayList<OrderObject> data;
    static OrderAdapter fa;
    static View view;

    public OderFragment() {
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

    public static void appendToList(JsonObject jo){
        if (data == null){
            data = new ArrayList<>();
        }
        data.add(0, new OrderObject(jo));
        fa.addNewRow(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        view = inflater.inflate(fragment_order, container, false);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        fa = new OrderAdapter(getActivity(), data);

        ListView listView = view.findViewById(R.id.getfoodlist);
        listView.setAdapter(fa);

        getDataAndSet();
        pullToRefresh.setOnRefreshListener(() -> {
            getDataAndSet();
            pullToRefresh.setRefreshing(false);
        });

        FloatingActionButton myFab =  view.findViewById(R.id.fab);
        myFab.setOnClickListener(v -> {
            Intent addFood = new Intent(getActivity(), AddFoodActivity.class);
            getActivity().startActivity(addFood);
        });

        return view;
    }

    void getDataAndSet(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_orders/");
        try {
            String response = process.execute().get();
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
