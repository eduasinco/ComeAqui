package com.example.eduardorodriguez.comeaqui.map;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.utilities.FoodElementFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;

import java.util.concurrent.ExecutionException;

public class MapCardFragment extends Fragment {

    TextView posterNameView;
    TextView posterUserName;

    ImageView posterImageView;
    ImageView starView;
    ImageView postImageView;

    CardView imageLayout;

    FoodPost foodPost;
    int favouriteId;
    boolean favourite;

    public MapCardFragment() {
        // Required empty public constructor
    }

    public static MapCardFragment newInstance(FoodPost foodPost) {
        MapCardFragment fragment = new MapCardFragment();
        Bundle args = new Bundle();
        args.putSerializable("object", foodPost);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_card, container, false);

        posterNameView = view.findViewById(R.id.poster_name);
        posterUserName = view.findViewById(R.id.poster_username);
        starView = view.findViewById(R.id.star);

        posterImageView = view.findViewById(R.id.poster_image);
        postImageView = view.findViewById(R.id.post_image);
        imageLayout = view.findViewById(R.id.image_layout);

        foodPost = (FoodPost) getArguments().getSerializable("object");

        getChildFragmentManager().beginTransaction()
                .replace(R.id.container2, FoodElementFragment.newInstance(foodPost))
                .commit();

        favouriteId = foodPost.favouriteId;

        posterNameView.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
        posterUserName.setText(foodPost.owner.email);
        starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
        MapFragment.markerPutColor(MapFragment.markers.get(foodPost.id), !foodPost.favourite ? R.color.grey : R.color.favourite);

        if(!foodPost.owner.profile_photo.contains("no-image")) Glide.with(view.getContext()).load(foodPost.owner.profile_photo).into(posterImageView);
        if(!foodPost.food_photo.contains("no-image")){
            imageLayout.setVisibility(View.VISIBLE);
            Glide.with(view.getContext()).load(foodPost.food_photo).into(postImageView);
        }

        posterImageView.setOnClickListener(v -> goToProfileView());
        setFavourite();
        return view;
    }

    void goToProfileView(){
        Intent k = new Intent(getContext(), ProfileViewActivity.class);
        k.putExtra("user_email", foodPost.owner);
        startActivity(k);
    }


    void setFavourite(){
        starView.setOnClickListener(v -> {
            foodPost.favourite = !foodPost.favourite;
            starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
            if (foodPost.favourite) {
                MapFragment.markerPutColor(MapFragment.markers.get(foodPost.id), R.color.favourite);
                PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/favourites/");
                try {
                    favouriteId = Integer.parseInt(createOrder.execute(new String[]{"food_post_id", "" + foodPost.id}).get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                String uri = getResources().getString(R.string.server) + "/favourite_detail/" + favouriteId + "/";
                Server deleteFoodPost = new Server("DELETE", uri);
                try {
                    deleteFoodPost.execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                MapFragment.markerPutColor(MapFragment.markers.get(foodPost.id), !foodPost.favourite ? R.color.grey : R.color.favourite);
            }
        });
    }

}
