package com.example.eduardorodriguez.comeaqui.profile.edit_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.EditAcountDetailsActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity {

    private TextView firstNameView;
    private TextView lastNameView;
    private TextView addBioView;
    private FrameLayout selectFrom;
    private TextView phoneNumber;
    private TextView bioTextView;
    private ImageView profileImageView;
    private ImageView backgroundImageView;


    @Override
    protected void onResume() {
        super.onResume();
        selectFrom.setVisibility(View.GONE);
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
            User user = (User) b.get("object");
            setProfile(user);
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

    private void saveFirebaseProfile(){
        uploadFirebaseUserImage();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.firebaseUser.id);
        reference.child("first_name").setValue(firstNameView.getText().toString());
        reference.child("last_name").setValue(lastNameView.getText().toString());
    }

    private void uploadFirebaseUserImage(){
        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child("user_image/" + MainActivity.firebaseUser.id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        firebaseStorage.putBytes(imageBytes);

        firebaseStorage.getDownloadUrl().addOnSuccessListener(uri -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.firebaseUser.id);
            reference.child("profile_photo").setValue(uri.toString());
        }).addOnFailureListener(exception -> {});
    }
}
