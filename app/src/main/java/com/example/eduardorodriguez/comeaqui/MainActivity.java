package com.example.eduardorodriguez.comeaqui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private Toolbar toolbar;

    private GetFoodFragment getFoodFragment;
    private GoEatFragment goEatFragment;
    private PrepareFragment prepareFragment;
    private CashFragment cashFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        getFoodFragment = new GetFoodFragment();
        goEatFragment = new GoEatFragment();
        prepareFragment = new PrepareFragment();
        cashFragment = new CashFragment();
        profileFragment = new ProfileFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // load the store fragment by default
        toolbar.setTitle("Get Food");

        Menu menu = mMainNav.getMenu();
        menu.findItem(R.id.nav_getfood).setIcon(R.drawable.getfoodfill);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Menu menu = mMainNav.getMenu();
                menu.findItem(R.id.nav_getfood).setIcon(R.drawable.getfood);
                menu.findItem(R.id.nav_goeat).setIcon(R.drawable.goeat);
                menu.findItem(R.id.nav_prepare).setIcon(R.drawable.prepare);
                menu.findItem(R.id.nav_cash).setIcon(R.drawable.receipt);
                menu.findItem(R.id.nav_profile).setIcon(R.drawable.profile);

                switch (menuItem.getItemId()){
                    case R.id.nav_getfood:
                        setFragment(getFoodFragment);
                        toolbar.setTitle("Get Food");
                        menuItem.setIcon(R.drawable.getfoodfill);
                        return true;
                    case R.id.nav_goeat:
                        setFragment(goEatFragment);
                        toolbar.setTitle("Go Eat");
                        menuItem.setIcon(R.drawable.goeatfill);
                        return true;
                    case R.id.nav_prepare:
                        setFragment(prepareFragment);
                        toolbar.setTitle("Do");
                        menuItem.setIcon(R.drawable.preparefill);
                        return true;
                    case R.id.nav_cash:
                        setFragment(cashFragment);
                        toolbar.setTitle("Cash");
                        menuItem.setIcon(R.drawable.receiptfill);
                        return true;
                    case R.id.nav_profile:
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
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
