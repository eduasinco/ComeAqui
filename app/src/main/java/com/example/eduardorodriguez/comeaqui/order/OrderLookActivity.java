package com.example.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.WebSocketMessage;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.ContinueCancelFragment;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;
import com.example.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class OrderLookActivity extends AppCompatActivity implements ContinueCancelFragment.OnFragmentInteractionListener {

    TextView postNameView;
    TextView plateName;
    TextView price;
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
    Button cancelOrderButton;
    FrameLayout cancelMessage;

    CardView imageCard;

    OrderObject order;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_look);
        context = getApplicationContext();
        plateName = findViewById(R.id.plate_name);
        posterNameView = findViewById(R.id.poster_name);
        posterDescription = findViewById(R.id.description);
        posterLocationView = findViewById(R.id.posterLocation);
        price = findViewById(R.id.price);
        subtotalView = findViewById(R.id.postSubtotalPrice);
        totalPriceView = findViewById(R.id.totalPrice);
        mealTimeView = findViewById(R.id.time);
        orderStatus = findViewById(R.id.order_status);

        posterImageView = findViewById(R.id.poster_image);
        postImageView = findViewById(R.id.image_layout);
        imageCard = findViewById(R.id.image_card);
        staticMapView = findViewById(R.id.static_map);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        cancelMessage = findViewById(R.id.cancel_message);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null){
            order = (OrderObject) b.get("object");
            GetAsyncTask getOrders = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/order_detail/" + order.id + "/");
            try {
                String response = getOrders.execute().get();
                if (response != null)
                createStringArray(new JsonParser().parse(response).getAsJsonObject());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            setCancelOrderButton();
        }


    }

    void setCancelOrderButton(){
        if (order.status.equals("CANCELED")){
            cancelOrderButton.setText("CANCELED");
            cancelOrderButton.setBackgroundColor(Color.WHITE);
            cancelOrderButton.setTextColor(ContextCompat.getColor(this, R.color.canceled));
        } else {
            cancelOrderButton.setOnClickListener(v -> {checkIfUserWantsToCancel();});
        }
    }

    void goToProfileView(User user){
        Intent k = new Intent(getApplicationContext(), ProfileViewActivity.class);
        k.putExtra("user_email", user);
        startActivity(k);
    }

    void checkIfUserWantsToCancel(){
        cancelMessage.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cancel_message, ContinueCancelFragment.newInstance(
                        "Your are canceling the order",
                        "Should you cancel after confirmation you would still owe the full fee"))
                .commit();
    }

    void cancelOrder(){
        PostAsyncTask orderStatus = new PostAsyncTask(context.getString(R.string.server) + "/set_order_status/");
        order.status = "CANCELED";
        try {
            orderStatus.execute(
                    new String[]{"order_id",  order.id + ""},
                    new String[]{"order_status", order.status}
            ).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        WebSocketMessage.send(this,
                "/ws/orders/" + order.poster.id +  "/",
                "{\"order_id\": \"" + order.id + "\", \"seen_poster\": false}"
        );
        finish();
    }

    void createStringArray(JsonObject jo){
        order = new OrderObject(jo);
        plateName.setText(order.post.plate_name);
        posterNameView.setText(order.poster.first_name + " " + order.poster.last_name);
        posterDescription.setText(order.post.description);
        posterLocationView.setText(order.post.address);
        price.setText("€" + order.post.price);
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
            posterImageView.setOnClickListener(v -> goToProfileView(order.poster));
        }
        if(!order.post.food_photo.contains("no-image")){
            imageCard.setVisibility(View.VISIBLE);
            Glide.with(context).load(order.post.food_photo).into(postImageView);
            imageCard.setOnClickListener((v) -> {
                Intent imageLook = new Intent(this, ImageLookActivity.class);
                imageLook.putExtra("image_url", order.post.food_photo);
                startActivity(imageLook);
            });
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_rating, RatingFragment.newInstance(MainActivity.user.rating, MainActivity.user.ratingN))
                .commit();
    }

    @Override
    public void onFragmentInteraction(boolean ok) {
        if (ok){
            cancelOrder();
        }
        cancelMessage.setVisibility(View.GONE);
    }
}
