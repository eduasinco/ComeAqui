package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class MessageLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_look);


        ImageView postFoodPhotoView = findViewById(R.id.postFoodPhoto);
        ImageView senderImageView = findViewById(R.id.senderImage);
        TextView senderNameView = findViewById(R.id.senderName);
        TextView postPlateNameView = findViewById(R.id.postPlateName);
        TextView postDescriptionView = findViewById(R.id.postDescription);
        TextView senderEmailView = findViewById(R.id.senderEmail);
        Button confrimButton = findViewById(R.id.confrimButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            String lastName = b.getString("lastName");
            String firstName = b.getString("firstName");
            String senderEmail = b.getString("senderEmail");
            String senderImage = b.getString("senderImage");
            String creationDate = b.getString("creationDate");
            final String id = b.getString("id");
            String postPlateName = b.getString("postPlateName");
            String postFoodPhoto = b.getString("postFoodPhoto");
            String postPrice = b.getString("postPrice");
            String postDescription = b.getString("postDescription");


            Glide.with(this).load("http://127.0.0.1:8000/media/" + postFoodPhoto).into(postFoodPhotoView);
            Glide.with(this).load("http://127.0.0.1:8000/media/" + senderImage).into(senderImageView);
            senderNameView.setText(firstName + " " + lastName);
            postPlateNameView.setText(postPlateName);
            postDescriptionView.setText(postDescription);
            senderEmailView.setText(senderEmail);

            confrimButton.setText("CONFIRM ORDER " + postPrice + "€");
            confrimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostAsyncTask createOrder = new PostAsyncTask("http://127.0.0.1:8000/create_order/");
                    createOrder.execute(
                            new String[]{"post_id", id}
                    );
                }

            });
        }
    }
}
