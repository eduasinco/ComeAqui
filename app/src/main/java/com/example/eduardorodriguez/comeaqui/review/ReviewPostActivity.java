package com.example.eduardorodriguez.comeaqui.review;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.R.color.colorPrimaryLight;
import static com.example.eduardorodriguez.comeaqui.R.color.grey_light;
import static com.example.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;

enum SubmitButtonStatus{
    NEXT, SUBMIT, DISABLED
}

public class ReviewPostActivity extends AppCompatActivity implements StarReasonFragment.OnFragmentInteractionListener {

    LinearLayout rateMealView;
    Button submitButton;
    ScrollView scrollView;

    OrderObject orderObject;
    int rating = 5;
    boolean[] reasonB = {false, false, false, false};
    String review = "";

    SubmitButtonStatus buttonStatus = SubmitButtonStatus.NEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView posterName = findViewById(R.id.poster_name);
        TextView amount = findViewById(R.id.amount);
        TextView cardLastNumbers = findViewById(R.id.card_last_numbers);
        rateMealView = findViewById(R.id.rate_meal_view);
        submitButton = findViewById(R.id.submitButton);
        scrollView = findViewById(R.id.scrollView);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("order") != null) {
            orderObject = (OrderObject) b.get("order");

            if(!orderObject.post.owner.profile_photo.contains("no-image")) {
                ImageView posterImage = findViewById(R.id.poster_image);
                Glide.with(this).load(orderObject.poster.profile_photo).into(posterImage);
            }

            posterName.setText(orderObject.poster.first_name);
            amount.setText(orderObject.post.price);
            cardLastNumbers.setText("USER CARD");
        }

        submitButton.setAlpha(0.5f);

        setTipButtons();
        setSubmitButton();
    }



    void submit(){
        PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_review/");
        try {
            String response = createOrder.execute(
                    new String[]{"order_id", "" + orderObject.id},
                    new String[]{"review", review},
                    new String[]{"rating", "" + rating},
                    new String[]{"star_reason", ""}
            ).get();
            JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
            ReviewObject reviewObject = new ReviewObject(jo);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        finish();
    }

    void setSubmitButton(){
        submitButton.setOnClickListener(v -> {
            if (buttonStatus == SubmitButtonStatus.NEXT){
                submitButton.setAlpha(0.5f);
                scrollView.fullScroll(View.FOCUS_DOWN);
                submitButton.setText("SUBMIT");
                rateMealView.setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.star_reason_frame, StarReasonFragment.newInstance())
                        .commit();
            } else {
                submit();
            }
        });
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
        buttonStatus = SubmitButtonStatus.SUBMIT;
        this.review = review;
    }

    @Override
    public void onHasToScrollIfNeeded() {
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
