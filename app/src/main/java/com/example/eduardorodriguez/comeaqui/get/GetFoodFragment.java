package com.example.eduardorodriguez.comeaqui.get;

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

import static com.example.eduardorodriguez.comeaqui.R.layout.fragment_get_food;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetFoodFragment extends Fragment {

    SwipeRefreshLayout pullToRefresh;

    public static ArrayList<GetFoodObject> data;
    static GetFoodAdapter fa;
    static View view;

    public GetFoodFragment() {
        // Required empty public constructor
    }

    public static void makeList(String jsonString){
        try {
            data = new ArrayList<>();
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new GetFoodObject(jo));
            }
            fa.addNewRow(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void appendToList(JsonObject jo){
        data.add(0, new GetFoodObject(jo));
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

        GetAsyncTask process = new GetAsyncTask( 1, "get_foods/");
        process.execute();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetAsyncTask process = new GetAsyncTask( 1, "get_foods/");
                process.execute();
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

    public static void setList(){

    }
}
