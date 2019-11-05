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
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import com.example.eduardorodriguez.comeaqui.map.add_food.AddImagesFragment;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class AddFoodActivity extends AppCompatActivity implements
        PlaceAutocompleteFragment.OnFragmentInteractionListener,
        SelectImageFromFragment.OnFragmentInteractionListener,
        ErrorMessageFragment.OnFragmentInteractionListener,
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        FoodTimePickerFragment.OnFragmentInteractionListener,
        WordLimitEditTextFragment.OnFragmentInteractionListener,
        AddImagesFragment.OnFragmentInteractionListener{
    EditText foodName;
    TextView price;
    ImageView image;
    SeekBar seekbar;
    ConstraintLayout descriptionLayout;
    Button submit;
    ImageView backView;
    ScrollView scrollView;
    FrameLayout errorMessage;
    TextView validationError;
    LinearLayout dinnerArray;
    private View mProgressView;


    Float price_data;
    boolean[] pressed = {false, false, false, false, false, false, false};
    ArrayList<Bitmap> imageBitmaps;
    int imageIndex;
    int dinners = 0;
    String postTimeString;
    double lat;
    double lng;
    String address;
    String description = "";

    boolean isAddressValid = true;
    Context context;

    PlaceAutocompleteFragment placeAutocompleteFragment;
    FoodTypeSelectorFragment foodTypeSelectorFragment;
    FoodTimePickerFragment foodTimePickerFragment;
    WordLimitEditTextFragment wordLimitEditTextFragment;
    AddImagesFragment addImageFragment;
    SelectImageFromFragment selectImagesFromFragment;

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
        imageBitmaps = new ArrayList<>();

        foodName = findViewById(R.id.plateName);
        price = findViewById(R.id.priceText);
        image = findViewById(R.id.image);
        seekbar = findViewById(R.id.seekBar);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        submit = findViewById(R.id.submitButton);
        backView = findViewById(R.id.back_arrow);
        scrollView = findViewById(R.id.scrollview);
        mProgressView = findViewById(R.id.post_progress);
        errorMessage = findViewById(R.id.error_message_frame);
        validationError = findViewById(R.id.validation_error);
        dinnerArray = findViewById(R.id.dinner_array);

        context = getApplicationContext();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            address = b.getString("address");
            lat = b.getDouble("lat");
            lng = b.getDouble("lng");

            placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance(address);
            foodTypeSelectorFragment = FoodTypeSelectorFragment.newInstance();
            foodTimePickerFragment = FoodTimePickerFragment.newInstance();
            wordLimitEditTextFragment = WordLimitEditTextFragment.newInstance();
            addImageFragment = AddImagesFragment.newInstance();
            selectImagesFromFragment = SelectImageFromFragment.newInstance(false);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.locationAutocomplete, placeAutocompleteFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.set_foot_types_frame, foodTypeSelectorFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.food_time_picker_frame, foodTimePickerFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.post_limit_text_edit, wordLimitEditTextFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.add_images_frame, addImageFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_image_from, selectImagesFromFragment)
                    .commit();
        }

        setPlateName();
        setFoodName();
        setPriceSeekBar();
        setDinerButtons();
        setSubmit();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            foodName.clearFocus();
        });

        backView.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectImagesFromFragment.hideCard();
    }

    void setPlateName(){
        foodName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){ }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before, int count){
                foodName.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
            }
            @Override
            public void afterTextChanged ( final Editable s){}
        });
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
                dinnerArray.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
                dinners = finalI + 1;
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryLighter));
                for (Button button2: dinersViews){
                    if (button2 != button){
                        button2.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            });
        }
    }

    void setSubmit(){
        submit.setOnClickListener(v -> {
            if (validateFrom()){
                postFood();
                finish();
            }
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
        boolean isValid = true;
        String errorText = "";
        if (foodName.getText().toString().trim().equals("")){
            foodName.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
            errorText = errorText + "You have to set a plate name \n";
            isValid = false;
        }
        if (!isAddressValid){
            placeAutocompleteFragment.setErrorBackground(true);
            errorText = errorText + "Pleas choose a valid address \n";
            isValid = false;
        }
        if (dinners == 0){
            dinnerArray.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
            errorText = errorText + "You have to set a dinners number \n";
            isValid = false;
        }
        if (postTimeString == null || postTimeString.equals("")){
            foodTimePickerFragment.setErrorBackground();
            errorText = errorText + "Please choose a valid meal time \n";
            isValid = false;
        }
        if (USER.timeZone == null || USER.timeZone.equals("")){
            errorText = errorText + "Problem with you timezone \n";
            System.out.println("THERE IS A PROBLEM WITH THE TIMEZONE");
            isValid = false;
        }
        if (price_data == null){
            seekbar.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
            errorText = errorText + "You have to set a meal price \n";
            isValid = false;
        }
        if (description.trim().equals("")){
            wordLimitEditTextFragment.setErrorBackground(true);
            errorText = errorText + "You have to set a meal description \n";
            isValid = false;
        }
        if (!isValid){
            validationError.setVisibility(View.VISIBLE);
            validationError.setText(errorText);
        } else {
            validationError.setVisibility(View.GONE);
        }
        return isValid;
    }

    void postFood(){
        try {
            showProgress(true);
            PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/foods/"){
                @Override
                protected void onPostExecute(String response) {
                    FoodPost foodPost = new FoodPost(new JsonParser().parse(response).getAsJsonObject());
                    postImages(foodPost.id);
                    //sendPostMessage(foodPost);
                }
            };
            post.execute(
                    new String[]{"plate_name", foodName.getText().toString()},
                    new String[]{"address", address},
                    new String[]{"lat", Double.toString(lat)},
                    new String[]{"lng", Double.toString(lng)},
                    new String[]{"max_dinners", Integer.toString(dinners)},
                    new String[]{"time", postTimeString},
                    new String[]{"time_zone", USER.timeZone},
                    new String[]{"price", price_data.toString()},
                    new String[]{"food_type", setTypes()},
                    new String[]{"description", description}
            ).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showErrorMessage();
            showProgress(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            showProgress(false);
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void postImages(int foodPostId){
        for (Bitmap image: imageBitmaps){
            showProgress(true);
            try {
                PostAsyncTask post = new PostAsyncTask(getResources().getString(R.string.server) + "/food_images/"){
                    @Override
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);
                    }
                };
                post.bitmap = image;
                post.execute(
                        new String[]{"post", "" + foodPostId},
                        new String[]{"image", ""}
                ).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                showErrorMessage();
                showProgress(false);
                Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
            }
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
                seekBar.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        try {
            selectImagesFromFragment.hideCard();
            Bitmap bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            imageBitmaps.add(imageIndex, bm);
            addImageFragment.addImage(uri, imageIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlacesAutocomplete(String address, double lat, double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.isAddressValid = true;
    }
    @Override
    public void onPlacesAutocompleteChangeText() {
        this.isAddressValid = false;
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

    @Override
    public void onAddImage(int index) {
        imageIndex = index;
        selectImagesFromFragment.showCard();
    }
}

