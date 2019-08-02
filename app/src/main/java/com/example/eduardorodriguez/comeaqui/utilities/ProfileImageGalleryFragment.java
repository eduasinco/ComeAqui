package com.example.eduardorodriguez.comeaqui.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.LoginActivity;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.settings.ChangePasswordActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileImageGalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileImageGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileImageGalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;
    int count;

    View view;
    LinearLayout imageListLayout;
    LinearLayout currentHorizontalLayout;

    DisplayMetrics displayMetrics;

    public ProfileImageGalleryFragment() {
        // Required empty public constructor
    }

    public static ProfileImageGalleryFragment newInstance(String param1, String param2) {
        ProfileImageGalleryFragment fragment = new ProfileImageGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        count = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_image_gallery, container, false);
        count = 0;
        displayMetrics = view.getContext().getResources().getDisplayMetrics();
        initializeFirstLayout();
        getPostFromUser();
        return view;
    }

    private void initializeFirstLayout(){
        imageListLayout = view.findViewById(R.id.image_list);

        LinearLayout horizontalLayout = new LinearLayout(getContext());
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                displayMetrics.widthPixels / 3));
        imageListLayout.addView(horizontalLayout);
        currentHorizontalLayout = horizontalLayout;
    }

    public int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    void getPostFromUser(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/user_food_posts/" + MainActivity.user.id + "/");
        try {
            String response = process.execute().get();
            if (response != null) {
                JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
                makeList(jsonArray);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void makeList(JsonArray jsonArray){
        for (JsonElement pa : jsonArray) {

            JsonObject jo = pa.getAsJsonObject();
            FoodPost foodPost = new FoodPost(jo);
            if (foodPost.food_photo != null && !foodPost.food_photo.contains("no-image")){
                count++;
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        displayMetrics.widthPixels / 3,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                currentHorizontalLayout.addView(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                int padding = 10;
                imageView.setPadding(padding,padding,padding,padding);

                makeImageRoundCornered(imageView, 20);

                Glide.with(view.getContext()).load(getResources().getString(R.string.server) + foodPost.food_photo).into(imageView);

                imageView.setOnClickListener((v) -> {
                    Intent imageLook = new Intent(getContext(), ImageLookActivity.class);
                    imageLook.putExtra("image_url", foodPost.food_photo);
                    startActivity(imageLook);
                });

                if (count % 3 == 0){
                    LinearLayout horizontalLayout = new LinearLayout(getContext());
                    horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            displayMetrics.widthPixels / 3));
                    horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                    imageListLayout.addView(horizontalLayout);
                    currentHorizontalLayout = horizontalLayout;
                }
            }
        }
    }

    private void makeImageRoundCornered(ImageView imageView, int curveRadius){
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), curveRadius);
            }
        });
        imageView.setClipToOutline(true);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
