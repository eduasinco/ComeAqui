package com.example.eduardorodriguez.comeaqui.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostImageObject;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HorizontalFoodPostImageDisplayFragment extends Fragment {
    private static final String FOOD_POST_ID = "foodPostId";
    private int foodPostId;
    private OnFragmentInteractionListener mListener;

    DisplayMetrics displayMetrics;

    ArrayList<FoodPostImageObject> foodPostImageObjects;

    LinearLayout imageList;

    public HorizontalFoodPostImageDisplayFragment() {}
    public static HorizontalFoodPostImageDisplayFragment newInstance(int foodPostId) {
        HorizontalFoodPostImageDisplayFragment fragment = new HorizontalFoodPostImageDisplayFragment();
        Bundle args = new Bundle();
        args.putInt(FOOD_POST_ID, foodPostId);
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
    public void onResume() {
        super.onResume();
        getFoodPostImages();
    }

    public int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizontal_image_display, container, false);
        imageList = view.findViewById(R.id.images_list);
        displayMetrics = view.getContext().getResources().getDisplayMetrics();
        return view;
    }
    void setImages(){
        imageList.removeAllViews();
        for (FoodPostImageObject io: foodPostImageObjects){

            CardView card = new CardView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(displayMetrics.widthPixels - dpToPx(100), dpToPx(200));
            lp.setMargins(dpToPx(24), dpToPx(8), 0, dpToPx(8));
            card.setLayoutParams(lp);
            card.setRadius(dpToPx(8));

            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            card.addView(imageView);
            imageList.addView(card);
            if(!io.image.contains("no-image")) {
                Glide.with(getContext()).load(io.image).into(imageView);
                imageView.setOnClickListener(v -> goToImageLook(io.image));
            }
        }
    }

    void goToImageLook(String url){
        Intent k = new Intent(getContext(), ImageLookActivity.class);
        k.putExtra("image_url", url);
        startActivity(k);
    }

    void getFoodPostImages(){
        try{
            foodPostImageObjects = new ArrayList<>();
            new GetAsyncTask("GET", getResources().getString(R.string.server) + "/food_images/" + foodPostId + "/"){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null){
                        for (JsonElement je: new JsonParser().parse(response).getAsJsonArray()){
                            foodPostImageObjects.add(new FoodPostImageObject(je.getAsJsonObject()));
                        }
                        setImages();
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
