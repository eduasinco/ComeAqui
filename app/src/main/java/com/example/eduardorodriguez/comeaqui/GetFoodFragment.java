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

    public static void makeList(String d){
        try {
            data = new ArrayList<>();
            JsonElement root = new JsonParser().parse(d);
            JsonElement jsonArray = root.getAsJsonObject().getAsJsonArray("data");
            for (JsonElement je: jsonArray.getAsJsonArray()){
                String food_photo = je.getAsJsonObject().get("food_photo").getAsString();
                String plate_name = je.getAsJsonObject().get("plate_name").getAsString();
                String price = je.getAsJsonObject().get("price").getAsString();
                String description = je.getAsJsonObject().get("description").getAsString();
                data.add(new String[]{food_photo, plate_name, price, description});
            }
            fa.addNewRow(data);
        } catch (Exception e){
            System.out.println(data.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_get_food, container, false);
        ListView list;
        list = (ListView) view.findViewById(R.id.getfoodlist);
        fa = new GetFoodAdapter(getActivity(), data);
        list.setAdapter(fa);

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
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
