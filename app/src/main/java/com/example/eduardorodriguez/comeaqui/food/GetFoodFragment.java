package com.example.eduardorodriguez.comeaqui.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.R.layout.fragment_get_food;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetFoodFragment extends Fragment {

    SwipeRefreshLayout pullToRefresh;

    public static ArrayList<FoodPost> data;
    static GetFoodAdapter fa;
    static View view;

    public GetFoodFragment() {
        // Required empty public constructor
    }

    public static void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new FoodPost(jo));
            }
            fa.addNewRow(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void appendToList(JsonObject jo){
        data.add(0, new FoodPost(jo));
        fa.addNewRow(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        view = inflater.inflate(fragment_get_food, container, false);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        fa = new GetFoodAdapter(getActivity(), data);

        ListView listView = view.findViewById(R.id.getfoodlist);
        listView.setAdapter(fa);

        getDataAndSet();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataAndSet();
                pullToRefresh.setRefreshing(false);
            }
        });

        FloatingActionButton myFab =  view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addFood = new Intent(getActivity(), AddFoodActivity.class);
                getActivity().startActivity(addFood);
            }
        });

        return view;
    }

    void getDataAndSet(){
        GetAsyncTask process = new GetAsyncTask("foods/");
        try {
            makeList(process.execute().get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
