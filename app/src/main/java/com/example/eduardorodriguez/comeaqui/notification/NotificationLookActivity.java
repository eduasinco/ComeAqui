package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class NotificationLookActivity extends AppCompatActivity {

    static Context context;

    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView timeView;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    Button placeOrderButton;

    ImageView postImage;
    ImageView dinnerImage;
    ImageView staticMapView;
    ConstraintLayout postImageLayout;

    NotificationObject notificationObject;

    public static void goToOrder(JsonObject jsonObject){
        try{
            OrderObject orderObject = new OrderObject(jsonObject);
            Intent goToOrders = new Intent(context, OrderLookActivity.class);
            goToOrders.putExtra("object", orderObject);
            context.startActivity(goToOrders);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_look);
        context = getApplicationContext();

        plateNameView = findViewById(R.id.postPlateName);
        descriptionView = findViewById(R.id.post_description);
        priceView = findViewById(R.id.price);
        timeView = findViewById(R.id.time);
        placeOrderButton = findViewById(R.id.placeOrderButton);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.dinner_name);
        posterLocationView = findViewById(R.id.posterLocation);

        postImage = findViewById(R.id.post_image);
        dinnerImage = findViewById(R.id.dinner_image);
        staticMapView = findViewById(R.id.static_map);
        postImageLayout = findViewById(R.id.post_image_layout);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("object") != null){
            notificationObject = (NotificationObject) b.get("object");
            boolean delete = b.getBoolean("delete");

            posterNameView.setText(notificationObject.sender.first_name + " " + notificationObject.sender.last_name);
            usernameView.setText(notificationObject.owner.email);
            plateNameView.setText(notificationObject.order.post.plate_name);
            descriptionView.setText(notificationObject.order.post.description);
            posterLocationView.setText(notificationObject.order.post.address);
            priceView.setText(notificationObject.order.post.price);
            timeView.setText(notificationObject.order.post.time);

            Bundle bundle = new Bundle();
            bundle.putSerializable("type", notificationObject.order.post.type);
            FoodTypeFragment fragment = new FoodTypeFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.types, fragment)
                    .commit();


            if(!notificationObject.sender.profile_photo.contains("no-image")) Glide.with(this).load(notificationObject.sender.profile_photo).into(dinnerImage);
            if(!notificationObject.order.post.food_photo.contains("no-image")){
                postImageLayout.setVisibility(View.VISIBLE);
                Glide.with(this).load(notificationObject.order.post.food_photo).into(postImage);
            }
            String url = "http://maps.google.com/maps/api/staticmap?center=" + notificationObject.order.post.lat + "," + notificationObject.order.post.lng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=" + getResources().getString(R.string.google_key);
            Glide.with(this).load(url).into(staticMapView);

            setPlaceButton(delete);
        }
    }

    void setPlaceButton(boolean delete){
        if (delete){
            placeOrderButton.setText("Delete Post");
            placeOrderButton.setBackgroundColor(Color.parseColor("#FFFF0E01"));
            placeOrderButton.setOnClickListener(v -> {
                Server deleteFoodPost = new Server("DELETE", getResources().getString(R.string.server) + "/foods/" + notificationObject.id + "/");
                deleteFoodPost.execute();
                Intent k = new Intent(NotificationLookActivity.this, MainActivity.class);
                k.putExtra("profile", true);
                startActivity(k);
            });
        }else{
            placeOrderButton.setOnClickListener(v -> {
                PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_order_and_notification/");
                try {
                    String response = createOrder.execute(
                            new String[]{"food_post_id", "" + notificationObject.id}
                    ).get();
                    JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                    FoodLookActivity.goToOrder(jo);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
