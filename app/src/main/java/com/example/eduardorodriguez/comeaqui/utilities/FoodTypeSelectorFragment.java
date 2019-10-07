package com.example.eduardorodriguez.comeaqui.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eduardorodriguez.comeaqui.R;


public class FoodTypeSelectorFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    boolean[] pressed = {false, false, false, false, false, false, false};
    ImageView vegetarian;
    ImageView vegan;
    ImageView cereal;
    ImageView spicy;
    ImageView fish;
    ImageView meat;
    ImageView dairy;


    public FoodTypeSelectorFragment() {}
    public static FoodTypeSelectorFragment newInstance() {
        return new FoodTypeSelectorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_type_selector, container, false);

        vegetarian = view.findViewById(R.id.vegetarian);
        vegan = view.findViewById(R.id.vegan);
        cereal = view.findViewById(R.id.cereal);
        spicy = view.findViewById(R.id.spicy);
        fish =  view.findViewById(R.id.fish);
        meat = view.findViewById(R.id.meat);
        dairy = view.findViewById(R.id.dairy);

        setFoodTypes();
        return view;
    }

    void setFoodTypes(){
        vegetarian.setOnClickListener(v -> {
            mListener.onFragmentInteraction(pressed);
            if(!pressed[4] && !pressed[5]){
                pressed[0] = !pressed[0];
            }
            if(pressed[0])
                vegetarian.setImageResource(R.drawable.vegetarianfill);
            else
                vegetarian.setImageResource(R.drawable.vegetarian);
        });
        vegan.setOnClickListener(v -> {
            mListener.onFragmentInteraction(pressed);
            if(!pressed[4] && !pressed[5] && !pressed[6]){
                pressed[1] = !pressed[1];
            }
            if(pressed[1])
                vegan.setImageResource(R.drawable.veganfill);
            else
                vegan.setImageResource(R.drawable.vegan);
        });
        cereal.setOnClickListener(v -> {
            mListener.onFragmentInteraction(pressed);
            pressed[2] = !pressed[2];
            if(pressed[2])
                cereal.setImageResource(R.drawable.cerealfill);
            else
                cereal.setImageResource(R.drawable.cereal);
        });
        spicy.setOnClickListener(v -> {
            mListener.onFragmentInteraction(pressed);
            pressed[3] = !pressed[3];
            if(pressed[3])
                spicy.setImageResource(R.drawable.spicyfill);
            else
                spicy.setImageResource(R.drawable.spicy);
        });

        fish.setOnClickListener(v -> {
            mListener.onFragmentInteraction(pressed);
            if(!pressed[0] && !pressed[1]){
                pressed[4] = !pressed[4];
            }
            if(pressed[4])
                fish.setImageResource(R.drawable.fishfill);
            else
                fish.setImageResource(R.drawable.fish);
        });
        meat.setOnClickListener(v -> {
            mListener.onFragmentInteraction(pressed);
            if(!pressed[0] && !pressed[1]){
                pressed[5] = !pressed[5];
            }
            if(pressed[5])
                meat.setImageResource(R.drawable.meatfill);
            else
                meat.setImageResource(R.drawable.meat);
        });
        dairy.setOnClickListener(v -> {
            mListener.onFragmentInteraction(pressed);
            if(!pressed[1]){
                pressed[6] = !pressed[6];
            }
            if(pressed[6])
                dairy.setImageResource(R.drawable.dairyfill);
            else
                dairy.setImageResource(R.drawable.dairy);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean[] pressed);
    }
}
