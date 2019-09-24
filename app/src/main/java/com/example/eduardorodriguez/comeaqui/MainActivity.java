package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.eduardorodriguez.comeaqui.objects.firebase_objects.FirebaseUser;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.eduardorodriguez.comeaqui.chat.ChatActivity;
import com.example.eduardorodriguez.comeaqui.notification.NotificationsFragment;
import com.example.eduardorodriguez.comeaqui.order.OrderFragment;
import com.example.eduardorodriguez.comeaqui.map.MapFragment;
import com.example.eduardorodriguez.comeaqui.profile.ProfileFragment;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

import static com.yalantis.ucrop.UCropFragment.TAG;

public class MainActivity extends AppCompatActivity {

    static public String data;
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Toolbar toolbar;
    private ImageView chatView;

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

        // load the store fragment by default

        Menu menu = mMainNav.getMenu();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b == null){
            setFragment(mapFragment);
            toolbar.setTitle(null);
            menu.findItem(R.id.nav_map).setIcon(R.drawable.foodfill);
        } else {
            switch (b.getString("goTo")){
                case "notifications":
                    setFragment(notificationFragment);
                    toolbar.setTitle(null);
                    menu.findItem(R.id.nav_notifications).setIcon(R.drawable.notificationfill);
                    break;
                case "orders":
                    setFragment(getPastOderFragment);
                    toolbar.setTitle(null);
                    menu.findItem(R.id.nav_orders).setIcon(R.drawable.orderfill);
                    break;
                case "profile":
                    setFragment(profileFragment);
                    toolbar.setTitle(null);
                    menu.findItem(R.id.nav_profile).setIcon(R.drawable.profilefill);
                    break;
            }
        }
        mMainNav.setOnNavigationItemSelectedListener(menuItem -> {
            Menu menu1 = mMainNav.getMenu();
            menu1.findItem(R.id.nav_map).setIcon(R.drawable.food);
            menu1.findItem(R.id.nav_notifications).setIcon(R.drawable.notification);
            menu1.findItem(R.id.nav_orders).setIcon(R.drawable.order);
            menu1.findItem(R.id.nav_profile).setIcon(R.drawable.profile);
            switch (menuItem.getItemId()){
                case R.id.nav_map:
                    setFragment(mapFragment);
                    menuItem.setIcon(R.drawable.foodfill);
                    return true;
                case R.id.nav_notifications:
                    setFragment(notificationFragment);
                    menuItem.setIcon(R.drawable.notificationfill);
                    return true;
                case R.id.nav_orders:
                    setFragment(getPastOderFragment);
                    menuItem.setIcon(R.drawable.orderfill);
                    return true;
                case R.id.nav_profile:
                    setFragment(profileFragment);
                    menuItem.setIcon(R.drawable.profilefill);
                    return true;
                    default:
                        return false;
            }

        });
        initializeUser();
        getFirebaseToken();
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
                    System.out.println("TOKEEEEEEEEN " + token.toString());

                    postTokenToServer(token.toString());
                    // Log and toast
//                    String msg = getString(R.string.msg_token_fmt, token);
//                    Log.d(TAG, msg);
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });
    }

    private void postTokenToServer(String token){
//        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//        String imei = TelephonyMgr.getDeviceId();

        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        PostAsyncTask postToken = new PostAsyncTask(getResources().getString(R.string.server) + "/fcm/v1/devices/");
        postToken.execute(
                new String[]{"dev_id", androidID},
                new String[]{"reg_id", token},
                new String[]{"name", "Edu's device"}
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
