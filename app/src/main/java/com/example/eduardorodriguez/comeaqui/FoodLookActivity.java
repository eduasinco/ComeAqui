package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment.PaymentMethodsActivity;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class FoodLookActivity extends AppCompatActivity {

    static Context context;

    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView timeView;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    TextView changePaymentMethod;
    Button placeOrderButton;

    ImageView postImage;
    ImageView posterImage;
    ImageView staticMapView;
    View backView;
    LinearLayout paymentMethod;
    CardView postImageLayout;

    FoodPost foodPost;

    private OkHttpClient client;
    WebSocket ws;

    public static void goToOrder(OrderObject orderObject){
        try{
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

        postImage = findViewById(R.id.post_image);
        posterImage = findViewById(R.id.poster_image);
        staticMapView = findViewById(R.id.static_map);
        postImageLayout = findViewById(R.id.image_layout);
        backView = findViewById(R.id.back);
        paymentMethod = findViewById(R.id.payment_method_layout);
        changePaymentMethod = findViewById(R.id.change_payment);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("object") != null){
            foodPost = (FoodPost) b.get("object");

            posterNameView.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
            usernameView.setText(foodPost.owner.email);
            plateNameView.setText(foodPost.plate_name);
            descriptionView.setText(foodPost.description);
            posterLocationView.setText(foodPost.address);
            priceView.setText(foodPost.price + "$");
            timeView.setText(foodPost.time);

            Bundle bundle = new Bundle();
            bundle.putSerializable("type", foodPost.type);
            FoodTypeFragment fragment = new FoodTypeFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.types, fragment)
                    .commit();


            if(!foodPost.owner.profile_photo.contains("no-image")) {
                Glide.with(this).load(foodPost.owner.profile_photo).into(posterImage);
                posterImage.setOnClickListener(v -> goToProfileView(foodPost.owner));
            }
            if(!foodPost.food_photo.contains("no-image")){
                postImageLayout.setVisibility(View.VISIBLE);
                Glide.with(this).load(foodPost.food_photo).into(postImage);
                postImageLayout.setOnClickListener((v) -> {
                    Intent imageLook = new Intent(this, ImageLookActivity.class);
                    imageLook.putExtra("image_url", foodPost.food_photo);
                    startActivity(imageLook);
                });
            }
            String url = "http://maps.google.com/maps/api/staticmap?center=" + foodPost.lat + "," + foodPost.lng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=" + getResources().getString(R.string.google_key);
            Glide.with(this).load(url).into(staticMapView);

            setPlaceButton();
        }

        changePaymentMethod.setOnClickListener(v -> {
            Intent paymentMethod = new Intent(this, PaymentMethodsActivity.class);
            startActivity(paymentMethod);
        });
        backView.setOnClickListener(v -> finish());
    }

    void goToProfileView(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("user", user);
        startActivity(k);
    }

    void setPlaceButton(){
        if (foodPost.owner.id == USER.id){
            paymentMethod.setVisibility(View.GONE);
            placeOrderButton.setText("Delete Post");
            placeOrderButton.setBackgroundColor(ContextCompat.getColor(this, R.color.canceled));
            placeOrderButton.setOnClickListener(v -> {
                Server deleteFoodPost = new Server("DELETE", getResources().getString(R.string.server) + "/foods/" + foodPost.id + "/");
                deleteFoodPost.execute();
                finish();
            });
        }else{
            placeOrderButton.setOnClickListener(v -> {
                PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_order_and_notification/");
                try {
                    String response = createOrder.execute(
                            new String[]{"food_post_id", "" + foodPost.id}
                    ).get();
                    JsonObject jo = new JsonParser().parse(response).getAsJsonObject().get("order").getAsJsonObject();
                    OrderObject orderObject = new OrderObject(jo);
                    FoodLookActivity.goToOrder(orderObject);

                    WebSocketMessage.send(this,
                        "/ws/notifications/" + orderObject.poster.id +  "/",
                        "{\"notification_id\": \"" + orderObject.id + "\", \"seen\": false}"
                    );
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            });
        }
    }
}
