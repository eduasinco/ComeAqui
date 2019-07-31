package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardorodriguez.comeaqui.R;


public class SelectImageFromFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "is_background";

    // TODO: Rename and change types of parameters
    private boolean isBackGound;


    public SelectImageFromFragment() {
        // Required empty public constructor
    }

    public static SelectImageFromFragment newInstance(boolean isBackground) {
        SelectImageFromFragment fragment = new SelectImageFromFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isBackground);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isBackGound = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_image_from, container, false);

        LinearLayout selectFromCamera = view.findViewById(R.id.select_from_camera);
        LinearLayout selectFromGallery = view.findViewById(R.id.select_from_gallery);
        ConstraintLayout outOfCard = view.findViewById(R.id.out_of_card);

        outOfCard.setVisibility(View.VISIBLE);
        outOfCard.setScaleX(0);
        outOfCard.setScaleY(0);
        outOfCard.animate().scaleX(1).scaleY(1).setDuration(200);

        selectFromCamera.setOnClickListener(v -> {
            Intent cropImage = new Intent(getContext(), CropImageActivity.class);
            cropImage.putExtra("is_camera", true);
            cropImage.putExtra("is_back_ground", isBackGound);
            startActivity(cropImage);
        });

        selectFromGallery.setOnClickListener(v -> {
            Intent cropImage = new Intent(getContext(), CropImageActivity.class);
            cropImage.putExtra("is_camera", false);
            cropImage.putExtra("is_back_ground", isBackGound);
            startActivity(cropImage);
        });

        outOfCard.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    outOfCard.animate().scaleX(0).scaleY(0).setDuration(200).withEndAction(() -> {
                        outOfCard.setVisibility(View.GONE);
                    });
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
            return true;
        });
        return view;
    }
}
