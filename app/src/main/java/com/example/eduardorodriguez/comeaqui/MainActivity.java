package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;

 import com.example.eduardorodriguez.comeaqui.login_and_register.LoginOrRegisterActivity;
import com.example.eduardorodriguez.comeaqui.map.NoLocationFragmentFragment;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.review.GuestsReviewActivity;
import com.example.eduardorodriguez.comeaqui.review.ReviewPostActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.chat.ChatActivity;
import com.example.eduardorodriguez.comeaqui.notification.NotificationsFragment;
import com.example.eduardorodriguez.comeaqui.order.OrderFragment;
import com.example.eduardorodriguez.comeaqui.map.MapFragment;
import com.example.eduardorodriguez.comeaqui.profile.ProfileFragment;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

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

    private FrameLayout mainFrame;

    private MapFragment mapFragment;
    private OrderFragment getPastOderFragment;
    private NotificationsFragment notificationFragment;
    private ProfileFragment profileFragment;

    private static Context context;
    TextView[] notArray;
    ImageView[] iv;
    Fragment[] fragments;
    int[] icons;

    @Override
    protected void onResume() {
        super.onResume();
        context = getApplicationContext();
        checkRatings();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatView = findViewById(R.id.chat);

        map = findViewById(R.id.map);
        orders = findViewById(R.id.order);
        notifications = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);

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

        SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false)) {
            USER = new User(new JsonParser().parse(pref.getString("user", "")).getAsJsonArray().get(0).getAsJsonObject());
        } else {
            Intent a = new Intent(this, LoginOrRegisterActivity.class);
            startActivity(a);
        }

        getPastOderFragment = new OrderFragment();
        mapFragment = new MapFragment();
        profileFragment = ProfileFragment.newInstance(USER.id);
        notificationFragment = new NotificationsFragment();

        chatView.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });

        if (checkLocationPermission()){
            setMapFragment(mapFragment);
        } else {
            setMapFragment(NoLocationFragmentFragment.newInstance());
        }
        map.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.foodfill));

        iv = new ImageView[]{map, orders, notifications, profile};
        fragments = new Fragment[]{new Fragment(), getPastOderFragment, notificationFragment, profileFragment};
        icons = new int[]{R.drawable.foodfill, R.drawable.orderfill, R.drawable.notificationfill, R.drawable.profilefill};

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("tab") != null) {
            String tab = b.getString("tab");
            openFragment(Integer.parseInt(tab));
        }

        navMap.setOnClickListener(v -> {
            mainFrame.setVisibility(View.INVISIBLE);
            openFragment(0);
        });
        navOrders.setOnClickListener(v -> {
            openFragment(1);
        });
         navNotifications.setOnClickListener(v -> {
             openFragment(2);
        });
        navProfile.setOnClickListener(v -> {
            openFragment(3);
        });
        listenToNotificationChanges();
    }

    private void openFragment(int i){
        initiateIcons(i);
        setFragment(fragments[i]);
        iv[i].setImageDrawable(ContextCompat.getDrawable(this, icons[i]));
    }

    void checkRatings(){
        checkUnratedOrdersAsDinner();
        checkUnratedOrdersAsPoster();
    }

    void checkUnratedOrdersAsPoster(){
        new GetAsyncTask(this,"GET", context.getResources().getString(R.string.server) + "/my_unreviewed_order_guest/"){
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
        new GetAsyncTask(this, "GET", context.getResources().getString(R.string.server) + "/my_unreviewed_order_post/"){
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
    public void listenToNotificationChanges(){
        try {
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/popups/" + USER.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Orders!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    runOnUiThread(() -> {
                        JsonObject message = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject();
                        try {
                            int notiNotSeen = message.get("notifications_not_seen").getAsInt();
                            if (notiNotSeen > 0) {
                                notNotifications.setVisibility(View.VISIBLE);
                                notNotifications.setText("" + notiNotSeen);
                            } else {
                                notNotifications.setVisibility(View.INVISIBLE);
                                notNotifications.setText("" + notiNotSeen);
                            }
                        } catch (Exception ignore) {}
                        try {
                            int unseenMessages = message.get("messages_not_seen").getAsInt();
                            if (unseenMessages > 0 ){
                                notChat.setVisibility(View.VISIBLE);
                                notChat.setText("" + unseenMessages);
                            } else {
                                notChat.setVisibility(View.INVISIBLE);
                                notChat.setText("" + unseenMessages);
                            }
                        } catch (Exception ignore) {}
                        try {
                            int ordersNotSeen = message.get("orders_not_seen").getAsInt();
                            if (ordersNotSeen > 0 ){
                                notOrders.setVisibility(View.VISIBLE);
                                notOrders.setText("" + ordersNotSeen);
                            } else {
                                notOrders.setVisibility(View.INVISIBLE);
                                notOrders.setText("" + ordersNotSeen);
                            }
                        } catch (Exception ignore) {}
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

    @Override
    public void finish() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    void showNoLocationNotification(){
        new AlertDialog.Builder(this)
                .setTitle("ComeAqui Location")
                .setMessage("We need your location to show you who is offering food and for them to see you")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                })
                .create()
                .show();
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showNoLocationNotification();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            Toast.makeText(this, "Not location access", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        setMapFragment(mapFragment);
                    }
                } else {
                    showNoLocationNotification();
                }
            }
        }
    }
}
