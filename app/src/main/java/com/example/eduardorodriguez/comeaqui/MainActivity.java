package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.example.eduardorodriguez.comeaqui.food.GetFoodFragment;
import com.example.eduardorodriguez.comeaqui.eat.MapFragment;
import com.example.eduardorodriguez.comeaqui.profile.ProfileFragment;
import com.example.eduardorodriguez.comeaqui.profile.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    static public String data;
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Toolbar toolbar;

    private GetFoodFragment getFoodFragment;
    private MapFragment mapFragment;
    private ProfileFragment profileFragment;

    public static User user;

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

        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);

        getFoodFragment = new GetFoodFragment();
        mapFragment = new MapFragment();
        profileFragment = new ProfileFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // load the store fragment by default

        Menu menu = mMainNav.getMenu();
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b == null){
            setFragment(mapFragment);
            toolbar.setTitle(null);
            menu.findItem(R.id.nav_eat).setIcon(R.drawable.eatfill);
        }
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Menu menu = mMainNav.getMenu();
                menu.findItem(R.id.nav_food).setIcon(R.drawable.food);
                menu.findItem(R.id.nav_eat).setIcon(R.drawable.eat);
                menu.findItem(R.id.nav_profile).setIcon(R.drawable.profile);
                switch (menuItem.getItemId()){
                    case R.id.nav_eat:
                        setFragment(mapFragment);
                        menuItem.setIcon(R.drawable.eatfill);
                        return true;
                    case R.id.nav_food:
                        setFragment(getFoodFragment);
                        menuItem.setIcon(R.drawable.foodfill);
                        return true;
                    case R.id.nav_profile:
                        setFragment(profileFragment);
                        menuItem.setIcon(R.drawable.profilefill);
                        return true;
                        default:
                            return false;
                }

            }
        });
        initializeUser();
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    private void initializeUser(){
        GetAsyncTask process = new GetAsyncTask("my_profile/");
        try {
            String response = process.execute().get();
            if (response != null)
                user = new User(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
