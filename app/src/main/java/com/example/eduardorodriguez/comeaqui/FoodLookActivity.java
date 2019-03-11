package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class FoodLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_look);

        ImageView image = findViewById(R.id.foodLookImage);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            String path = b.getString("SRC");
            Glide.with(this).load(path).into(image);
        }
    }
}
