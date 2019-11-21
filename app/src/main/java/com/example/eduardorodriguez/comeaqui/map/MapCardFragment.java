package com.example.eduardorodriguez.comeaqui.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.FoodElementFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.Server;

import com.example.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.example.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
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

    ArrayList<AsyncTask> tasks = new ArrayList<>();
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
                .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(foodPost.id, 0, 4, 100, 0, 0))
                .commit();

        favouriteId = foodPost.favouriteId;

        posterNameView.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
        posterUserName.setText(foodPost.owner.username);
        starView.setImageResource(foodPost.favourite ? R.drawable.star_fill: R.drawable.star);
        MapFragment.setMarkerDesign(MapFragment.markerHashMap.get(foodPost.id), true);

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
                MapFragment.setMarkerDesign(MapFragment.markerHashMap.get(foodPost.id), true);
                PostAsyncTask putFavourite = new PostAsyncTask(getResources().getString(R.string.server) + "/favourites/");
                tasks.add(putFavourite.execute(new String[]{"food_post_id", "" + foodPost.id}));
            } else {
                String uri = getResources().getString(R.string.server) + "/favourite_detail/" + favouriteId + "/";
                Server deleteFoodPost = new Server(getContext(),"DELETE", uri);
                try {
                    deleteFoodPost.execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                MapFragment.setMarkerDesign(MapFragment.markerHashMap.get(foodPost.id), true);
            }
        });
    }

    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getContext(), "POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                favouriteId = Integer.parseInt(response);
            }
            super.onPostExecute(response);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
    }
}
