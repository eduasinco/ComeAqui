package com.example.eduardorodriguez.comeaqui.eat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.FoodElementFragment;
import com.example.eduardorodriguez.comeaqui.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;

public class MapCardFragment extends Fragment {

    TextView posterNameView;
    TextView posterUserName;

    ImageView posterImageView;
    ImageView starView;

    FoodPost foodPost;
    boolean favourite;

    public MapCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_card, container, false);

        posterNameView = view.findViewById(R.id.poster_name);
        posterUserName = view.findViewById(R.id.poster_username);
        starView = view.findViewById(R.id.star);


        posterImageView = view.findViewById(R.id.poster_image);

        foodPost = (FoodPost) getArguments().getSerializable("object");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", foodPost);
        FoodElementFragment fragment = new FoodElementFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container3, fragment)
                .commit();

        posterNameView.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
        posterUserName.setText(foodPost.owner.email);
        starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
        EatFragment.markerPutColor(EatFragment.markers.get(foodPost.id), !foodPost.favourite ? R.color.grey : R.color.favourite);

        if(!foodPost.owner.profile_photo.contains("no-image")) Glide.with(view.getContext()).load("http://127.0.0.1:8000/media/" + foodPost.owner.profile_photo).into(posterImageView);

        setFavourite();
        return view;
    }

    void setFavourite(){
        starView.setOnClickListener(v -> {
            foodPost.favourite = !foodPost.favourite;
            starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
            if (foodPost.favourite) {
                EatFragment.markerPutColor(EatFragment.markers.get(foodPost.id), R.color.favourite);
                PostAsyncTask createOrder = new PostAsyncTask("http://127.0.0.1:8000/favourites/");
                createOrder.execute(
                        new String[]{"food_post_id", "" + foodPost.id}
                );
            } else {
                EatFragment.markerPutColor(EatFragment.markers.get(foodPost.id), !foodPost.favourite ? R.color.grey : R.color.favourite);
            }
        });
    }

}
