package com.example.eduardorodriguez.comeaqui.profile.edit_profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.EditAcountDetailsActivity;
import com.example.eduardorodriguez.comeaqui.R;


import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EditProfileActivity extends AppCompatActivity implements SelectImageFromFragment.OnFragmentInteractionListener{

    private TextView firstNameView;
    private TextView lastNameView;
    private TextView addBioView;
    private TextView phoneNumber;
    private TextView bioTextView;
    private ImageView profileImageView;
    private ImageView backgroundImageView;

    private SelectImageFromFragment selectImageFromFragment;

    boolean isBackGound;
    int userId;

    @Override
    protected void onResume() {
        super.onResume();
        selectImageFromFragment.hideCard();
        getUser(userId);
    }

    private void setProfile(User user){
        if(user.profile_photo != null && !user.profile_photo.contains("no-image")) Glide.with(this).load(user.profile_photo).into(profileImageView);
        if(user.background_photo != null && !user.background_photo.contains("no-image")) Glide.with(this).load(user.background_photo).into(backgroundImageView);
        firstNameView.setText(user.first_name);
        lastNameView.setText(user.last_name);
        phoneNumber.setText(user.phone_number);
        if (user.bio != null && !user.bio.equals(""))
            bioTextView.setText(user.bio);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileImageView = findViewById(R.id.profile_image);
        addBioView = findViewById(R.id.add_bio);
        bioTextView = findViewById(R.id.bioText);
        TextView editProfilePhotoView = findViewById(R.id.edit_profile_picture);
        TextView editCoverPhoto = findViewById(R.id.edit_cover_photo);
        ImageView backView = findViewById(R.id.back_arrow);
        backgroundImageView = findViewById(R.id.background_image);
        TextView editAccountDetailsView = findViewById(R.id.edit_account_details);
        firstNameView = findViewById(R.id.first_name);
        lastNameView = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            userId = b.getInt("userId");
            User user = getUser(userId);
            setProfile(user);
        }

        selectImageFromFragment = SelectImageFromFragment.newInstance(false);

        editProfilePhotoView.setOnClickListener(v -> {
            isBackGound = false;
            selectImageFromFragment.showCard();

        });


        editCoverPhoto.setOnClickListener(v -> {
            isBackGound = true;
            selectImageFromFragment.showCard();
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.select_from, selectImageFromFragment)
                .commit();

        addBioView.setOnClickListener(v -> {
            Intent bioActivity = new Intent(this, AddBioActivity.class);
            startActivity(bioActivity);
        });

        bioTextView.setOnClickListener(v -> {
            Intent bioActivity = new Intent(this, AddBioActivity.class);
            startActivity(bioActivity);
        });

        editAccountDetailsView.setOnClickListener(v -> {
            Intent bioActivity = new Intent(this, EditAcountDetailsActivity.class);
            startActivity(bioActivity);
        });

        backView.setOnClickListener(v -> finish());
    }

    public User getUser(int userId) {
        new GetAsyncTask(getResources().getString(R.string.server) + "/profile_detail/" + userId + "/").execute();
        return null;
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
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
                setProfile(new User(new JsonParser().parse(response).getAsJsonObject()));
            }
            super.onPostExecute(response);
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        saveProfileImage(uri);
    }


    private void saveProfileImage(Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            PatchImageAsyncTask putTask = new PatchImageAsyncTask(getResources().getString(R.string.server) + "/edit_profile/", bitmap);
            if (isBackGound){
                putTask.execute("background_photo");
            } else {
                putTask.execute("profile_photo");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class PatchImageAsyncTask extends AsyncTask<String, Void, String> {
        String uri;
        Bitmap imageBitmap;

        public PatchImageAsyncTask(String uri, Bitmap imageBitmap){
            this.uri = uri;
            this.imageBitmap = imageBitmap;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                return ServerAPI.uploadImage(getApplicationContext(),"PATCH", this.uri, params[0], this.imageBitmap);
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
}
