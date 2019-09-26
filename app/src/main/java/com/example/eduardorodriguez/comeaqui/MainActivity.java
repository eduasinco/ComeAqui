package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.yalantis.ucrop.UCropFragment.TAG;

public class MainActivity extends AppCompatActivity {

    static public String data;
    private LinearLayout mMainNav;
    private FrameLayout mMainFrame;
    private Toolbar toolbar;
    private ImageView chatView;

    private ImageView map;
    private ImageView orders;
    private ImageView notifications;
    private ImageView profile;

    private TextView notMap;
    private TextView notOrders;
    private TextView notNotifications;
    private TextView notProfile;

    private OrderFragment getPastOderFragment;
    private MapFragment mapFragment;
    private ProfileFragment profileFragment;
    private NotificationsFragment notificationFragment;

    public static User user;
    public static FirebaseUser firebaseUser;

    private static Context context;

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

        map = findViewById(R.id.map);
        orders = findViewById(R.id.order);
        notifications = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);

        notMap = findViewById(R.id.not_map);
        notOrders = findViewById(R.id.not_order);
        notNotifications = findViewById(R.id.not_not);
        notProfile = findViewById(R.id.not_profile);

        context = getApplicationContext();

        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);
        chatView = findViewById(R.id.chat);

        getPastOderFragment = new OrderFragment();
        mapFragment = new MapFragment();
        profileFragment = new ProfileFragment();
        notificationFragment = new NotificationsFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        chatView.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this, ChatActivity.class);
            startActivity(chatIntent);
        });

        setFragment(mapFragment);
        toolbar.setTitle(null);
        map.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.foodfill));

        map.setOnClickListener(v -> {
            initiateIcons();
            setFragment(mapFragment);
            map.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.foodfill));
        });
        orders.setOnClickListener(v -> {
            initiateIcons();
            setFragment(getPastOderFragment);
            orders.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.orderfill));
        });
         notifications.setOnClickListener(v -> {
             initiateIcons();
             setFragment(notificationFragment);
             notifications.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.notificationfill));
        });
        profile.setOnClickListener(v -> {
            initiateIcons();
            setFragment(profileFragment);
            profile.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.profilefill));
        });

        initializeUser();
        getFirebaseToken();
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


    private void initiateIcons(){
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
