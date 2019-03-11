package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.gson.*;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetFoodFragment extends Fragment {

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
            JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();
            JsonArray paymentsArray = rootObj.getAsJsonArray("data");
            for (JsonElement pa : paymentsArray) {
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
        JsonObject rootObj = parser.parse(jsonString).getAsJsonObject();
        JsonObject jo = rootObj.getAsJsonObject("data");
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

        view = inflater.inflate(R.layout.fragment_get_food, container, false);
        ListView list;
        list =  view.findViewById(R.id.getfoodlist);
        fa = new GetFoodAdapter(getActivity(), data);
        list.setAdapter(fa);

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
