package com.example.eduardorodriguez.comeaqui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.eduardorodriguez.comeaqui.AutocompleteLocationFragment;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class AddFoodActivity extends AppCompatActivity {
    EditText foodName;
    TextView price;
    ImageView image;
    SeekBar seekbar;
    ConstraintLayout descriptionLayout;
    EditText description;
    Button submit;
    ImageView doPhoto;
    ImageView backView;
    TimePicker timePicker;


    Float price_data = 0f;
    boolean[] pressed = {false, false, false, false, false, false, false};
    Bitmap imageBitmap;
    int diners;
    double lat;
    double lng;
    String address;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView photo = findViewById(R.id.photo);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);

            ViewGroup.LayoutParams params = photo.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            photo.setLayoutParams(params);

        }
    }

    private String setTypes(){
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
        setContentView(R.layout.activity_add_food);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        foodName = findViewById(R.id.plateName);
        price = findViewById(R.id.priceText);
        image = findViewById(R.id.image);
        seekbar = findViewById(R.id.seekBar);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        description = findViewById(R.id.orderMessage);
        submit = findViewById(R.id.submitButton);
        timePicker = findViewById(R.id.timePicker);
        doPhoto = findViewById(R.id.photo);
        backView = findViewById(R.id.back);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            address = b.getString("address");
            lat = b.getDouble("lat");
            lng = b.getDouble("lng");

            Bundle bundle = new Bundle();
            bundle.putString("address", address);
            AutocompleteLocationFragment autocompleteLocationFragment = new AutocompleteLocationFragment();
            autocompleteLocationFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.locationAutocomplete, autocompleteLocationFragment)
                    .commit();
        }
        setFoodName();
        setPriceSeekBar();
        setFoodTypes();
        setDoPhoto();
        setDinerButtons();
        setSubmit();

        backView.setOnClickListener(v -> finish());
    }

    void setDinerButtons(){
        Button diner1 = findViewById(R.id.diner1);
        Button diner2 = findViewById(R.id.diner2);
        Button diner3 = findViewById(R.id.diner3);
        Button diner4 = findViewById(R.id.diner4);

        Button[] dinersViews = new Button[]{diner1, diner2, diner3, diner4};
        for (int i = 0; i < dinersViews.length; i++){
            int finalI = i;
            Button button = dinersViews[i];
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diners = finalI + 1;
                    button.setBackgroundColor(Color.TRANSPARENT);
                    for (Button button2: dinersViews){
                        if (button2 != button){
                            button2.setBackgroundColor(Color.WHITE);
                        }
                    }
                }
            });
        }
    }

    void setSubmit(){
        submit.setOnClickListener(v -> {
            PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/foods/");
            post.bitmap = imageBitmap;
            try {
                String response = post.execute(
                        new String[]{"plate_name", foodName.getText().toString()},
                        new String[]{"address", address},
                        new String[]{"lat", Double.toString(lat)},
                        new String[]{"lng", Double.toString(lng)},
                        new String[]{"diners", Integer.toString(diners)},
                        new String[]{"time", timePicker.getHour() + ":" + timePicker.getMinute()},
                        new String[]{"price", price_data.toString()},
                        new String[]{"food_type", setTypes()},
                        new String[]{"description", description.getText().toString()},
                        new String[]{"food_photo", ""}
                ).get();
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                if (response != null) {
                    // PastOderFragment.appendToList(response);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent k = new Intent(AddFoodActivity.this, MainActivity.class);
            startActivity(k);

        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void setFoodName(){
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
    }

    void setPriceSeekBar(){
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
    }

    @SuppressLint("ClickableViewAccessibility")
    void setDoPhoto(){
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
    }

    void setFoodTypes(){
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

