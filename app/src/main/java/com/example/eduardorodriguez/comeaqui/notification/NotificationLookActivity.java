package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.google.gson.JsonObject;

public class NotificationLookActivity extends AppCompatActivity {

    static Context context;

    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView timeView;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    Button confirmCancelButton;

    ImageView postImage;
    ImageView dinnerImage;
    ImageView staticMapView;
    ImageView backView;
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
        confirmCancelButton = findViewById(R.id.placeOrderButton);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.dinner_name);
        posterLocationView = findViewById(R.id.posterLocation);

        postImage = findViewById(R.id.post_image);
        dinnerImage = findViewById(R.id.dinner_image);
        staticMapView = findViewById(R.id.static_map);
        postImageLayout = findViewById(R.id.post_image_layout);
        backView = findViewById(R.id.back_arrow);

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

            setConfirmCancelButton();

            confirmCancelButton.setOnClickListener(v -> {
                if (notificationObject.order.status.equals("CANCELED")){
                    NotificationsFragment.confirmOrder(notificationObject.order, true, this);
                } else if (notificationObject.order.status.equals("CONFIRMED")) {
                    NotificationsFragment.confirmOrder(notificationObject.order,false, this);
                }
                finish();
            });
        }
        backView.setOnClickListener(v -> finish());
        dinnerImage.setOnClickListener(v -> {
            goToProfileView();
        });
    }

    void goToProfileView(){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("user_email", notificationObject.sender);
        startActivity(k);
    }

    void setConfirmCancelButton(){
        if (notificationObject.order.status.equals("CANCELED")){
            confirmCancelButton.setBackgroundColor(getResources().getColor(R.color.success));
        } else if (notificationObject.order.status.equals("CONFIRMED")) {
            confirmCancelButton.setBackgroundColor(getResources().getColor(R.color.canceled));
            confirmCancelButton.setText("CANCEL");
        }
    }
}
