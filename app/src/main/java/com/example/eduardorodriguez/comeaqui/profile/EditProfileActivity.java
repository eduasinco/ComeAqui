package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EditProfileActivity extends AppCompatActivity {

    private Bitmap imageBitmap;
    private ImageView profileImageView;
    private ImageView backView;
    private ImageView backgroundImageView;
    private TextView editFirstNameView;
    private TextView editLastNameView;
    private TextView bioView;
    private TextView editCoverPhoto;
    private TextView editProfilePhotoView;
    private FrameLayout selectFrom;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView profileImageView = findViewById(R.id.profile_image);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            profileImageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectFrom.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileImageView = findViewById(R.id.profile_image);
        editFirstNameView = findViewById(R.id.editFirstName);
        editLastNameView = findViewById(R.id.editLastName);
        bioView = findViewById(R.id.orderMessage);
        editProfilePhotoView = findViewById(R.id.edit_profile_picture);
        editCoverPhoto = findViewById(R.id.edit_cover_photo);
        backView = findViewById(R.id.back);
        backgroundImageView = findViewById(R.id.background_image);
        selectFrom = findViewById(R.id.select_from);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            User user = (User) b.get("object");
            if(user.profile_photo != null && !user.profile_photo.contains("no-image")) Glide.with(this).load(user.profile_photo).into(profileImageView);
            if(user.background_photo != null && !user.background_photo.contains("no-image")) Glide.with(this).load(user.background_photo).into(backgroundImageView);
            editFirstNameView.setText(user.first_name);
            editLastNameView.setText(user.last_name);
            bioView.setText(user.bio);
        }

        editProfilePhotoView.setOnClickListener(v -> {
            selectFrom.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_from, SelectImageFromFragment.newInstance(false))
                    .commit();
        });

        editCoverPhoto.setOnClickListener(v -> {
            selectFrom.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_from, SelectImageFromFragment.newInstance(true))
                    .commit();
        });

        backView.setOnClickListener(v -> finish());
    }

    private void patchProfileData(){
        PatchAsyncTask putTast = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
        try {
            putTast.execute("first_name", editFirstNameView.getText().toString()).get(5, TimeUnit.SECONDS);
            PatchAsyncTask putTast2 = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
            putTast2.execute("last_name", editLastNameView.getText().toString()).get(5, TimeUnit.SECONDS);
            PatchAsyncTask putTast3 = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
            putTast3.execute("bio", bioView.getText().toString()).get(5, TimeUnit.SECONDS);
            if (imageBitmap != null){
                PatchAsyncTask putTast4 = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
                putTast4.imageBitmap = imageBitmap;
                putTast4.execute("profile_photo", "", "true").get(15, TimeUnit.SECONDS);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void saveFirebaseProfile(){
        uploadFirebaseUserImage();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.firebaseUser.id);
        reference.child("first_name").setValue(editFirstNameView.getText().toString());
        reference.child("last_name").setValue(editLastNameView.getText().toString());
    }

    private void uploadFirebaseUserImage(){
        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child("user_image/" + MainActivity.firebaseUser.id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        firebaseStorage.putBytes(imageBytes);

        firebaseStorage.getDownloadUrl().addOnSuccessListener(uri -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.firebaseUser.id);
            reference.child("profile_photo").setValue(uri.toString());
        }).addOnFailureListener(exception -> {});
    }
}
