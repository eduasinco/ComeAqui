package com.example.eduardorodriguez.comeaqui.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.add_food.AddImagesFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTimePickerFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.WordLimitEditTextFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditFoodPostActivity extends AppCompatActivity implements
        WordLimitEditTextFragment.OnFragmentInteractionListener,
        SelectImageFromFragment.OnFragmentInteractionListener,
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        AddImagesFragment.OnFragmentInteractionListener{

    ImageButton close;
    Button edit;
    EditText plateName;
    TextView time;
    TextView price;
    ProgressBar editProgress;

    FoodTypeSelectorFragment foodTypeSelectorFragment;
    FoodTimePickerFragment foodTimePickerFragment;
    WordLimitEditTextFragment wordLimitEditTextFragment;
    AddImagesFragment addImageFragment;
    SelectImageFromFragment selectImageFromLayout;

    Bitmap[] imageBitmaps = new Bitmap[3];
    int imageIndex;
    FoodPostDetail foodPostDetail;

    String description;
    String types;

    @Override
    protected void onResume() {
        super.onResume();
        selectImageFromLayout.hideCard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_post);

        close = findViewById(R.id.close);
        edit = findViewById(R.id.post_reply);
        plateName = findViewById(R.id.postPlateName);
        time = findViewById(R.id.time);
        price = findViewById(R.id.price);
        editProgress = findViewById(R.id.edit_progress);

        foodTypeSelectorFragment = FoodTypeSelectorFragment.newInstance();
        foodTimePickerFragment = FoodTimePickerFragment.newInstance();
        wordLimitEditTextFragment = WordLimitEditTextFragment.newInstance();
        addImageFragment = AddImagesFragment.newInstance();
        selectImageFromLayout = SelectImageFromFragment.newInstance(false);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) {
            int foodPostId = b.getInt("foodPostId");
            new GetAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostId + "/").execute();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.post_limit_text_edit, wordLimitEditTextFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.set_foot_types_frame, foodTypeSelectorFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_images_frame, addImageFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.select_image_from, selectImageFromLayout)
                .commit();

        edit.setOnClickListener(v -> postEditFood());
        close.setOnClickListener(v -> finish());
    }

    void setViewDetails(){
        plateName.setText(foodPostDetail.plate_name);
        types = foodPostDetail.type;
        wordLimitEditTextFragment.setText(foodPostDetail.description);
        foodTypeSelectorFragment.setTypes(foodPostDetail.type);
        time.setText(foodPostDetail.time);
        price.setText(foodPostDetail.price + "$");
    }

    void showProgress(boolean show){
        if (show){
            edit.setVisibility(View.GONE);
            editProgress.setVisibility(View.VISIBLE);
        } else {
            edit.setVisibility(View.VISIBLE);
            editProgress.setVisibility(View.GONE);
        }
    }

    void postEditFood(){
        if(validateFrom()){
            edit();
        }
    }

    void edit(){
        patchPost();
        Bitmap[] bitmapsToPost = Arrays.copyOfRange(imageBitmaps, foodPostDetail.images.size(), imageBitmaps.length);
        PostImagesAsyncTask post = new PostImagesAsyncTask(
                getResources().getString(R.string.server) + "/add_food_images/" + foodPostDetail.id + "/",
                bitmapsToPost
        );
        post.execute();
    }

    void patchPost(){
        new PatchAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostDetail.id + "/").execute(
                new String[]{"plate_name", plateName.getText().toString()},
                new String[]{"food_type", types},
                new String[]{"description", description}
        );
    }
    class PatchAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public PatchAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "PATCH", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
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
                    ServerAPI.uploadImage(getApplicationContext(),"PATCH", this.uri + imageId + "/", "food_photo", this.bitmapHashMap.get(imageId));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            showProgress(false);
            finish();
            super.onPostExecute(response);
        }
    }

    class PostImagesAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public Bitmap[] bitmaps;
        public PostImagesAsyncTask(String uri, Bitmap[] bitmaps){
            this.uri = uri;
            this.bitmaps = bitmaps;
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                for (Bitmap image: this.bitmaps){
                    ServerAPI.uploadImage(getApplicationContext(), "PATCH",  this.uri, "image", image);
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
            for(int i = 0; i < imageBitmaps.length; i++){
                if (imageBitmaps[i] != null && i <= foodPostDetail.images.size()-1){
                    bitmapHashMap.put(foodPostDetail.images.get(i).id, imageBitmaps[i]);
                }
            }
            PatchImagesAsyncTask patch = new PatchImagesAsyncTask(getResources().getString(R.string.server) + "/edit_image/", bitmapHashMap);
            patch.execute();
            super.onPostExecute(response);
        }
    }

    boolean validateFrom(){
        boolean isValid = true;
        if (plateName.getText().toString().trim().equals("")){
            plateName.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
            isValid = false;
        }
        if (description.trim().equals("")){
            wordLimitEditTextFragment.setErrorBackground(true);
            isValid = false;
        }
        if (!isValid){
            Toast.makeText(this, "Some fields are not valid", Toast.LENGTH_LONG).show();
        }
        return isValid;
    }

    @Override
    public void onTextChanged(String description) {
        this.description = description;
    }

    @Override
    public void onAddImage(int index) {
        imageIndex = index;
        selectImageFromLayout.showCard();
    }

    @Override
    public void onFragmentInteraction(boolean[] pressed) {
        types = setTypes(pressed);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        try {
            selectImageFromLayout.hideCard();
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageBitmaps[imageIndex] = bm;
            addImageFragment.addImage(uri, imageIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String setTypes(boolean[] pressed){
        StringBuilder types = new StringBuilder();
        for (boolean p: pressed){
            if (p) {
                types.append(1);
            }else{
                types.append(0);
            }
        }
        return types.toString();
    }

    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
                setViewDetails();
                addImageFragment.initializeImages(foodPostDetail.images);
            }
            showProgress(false);
            super.onPostExecute(response);
        }

    }
}
