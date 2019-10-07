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
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.eduardorodriguez.comeaqui.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.WebSocketMessage;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.profile.SelectImageFromFragment;
import com.example.eduardorodriguez.comeaqui.utilities.AutocompleteLocationFragment;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.utilities.ContinueCancelFragment;
import com.example.eduardorodriguez.comeaqui.utilities.ErrorMessageFragment;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeSelectorFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class AddFoodActivity extends AppCompatActivity implements
        SelectImageFromFragment.OnFragmentInteractionListener,
        ErrorMessageFragment.OnFragmentInteractionListener,
        FoodTypeSelectorFragment.OnFragmentInteractionListener {
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
    TextView timeTextView;
    ScrollView scrollView;
    FrameLayout errorMessage;
    private View mProgressView;


    Float price_data = 0f;
    boolean[] pressed;
    Bitmap imageBitmap;
    int diners;
    boolean isNow = false;
    String postTimeString;
    double lat;
    double lng;
    String address;


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
        description = findViewById(R.id.bioText);
        submit = findViewById(R.id.submitButton);
        timePicker = findViewById(R.id.timePicker);
        doPhoto = findViewById(R.id.photo);
        backView = findViewById(R.id.back_arrow);
        selectFromLayout = findViewById(R.id.select_image_from);
        timeTextView = findViewById(R.id.time_text);
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

            Bundle bundle = new Bundle();
            bundle.putString("address", address);
            AutocompleteLocationFragment autocompleteLocationFragment = new AutocompleteLocationFragment();
            autocompleteLocationFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.locationAutocomplete, autocompleteLocationFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.set_foot_types_frame, FoodTypeSelectorFragment.newInstance())
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
        setTimeLogic();
        setTimePickerLogic();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            foodName.clearFocus();
        });

        backView.setOnClickListener(v -> finish());
    }

    void setTimePickerLogic(){
        timePicker.setOnTimeChangedListener((arg0, arg1, arg2) -> {
            timeTextView.setText("Today at: " + arg0.getHour() + ":" + arg0.getMinute());

            Date now = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddZ");
            format.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String formattedDate = format.format(now);
            try {
                Date todayDate = new SimpleDateFormat("yyyy-MM-ddZ", Locale.US).parse(formattedDate);
                Date postTimeDate = new Date(todayDate.getTime() + (arg0.getHour()*60 + arg0.getMinute())*60*1000);
                postTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mmZ").format(postTimeDate);
                if (now.getTime() + minutes*60*1000 > postTimeDate.getTime()){
                    Date date = new Date(now.getTime() + minutes*60*1000);
                    DateFormat formatter = new SimpleDateFormat("HH:mm");
                    formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                    String dateFormatted = formatter.format(date);

                    timeTextView.setText("Please pick a time greater than " + dateFormatted);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    void setTimeLogic(){
        Button nowButton = findViewById(R.id.anytime_button);
        Button scheduleButton = findViewById(R.id.schedule_button);

        nowButton.setOnClickListener(v -> {

            Date now = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");
            sdf.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String nowString = sdf.format(now);
            try {
                Date nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.US).parse(nowString);
                Date postTimeDate = new Date(nowDate.getTime() + minutes*60*1000);
                postTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mmZ").format(postTimeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            isNow = true;
            timeTextView.setText("Now (the post will be visible for an hour)");
            nowButton.setBackgroundColor(Color.TRANSPARENT);
            scheduleButton.setBackground(ContextCompat.getDrawable(this, R.drawable.text_input_shape));
            showTimePicker(false);
        });

        scheduleButton.setOnClickListener(v -> {
            timeTextView.setText("-- --");
            nowButton.setBackground(ContextCompat.getDrawable(this, R.drawable.text_input_shape));
            scheduleButton.setBackgroundColor(Color.TRANSPARENT);
            showTimePicker(true);
        });
    }

    void showTimePicker(boolean show){
        int duration = 200;
        if (show){
            timePicker.setScaleX(0);
            timePicker.setVisibility(View.VISIBLE);
            timePicker.animate().scaleX(1).setDuration(duration);
        } else {
            timePicker.animate().scaleX(0).setDuration(duration).withEndAction(() -> timePicker.setVisibility(View.GONE));
        }
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
                    new String[]{"description", description.getText().toString()},
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
    public void onFragmentInteraction() {
        errorMessage.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentInteraction(boolean[] pressed) {
        this.pressed = pressed;
    }
}

