package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class FoodElementFragment extends Fragment {

    TextView postTime;
    TextView postPrice;
    TextView posterDescriptionView;
    TextView postNameView;
    ImageView cardButtonView;


    ImageView postImageView;


    public FoodElementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_food_element, container, false);

        postNameView = view.findViewById(R.id.plate_name);
        postTime = view.findViewById(R.id.time);
        postPrice = view.findViewById(R.id.price);
        posterDescriptionView = view.findViewById(R.id.description);
        postImageView = view.findViewById(R.id.post_iamge);
        cardButtonView = view.findViewById(R.id.card_button);

        FoodPost foodPost = (FoodPost) getArguments().getSerializable("object");

        postNameView.setText(foodPost.plate_name);
        postPrice.setText(foodPost.price + "€");
        postTime.setText(foodPost.time);
        posterDescriptionView.setText(foodPost.description);

        if(!foodPost.food_photo.contains("no-image")) Glide.with(view.getContext()).load("http://127.0.0.1:8000" + foodPost.food_photo).into(postImageView);

        Bundle bundle = new Bundle();
        bundle.putSerializable("type", foodPost.type);
        FoodTypeFragment fragment = new FoodTypeFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container2, fragment)
                .commit();

        cardButtonView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cardButtonView.setVisibility(View.INVISIBLE);
                    break;

                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    cardButtonView.setVisibility(View.VISIBLE);
                    Intent foodLook = new Intent(getContext(), FoodLookActivity.class);
                    foodLook.putExtra("object", foodPost);
                    getContext().startActivity(foodLook);
                    break;
                default:
                    return false;
            }
            return true;
        });

        return view;
    }
}
