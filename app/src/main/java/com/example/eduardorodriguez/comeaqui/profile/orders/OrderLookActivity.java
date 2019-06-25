package com.example.eduardorodriguez.comeaqui.profile.orders;

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

import java.util.concurrent.ExecutionException;

public class OrderLookActivity extends AppCompatActivity {

    static String[] data;

    TextView postNameView;
    TextView posterMessageView;
    TextView posterLocationView;
    TextView postStatusView;
    TextView postPriceView;
    TextView subtotalView;
    TextView totalPriceView;

    ImageView posterImageView;
    ImageView postImageView;
    ImageView staticMapView;

    OrderObject order;

    Context context;


    public void createStringArray(JsonObject jo){
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
        posterLocationView.setText(order.postAddress);
        postPriceView.setText("€" + order.postPrice);
        subtotalView.setText("€" + order.postPrice);
        totalPriceView.setText("€" + order.postPrice);
        String url = "http://maps.google.com/maps/api/staticmap?center=" + order.postLat + "," + order.postLng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=AIzaSyDqkl1DgwHu03SmMoqVey3sgR62GnJ-VY4";
        Glide.with(this).load(url).into(staticMapView);

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
        staticMapView = findViewById(R.id.static_map);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null){
            OrderObject orderObject = (OrderObject) b.get("object");
            GetAsyncTask getOrders = new GetAsyncTask("order_detail/" + orderObject.id + "/");
            try {
                String response = getOrders.execute().get();
                if (response != null)
                createStringArray(new JsonParser().parse(response).getAsJsonObject());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
