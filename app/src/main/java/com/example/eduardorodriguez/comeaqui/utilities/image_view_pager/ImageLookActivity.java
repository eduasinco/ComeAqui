package com.example.eduardorodriguez.comeaqui.utilities.image_view_pager;

import android.content.Intent;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;

import java.util.ArrayList;

public class ImageLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_look);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("image_urls") != null) {
            ArrayList<String> imageUrls = b.getStringArrayList("image_urls");
            int index = b.getInt("index", 0);

            ViewPager viewPager = findViewById(R.id.view_pager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, imageUrls);
            viewPager.setAdapter(adapter);
        }

        findViewById(R.id.close).setOnClickListener((v) -> finish());
    }
}
