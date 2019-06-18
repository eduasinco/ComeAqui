package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;
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

    static OrderObject order;

    static Context context;


    public static void putData(String jsonString){
        JsonParser parser = new JsonParser();
        JsonObject jo = parser.parse(jsonString).getAsJsonObject();
        createStringArray(jo);
    }

    public static void createStringArray(JsonObject jo){
        order = new OrderObject(jo);

        switch (order.orderStatus){
            case "PENDING":
                postStatusView.setText(order.orderStatus);
                postStatusView.setTextColor(Color.parseColor("#FFC60000"));
                break;
            case "CONFIRMED":
                postStatusView.setText(order.orderStatus);
                postStatusView.setTextColor(Color.parseColor("#FF1EB600"));
                break;
            case "DELIVERED":
                postStatusView.setText(order.orderStatus);
                postStatusView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                break;
        }

        postNameView.setText(order.postPlateName);
        posterMessageView.setText(order.posterFirstName + " is preparing your order");
        posterLocationView.setText(order.posterLocation);
        postPriceView.setText("€" + order.postPrice);
        subtotalView.setText("€" + order.postPrice);
        totalPriceView.setText("€" + order.postPrice);

        String initialUri = "http://127.0.0.1:8000/media/";
        if(!order.posterImage.contains("no-image")) Glide.with(context).load(initialUri + order.posterImage).into(posterImageView);
        if(!order.postFoodPhoto.contains("no-image")) Glide.with(context).load(initialUri + order.postFoodPhoto).into(postImageView);
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
