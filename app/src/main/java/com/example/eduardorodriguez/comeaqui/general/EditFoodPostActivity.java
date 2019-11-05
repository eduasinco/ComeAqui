package com.example.eduardorodriguez.comeaqui.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.add_food.AddImagesFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTimePickerFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.WordLimitEditTextFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EditFoodPostActivity extends AppCompatActivity implements
        WordLimitEditTextFragment.OnFragmentInteractionListener,
        SelectImageFromFragment.OnFragmentInteractionListener,
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        AddImagesFragment.OnFragmentInteractionListener{

    ImageButton close;
    Button post;
    EditText plateName;
    FrameLayout waitingFrame;

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
        post = findViewById(R.id.post_reply);
        plateName = findViewById(R.id.postPlateName);
        waitingFrame = findViewById(R.id.waiting_frame);

        foodTypeSelectorFragment = FoodTypeSelectorFragment.newInstance();
        foodTimePickerFragment = FoodTimePickerFragment.newInstance();
        wordLimitEditTextFragment = WordLimitEditTextFragment.newInstance();
        addImageFragment = AddImagesFragment.newInstance();
        selectImageFromLayout = SelectImageFromFragment.newInstance(false);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) {
            int foodPostId = b.getInt("foodPostId");
            getFoodPost(foodPostId);
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

        post.setOnClickListener(v -> postEditFood());
        close.setOnClickListener(v -> finish());
    }

    void setViewDetails(){
        plateName.setText(foodPostDetail.plate_name);
        types = foodPostDetail.type;
        wordLimitEditTextFragment.setText(foodPostDetail.description);
        foodTypeSelectorFragment.setTypes(foodPostDetail.type);
    }

    void getFoodPost(int foodPostId){
        try{
            new GetAsyncTask("GET", getResources().getString(R.string.server) + "/foods/" + foodPostId + "/"){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null){
                        foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
                        setViewDetails();
                        addImageFragment.initializeImages(foodPostDetail.images);
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }
    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitingFrame.setVisibility(View.GONE);
        }
    }

    void postEditFood(){
        if(validateFrom()){
            edit();
        }
    }

    void edit(){
        patchPost();
        for(int i = 0; i < imageBitmaps.length; i++){
            if (imageBitmaps[i] != null){
                if (i > foodPostDetail.images.size()-1){
                    postImage(imageBitmaps[i]);
                } else {
                    patchImage(foodPostDetail.images.get(i).id, imageBitmaps[i]);
                }
            }
        }
    }

    void patchPost(){
        PatchAsyncTask putTast = new PatchAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostDetail.id + "/"){
            @Override
            protected void onPostExecute(JSONObject response) {
                super.onPostExecute(response);
                finish();
            }
        };
        try {
            putTast.execute(
                    new String[]{"plate_name", plateName.getText().toString()},
                    new String[]{"food_type", types},
                    new String[]{"description", description}
            ).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void patchImage(int imageId, Bitmap image){
        PatchAsyncTask patch = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_image/" + imageId + "/"){
            @Override
            protected void onPostExecute(JSONObject response) {
                super.onPostExecute(response);
            }
        };
        patch.bitmap = image;
        try {
            patch.execute(
                    new String[]{"food_photo", "image"}
            ).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void postImage(Bitmap image){
        try {
            PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/food_images/"){
                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                }
            };
            post.bitmap = image;
            post.execute(
                    new String[]{"post", "" + foodPostDetail.id},
                    new String[]{"image", ""}
            ).get(10, TimeUnit.SECONDS);
            finish();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
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
}
