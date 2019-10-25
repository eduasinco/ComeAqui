package com.example.eduardorodriguez.comeaqui.map;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.eduardorodriguez.comeaqui.utilities.HorizontalFoodPostImageDisplayFragment;
import com.example.eduardorodriguez.comeaqui.utilities.RatingFragment;

import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class MapCardFragment extends Fragment {

    TextView posterNameView;
    TextView posterUserName;
    ImageView posterImageView;
    ImageView starView;
    CardView cardView;
    FoodPost foodPost;

    int favouriteId;

    public MapCardFragment() {}

    public static MapCardFragment newInstance() {
        return new MapCardFragment();
    }

    public void showPost(FoodPost foodPost){
        this.foodPost = foodPost;
        setView();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_card, container, false);

        posterNameView = view.findViewById(R.id.poster_name);
        posterUserName = view.findViewById(R.id.poster_username);
        starView = view.findViewById(R.id.star);
        posterImageView = view.findViewById(R.id.poster_image);
        cardView = view.findViewById(R.id.map_card);

        return view;
    }

    public void moveCardUp(boolean up){
        int move = cardView.getMeasuredHeight() + ((ConstraintLayout.LayoutParams) cardView.getLayoutParams()).bottomMargin * 2;
        if (up) {
            cardView.setVisibility(View.VISIBLE);
            cardView.setTranslationY(move);
            cardView.animate().translationY(0).setDuration(move / 2);
        } else {
            cardView.animate().translationY(move).setDuration(move / 2);
        }
    }

    void setView(){
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container2, FoodElementFragment.newInstance(foodPost))
                .commit();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_rating, RatingFragment.newInstance(USER.rating, USER.ratingN))
                .commit();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalFoodPostImageDisplayFragment.newInstance(foodPost.id,"SMALL"))
                .commit();

        favouriteId = foodPost.favouriteId;

        posterNameView.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
        posterUserName.setText(foodPost.owner.username);
        starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
        MapFragment.markerPutColor(MapFragment.markerHashMap.get(foodPost.id), !foodPost.favourite ? R.color.grey : R.color.favourite);

        if(!foodPost.owner.profile_photo.contains("no-image")) Glide.with(getContext()).load(foodPost.owner.profile_photo).into(posterImageView);

        posterImageView.setOnClickListener(v -> goToProfileView());
        setFavourite();
    }

    void goToProfileView(){
        Intent k = new Intent(getContext(), ProfileViewActivity.class);
        k.putExtra("userId", foodPost.owner.id);
        startActivity(k);
    }


    void setFavourite(){
        starView.setOnClickListener(v -> {
            foodPost.favourite = !foodPost.favourite;
            starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
            if (foodPost.favourite) {
                MapFragment.markerPutColor(MapFragment.markerHashMap.get(foodPost.id), R.color.favourite);
                PostAsyncTask putFavourite = new PostAsyncTask(getResources().getString(R.string.server) + "/favourites/");
                try {
                    favouriteId = Integer.parseInt(putFavourite.execute(new String[]{"food_post_id", "" + foodPost.id}).get());
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
                MapFragment.markerPutColor(MapFragment.markerHashMap.get(foodPost.id), !foodPost.favourite ? R.color.grey : R.color.favourite);
            }
        });
    }

}
