package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MessageLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_look);


        ImageView image = findViewById(R.id.foodLookImage);
        TextView plateNameView = findViewById(R.id.name);
        TextView descriptionView = findViewById(R.id.descriptionId);
        Button addButtonView = findViewById(R.id.addButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            String lastName = b.getString("lastName");
            String senderEmail = b.getString("senderEmail");
            String senderImage = b.getString("senderImage");
            String creationDate = b.getString("creationDate");
            final String id = b.getString("id");
            String postPlateName = b.getString("postPlateName");
            String postFoodPhoto = b.getString("postFoodPhoto");
            String postPrice = b.getString("postPrice");
            String postDescription = b.getString("postDescription");

            addButtonView.setOnClickListener(new View.OnClickListener() {
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
