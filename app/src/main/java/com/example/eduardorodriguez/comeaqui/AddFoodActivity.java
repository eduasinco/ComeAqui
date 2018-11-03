package com.example.eduardorodriguez.comeaqui;

import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;

public class AddFoodActivity extends AppCompatActivity {

    EditText foodName;
    TextView price;
    ImageView image;
    Spinner spinner;
    SeekBar seekbar;
    ConstraintLayout descriptionLayout;
    EditText description;

    ImageView vegetarian;
    ImageView vegan;
    ImageView cereal;
    ImageView spicy;
    ImageView fish;
    ImageView meat;
    ImageView dairy;
    boolean[] pressed = new boolean[]{false, false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        foodName = (EditText) findViewById(R.id.foodName);
        price = (TextView) findViewById(R.id.price);
        image = (ImageView) findViewById(R.id.image);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        descriptionLayout = (ConstraintLayout) findViewById(R.id.descriptionLayout);
        description = (EditText) findViewById(R.id.description);

        initialAnimations();
        foodName.isFocused();
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
                float pro = (float) progress/100;
                String priceText = String.format("%.02f", pro) + " $";
                price.setText(priceText);
            }
        });

        vegetarian = (ImageView) findViewById(R.id.vegetarian);
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
        vegan = (ImageView) findViewById(R.id.vegan);
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
        cereal = (ImageView) findViewById(R.id.cereal);
        cereal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressed[2] = !pressed[2];
                if(pressed[2])
                    cereal.setImageResource(R.drawable.cerealfill);
                else
                    cereal.setImageResource(R.drawable.cereal);
            }
        });
        spicy = (ImageView) findViewById(R.id.spicy);
        spicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressed[3] = !pressed[3];
                if(pressed[3])
                    spicy.setImageResource(R.drawable.spicyfill);
                else
                    spicy.setImageResource(R.drawable.spicy);
            }
        });
        fish = (ImageView) findViewById(R.id.fish);
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
        meat = (ImageView) findViewById(R.id.meat);
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
        dairy = (ImageView) findViewById(R.id.dairy);
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
        spinner.setTranslationY(transfromToDP(500f));
        seekbar.setTranslationY(transfromToDP(500f));
        descriptionLayout.setTranslationY(transfromToDP(500f));
        description.setTranslationY(transfromToDP(500f));
        foodName.animate()
                .translationYBy(transfromToDP(50f))
                .setDuration(time*5)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        spinner.animate()
                                .translationYBy(-transfromToDP(500f))
                                .setDuration(time)
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

