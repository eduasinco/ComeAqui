package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;
import com.example.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class NotificationLookActivity extends AppCompatActivity {

    static Context context;

    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView timeView;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    TextView statucMessage;
    Button confirmCancelButton;

    ImageView postImage;
    ImageView dinnerImage;
    ImageView staticMapView;
    ImageView backView;
    CardView postImageLayout;
    View confirmNotificationProgress;

    OrderObject orderObject;

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
        confirmCancelButton = findViewById(R.id.placeOrderButton);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.dinner_name);
        posterLocationView = findViewById(R.id.posterLocation);
        statucMessage = findViewById(R.id.status_message);

        postImage = findViewById(R.id.image);
        dinnerImage = findViewById(R.id.dinner_image);
        staticMapView = findViewById(R.id.static_map);
        postImageLayout = findViewById(R.id.image_layout);
        backView = findViewById(R.id.back_arrow);
        confirmNotificationProgress = findViewById(R.id.confirm_notification_progress);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("object") != null){
            orderObject = (OrderObject) b.get("object");
            boolean delete = b.getBoolean("delete");

            posterNameView.setText(orderObject.owner.first_name + " " + orderObject.owner.last_name);
            usernameView.setText(orderObject.owner.username);
            plateNameView.setText(orderObject.post.plate_name);
            descriptionView.setText(orderObject.post.description);
            posterLocationView.setText(orderObject.post.address);
            priceView.setText(orderObject.post.price);
            timeView.setText(orderObject.post.time);

            Bundle bundle = new Bundle();
            bundle.putSerializable("type", orderObject.post.type);
            FoodTypeFragment fragment = new FoodTypeFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.types, fragment)
                    .commit();


            if(!orderObject.owner.profile_photo.contains("no-image")) Glide.with(this).load(orderObject.owner.profile_photo).into(dinnerImage);
            if(!orderObject.post.food_photo.contains("no-image")){
                postImageLayout.setVisibility(View.VISIBLE);
                Glide.with(this).load(orderObject.post.food_photo).into(postImage);
                postImageLayout.setOnClickListener((v) -> {
                    Intent imageLook = new Intent(this, ImageLookActivity.class);
                    imageLook.putExtra("image_url", orderObject.post.food_photo);
                    startActivity(imageLook);
                });
            }
            String url = "http://maps.google.com/maps/api/staticmap?center=" + orderObject.post.lat + "," + orderObject.post.lng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=" + getResources().getString(R.string.google_key);
            Glide.with(this).load(url).into(staticMapView);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_rating, RatingFragment.newInstance(USER.rating, USER.ratingN))
                    .commit();

            setConfirmCancelButton();
        }
        backView.setOnClickListener(v -> finish());
        dinnerImage.setOnClickListener(v -> {
            goToProfileView();
        });
    }

    void showProgress(boolean show){
        if (show){
            confirmNotificationProgress.setVisibility(View.VISIBLE);
            confirmCancelButton.setVisibility(View.GONE);
        } else {
            confirmNotificationProgress.setVisibility(View.GONE);
            confirmCancelButton.setVisibility(View.VISIBLE);
        }
    }

    void setConfirmCancelButton(){
        if (orderObject.status.equals("PENDING")){
            confirmCancelButton.setOnClickListener(v -> {
                showProgress(true);
                confirmOrder(orderObject, true, this);
                finish();
            });
        } else {
            confirmCancelButton.setVisibility(View.GONE);
            statucMessage.setVisibility(View.VISIBLE);
        }

        if (orderObject.status.equals("CONFIRMED")){
            statucMessage.setText("CONFIRMED");
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.confirm));
        } else if (orderObject.status.equals("CANCELED")){
            statucMessage.setText("CANCELED");
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.canceled));
        }
    }

    void confirmOrder(OrderObject order, boolean confirm, Context context){
        PostAsyncTask orderStatus = new PostAsyncTask(context.getString(R.string.server) + "/set_order_status/");
        order.status = confirm ? "CONFIRMED" : "CANCELED";
        try {
            orderStatus.execute(
                    new String[]{"order_id",  order.id + ""},
                    new String[]{"order_status", order.status}
            ).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showProgress(false);
        }
        WebSocketMessage.send(this,
                "/ws/orders/" + order.owner.id +  "/",
                "{\"order_id\": \"" + order.id + "\", \"seen_owner\": false}"
        );
    }

    void goToProfileView(){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("user", orderObject.owner);
        startActivity(k);
    }
}
