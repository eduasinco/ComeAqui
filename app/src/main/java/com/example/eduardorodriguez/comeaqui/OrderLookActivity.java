package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OrderLookActivity extends AppCompatActivity {

    static String[] data;

    static TextView postNameView;
    static TextView posterMessageView;
    static TextView posterLocationView;
    static TextView postStatusView;
    static TextView postPriceView;
    static TextView subtotalView;
    static TextView totalPriceView;

    static ImageView posterImageView;
    static ImageView postImageView;

    static Context context;


    public static void putData(String jsonString){
        JsonParser parser = new JsonParser();
        JsonObject jo = parser.parse(jsonString).getAsJsonObject();
        createStringArray(jo);
    }

    public static void createStringArray(JsonObject jo){
        String id = jo.get("id").getAsNumber().toString();
        String owner = jo.get("owner").getAsString();
        String orderStatus = jo.get("order_status").getAsString();
        String postPlateName = jo.get("post_plate_name").getAsString();
        String postFoodPhoto = jo.get("post_food_photo").getAsString();
        String postPrice = jo.get("post_price").getAsString();
        String postDescription = jo.get("poster_first_name").getAsString();
        String posterFirstName = jo.get("poster_first_name").getAsString();
        String posterLastName = jo.get("poster_last_name").getAsString();
        String posterEmail = jo.get("poster_email").getAsString();
        String posterImage = jo.get("poster_image").getAsString();
        String posterLocation = jo.get("poster_location").getAsString();
        String posterPhoneNumber = jo.get("poster_phone_number").getAsString();
        String posterPhoneCode = jo.get("poster_phone_code").getAsString();

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
                postStatusView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                break;
        }

        postNameView.setText(postPlateName);
        posterMessageView.setText(posterFirstName + " is preparing your order");
        posterLocationView.setText(posterLocation);
        postPriceView.setText("€" + postPrice);
        subtotalView.setText("€" + postPrice);
        totalPriceView.setText("€" + postPrice);

        String initialUri = "http://127.0.0.1:8000/media/";
        if(!posterImage.contains("no-image")) Glide.with(context).load(initialUri + posterImage).into(posterImageView);
        if(!postFoodPhoto.contains("no-image")) Glide.with(context).load(initialUri + postFoodPhoto).into(postImageView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_look);
        context = getApplicationContext();
        postNameView = findViewById(R.id.postName);
        posterMessageView = findViewById(R.id.posterMessage);
        posterLocationView = findViewById(R.id.posterLocation);
        postStatusView = findViewById(R.id.postStatus);
        postPriceView = findViewById(R.id.postPrice);
        subtotalView = findViewById(R.id.postSubtotalPrice);
        totalPriceView = findViewById(R.id.totalPrice);

        posterImageView = findViewById(R.id.posterImage);
        postImageView = findViewById(R.id.postImage);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null){
            GetAsyncTask getOrders = new GetAsyncTask(8, "order_detail/" + b.getString("id") + "/");
            getOrders.execute();
        }

    }
}
