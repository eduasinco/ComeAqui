package com.example.eduardorodriguez.comeaqui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.io.InputStream;

public class AddGoFoodActivity extends AppCompatActivity {

    EditText foodName;
    TextView price;
    ImageView image;
    SeekBar seekbar;
    ConstraintLayout descriptionLayout;
    EditText description;
    TimePicker timePicker;
    Button submit;

    String plateName;
    Float price_data;
    boolean[] pressed = {false, false, false, false, false, false, false};
    String description_data;
    Bitmap imageBitmap;
    InputStream is;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imagePhoto = findViewById(R.id.imagePhoto);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imagePhoto.setImageBitmap(imageBitmap);
        }
    }

    public String setTypes(){
        StringBuilder types = new StringBuilder();
        for (boolean p: pressed){
            if (p) {
                types.append(1);
            }else{
                types.append(0);
            }
        }
        return types.toString();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_go_food);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        foodName = findViewById(R.id.plateName);
        price = findViewById(R.id.priceText);
        image = findViewById(R.id.image);
        seekbar = findViewById(R.id.seekBar);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        description = findViewById(R.id.orderMessage);
        timePicker = findViewById(R.id.simpleTimePicker);
        submit = findViewById(R.id.submitButton);

        submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                Bundle b = intent.getExtras();

                PostAsyncTask post = new PostAsyncTask("http://127.0.0.1:8000/foods/");
                post.bitmap = imageBitmap;
                String time = timePicker.getHour() + ":" + timePicker.getMinute();
                post.execute(
                        new String[]{"plate_name", foodName.getText().toString()},
                        new String[]{"price", price_data.toString()},
                        new String[]{"food_type", setTypes()},
                        new String[]{"description", description.getText().toString()},
                        new String[]{"is_go_food", "true"},
                        new String[]{"go_food_time", time},
                        new String[]{"food_photo", "", "img"}
                );
                try {
                    Intent k = new Intent(AddGoFoodActivity.this, MainActivity.class);
                    startActivity(k);
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        });

        initialAnimations();
        foodName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                foodName.setHint("");
                return false;
            }
        });

        foodName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    foodName.setHint("What are you making?");
                }
            }
        });

        seekbar.setMax(1000);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                price_data = (float) progress/100;
                String priceText = String.format("%.02f", price_data) + "€";
                price.setText(priceText);
            }
        });

        ImageView doPhoto = (ImageView) findViewById(R.id.photo);
        doPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                return false;
            }
        });
        final ImageView vegetarian = (ImageView) findViewById(R.id.vegetarian);
        vegetarian.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!pressed[4] && !pressed[5]){
                    pressed[0] = !pressed[0];
                }
                if(pressed[0])
                    vegetarian.setImageResource(R.drawable.vegetarianfill);
                else
                    vegetarian.setImageResource(R.drawable.vegetarian);
            }
        });
        final ImageView vegan = (ImageView) findViewById(R.id.vegan);
        vegan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!pressed[4] && !pressed[5] && !pressed[6]){
                    pressed[1] = !pressed[1];
                }
                if(pressed[1])
                    vegan.setImageResource(R.drawable.veganfill);
                else
                    vegan.setImageResource(R.drawable.vegan);
            }
        });
        final ImageView cereal = (ImageView) findViewById(R.id.cereal);
        cereal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressed[2] = !pressed[2];
                if(pressed[2])
                    cereal.setImageResource(R.drawable.cerealfill);
                else
                    cereal.setImageResource(R.drawable.cereal);
            }
        });
        final ImageView spicy = (ImageView) findViewById(R.id.spicy);
        spicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressed[3] = !pressed[3];
                if(pressed[3])
                    spicy.setImageResource(R.drawable.spicyfill);
                else
                    spicy.setImageResource(R.drawable.spicy);
            }
        });

        final ImageView fish = (ImageView) findViewById(R.id.fish);
        fish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!pressed[0] && !pressed[1]){
                    pressed[4] = !pressed[4];
                }
                if(pressed[4])
                    fish.setImageResource(R.drawable.fishfill);
                else
                    fish.setImageResource(R.drawable.fish);
            }
        });
        final ImageView meat = (ImageView) findViewById(R.id.meat);
        meat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!pressed[0] && !pressed[1]){
                    pressed[5] = !pressed[5];
                }
                if(pressed[5])
                    meat.setImageResource(R.drawable.meatfill);
                else
                    meat.setImageResource(R.drawable.meat);
            }
        });
        final ImageView dairy = (ImageView) findViewById(R.id.dairy);
        dairy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!pressed[1]){
                    pressed[6] = !pressed[6];
                }
                if(pressed[6])
                    dairy.setImageResource(R.drawable.dairyfill);
                else
                    dairy.setImageResource(R.drawable.dairy);
            }
        });

    }

    private void initialAnimations(){
        final int time = 100;
        foodName.setTranslationY(-transfromToDP(50f));
        seekbar.setTranslationY(transfromToDP(500f));
        descriptionLayout.setTranslationY(transfromToDP(500f));
        description.setTranslationY(transfromToDP(500f));
        foodName.animate()
                .translationYBy(transfromToDP(50f))
                .setDuration(time*5)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        seekbar.animate()
                                .translationYBy(-transfromToDP(500f))
                                .setDuration(150)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        descriptionLayout.animate()
                                                .translationYBy(-transfromToDP(500f))
                                                .setDuration(200);
                                        description.animate()
                                                .translationYBy(-transfromToDP(500f))
                                                .setDuration(300)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                    }
                                                })
                                                .start();
                                    }
                                })
                                .start();

                    }
                })
                .start();


    }

    public float transfromToDP(Float dip){
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return px;
    }
}
