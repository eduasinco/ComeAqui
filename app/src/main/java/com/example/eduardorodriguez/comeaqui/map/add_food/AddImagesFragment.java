package com.example.eduardorodriguez.comeaqui.map.add_food;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.AddFoodActivity;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.example.eduardorodriguez.comeaqui.profile.UserPostFragment;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class AddImagesFragment extends Fragment {
    private static final String FOOD_POST_ID = "foodPostId";
    private OnFragmentInteractionListener mListener;

    ImageView[] imageViews;
    ImageView[] addImageViews;
    LinkedList<Bitmap> imageBitmaps;

    int indexClicked;

    FoodPost foodPostDetail;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public AddImagesFragment() {}

    public void addImage(Uri uri){
        imageViews[indexClicked].setImageURI(uri);
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            if (indexClicked < imageBitmaps.size()){
                imageBitmaps.remove(indexClicked);
            }
            imageBitmaps.add(indexClicked, bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setImageAbailable();
    }

    public void initializeFoodPost(){
        for (int i = 0; i < foodPostDetail.images.size(); i++){
            if(!foodPostDetail.images.get(i).image.contains("no-image")){
                tasks.add(new GetImageBitmap(foodPostDetail.images.get(i).image).execute());
                Glide.with(getContext()).load(foodPostDetail.images.get(i).image).into(imageViews[i]);
            }
        }
    }
    class GetImageBitmap extends AsyncTask<String[], Void, Bitmap> {
        private String uri;
        public GetImageBitmap(String uri){
            this.uri = uri;
        }
        @Override
        protected Bitmap doInBackground(String[]... params) {
            try {
                URL url = new URL(this.uri);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch(IOException e) {
                System.out.println(e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap image) {
            if (image != null){
                imageBitmaps.add(image);
                setImageAbailable();
            }
            super.onPostExecute(image);
        }
    }

    public static AddImagesFragment newInstance(Integer foodPostId) {
        AddImagesFragment fragment = new AddImagesFragment();
        if(null != foodPostId){
            Bundle args = new Bundle();
            args.putInt(FOOD_POST_ID, foodPostId);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getFoodPostDetailsAndSet(getArguments().getInt(FOOD_POST_ID));
        } else {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_images, container, false);
        imageBitmaps = new LinkedList<>();
        imageViews = new ImageView[]{
                view.findViewById(R.id.image0),
                view.findViewById(R.id.image1),
                view.findViewById(R.id.image2)
        };
        addImageViews = new ImageView[]{
                view.findViewById(R.id.add_image_0),
                view.findViewById(R.id.add_image_1),
                view.findViewById(R.id.add_image_2)
        };
        setImageAbailable();
        return view;
    }

    void getFoodPostDetailsAndSet(int foodPostId){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostId + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                foodPostDetail = new SavedFoodPost(new JsonParser().parse(response).getAsJsonObject());
                initializeFoodPost();
                setImageAbailable();
            }
            super.onPostExecute(response);
        }
    }

    public void uploadImages(){
        List<Bitmap> bitmapsToPost = imageBitmaps.subList(foodPostDetail.images.size(), imageBitmaps.size());
        PostImagesAsyncTask postI = new PostImagesAsyncTask(
                getResources().getString(R.string.server) + "/add_food_images/" + foodPostDetail.id + "/",
                bitmapsToPost
        );
        tasks.add(postI.execute());
    }

    class PostImagesAsyncTask extends AsyncTask<String, Void, String> {
        String uri;
        List<Bitmap> images;
        public PostImagesAsyncTask(String uri, List<Bitmap> imageBitmaps){
            this.uri = uri;
            this.images = imageBitmaps;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                for (int i = 0; i < this.images.size(); i++){
                    Bitmap image = this.images.get(i);
                    if (null != image)
                        ServerAPI.uploadImage(getContext(), "PATCH",  this.uri, "image", image);
                }
                return "";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            HashMap<Integer, Bitmap> bitmapHashMap= new HashMap<>();
            for(int i = 0; i < foodPostDetail.images.size(); i++){
                if (imageBitmaps.get(i) != null){
                    bitmapHashMap.put(foodPostDetail.images.get(i).id, imageBitmaps.get(i));
                }
            }
            PatchImagesAsyncTask patch = new PatchImagesAsyncTask(getResources().getString(R.string.server) + "/edit_image/", bitmapHashMap);
            tasks.add(patch.execute());
            super.onPostExecute(response);
        }
    }

    class PatchImagesAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        HashMap<Integer, Bitmap> bitmapHashMap;

        public PatchImagesAsyncTask(String uri, HashMap<Integer, Bitmap> bitmapHashMap){
            this.uri = uri;
            this.bitmapHashMap = bitmapHashMap;
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                for (Integer imageId: bitmapHashMap.keySet()){
                    ServerAPI.uploadImage(getContext(),"PATCH", this.uri + imageId + "/", "food_photo", this.bitmapHashMap.get(imageId));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            mListener.onImageUploadFinished();
            super.onPostExecute(response);
        }
    }

    void setImageAbailable(){
        int i = 0;
        while (i < imageBitmaps.size()){
            final int finalI = i;
            imageViews[i].setOnClickListener(v -> {
                indexClicked = finalI;
                mListener.onAddImage();
            });
            i++;
        }
        if (i < imageViews.length) {
            final int finalI = i;
            imageViews[i].setOnClickListener(v ->{
                indexClicked = finalI;
                mListener.onAddImage();
            });
            addImageViews[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.add_image_icon));
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

    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onAddImage();
        void onImageUploadFinished();
    }
}
