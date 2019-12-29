package com.example.eduardorodriguez.comeaqui.review;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;

import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.R.color.colorPrimaryLight;
import static com.example.eduardorodriguez.comeaqui.R.color.grey_light;
import static com.example.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;


public class ReviewHostActivity extends AppCompatActivity implements StarReasonFragment.OnFragmentInteractionListener {
    enum SubmitButtonStatus{
        NEXT, SUBMIT
    }

    LinearLayout rateMealView;
    TextView posterRating;
    Button submitButton;
    ScrollView scrollView;
    View progress;

    OrderObject orderObject;
    int rating = 5;
    boolean[] reasonB = {false, false, false, false};
    String review = "";

    boolean readyForSubmit = false;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView posterName = findViewById(R.id.poster_name);
        TextView amount = findViewById(R.id.amount);
        TextView cardLastNumbers = findViewById(R.id.card_last_numbers);
        rateMealView = findViewById(R.id.rate_meal_view);
        posterRating = findViewById(R.id.poster_rating);
        submitButton = findViewById(R.id.submitButton);
        scrollView = findViewById(R.id.scrollView);
        progress = findViewById(R.id.review_progress);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("order") != null) {
            orderObject = (OrderObject) b.get("order");

            if(!orderObject.post.owner.profile_photo.contains("no-image")) {
                ImageView posterImage = findViewById(R.id.poster_image);
                Glide.with(this).load(orderObject.poster.profile_photo).into(posterImage);
            }

            blockSubmitButton(true);

            posterName.setText(orderObject.poster.first_name);
            amount.setText("USD" + orderObject.post.price / 100.d);
            cardLastNumbers.setText("USER CARD");
            posterRating.setText(orderObject.owner.rating + "");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.star_reason_frame, StarReasonFragment.newInstance())
                    .commit();
        }

        setTipButtons();
    }

    void blockSubmitButton(boolean block){
        submitButton.setAlpha(block ? 0.5f: 1);
        submitButton.setOnClickListener(v -> {
            if (!block){
                if (readyForSubmit){
                    submit();
                } else {
                    submitButton.setAlpha(0.5f);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    submitButton.setText("SUBMIT");
                    rateMealView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    void submit(){
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/create_review/").execute(
                new String[]{"order_id", "" + orderObject.id},
                new String[]{"review", review},
                new String[]{"rating", "" + rating},
                new String[]{"star_reason", ""}
        ));
    }
    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            submitButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                ReviewObject reviewObject = new ReviewObject(jo);
                finish();
            }
            submitButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
    }

    void setTipButtons(){
        Button[] tipViews = new Button[]{
                findViewById(R.id.tip0),
                findViewById(R.id.tip1),
                findViewById(R.id.tip2),
                findViewById(R.id.tip3)
        };
        for (int i = 0; i < tipViews.length; i++){
            Button button = tipViews[i];
            button.setOnClickListener(v -> {
                blockSubmitButton(false);
                submitButton.setAlpha(1);
                for (Button tip: tipViews){
                    button.setBackgroundColor(ContextCompat.getColor(this, colorPrimaryLight));
                    button.setTextColor(Color.WHITE);
                }
                for (Button button2: tipViews){
                    if (button2 != button){
                        button2.setBackgroundColor(ContextCompat.getColor(this, grey_light));
                        button2.setTextColor(ContextCompat.getColor(this, secondary_text_default_material_light));
                    }
                }
            });
        }
    }

    @Override
    public void onFragmentInteraction(int rating, boolean[] reasonB, String review) {
        submitButton.setAlpha(1);
        readyForSubmit = true;
        this.review = review;
    }

    @Override
    public void onHasToScrollIfNeeded() {
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onRating(int rating) {
        this.rating = rating;
    }

    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
