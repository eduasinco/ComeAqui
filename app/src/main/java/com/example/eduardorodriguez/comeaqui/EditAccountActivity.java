package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.hbb20.CountryCodePicker;

public class EditAccountActivity extends AppCompatActivity {

    private Bitmap imageBitmap;
    private ImageView profileImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imagePhoto = findViewById(R.id.imagePhoto);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imagePhoto.setImageBitmap(imageBitmap);
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        final CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);
        final EditText phoneNumberView = findViewById(R.id.phone);
        ccp.registerCarrierNumberEditText(phoneNumberView);


        profileImageView = findViewById(R.id.profile_image);
        final EditText editFirstNameView = findViewById(R.id.editFirstName);
        final EditText editLastNameView = findViewById(R.id.editLastName);
        final EditText emailView = findViewById(R.id.email);
        final EditText passwordView = findViewById(R.id.password);
        final Button saveButtonView = findViewById(R.id.saveButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            String firstName = b.getString("firstName");
            String lastName = b.getString("lastName");
            String phoneNumber = b.getString("phoneNumber");
            String profilePhoto = b.getString("profilePhoto");

            if(profilePhoto != null && !profilePhoto.contains("no-image"))
                Glide.with(this).load(profilePhoto).into(profileImageView);

            editFirstNameView.setText(firstName);
            editLastNameView.setText(lastName);
            emailView.setText(phoneNumber);
            passwordView.setText("");
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

        passwordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(EditAccountActivity.this, ChangePasswordActivity.class);
                startActivity(k);
            }
        });

        saveButtonView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatchAsyncTask putTast = new PatchAsyncTask();
                putTast.execute("first_name", editFirstNameView.getText().toString());
                PatchAsyncTask putTast2 = new PatchAsyncTask();
                putTast2.execute("last_name", editLastNameView.getText().toString());
                PatchAsyncTask putTast3 = new PatchAsyncTask();
                putTast3.execute("phone_number", ccp.getDefaultCountryCode() + phoneNumberView.getText().toString());
                if (imageBitmap != null){
                    PatchAsyncTask putTast6 = new PatchAsyncTask();
                    putTast6.imageBitmap = imageBitmap;
                    putTast6.execute("profile_photo", "", "true");
                }
                Intent k = new Intent(EditAccountActivity.this, SettingsActivity.class);
                startActivity(k);
            }
        });

    }
}
