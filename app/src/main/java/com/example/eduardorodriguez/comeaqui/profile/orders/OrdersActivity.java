package com.example.eduardorodriguez.comeaqui.profile.orders;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.eduardorodriguez.comeaqui.R;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container1, new OrderFragment())
                .commit();
    }
}
