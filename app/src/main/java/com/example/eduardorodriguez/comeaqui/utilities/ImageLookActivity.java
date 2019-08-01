package com.example.eduardorodriguez.comeaqui.utilities;

import android.content.Intent;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;

public class ImageLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_look);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("image_url") != null) {
            String imageUrl = b.getString("image_url");
            Glide.with(this).load(getResources().getString(R.string.server) + imageUrl).into(((ImageView) findViewById(R.id.image)));
        }

        findViewById(R.id.close).setOnClickListener((v) -> finish());
    }
}
