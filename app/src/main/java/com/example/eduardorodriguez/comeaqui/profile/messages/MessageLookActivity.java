package com.example.eduardorodriguez.comeaqui.profile.messages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.server.PutAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;

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
        final Button confrimButton = findViewById(R.id.confrimButton);
        final Button cancelButtonView = findViewById(R.id.cancelButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            int pos = b.getInt("pos");
            MessageObject message = MyMessagesRecyclerViewAdapter.mValues.get(pos);
            String lastName = message.lastName;
            String firstName = message.firstName;
            String senderEmail = message.senderEmail;
            String senderImage = message.senderImage;
            String creationDate = message.creationDate;
            final String id = message.id;
            String postPlateName = message.postPlateName;
            String postFoodPhoto = message.postFoodPhoto;
            String postPrice = message.postPrice;
            String postDescription = message.postDescription;
            final String post = message.post;
            final String poster = message.poster;


            String url = "http://127.0.0.1:8000/media/";
            Glide.with(this).load(url + postFoodPhoto).into(postFoodPhotoView);
            Glide.with(this).load(url + senderImage).into(senderImageView);
            senderNameView.setText(firstName + " " + lastName);
            postPlateNameView.setText(postPlateName);
            postDescriptionView.setText(postDescription);
            senderEmailView.setText(senderEmail);


            confrimButton.setText("CONFIRM ORDER " + postPrice + "€");
            confrimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confrimButton.setVisibility(View.GONE);
                    PutAsyncTask createOrder = new PutAsyncTask("http://127.0.0.1:8000/order_detail/" + id + "/");
                    createOrder.execute(
                            new String[]{"order_status", "CONFIRMED"},
                            new String[]{"post", post},
                            new String[]{"poster", poster}
                    );
                }
            });
            cancelButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PutAsyncTask createOrder = new PutAsyncTask("http://127.0.0.1:8000/order_detail/" + id + "/");
                    createOrder.execute(
                            new String[]{"order_status", "CANCELED"},
                            new String[]{"post", post},
                            new String[]{"poster", poster}
                    );
                }
            });
        }
    }
}
