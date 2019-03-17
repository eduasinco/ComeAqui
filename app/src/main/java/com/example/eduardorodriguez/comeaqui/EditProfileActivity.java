package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        ImageView profileImageView = findViewById(R.id.profile_image);
        TextView editFirstNameView = findViewById(R.id.editFirstName);
        TextView editLastNameView = findViewById(R.id.editLastName);
        TextView bioView = findViewById(R.id.bio);

        FloatingActionButton myFab =  findViewById(R.id.fabCamera);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        if(b != null){
            String firstName = b.getString("firstName");
            String lastName = b.getString("lastName");
            String bio = b.getString("bio");
            String profile_photo = b.getString("profile_photo");

            if(!profile_photo.contains("no-image")) Glide.with(this).load(profile_photo).into(profileImageView);
            editFirstNameView.setHint(firstName);
            editLastNameView.setHint(lastName);
            bioView.setHint(bio);
        }
    }
}
