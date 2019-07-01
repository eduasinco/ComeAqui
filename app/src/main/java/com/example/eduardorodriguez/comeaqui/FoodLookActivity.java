package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderObject;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class FoodLookActivity extends AppCompatActivity {

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
    ImageView posterImage;
    ImageView staticMapView;
    ConstraintLayout postImageLayout;

    FoodPost getFoodObject;

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
        setContentView(R.layout.activity_food_look);
        context = getApplicationContext();

        plateNameView = findViewById(R.id.postPlateName);
        descriptionView = findViewById(R.id.post_description);
        priceView = findViewById(R.id.price);
        timeView = findViewById(R.id.time);
        placeOrderButton = findViewById(R.id.placeOrderButton);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.poster_name);
        posterLocationView = findViewById(R.id.posterLocation);

        postImage = findViewById(R.id.postFoodPhoto);
        posterImage = findViewById(R.id.poster_image);
        staticMapView = findViewById(R.id.static_map);
        postImageLayout = findViewById(R.id.image_layout);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("object") != null){
            getFoodObject = (FoodPost) b.get("object");
            boolean delete = b.getBoolean("delete");

            posterNameView.setText(getFoodObject.owner.first_name + " " + getFoodObject.owner.last_name);
            usernameView.setText(getFoodObject.owner.email);
            plateNameView.setText(getFoodObject.plate_name);
            descriptionView.setText(getFoodObject.description);
            posterLocationView.setText(getFoodObject.address);
            priceView.setText(getFoodObject.price);
            timeView.setText(getFoodObject.time);

            Bundle bundle = new Bundle();
            bundle.putSerializable("type", getFoodObject.type);
            FoodTypeFragment fragment = new FoodTypeFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.types, fragment)
                    .commit();

            if(!getFoodObject.owner.profile_photo.contains("no-image")){
                postImageLayout.setVisibility(View.VISIBLE);
                Glide.with(this).load(getFoodObject.owner.profile_photo).into(posterImage);
            }
            if(!getFoodObject.food_photo.contains("no-image")) Glide.with(this).load(getFoodObject.food_photo).into(postImage);

            final StringBuilder path = new StringBuilder();
            path.append(getResources().getString(R.string.server));
            path.append(getFoodObject.food_photo);

            Glide.with(this).load(path.toString()).into(postImage);

            setPlaceButton(delete);
        }
    }

    void setPlaceButton(boolean delete){
        if (delete){
            placeOrderButton.setText("Delete Post");
            placeOrderButton.setBackgroundColor(Color.parseColor("#FFFF0E01"));
            placeOrderButton.setOnClickListener(v -> {
                Server deleteFoodPost = new Server("DELETE", getResources().getString(R.string.server) + "/foods/" + getFoodObject.id + "/");
                deleteFoodPost.execute();
                Intent k = new Intent(FoodLookActivity.this, MainActivity.class);
                k.putExtra("profile", true);
                startActivity(k);
            });
        }else{
            placeOrderButton.setOnClickListener(v -> {
                PostAsyncTask emitMessage = new PostAsyncTask(getResources().getString(R.string.server) + "/send_message/");
                emitMessage.execute(
                        new String[]{"food_post_id", "" + getFoodObject.id}
                );
                PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_order/");
                try {
                    String response = createOrder.execute(
                            new String[]{"food_post_id", "" + getFoodObject.id}
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
