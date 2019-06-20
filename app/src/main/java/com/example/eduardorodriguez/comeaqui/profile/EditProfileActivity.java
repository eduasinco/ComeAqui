package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;

import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {

    Bitmap imageBitmap;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView profileImageView = findViewById(R.id.profile_image);
        final TextView editFirstNameView = findViewById(R.id.editFirstName);
        final TextView editLastNameView = findViewById(R.id.editLastName);
        final TextView bioView = findViewById(R.id.orderMessage);
        final Button saveButtonView = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            User user = (User) b.get("object");

            if(user.profile_photo != null && !user.profile_photo.contains("no-image")) Glide.with(this).load(user.profile_photo).into(profileImageView);
            editFirstNameView.setText(user.first_name);
            editLastNameView.setText(user.last_name);
            bioView.setText(user.bio);
        }

        FloatingActionButton myFab =  findViewById(R.id.fabCamera);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        saveButtonView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatchAsyncTask putTast = new PatchAsyncTask();
                try {
                    putTast.execute("first_name", editFirstNameView.getText().toString()).get();
                    PatchAsyncTask putTast2 = new PatchAsyncTask();
                    putTast2.execute("last_name", editLastNameView.getText().toString()).get();
                    PatchAsyncTask putTast3 = new PatchAsyncTask();
                    putTast3.execute("bio", bioView.getText().toString()).get();
                    if (imageBitmap != null){
                        PatchAsyncTask putTast4 = new PatchAsyncTask();
                        putTast4.imageBitmap = imageBitmap;
                        putTast4.execute("profile_photo", "", "true").get();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                Intent k = new Intent(EditProfileActivity.this, MainActivity.class);
                k.putExtra("profile", true);
                startActivity(k);
            }
        });
    }
}
