package com.example.eduardorodriguez.comeaqui;

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
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;

public class FoodElementFragment extends Fragment {

    TextView postTime;
    TextView postPrice;
    TextView posterDescriptionView;
    TextView postNameView;
    View cardButtonView;


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

        View view = inflater.inflate(R.layout.fragment_card_button, container, false);

        postNameView = view.findViewById(R.id.plate_name);
        postTime = view.findViewById(R.id.time);
        postPrice = view.findViewById(R.id.price);
        posterDescriptionView = view.findViewById(R.id.description);
        cardButtonView = view.findViewById(R.id.cardButton);

        FoodPost foodPost = (FoodPost) getArguments().getSerializable("object");

        postNameView.setText(foodPost.plate_name);
        postPrice.setText(foodPost.price + "€");
        postTime.setText(foodPost.time);
        posterDescriptionView.setText(foodPost.description);


        Bundle bundle = new Bundle();
        bundle.putSerializable("type", foodPost.type);
        FoodTypeFragment fragment = new FoodTypeFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container3, fragment)
                .commit();

        cardButtonView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cardButtonView.setBackgroundColor(Color.TRANSPARENT);
                    break;

                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    cardButtonView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.box_stroke));
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
