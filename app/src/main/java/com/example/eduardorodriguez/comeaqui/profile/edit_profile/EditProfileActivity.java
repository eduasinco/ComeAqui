package com.example.eduardorodriguez.comeaqui.profile.edit_profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.EditAcountDetailsActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EditProfileActivity extends AppCompatActivity implements SelectImageFromFragment.OnFragmentInteractionListener{

    private TextView firstNameView;
    private TextView lastNameView;
    private TextView addBioView;
    private FrameLayout selectFrom;
    private TextView phoneNumber;
    private TextView bioTextView;
    private ImageView profileImageView;
    private ImageView backgroundImageView;

    boolean isBackGound;
    int userId;

    @Override
    protected void onResume() {
        super.onResume();
        selectFrom.setVisibility(View.GONE);
        setProfile(getUser(userId));
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
        selectFrom = findViewById(R.id.select_from);
        TextView editAccountDetailsView = findViewById(R.id.edit_account_details);
        firstNameView = findViewById(R.id.first_name);
        lastNameView = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        TextView creditCardNumber = findViewById(R.id.credit_card_number);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            userId = b.getInt("userId");
            User user = getUser(userId);
            setProfile(user);
        }

        editProfilePhotoView.setOnClickListener(v -> {
            isBackGound = false;
            selectFrom.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_from, SelectImageFromFragment.newInstance(false))
                    .commit();
        });

        editCoverPhoto.setOnClickListener(v -> {
            isBackGound = true;
            selectFrom.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_from, SelectImageFromFragment.newInstance(true))
                    .commit();
        });

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
        try {
            String response = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/profile_detail/" + userId + "/").execute().get(10, TimeUnit.SECONDS);
            if (response != null){
                return new User(new JsonParser().parse(response).getAsJsonObject());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        saveProfileImage(uri);
    }


    private void saveProfileImage(Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            if (bitmap != null){
                PatchAsyncTask putTask = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
                putTask.bitmap = bitmap;
                if (isBackGound){
                    putTask.execute(new String[]{"background_photo", "image"}).get(15, TimeUnit.SECONDS);
                }else {
                    putTask.execute(new String[]{"profile_photo", "image"}).get(15, TimeUnit.SECONDS);
                }
            }
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
