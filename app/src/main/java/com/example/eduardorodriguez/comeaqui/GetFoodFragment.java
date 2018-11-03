package com.example.eduardorodriguez.comeaqui;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetFoodFragment extends Fragment {

    public GetFoodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[][] data = {
                {Integer.toString(R.drawable.hamburger), "Lugar de la Comida", "Precio - 10.20$", "Descripcion de la Comida"},
                {Integer.toString(R.drawable.hamburger2), "Lugar de la Comida", "Precio - 6.80$", "Descripcion de la Comida"},
                {Integer.toString(R.drawable.hamburger3), "Lugar de la Comida", "Precio - 5.95$", "Descripcion de la Comida"},
                {Integer.toString(R.drawable.hamburger4), "Lugar de la Comida", "Precio - 3.25$", "Descripcion de la Comida"},
                {Integer.toString(R.drawable.hamburger5), "Lugar de la Comida", "Precio - 4.50$", "Descripcion de la Comida"}
        };


        View view = inflater.inflate(R.layout.fragment_get_food, container, false);
        ListView list;
        list = (ListView) view.findViewById(R.id.getfoodlist);
        list.setAdapter(new GetFoodAdapter(getActivity(), data));

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addFood = new Intent(getActivity(), AddFoodActivity.class);
                getActivity().startActivity(addFood);
            }
        });

        return view;
    }

}
