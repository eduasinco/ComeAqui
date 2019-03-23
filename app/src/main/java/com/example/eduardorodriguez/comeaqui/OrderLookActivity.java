package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class OrderLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_look);

        TextView postNameView = findViewById(R.id.postName);
        TextView posterMessageView = findViewById(R.id.posterMessage);
        TextView posterLocationView = findViewById(R.id.posterLocation);
        TextView postStatusView = findViewById(R.id.postStatus);
        TextView postPriceView = findViewById(R.id.postPrice);
        TextView subtotalView = findViewById(R.id.postSubtotalPrice);
        TextView totalPriceView = findViewById(R.id.totalPrice);

        ImageView posterImageView = findViewById(R.id.posterImage);
        ImageView postImageView = findViewById(R.id.postImage);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null) {
            String orderStatus = b.getString("orderStatus");
            String postPlateName = b.getString("postPlateName");
            String postFoodPhoto = b.getString("postFoodPhoto");
            String postPrice = b.getString("postPrice");
            String postDescription = b.getString("postDescription");
            String posterFirstName = b.getString("posterFirstName");
            String posterLastName = b.getString("posterLastName");
            String posterEmail = b.getString("posterEmail");
            String posterImage = b.getString("posterImage");
            String posterLocation = b.getString("posterLocation");
            String posterPhoneNumber = b.getString("posterPhoneNumber");
            String posterPhoneCode = b.getString("posterPhoneCode");

            switch (orderStatus){
                case "PENDING":
                    postStatusView.setText(orderStatus);
                    postStatusView.setTextColor(Color.parseColor("#FFC60000"));
                    break;
                case "CONFIRMED":
                    postStatusView.setText(orderStatus);
                    postStatusView.setTextColor(Color.parseColor("#FF1EB600"));
                    break;
                case "DELIVERED":
                    postStatusView.setText(orderStatus);
                    postStatusView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    break;
            }

            postNameView.setText(postPlateName);
            posterMessageView.setText(posterFirstName + " is preparing your order");
            posterLocationView.setText(posterLocation);
            postPriceView.setText("€" + postPrice);
            subtotalView.setText("€" + postPrice);
            totalPriceView.setText("€" + postPrice);

            String initialUri = "http://127.0.0.1:8000/media/";
            if(!posterImage.contains("no-image")) Glide.with(this).load(initialUri + posterImage).into(posterImageView);
            if(!postFoodPhoto.contains("no-image")) Glide.with(this).load(initialUri + postFoodPhoto).into(postImageView);


        }
    }
}
