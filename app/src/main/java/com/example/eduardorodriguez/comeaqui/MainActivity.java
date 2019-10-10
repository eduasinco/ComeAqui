package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.FirebaseUser;
import com.example.eduardorodriguez.comeaqui.review.GuestsReviewActivity;
import com.example.eduardorodriguez.comeaqui.review.ReviewPostActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.example.eduardorodriguez.comeaqui.chat.ChatActivity;
import com.example.eduardorodriguez.comeaqui.notification.NotificationsFragment;
import com.example.eduardorodriguez.comeaqui.order.OrderFragment;
import com.example.eduardorodriguez.comeaqui.map.MapFragment;
import com.example.eduardorodriguez.comeaqui.profile.ProfileFragment;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class MainActivity extends AppCompatActivity {

    static public String data;
    private ImageView chatView;

    private ImageView map;
    private ImageView orders;
    private ImageView notifications;
    private ImageView profile;

    private TextView notMap;
    private TextView notOrders;
    private TextView notNotifications;
    private TextView notProfile;
    private TextView notChat;

    private ConstraintLayout navMap;
    private ConstraintLayout navOrders;
    private ConstraintLayout navNotifications;
    private ConstraintLayout navProfile;

    private FrameLayout mapFrame;
    private FrameLayout mainFrame;

    private MapFragment mapFragment;
    private OrderFragment getPastOderFragment;
    private NotificationsFragment notificationFragment;
    private ProfileFragment profileFragment;
    public static FirebaseUser firebaseUser;

    private static Context context;

    int currentFrame, previousFrame = 0;
    TextView[] notArray;

    @Override
    protected void onResume() {
        super.onResume();
        context = getApplicationContext();
        setNotificationsBubbles();
        checkRatings();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    0);
        }
        chatView = findViewById(R.id.chat);

        map = findViewById(R.id.map);
        orders = findViewById(R.id.order);
        notifications = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);

        mapFrame = findViewById(R.id.map_frame);
        mainFrame = findViewById(R.id.main_frame);

        navMap = findViewById(R.id.nav_map);
        navOrders = findViewById(R.id.nav_orders);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        notMap = findViewById(R.id.not_map);
        notOrders = findViewById(R.id.not_order);
        notNotifications = findViewById(R.id.not_not);
        notProfile = findViewById(R.id.not_profile);
        notChat = findViewById(R.id.notChat);
        notArray = new TextView[]{notMap, notOrders, notNotifications, notProfile};

        getPastOderFragment = new OrderFragment();
        mapFragment = new MapFragment();
        profileFragment = ProfileFragment.newInstance(USER);
        notificationFragment = new NotificationsFragment();

        chatView.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });

        setMapFragment(mapFragment);
        map.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.foodfill));

        navMap.setOnClickListener(v -> {
            initiateIcons(0);
            mainFrame.setVisibility(View.INVISIBLE);
            setFragment(new Fragment());
            map.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.foodfill));
        });
        navOrders.setOnClickListener(v -> {
            initiateIcons(1);
            setFragment(getPastOderFragment);
            orders.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.orderfill));
        });
         navNotifications.setOnClickListener(v -> {
             initiateIcons(2);
             setFragment(notificationFragment);
             notifications.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.notificationfill));
        });
        navProfile.setOnClickListener(v -> {
            initiateIcons(3);
            setFragment(profileFragment);
            profile.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.profilefill));
        });
        setNotificationsBubbles();
        listenToOrdersChanges();
        listenToChatMessages();
    }

    void checkRatings(){
        checkUnratedOrdersAsDinner();
        checkUnratedOrdersAsPoster();
    }

    void checkUnratedOrdersAsPoster(){
        new GetAsyncTask("GET", context.getResources().getString(R.string.server) + "/my_unreviewed_order_guest/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                    if (ja.size() > 0){
                        OrderObject orderObject = new OrderObject(ja.get(0).getAsJsonObject());
                        Intent k = new Intent(context, GuestsReviewActivity.class);
                        k.putExtra("order", orderObject);
                        startActivity(k);
                    }
                }
                super.onPostExecute(response);
            }
        }.execute();
    }

    void checkUnratedOrdersAsDinner(){
        new GetAsyncTask("GET", context.getResources().getString(R.string.server) + "/my_unreviewed_order_post/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                    if (ja.size() > 0){
                        OrderObject orderObject = new OrderObject(ja.get(0).getAsJsonObject());
                        Intent k = new Intent(context, ReviewPostActivity.class);
                        k.putExtra("order", orderObject);
                        startActivity(k);
                    }
                }
                super.onPostExecute(response);
            }
        }.execute();
    }


    private void initiateIcons(int cf){
        previousFrame = currentFrame;
        currentFrame = cf;
        notArray[previousFrame].setVisibility(View.INVISIBLE);
        map.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.food));
        orders.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.order));
        notifications.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.notification));
        profile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.profile));
    }


    private void setMapFragment(Fragment fragment) {
        mainFrame.setVisibility(View.INVISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.map_frame, fragment).commit();
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
        mainFrame.setVisibility(View.VISIBLE);
    }

    public void listenToOrdersChanges(){
        try {
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/orders/" + USER.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    //unOnUiThread(() -> Toast.makeText(getApplicationContext(), "Orders!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    final String message = s;
                    runOnUiThread(() -> {
                        int ordersNotSeen = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("orders_not_seen_owner").getAsInt();
                        if (ordersNotSeen > 0 ){
                            notOrders.setVisibility(View.VISIBLE);
                            notOrders.setText("" + ordersNotSeen);
                        }

                        int notificationsNotSeen = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("orders_not_seen_poster").getAsInt();
                        if (notificationsNotSeen > 0 ){
                            notNotifications.setVisibility(View.VISIBLE);
                            notNotifications.setText("" + notificationsNotSeen);
                        }
                    });
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }
                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public void listenToChatMessages(){
        try {
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/unread_messages/" + USER.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Unread Messages!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    final String message = s;
                    runOnUiThread(() -> {
                        JsonObject jsonMessage = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject();
                        int ordersNotSeen = jsonMessage.get("unread_messages").getAsInt();
                        if (ordersNotSeen > 0 ){
                            notChat.setVisibility(View.VISIBLE);
                            notChat.setText("" + ordersNotSeen);
                        }
                    });
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }
                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setNotificationsBubbles(){
        setUnseenOrders();
        setUnseenNotifications();
        setUnseenChatMessages();
    }
    private void setUnseenOrders(){
        ArrayList<OrderObject> orderObjects = new ArrayList<>();
        new GetAsyncTask("GET", getResources().getString(R.string.server) + "/unseen_orders/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                        orderObjects.add(new OrderObject(pa.getAsJsonObject()));
                    }
                }
                if (orderObjects.size() > 0){
                    notOrders.setVisibility(View.VISIBLE);
                    notOrders.setText("" + orderObjects.size());
                }
                super.onPostExecute(response);
            }
        }.execute();
    }
    private void setUnseenNotifications(){
        ArrayList<OrderObject> orderObjects = new ArrayList<>();
        new GetAsyncTask("GET", getResources().getString(R.string.server) + "/unseen_notifications/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                        orderObjects.add(new OrderObject(pa.getAsJsonObject()));
                    }
                }
                if (orderObjects.size() > 0){
                    notNotifications.setVisibility(View.VISIBLE);
                    notNotifications.setText("" + orderObjects.size());
                }
                super.onPostExecute(response);
            }
        }.execute();
    }
    private void setUnseenChatMessages(){
        new GetAsyncTask("GET", getResources().getString(R.string.server) + "/unseen_chat_messages/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    Integer count = new JsonParser().parse(response).getAsJsonObject().get("count").getAsInt();
                    if (count > 0){
                        notChat.setVisibility(View.VISIBLE);
                        notChat.setText("" + count);
                    } else {
                        notChat.setVisibility(View.GONE);
                    }
                }
                super.onPostExecute(response);
            }
        }.execute();
    }
}
