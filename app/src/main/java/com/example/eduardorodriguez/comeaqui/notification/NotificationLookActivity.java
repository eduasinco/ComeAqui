package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.example.eduardorodriguez.comeaqui.utilities.HorizontalFoodPostImageDisplayFragment;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;
import com.example.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    ImageView dinnerImage;
    ImageView staticMapView;
    ImageView backView;
    View confirmNotificationProgress;
    FrameLayout waitingFrame;

    OrderObject orderObject;


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

        dinnerImage = findViewById(R.id.dinner_image);
        staticMapView = findViewById(R.id.static_map);
        backView = findViewById(R.id.back_arrow);
        confirmNotificationProgress = findViewById(R.id.confirm_notification_progress);
        waitingFrame = findViewById(R.id.waiting_frame);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("orderId") != null){
            int orderId = b.getInt("orderId");
            getOrderObject(orderId);
        }
        backView.setOnClickListener(v -> finish());
        dinnerImage.setOnClickListener(v -> {
            goToProfileView();
        });
    }

    void getOrderObject(int orderId){
        try {
            new GetAsyncTask("GET", context.getResources().getString(R.string.server) + "/order_detail/" + orderId + "/"){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null){
                        orderObject = new OrderObject(new JsonParser().parse(response).getAsJsonObject());
                        startWaitingFrame(false);
                        setDetails();
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.wait_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitingFrame.setVisibility(View.GONE);
        }
    }

    void setDetails(){
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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalFoodPostImageDisplayFragment.newInstance(orderObject.post.id, "big"))
                .commit();
        String url = "http://maps.google.com/maps/api/staticmap?center=" + orderObject.post.lat + "," + orderObject.post.lng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=" + getResources().getString(R.string.google_key);
        Glide.with(this).load(url).into(staticMapView);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_rating, RatingFragment.newInstance(orderObject.owner.rating, orderObject.owner.ratingN))
                .commit();

        setConfirmCancelButton();
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
        } else if (orderObject.status.equals("FINISHED")){
            statucMessage.setText("FINISHED");
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
        k.putExtra("userId", orderObject.owner.id);
        startActivity(k);
    }
}
