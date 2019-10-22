package com.example.eduardorodriguez.comeaqui.utilities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;

public class FoodElementFragment extends Fragment {

    TextView postTime;
    TextView postPrice;
    TextView posterDescriptionView;
    TextView postNameView;
    View cardButtonView;
    View cardButtonProgress;

    boolean annuleFoodLook = false;

    public FoodElementFragment() {
        // Required empty public constructor
    }

    public static FoodElementFragment newInstance(FoodPost foodPost) {
        FoodElementFragment fragment = new FoodElementFragment();
        Bundle args = new Bundle();
        args.putSerializable("object", foodPost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card_button, container, false);

        postNameView = view.findViewById(R.id.plate_name);
        postTime = view.findViewById(R.id.time);
        postPrice = view.findViewById(R.id.price);
        posterDescriptionView = view.findViewById(R.id.description);
        cardButtonView = view.findViewById(R.id.cardButton);
        cardButtonProgress = view.findViewById(R.id.card_button_progress);

        FoodPost foodPost = (FoodPost) getArguments().getSerializable("object");

        postNameView.setText(foodPost.plate_name);
        postPrice.setText(foodPost.price + "€");
        postTime.setText(foodPost.time);
        posterDescriptionView.setText(foodPost.description);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.food_types, FoodTypeFragment.newInstance(foodPost.type))
                .commit();


        cardButtonView.setOnClickListener(v -> {
            showProgress(true);
            Intent foodLook = new Intent(getContext(), FoodLookActivity.class);
            foodLook.putExtra("foodPostId", foodPost.id);
            getContext().startActivity(foodLook);
        });
        cardButtonView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cardButtonView.setBackgroundColor(Color.TRANSPARENT);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    cardButtonView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.box_stroke));
                    break;
                default:
                    return false;
            }
            return false;
        });

        return view;
    }

    void showProgress(boolean show){
        if (show){
            cardButtonProgress.setVisibility(View.VISIBLE);
            cardButtonView.setVisibility(View.INVISIBLE);
        } else {
            cardButtonProgress.setVisibility(View.GONE);
            cardButtonView.setVisibility(View.VISIBLE);
        }
    }
}
