package com.example.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class OrderLookActivity extends AppCompatActivity {

    TextView postNameView;
    TextView posterDescription;
    TextView posterLocationView;
    TextView postPriceView;
    TextView subtotalView;
    TextView totalPriceView;
    TextView mealTimeView;
    TextView posterNameView;
    TextView orderStatus;

    ImageView posterImageView;
    ImageView postImageView;
    ImageView staticMapView;

    OrderObject order;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_look);
        context = getApplicationContext();
        postNameView = findViewById(R.id.postName);
        posterNameView = findViewById(R.id.poster_name);
        posterDescription = findViewById(R.id.description);
        posterLocationView = findViewById(R.id.posterLocation);
        postPriceView = findViewById(R.id.postPrice);
        subtotalView = findViewById(R.id.postSubtotalPrice);
        totalPriceView = findViewById(R.id.totalPrice);
        mealTimeView = findViewById(R.id.time);
        orderStatus = findViewById(R.id.order_status);

        posterImageView = findViewById(R.id.poster_image);
        postImageView = findViewById(R.id.post_image);
        staticMapView = findViewById(R.id.static_map);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null){
            OrderObject orderObject = (OrderObject) b.get("object");
            GetAsyncTask getOrders = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/order_detail/" + orderObject.id + "/");
            try {
                String response = getOrders.execute().get();
                if (response != null)
                createStringArray(new JsonParser().parse(response).getAsJsonObject());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    void createStringArray(JsonObject jo){
        order = new OrderObject(jo);
        postNameView.setText(order.post.plate_name);
        posterNameView.setText(order.poster.first_name + " " + order.poster.last_name);
        posterDescription.setText(order.post.description);
        posterLocationView.setText(order.post.address);
        postPriceView.setText("€" + order.post.price);
        subtotalView.setText("€" + order.post.price);
        totalPriceView.setText("€" + order.post.price);
        mealTimeView.setText(order.post.time);
        orderStatus.setText(order.status);

        if (order.status.equals("CONFIRMED")){
            orderStatus.setTextColor(getResources().getColor(R.color.success));
        } else if (order.status.equals("CANCELED")){
            orderStatus.setTextColor(getResources().getColor(R.color.canceled));
        } else {
            orderStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        String url = "http://maps.google.com/maps/api/staticmap?center=" + order.post.lat + "," + order.post.lng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=AIzaSyDqkl1DgwHu03SmMoqVey3sgR62GnJ-VY4";
        Glide.with(this).load(url).into(staticMapView);
        if(!order.poster.profile_photo.contains("no-image")) {
            Glide.with(context).load(order.poster.profile_photo).into(posterImageView);
        }
        if(!order.post.food_photo.contains("no-image")){
            postImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(order.post.food_photo).into(postImageView);
        }
    }
}
