package com.example.eduardorodriguez.comeaqui.utilities;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;

public class RatingFragment extends Fragment {
    private static final String RATING = "rating";
    private static final String RATING_N = "rating_n";

    private float rating;
    private int ratingN;

    ImageView[] starArray;
    TextView ratingTextView;
    TextView ratingNTextView;

    public RatingFragment() {}

    public static Fragment newInstance(float rating, int ratingN) {
        Fragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putFloat(RATING, rating);
        args.putInt(RATING_N, ratingN);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rating = getArguments().getFloat(RATING);
            ratingN = getArguments().getInt(RATING_N);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        ratingTextView = view.findViewById(R.id.profile_rating);
        ratingNTextView = view.findViewById(R.id.rating_n);
        starArray = new ImageView[]{
                view.findViewById(R.id.star0),
                view.findViewById(R.id.star1),
                view.findViewById(R.id.star2),
                view.findViewById(R.id.star3),
                view.findViewById(R.id.star4)
        };

        for (int i = 0; i < starArray.length; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_empty));
        }

        for (int i = 0; i < rating - 1; i++){
            starArray[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_filled));
        }

        double decimal = rating - Math.floor(rating);
        if(decimal < 0.75 && decimal >= 0.25){
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_half_filled));
        } else if (decimal >= 0.75){
            starArray[(int) rating].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_filled));
        }

        ratingTextView.setText(String.format("%.02f", rating) + "");
        ratingNTextView.setText("(" + ratingN + ")");
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}