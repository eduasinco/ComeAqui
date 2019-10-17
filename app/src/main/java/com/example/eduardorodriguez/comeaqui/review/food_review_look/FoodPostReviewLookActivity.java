package com.example.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;

public class FoodPostReviewLookActivity extends AppCompatActivity implements FoodReviewFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_post_review_look);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("foodPost") != null) {
            FoodPost foodPost = (FoodPost) b.get("foodPost");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.review_list, FoodReviewFragment.newInstance(foodPost.id))
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(ReviewObject item) {

    }
}
