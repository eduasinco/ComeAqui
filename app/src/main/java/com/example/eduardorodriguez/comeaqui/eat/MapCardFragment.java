package com.example.eduardorodriguez.comeaqui.eat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;

public class MapCardFragment extends Fragment {

    TextView posterNameView;
    TextView posterDescriptionView;
    TextView posterUserName;
    TextView postNameView;
    TextView postTime;
    TextView postPrice;

    ImageView postImageView;
    ImageView posterImageView;

    public MapCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_card, container, false);

        posterNameView = view.findViewById(R.id.poster_name);
        posterUserName = view.findViewById(R.id.poster_username);
        postNameView = view.findViewById(R.id.plate_name);
        posterDescriptionView = view.findViewById(R.id.description);
        postTime = view.findViewById(R.id.time);
        postPrice = view.findViewById(R.id.price);

        postImageView = view.findViewById(R.id.post_iamge);
        posterImageView = view.findViewById(R.id.poster_image);

        FoodPost foodPost = (FoodPost) getArguments().getSerializable("object");

        posterNameView.setText(foodPost.owner_first_name + " " + foodPost.owner_last_name);
        posterUserName.setText(foodPost.owner_username);
        postNameView.setText(foodPost.plate_name);
        postPrice.setText(foodPost.price + "€");
        postTime.setText(foodPost.time);
        posterDescriptionView.setText(foodPost.description);
        return view;
    }
}