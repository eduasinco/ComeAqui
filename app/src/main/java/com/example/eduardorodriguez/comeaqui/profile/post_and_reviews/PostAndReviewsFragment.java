package com.example.eduardorodriguez.comeaqui.profile.post_and_reviews;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostReview;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostAndReviewsFragment extends Fragment {
    private static final String USER = "user";
    private User user;
    private static MyPostAndReviewsRecyclerViewAdapter adapter;

    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostAndReviewsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PostAndReviewsFragment newInstance(User user) {
        PostAndReviewsFragment fragment = new PostAndReviewsFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postandreviews_list, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        getPostFromUser();
        return view;
    }

    void getPostFromUser(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/user_food_posts_reviews/" + user.id + "/");
        try {
            String response = process.execute().get();
            if (response != null){
                ArrayList<FoodPostReview> data = new ArrayList<>();
                for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                    JsonObject jo = pa.getAsJsonObject();
                    try {
                        FoodPostReview foodPost = new FoodPostReview(jo);
                        data.add(foodPost);
                    } catch (Exception ignore){}
                }
                adapter = new MyPostAndReviewsRecyclerViewAdapter(data);
                recyclerView.setAdapter(adapter);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
