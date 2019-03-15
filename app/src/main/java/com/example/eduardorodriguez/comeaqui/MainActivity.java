package com.example.eduardorodriguez.comeaqui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    static public String data;
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Toolbar toolbar;

    private GetFoodFragment getFoodFragment;
    private GoEatFragment goEatFragment;
    private ProfileFragment profileFragment;

    public static void printData(){
        System.out.print(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);

        getFoodFragment = new GetFoodFragment();
        goEatFragment = new GoEatFragment();
        profileFragment = new ProfileFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // load the store fragment by default
        toolbar.setTitle("Get Food");

        Menu menu = mMainNav.getMenu();
        menu.findItem(R.id.nav_getfood).setIcon(R.drawable.goeatfill);
        setFragment(getFoodFragment);
        GetFoodAsyncTask process = new GetFoodAsyncTask();
        process.execute();
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Menu menu = mMainNav.getMenu();
                menu.findItem(R.id.nav_getfood).setIcon(R.drawable.goeat);
                menu.findItem(R.id.nav_go).setIcon(R.drawable.prepare);
                menu.findItem(R.id.nav_profile).setIcon(R.drawable.profile);
                switch (menuItem.getItemId()){
                    case R.id.nav_getfood:
                        setFragment(getFoodFragment);
                        GetFoodAsyncTask process = new GetFoodAsyncTask();
                        process.execute();
                        toolbar.setTitle("Get");
                        menuItem.setIcon(R.drawable.goeatfill);
                        return true;
                    case R.id.nav_go:
                        setFragment(goEatFragment);
                        toolbar.setTitle("Go");
                        menuItem.setIcon(R.drawable.preparefill);
                        return true;
                    case R.id.nav_profile:
                        GetFoodAsyncTask process2 = new GetFoodAsyncTask();
                        process2.execute();
                        setFragment(profileFragment);
                        toolbar.setTitle("Profile");
                        menuItem.setIcon(R.drawable.profilefill);
                        return true;
                        default:
                            return false;
                }

            }
        });
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
