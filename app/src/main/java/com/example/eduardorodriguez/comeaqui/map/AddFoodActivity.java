package com.example.eduardorodriguez.comeaqui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.utilities.AutocompleteLocationFragment;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;

import java.io.IOException;

public class AddFoodActivity extends AppCompatActivity implements SelectImageFromFragment.OnFragmentInteractionListener{
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
    FrameLayout selectFromLayout;


    Float price_data = 0f;
    boolean[] pressed = {false, false, false, false, false, false, false};
    Bitmap imageBitmap;
    int diners;
    double lat;
    double lng;
    String address;

    Context context;

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

    @Override
    protected void onResume() {
        super.onResume();
        selectFromLayout.setVisibility(View.GONE);
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
        description = findViewById(R.id.bioText);
        submit = findViewById(R.id.submitButton);
        timePicker = findViewById(R.id.timePicker);
        doPhoto = findViewById(R.id.photo);
        backView = findViewById(R.id.back_arrow);
        selectFromLayout = findViewById(R.id.select_image_from);

        context = getApplicationContext();


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

        doPhoto.setOnClickListener((v) -> {
            selectFromLayout.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_image_from, SelectImageFromFragment.newInstance(false))
                    .commit();
        });


        setFoodName();
        setPriceSeekBar();
        setFoodTypes();
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
            button.setOnClickListener(v -> {
                diners = finalI + 1;
                button.setBackgroundColor(Color.TRANSPARENT);
                for (Button button2: dinersViews){
                    if (button2 != button){
                        button2.setBackgroundColor(Color.WHITE);
                    }
                }
            });
        }
    }

    void setSubmit(){
        submit.setOnClickListener(v -> {
            PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/foods/");
            post.bitmap = imageBitmap;

            post.execute(
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
            );
            finish();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void setFoodName(){
        foodName.setOnTouchListener((v, event) -> {
            foodName.setHint("");
            return false;
        });

        foodName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                foodName.setHint("What are you making?");
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


    void setFoodTypes(){
        final ImageView vegetarian = findViewById(R.id.vegetarian);
        vegetarian.setOnClickListener(v -> {
            if(!pressed[4] && !pressed[5]){
                pressed[0] = !pressed[0];
            }
            if(pressed[0])
                vegetarian.setImageResource(R.drawable.vegetarianfill);
            else
                vegetarian.setImageResource(R.drawable.vegetarian);
        });
        final ImageView vegan = findViewById(R.id.vegan);
        vegan.setOnClickListener(v -> {
            if(!pressed[4] && !pressed[5] && !pressed[6]){
                pressed[1] = !pressed[1];
            }
            if(pressed[1])
                vegan.setImageResource(R.drawable.veganfill);
            else
                vegan.setImageResource(R.drawable.vegan);
        });
        final ImageView cereal = findViewById(R.id.cereal);
        cereal.setOnClickListener(v -> {
            pressed[2] = !pressed[2];
            if(pressed[2])
                cereal.setImageResource(R.drawable.cerealfill);
            else
                cereal.setImageResource(R.drawable.cereal);
        });
        final ImageView spicy = findViewById(R.id.spicy);
        spicy.setOnClickListener(v -> {
            pressed[3] = !pressed[3];
            if(pressed[3])
                spicy.setImageResource(R.drawable.spicyfill);
            else
                spicy.setImageResource(R.drawable.spicy);
        });

        final ImageView fish =  findViewById(R.id.fish);
        fish.setOnClickListener(v -> {
            if(!pressed[0] && !pressed[1]){
                pressed[4] = !pressed[4];
            }
            if(pressed[4])
                fish.setImageResource(R.drawable.fishfill);
            else
                fish.setImageResource(R.drawable.fish);
        });
        final ImageView meat = findViewById(R.id.meat);
        meat.setOnClickListener(v -> {
            if(!pressed[0] && !pressed[1]){
                pressed[5] = !pressed[5];
            }
            if(pressed[5])
                meat.setImageResource(R.drawable.meatfill);
            else
                meat.setImageResource(R.drawable.meat);
        });
        final ImageView dairy = findViewById(R.id.dairy);
        dairy.setOnClickListener(v -> {
            if(!pressed[1]){
                pressed[6] = !pressed[6];
            }
            if(pressed[6])
                dairy.setImageResource(R.drawable.dairyfill);
            else
                dairy.setImageResource(R.drawable.dairy);
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
                .withEndAction(() -> seekbar.animate()
                        .translationYBy(-transfromToDP(500f))
                        .setDuration(150)
                        .withEndAction(() -> {
                            descriptionLayout.animate()
                                    .translationYBy(-transfromToDP(500f))
                                    .setDuration(200);
                            description.animate()
                                    .translationYBy(-transfromToDP(500f))
                                    .setDuration(300)
                                    .withEndAction(() -> {

                                    })
                                    .start();
                        })
                        .start())
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

    @Override
    public void onFragmentInteraction(Uri uri) {
        try {
            selectFromLayout.setVisibility(View.GONE);
            imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            doPhoto.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            doPhoto.getLayoutParams().height = 500;
            doPhoto.setImageURI(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

