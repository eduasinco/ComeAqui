package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.MessageObject;
import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.NotificationFirebase;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okio.ByteString;

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
    ImageView backView;
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
        backView = findViewById(R.id.back_arrow);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("object") != null){
            foodPost = (FoodPost) b.get("object");
            boolean delete = b.getBoolean("delete");

            posterNameView.setText(foodPost.owner.first_name + " " + foodPost.owner.last_name);
            usernameView.setText(foodPost.owner.email);
            plateNameView.setText(foodPost.plate_name);
            descriptionView.setText(foodPost.description);
            posterLocationView.setText(foodPost.address);
            priceView.setText(foodPost.price);
            timeView.setText(foodPost.time);

            Bundle bundle = new Bundle();
            bundle.putSerializable("type", foodPost.type);
            FoodTypeFragment fragment = new FoodTypeFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.types, fragment)
                    .commit();


            if(!foodPost.owner.profile_photo.contains("no-image")) Glide.with(this).load(foodPost.owner.profile_photo).into(posterImage);
            if(!foodPost.food_photo.contains("no-image")){
                postImageLayout.setVisibility(View.VISIBLE);
                Glide.with(this).load(foodPost.food_photo).into(postImage);
            }
            String url = "http://maps.google.com/maps/api/staticmap?center=" + foodPost.lat + "," + foodPost.lng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=" + getResources().getString(R.string.google_key);
            Glide.with(this).load(url).into(staticMapView);


            setPlaceButton(delete);
        }

        backView.setOnClickListener(v -> finish());
    }

    void setPlaceButton(boolean delete){
        if (delete){
            placeOrderButton.setText("Delete Post");
            placeOrderButton.setBackgroundColor(Color.parseColor("#FFFF0E01"));
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
                    JsonObject jo = new JsonParser().parse(response).getAsJsonObject().get("notification").getAsJsonObject();
                    NotificationObject notificationObject = new NotificationObject(jo);
                    FoodLookActivity.goToOrder(notificationObject.order);

                    WebSocketMessage.send(this,
                        "/ws/notifications/" + foodPost.owner.id +  "/",
                        "{\"notification_id\": \"" + notificationObject.id + "\", \"seen\": false}"
                    );
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            });
        }
    }

    private void createNotificationFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userNotifications");
        NotificationFirebase notification = new NotificationFirebase();
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        public FoodLookActivity activity;
        public EchoWebSocketListener(FoodLookActivity activity) {
            this.activity = activity;
        }
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // activity.runOnUiThread(() -> Toast.makeText(activity, "Connection Established!", Toast.LENGTH_LONG).show());
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output(text);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output(bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }

    private void start() {
        Request request = new Request.Builder().url(getResources().getString(R.string.server) + "/ws/notification/" + foodPost.owner.id +  "/")
                .build();
        EchoWebSocketListener listener = new EchoWebSocketListener(this);
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    void output(final String txt) {
        runOnUiThread(() -> {
            MessageObject brandNewMessage = new MessageObject(new JsonParser().parse(txt).getAsJsonObject().get("message").getAsJsonObject());
        });
    }
}
