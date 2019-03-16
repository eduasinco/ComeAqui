package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.gson.*;

import java.util.ArrayList;

import static com.example.eduardorodriguez.comeaqui.R.layout.fragment_get_food;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetFoodFragment extends Fragment {

    SwipeRefreshLayout pullToRefresh;

    public static ArrayList<String[]> data;
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
                data.add(createStringArray(jo));
            }
            fa.addNewRow(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void appendToList(String jsonString){
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
        JsonObject jo = jsonArray.get(0).getAsJsonObject();
        data.add(0, createStringArray(jo));
        fa.addNewRow(data);
    }

    public static String[] createStringArray(JsonObject jo){
        String plate_name = jo.get("plate_name").getAsString();
        String price = jo.get("price").getAsString();
        String type = jo.get("food_type").getAsString();
        String description = jo.get("description").getAsString();
        String food_photo = jo.get("food_photo").getAsString();
        String[] add = new String[]{plate_name, price, type, description, food_photo};
        return add;
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

        GetFoodAsyncTask process = new GetFoodAsyncTask( 1);
        process.execute();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetFoodAsyncTask process = new GetFoodAsyncTask( 1);
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
