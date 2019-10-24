package com.example.eduardorodriguez.comeaqui.map.add_food;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostImageObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AddImagesFragment extends Fragment {
    private static final String FOOD_POST_ID = "foodPostId";
    private OnFragmentInteractionListener mListener;
    Integer foodPostId;

    ImageView[] imageViews;
    boolean[] imagesSet = {false, false, false};

    public AddImagesFragment() {}

    public void addImage(Uri uri, int index){
        imagesSet[index] = true;
        imageViews[index].setImageURI(uri);
        setImageAbailable();
    }

    public static AddImagesFragment newInstance() {
        return new AddImagesFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_images, container, false);
        imageViews = new ImageView[]{
                view.findViewById(R.id.image0),
                view.findViewById(R.id.image1),
                view.findViewById(R.id.image2)
        };
        setImageAbailable();
        return view;
    }

    void setImageAbailable(){
        int i = 0;
        while (i < imageViews.length){
            if (imagesSet[i]) {
                final int finalI = i;
                imageViews[i].setOnClickListener(v -> {
                    mListener.onAddImage(finalI);
                });
            } else {
                break;
            }
            i++;
        }
        final int finalI = i;
        if (i < imageViews.length) {
            imageViews[i].setOnClickListener(v -> mListener.onAddImage(finalI));
            imageViews[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.camera_icon));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onAddImage(int index);
    }
}
