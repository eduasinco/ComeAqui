package com.example.eduardorodriguez.comeaqui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.eduardorodriguez.comeaqui.WebSocketMessage;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTimePickerFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.WordLimitEditTextFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.utilities.ErrorMessageFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;
import com.example.eduardorodriguez.comeaqui.utilities.place_autocomplete.PlaceAutocompleteFragment;
import com.google.gson.JsonParser;

import java.io.IOException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class AddFoodActivity extends AppCompatActivity implements
        PlaceAutocompleteFragment.OnFragmentInteractionListener,
        SelectImageFromFragment.OnFragmentInteractionListener,
        ErrorMessageFragment.OnFragmentInteractionListener,
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        FoodTimePickerFragment.OnFragmentInteractionListener,
        WordLimitEditTextFragment.OnFragmentInteractionListener {
    EditText foodName;
    TextView price;
    ImageView image;
    SeekBar seekbar;
    ConstraintLayout descriptionLayout;
    Button submit;
    ImageView doPhoto;
    ImageView backView;
    FrameLayout selectFromLayout;
    ScrollView scrollView;
    FrameLayout errorMessage;
    private View mProgressView;


    Float price_data = 0f;
    boolean[] pressed;
    Bitmap imageBitmap;
    int diners;
    String postTimeString;
    double lat;
    double lng;
    String address;
    String description;

    int minutes = 30;
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
        submit = findViewById(R.id.submitButton);
        doPhoto = findViewById(R.id.photo);
        backView = findViewById(R.id.back_arrow);
        selectFromLayout = findViewById(R.id.select_image_from);
        scrollView = findViewById(R.id.scrollview);
        mProgressView = findViewById(R.id.post_progress);
        errorMessage = findViewById(R.id.error_message_frame);



        context = getApplicationContext();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            address = b.getString("address");
            lat = b.getDouble("lat");
            lng = b.getDouble("lng");



            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.locationAutocomplete, PlaceAutocompleteFragment.newInstance(address))
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.set_foot_types_frame, FoodTypeSelectorFragment.newInstance())
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.food_time_picker_frame, FoodTimePickerFragment.newInstance())
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.post_limit_text_edit, WordLimitEditTextFragment.newInstance())
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
        setDinerButtons();
        setSubmit();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            foodName.clearFocus();
        });

        backView.setOnClickListener(v -> finish());
    }

    void setDinerButtons(){
        Button diner1 = findViewById(R.id.dinner0);
        Button diner2 = findViewById(R.id.dinner1);
        Button diner3 = findViewById(R.id.dinner2);
        Button diner4 = findViewById(R.id.dinner3);

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
            showProgress(true);
            postFood();
        });
    }

    void showProgress(boolean show){
        if (show){
            mProgressView.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        } else {
            mProgressView.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }
    }

    boolean validateFrom(){
        if (foodName.getText().toString().trim().equals("")){
            return false;
        }
        if (address.trim().equals("")){
            return false;
        }
        return true;
    }

    void postFood(){
        PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/foods/");
        post.bitmap = imageBitmap;

        try {
            String response = post.execute(
                    new String[]{"plate_name", foodName.getText().toString()},
                    new String[]{"address", address},
                    new String[]{"lat", Double.toString(lat)},
                    new String[]{"lng", Double.toString(lng)},
                    new String[]{"diners", Integer.toString(diners)},
                    new String[]{"time", postTimeString},
                    new String[]{"time_zone", USER.timeZone},
                    new String[]{"price", price_data.toString()},
                    new String[]{"food_type", setTypes()},
                    new String[]{"description", description},
                    new String[]{"food_photo", ""}
            ).get();
            FoodPost foodPost = new FoodPost(new JsonParser().parse(response).getAsJsonObject());
            WebSocketMessage.send(this,
                    "/ws/posts/",
                    "{\"post_id\": " + foodPost.id + "}"
            );
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage();
            showProgress(false);
        }
    }
    void showErrorMessage(){
        errorMessage.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.error_message_frame, ErrorMessageFragment.newInstance(
                        "Error during posting",
                        "Please make sure that you have connection to the internet"))
                .commit();
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

    @Override
    public void onPlacesAutocomplete(String address, double lat, double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public void onFragmentInteraction() {
        errorMessage.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentInteraction(boolean[] pressed) {
        this.pressed = pressed;
    }

    @Override
    public void onTextChanged(String description) {
        this.description = description;
    }

    @Override
    public void onFragmentInteraction(String postTimeString) {
        this.postTimeString = postTimeString;
    }
}

