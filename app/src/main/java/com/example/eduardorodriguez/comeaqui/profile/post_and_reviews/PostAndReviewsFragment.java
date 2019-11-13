package com.example.eduardorodriguez.comeaqui.profile.post_and_reviews;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostReview;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PostAndReviewsFragment extends Fragment {
    private static final String USER_ID = "userId";
    private int userId;
    private static MyPostAndReviewsRecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    FrameLayout waitFrame;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostAndReviewsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PostAndReviewsFragment newInstance(int user) {
        PostAndReviewsFragment fragment = new PostAndReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPostFromUser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postandreviews_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_user_postandreviews);
        waitFrame = view.findViewById(R.id.wait_frame);

        getPostFromUser();
        return view;
    }

    void getPostFromUser(){
        try {
            startWaitingFrame(true);
            new GetAsyncTask(getContext(),"GET", getResources().getString(R.string.server) + "/user_food_posts_reviews/" + userId + "/"){
                @Override
                protected void onPostExecute(String response) {
                    startWaitingFrame(false);
                    if (response != null){
                        ArrayList<FoodPostReview> data = new ArrayList<>();
                        for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                            JsonObject jo = pa.getAsJsonObject();
                            FoodPostReview foodPost = new FoodPostReview(jo);
                            data.add(foodPost);
                        }
                        adapter = new MyPostAndReviewsRecyclerViewAdapter(data);
                        recyclerView.setAdapter(adapter);
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(getContext(), "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitFrame.setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction()
                    .replace(R.id.wait_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitFrame.setVisibility(View.GONE);
        }
    }
}
