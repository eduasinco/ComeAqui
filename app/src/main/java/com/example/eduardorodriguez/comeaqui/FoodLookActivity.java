package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderObject;
import com.example.eduardorodriguez.comeaqui.server.DeleteAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FoodLookActivity extends AppCompatActivity {

    static Context context;

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

        ImageView image = findViewById(R.id.postFoodPhoto);
        TextView plateNameView = findViewById(R.id.postPlateName);
        TextView descriptionView = findViewById(R.id.postDescription);
        Button placeOrderButton = findViewById(R.id.placeOrderButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("object") != null){
            final FoodPost getFoodObject = (FoodPost) b.get("object");
            boolean delete = b.getBoolean("delete");

            plateNameView.setText(getFoodObject.plate_name);
            descriptionView.setText(getFoodObject.description);
            final StringBuilder path = new StringBuilder();
            path.append("http://127.0.0.1:8000");
            path.append(getFoodObject.food_photo);

            Glide.with(this).load(path.toString()).into(image);

            ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
            imageViewArrayList.add((ImageView) findViewById(R.id.vegetarian));
            imageViewArrayList.add((ImageView) findViewById(R.id.vegan));
            imageViewArrayList.add((ImageView) findViewById(R.id.celiac));
            imageViewArrayList.add((ImageView) findViewById(R.id.spicy));
            imageViewArrayList.add((ImageView) findViewById(R.id.fish));
            imageViewArrayList.add((ImageView) findViewById(R.id.meat));
            imageViewArrayList.add((ImageView) findViewById(R.id.dairy));
            int[] resources = new int[]{
                    R.drawable.vegetarianfill,
                    R.drawable.veganfill,
                    R.drawable.cerealfill,
                    R.drawable.spicyfill,
                    R.drawable.fishfill,
                    R.drawable.meatfill,
                    R.drawable.dairyfill,
            };

            for (int i = 0; i < getFoodObject.type.length(); i++){
                if (getFoodObject.type.charAt(i) == '1'){
                    imageViewArrayList.get(i).setImageResource(resources[i]);
                }
            }
            if (delete){
                placeOrderButton.setText("Delete Post");
                placeOrderButton.setBackgroundColor(Color.parseColor("#FFFF0E01"));
                placeOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteAsyncTask deleteFoodPost = new DeleteAsyncTask(getFoodObject.id);
                        deleteFoodPost.execute();
                        Intent k = new Intent(FoodLookActivity.this, MainActivity.class);
                        k.putExtra("profile", true);
                        startActivity(k);
                    }
                });
            }else{
                placeOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostAsyncTask emitMessage = new PostAsyncTask("http://127.0.0.1:8000/send_message/");
                        emitMessage.execute(
                                new String[]{"food_post_id", "" + getFoodObject.id}
                        );
                        PostAsyncTask createOrder = new PostAsyncTask("http://127.0.0.1:8000/create_order/");
                        try {
                            JsonObject response = createOrder.execute(
                                    new String[]{"food_post_id", "" + getFoodObject.id}
                            ).get();
                            FoodLookActivity.goToOrder(response);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
