package com.example.eduardorodriguez.comeaqui.review;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;

public class GuestsReviewActivity extends AppCompatActivity implements GuestReviewFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guests_review);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("foodPostId") != null) {
            int foodPostId = b.getInt("foodPostId");

        }
    }

    @Override
    public void onListFragmentInteraction(OrderObject item) {

    }
}
