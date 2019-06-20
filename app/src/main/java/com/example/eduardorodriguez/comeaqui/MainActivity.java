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
            setFragment(getFoodFragment);
            toolbar.setTitle("Get Food");
            menu.findItem(R.id.nav_getfood).setIcon(R.drawable.getfoodfill);
        }else if(b.getBoolean("map")){
            setFragment(mapFragment);
            toolbar.setTitle("Go");
            menu.findItem(R.id.nav_go).setIcon(R.drawable.preparefill);
        }else if(b.getBoolean("profile")){
            setFragment(profileFragment);
            toolbar.setTitle("Profile");
            menu.findItem(R.id.nav_profile).setIcon(R.drawable.profilefill);
        }
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Menu menu = mMainNav.getMenu();
                menu.findItem(R.id.nav_getfood).setIcon(R.drawable.getfood);
                menu.findItem(R.id.nav_go).setIcon(R.drawable.prepare);
                menu.findItem(R.id.nav_profile).setIcon(R.drawable.profile);
                switch (menuItem.getItemId()){
                    case R.id.nav_getfood:
                        setFragment(getFoodFragment);
                        menuItem.setIcon(R.drawable.getfoodfill);
                        return true;
                    case R.id.nav_go:
                        setFragment(mapFragment);
                        menuItem.setIcon(R.drawable.preparefill);
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
            user = new User(process.execute().get().get(0).getAsJsonObject());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
