package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.FirebaseUser;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
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
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.yalantis.ucrop.UCropFragment.TAG;

public class MainActivity extends AppCompatActivity {

    static public String data;
    private Toolbar toolbar;
    private ImageView chatView;

    private ImageView map;
    private ImageView orders;
    private ImageView notifications;
    private ImageView profile;

    private static TextView notMap;
    private static TextView notOrders;
    private static TextView notNotifications;
    private static TextView notProfile;
    private static TextView notChat;

    private OrderFragment getPastOderFragment;
    private MapFragment mapFragment;
    private ProfileFragment profileFragment;
    private NotificationsFragment notificationFragment;

    public static User user;
    public static FirebaseUser firebaseUser;

    private static Context context;

    int currentFrame, previousFrame = 0;
    TextView[] notArray;


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
        context = getApplicationContext();

        chatView = findViewById(R.id.chat);

        map = findViewById(R.id.map);
        orders = findViewById(R.id.order);
        notifications = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);

        notMap = findViewById(R.id.not_map);
        notOrders = findViewById(R.id.not_order);
        notNotifications = findViewById(R.id.not_not);
        notProfile = findViewById(R.id.not_profile);
        notChat = findViewById(R.id.notChat);
        notArray = new TextView[]{notMap, notOrders, notNotifications, notProfile};

        getPastOderFragment = new OrderFragment();
        mapFragment = new MapFragment();
        profileFragment = new ProfileFragment();
        notificationFragment = new NotificationsFragment();

        chatView.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });

        setFragment(mapFragment);
        map.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.foodfill));

        map.setOnClickListener(v -> {
            initiateIcons(0);
            setFragment(mapFragment);
            map.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.foodfill));
        });
        orders.setOnClickListener(v -> {
            initiateIcons(1);
            setFragment(getPastOderFragment);
            orders.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.orderfill));
        });
         notifications.setOnClickListener(v -> {
             initiateIcons(2);
             setFragment(notificationFragment);
             notifications.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.notificationfill));
        });
        profile.setOnClickListener(v -> {
            initiateIcons(3);
            setFragment(profileFragment);
            profile.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.profilefill));
        });

        initializeUser();
        getFirebaseToken();
        setNotificationsBubbles();
        listenToOrdersChanges();
        listenToNotificationsChanges();
        listenToChatMessages();
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

    private void getFirebaseToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    System.out.println("TOKEEEEEEEEN " + token);

                    postTokenToServer(token);
                });
    }
    private void postTokenToServer(String token){
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        PostAsyncTask postToken = new PostAsyncTask(getResources().getString(R.string.server) + "/fcm/v1/devices/");
        postToken.execute(
                new String[]{"dev_id", androidID},
                new String[]{"reg_id", token},
                new String[]{"name", "" + user.id}
        );
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }

    public static User initializeUser(){
        GetAsyncTask process = new GetAsyncTask("GET", context.getResources().getString(R.string.server) + "/my_profile/");
        try {
            String response = process.execute().get();
            if (response != null)
                user = new User(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // initializeFirebaseUser();
        return user;
    }

    public void listenToOrdersChanges(){
        try {
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/orders/" + user.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Orders!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    final String message = s;
                    runOnUiThread(() -> {
                        int ordersNotSeen = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("orders_not_seen").getAsInt();
                        if (ordersNotSeen > 0 ){
                            notOrders.setVisibility(View.VISIBLE);
                            notOrders.setText("" + ordersNotSeen);
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
    public void listenToNotificationsChanges(){
        try {
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/notifications/" + user.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Notifications!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    final String message = s;
                    runOnUiThread(() -> {
                        int ordersNotSeen = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("notifications_not_seen").getAsInt();
                        if (ordersNotSeen > 0 ){
                            notNotifications.setVisibility(View.VISIBLE);
                            notNotifications.setText("" + ordersNotSeen);
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
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/unread_messages/" + user.id +  "/");
            WebSocketClient mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Unread Messages!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    final String message = s;
                    runOnUiThread(() -> {
                        int ordersNotSeen = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject().get("unread_messages").getAsInt();
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
    }
    private void setUnseenOrders(){
        ArrayList<OrderObject> orderObjects = new ArrayList<>();
        GetAsyncTask getPostLocations = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/unseen_orders/");
        try {
            String response = getPostLocations.execute().get();
            if (response != null)
                for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                    orderObjects.add(new OrderObject(pa.getAsJsonObject()));
                }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (orderObjects.size() > 0){
            notOrders.setVisibility(View.VISIBLE);
            notOrders.setText("" + orderObjects.size());
        }
    }
    private void setUnseenNotifications(){
        ArrayList<NotificationObject> notificationObjects = new ArrayList<>();
        GetAsyncTask getPostLocations = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/unseen_notifications/");
        try {
            String response = getPostLocations.execute().get();
            if (response != null)
                for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                    notificationObjects.add(new NotificationObject(pa.getAsJsonObject()));
                }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (notificationObjects.size() > 0){
            notNotifications.setVisibility(View.VISIBLE);
            notNotifications.setText("" + notificationObjects.size());
        }
    }


    public void createBubble(int n, ConstraintLayout constraintView, View icon){
        TextView textView = new TextView(this);
        textView.setText(n + "");
        textView.setLayoutParams(new ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, dpToPx(18)));
        textView.setBackground(ContextCompat.getDrawable(this, R.drawable.box_notification));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(dpToPx(6),0, dpToPx(6), 0);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(10);
        textView.setId(View.generateViewId());
        constraintView.addView(textView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintView);
        constraintSet.connect(textView.getId(), ConstraintSet.LEFT, icon.getId(), ConstraintSet.LEFT, dpToPx(15));
        constraintSet.connect(textView.getId(), ConstraintSet.BOTTOM, icon.getId(), ConstraintSet.BOTTOM, dpToPx(15));
        constraintSet.applyTo(constraintView);
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    private void initializeFirebaseUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("email")
                .equalTo(MainActivity.user.email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseUser = dataSnapshot.getChildren().iterator().next().getValue(FirebaseUser.class);
                firebaseUser.id = dataSnapshot.getChildren().iterator().next().getKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
