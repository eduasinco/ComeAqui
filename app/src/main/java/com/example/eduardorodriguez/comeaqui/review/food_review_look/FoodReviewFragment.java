package com.example.eduardorodriguez.comeaqui.review.food_review_look;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostReview;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class FoodReviewFragment extends Fragment {

    private static final String FOOD_POST_ID = "foodPostId";
    private int foodPostId;
    private OnListFragmentInteractionListener mListener;

    private MyFoodReviewRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<ReviewObject> reviews;

    FoodPostReview foodPostReview;

    public FoodReviewFragment() {}
    public static FoodReviewFragment newInstance(int columnCount) {
        FoodReviewFragment fragment = new FoodReviewFragment();
        Bundle args = new Bundle();
        args.putInt(FOOD_POST_ID, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            foodPostId = getArguments().getInt(FOOD_POST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foodreview_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        getReviewsFrompFoodPost(foodPostId);
        return view;
    }

    void getReviewsFrompFoodPost(int foodPostId){
        new GetAsyncTask("GET", getResources().getString(R.string.server) + "/food_reviews/" + foodPostId + "/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    foodPostReview = new FoodPostReview(new JsonParser().parse(response).getAsJsonObject());
                    reviews = foodPostReview.reviews;
                    for (int i = 0; i < 3; i++){
                        reviews.addAll(reviews);
                    }
                    adapter = new MyFoodReviewRecyclerViewAdapter(reviews);
                    recyclerView.setAdapter(adapter);
                }
                super.onPostExecute(response);
            }
        }.execute();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ReviewObject item);
    }
}
