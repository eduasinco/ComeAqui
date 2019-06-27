package com.example.eduardorodriguez.comeaqui.eat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.FoodElementFragment;
import com.example.eduardorodriguez.comeaqui.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;

public class MapCardFragment extends Fragment {

    TextView posterNameView;
    TextView posterUserName;

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


        posterImageView = view.findViewById(R.id.poster_image);

        FoodPost foodPost = (FoodPost) getArguments().getSerializable("object");

        Bundle bundle = new Bundle();
        bundle.putSerializable("object", foodPost);
        FoodElementFragment fragment = new FoodElementFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container3, fragment)
                .commit();

        posterNameView.setText(foodPost.owner_first_name + " " + foodPost.owner_last_name);
        posterUserName.setText(foodPost.owner_username);


        if(!foodPost.owner_photo.contains("no-image")) Glide.with(view.getContext()).load("http://127.0.0.1:8000/media/" + foodPost.owner_photo).into(posterImageView);

        return view;
    }
}
