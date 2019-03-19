package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import java.util.ArrayList;

public class FoodLookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_look);

        ImageView image = findViewById(R.id.foodLookImage);
        TextView plateNameView = findViewById(R.id.name);
        TextView descriptionView = findViewById(R.id.descriptionId);
        Button addButtonView = findViewById(R.id.addButton);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null){
            final String id = b.getString("id");
            String path = b.getString("src");
            String name = b.getString("name");
            String description = b.getString("des");
            String types = b.getString("types");
            boolean delete = b.getBoolean("delete");
            plateNameView.setText(name);
            descriptionView.setText(description);
            Glide.with(this).load(path).into(image);

            ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
            imageViewArrayList.add((ImageView) findViewById(R.id.vegetarian));
            imageViewArrayList.add((ImageView) findViewById(R.id.vegan));
            imageViewArrayList.add((ImageView) findViewById(R.id.celiac));
            imageViewArrayList.add((ImageView) findViewById(R.id.spicy));
            imageViewArrayList.add((ImageView) findViewById(R.id.fish));
            imageViewArrayList.add((ImageView) findViewById(R.id.meat));
            imageViewArrayList.add((ImageView) findViewById(R.id.dairy));
            int[] resources = new int[]{
                    R.drawable.vegetarianfill,
                    R.drawable.veganfill,
                    R.drawable.cerealfill,
                    R.drawable.spicyfill,
                    R.drawable.fishfill,
                    R.drawable.meatfill,
                    R.drawable.dairyfill,
            };

            for (int i = 0; i < types.length(); i++){
                if (types.charAt(i) == '1'){
                    imageViewArrayList.get(i).setImageResource(resources[i]);
                }
            }
            if (delete){
                addButtonView.setText("Delete Post");
                addButtonView.setBackgroundColor(Color.parseColor("#FFFF0E01"));
                addButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteAsyncTask deleteFoodPost = new DeleteAsyncTask(id);
                        deleteFoodPost.execute();
                        Intent k = new Intent(FoodLookActivity.this, MainActivity.class);
                        k.putExtra("s", " ");
                        startActivity(k);
                    }
                });
            }else{
                addButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostAsyncTask emiMessage = new PostAsyncTask("http://127.0.0.1:8000/messages/");
                        emiMessage.execute(
                                new String[]{"sender_email", LoginActivity.email, ""}
                        );
                        Intent k = new Intent(FoodLookActivity.this, MainActivity.class);
                        startActivity(k);
                    }
                });
            }
        }
    }
}
