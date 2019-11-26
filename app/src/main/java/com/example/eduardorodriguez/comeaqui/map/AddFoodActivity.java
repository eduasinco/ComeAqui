package com.example.eduardorodriguez.comeaqui.map;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.eduardorodriguez.comeaqui.map.add_food.AddImagesFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodDateTimePickerFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.WordLimitEditTextFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.SavedFoodPost;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.ErrorMessageFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;
import com.example.eduardorodriguez.comeaqui.utilities.place_autocomplete.PlaceAutocompleteFragment;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class AddFoodActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        PlaceAutocompleteFragment.OnFragmentInteractionListener,
        SelectImageFromFragment.OnFragmentInteractionListener,
        ErrorMessageFragment.OnFragmentInteractionListener,
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        FoodDateTimePickerFragment.OnFragmentInteractionListener,
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
    EditText dinnerPicker;
    ImageButton options;
    private View mProgressView;


    Float price_data;
    boolean[] pressed = {false, false, false, false, false, false, false};
    ArrayList<Bitmap> imageBitmaps;
    int imageIndex;
    long dinners = 0;
    String startTime;
    String endTime;
    double lat;
    double lng;
    String address;
    String description = "";

    boolean isAddressValid = true;
    Context context;

    boolean firstSave = true;

    PlaceAutocompleteFragment placeAutocompleteFragment;
    FoodTypeSelectorFragment foodTypeSelectorFragment;
    FoodDateTimePickerFragment foodTimePickerFragment;
    WordLimitEditTextFragment wordLimitEditTextFragment;
    AddImagesFragment addImageFragment;
    SelectImageFromFragment selectImagesFromFragment;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

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
        options = findViewById(R.id.options);

        dinnerPicker = findViewById(R.id.dinners);

        context = getApplicationContext();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            address = b.getString("address");
            lat = b.getDouble("lat");
            lng = b.getDouble("lng");

            placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance(address, false);
            foodTypeSelectorFragment = FoodTypeSelectorFragment.newInstance();
            foodTimePickerFragment = FoodDateTimePickerFragment.newInstance();
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

        setTextInputs();
        setFoodName();
        setPriceSeekBar();
        setSubmit();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            foodName.clearFocus();
        });

        setOptionsMenu();
        backView.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectImagesFromFragment.hideCard();
    }

    void setTextInputs(){
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

        dinnerPicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){ }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before, int count){
                dinnerPicker.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
                if (s.toString().length() > 0)
                    dinners = Long.parseLong(s.toString());
            }
            @Override
            public void afterTextChanged ( final Editable s){}
        });
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void setSubmit(){
        submit.setOnClickListener(v -> {
            if (validateFrom()){
                postFood("POST", true);
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
            dinnerPicker.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
            errorText = errorText + "You have to set a dinners number \n";
            isValid = false;
        }
        if (startTime == null || startTime.equals("")){
            foodTimePickerFragment.setErrorBackground();
            errorText = errorText + "Please choose a valid meal start_time \n";
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

    void setOptionsMenu(){
        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, options);
            popupMenu.getMenu().add("Save");

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(item.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });
    }

    void setOptionsActions(String title){
        switch (title){
            case "Save":
                postFood(firstSave ? "POST" : "PATCH", false);
                break;
        }
    }

    void postFood(String method, boolean visible){
        UploadPost post = new UploadPost(getResources().getString(R.string.server) + "/foods/", method);
        tasks.add(post.execute(
                new String[]{"plate_name", foodName.getText().toString()},
                new String[]{"address", address},
                new String[]{"lat", Double.toString(lat)},
                new String[]{"lng", Double.toString(lng)},
                new String[]{"max_dinners", dinners + ""},
                new String[]{"start_time", startTime == null ? "" : startTime},
                new String[]{"end_time", endTime == null ? "" : endTime},
                new String[]{"time_zone", USER.timeZone},
                new String[]{"price", price_data == null ? "" : price_data.toString()},
                new String[]{"food_type", setTypes()},
                new String[]{"description", description},
                new String[]{"visible", visible + ""}
        ));
    }
    private class UploadPost extends AsyncTask<String[], Void, String> {
        String uri;
        String method;

        public UploadPost(String uri, String method){
            this.uri = uri;
            this.method = method;
        }
        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), this.method, this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                SavedFoodPost foodPost = new SavedFoodPost(new JsonParser().parse(response).getAsJsonObject());
                postImages(foodPost.id);
            }
            showProgress(false);
            super.onPostExecute(response);
        }
    }
    void postImages(int foodPostId){
        PostImagesAsyncTask post = new PostImagesAsyncTask(
                getResources().getString(R.string.server) + "/add_food_images/" + foodPostId + "/",
                imageBitmaps
        );
        tasks.add(post.execute());
    }
    class PostImagesAsyncTask extends AsyncTask<String, Void, String> {
        String uri;
        List<Bitmap> images;
        public PostImagesAsyncTask(String uri, List<Bitmap> imageBitmaps){
            this.uri = uri;
            this.images = imageBitmaps;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                for (int i = 0; i < this.images.size(); i++){
                    Bitmap image = this.images.get(i);
                    ServerAPI.uploadImage(getApplicationContext(), "PATCH",  this.uri, "image", image);
                }
                return "";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            showProgress(false);
            finish();
            super.onPostExecute(response);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        foodTimePickerFragment.onDateSet(view, year, month, dayOfMonth);
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
                String priceText = String.format("%.02f", price_data) + "$";
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
    public void onListPlaceChosen(String address, double lat, double lng) {
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
    public void closeButtonPressed() {}

    @Override
    public void searchButtonClicked() {}

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
    public void onFragmentInteraction(String startDate, String endDate) {
        this.startTime = startDate;
        this.endTime = endDate;
    }

    @Override
    public void onAddImage(int index) {
        imageIndex = index;
        selectImagesFromFragment.showCard();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}

