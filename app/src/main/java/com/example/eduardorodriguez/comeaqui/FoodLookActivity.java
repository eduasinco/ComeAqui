package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FoodLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_look);

        ImageView image = (ImageView) findViewById(R.id.foodLookImage);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            image.setImageResource(b.getInt("IMG"));
        }
    }
}
