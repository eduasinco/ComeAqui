package com.example.eduardorodriguez.comeaqui.review;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.FoodLookActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.WebSocketMessage;
import com.example.eduardorodriguez.comeaqui.objects.NotificationObject;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.R.color.colorPrimaryLight;
import static com.example.eduardorodriguez.comeaqui.R.color.grey_light;
import static com.example.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;

enum SubmitButtonStatus{
    NEXT, SUBMIT, DISABLED
}

public class ReviewActivity extends AppCompatActivity {

    TextView rateMessage1;
    TextView rateMessage2;
    TextView reason0;
    TextView reason1;
    TextView reason2;
    TextView reason3;
    LinearLayout onceRateView;
    LinearLayout rateMealView;
    EditText review;
    Button submitButton;
    ScrollView scrollView;

    OrderObject orderObject;
    int rating = 5;
    boolean[] reasonB = {false, false, false, false};

    SubmitButtonStatus buttonStatus = SubmitButtonStatus.NEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView posterName = findViewById(R.id.poster_name);
        TextView amount = findViewById(R.id.amount);
        TextView cardLastNumbers = findViewById(R.id.card_last_numbers);
        rateMessage1 = findViewById(R.id.rate_message1);
        rateMessage2 = findViewById(R.id.rate_message2);
        reason0 = findViewById(R.id.reason1);
        reason1 = findViewById(R.id.reason2);
        reason2 = findViewById(R.id.reason3);
        reason3 = findViewById(R.id.reason4);
        onceRateView = findViewById(R.id.once_rate_view);
        rateMealView = findViewById(R.id.rate_meal_view);
        review = findViewById(R.id.review);
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
        setStars();
        setSubmitButton();
        setReasonFunctionality();
        setEditTextWatcher();
    }

    void setEditTextWatcher(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            scrollView.fullScroll(View.FOCUS_DOWN);
        }
        review.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    void setReasonFunctionality(){
        TextView[] reasons = new TextView[]{
                reason0,
                reason1,
                reason2,
                reason3
        };

        for (int i = 0; i < reasons.length; i++){
            TextView reason = reasons[i];
            final int finalI = i;
            reason.setOnClickListener(v -> {
                reasonB[finalI] = !reasonB[finalI];
                if (reasonB[finalI]){
                    reason.setBackground(ContextCompat.getDrawable(this, R.drawable.box_primary_color));
                    reason.setTextColor(Color.WHITE);
                } else {
                    reason.setBackground(ContextCompat.getDrawable(this, R.drawable.box_thik_stroke_grey));
                    reason.setTextColor(ContextCompat.getColor(this, secondary_text_default_material_light));
                }
            });
        }
    }

    void submit(){
        PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_review/");
        try {
            String response = createOrder.execute(
                    new String[]{"order_id", "" + orderObject.id},
                    new String[]{"review", review.getText().toString()},
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
            } else {
                submit();
            }
        });
    }


    void setStars(){
        ImageView[] starArray = new ImageView[]{
                findViewById(R.id.star0),
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4)
        };

        for (int i = 0; i < starArray.length; i++){
            final int finalI = i;
            ImageView star = starArray[i];
            star.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_empty));
            star.setOnClickListener(v -> {
                submitButton.setAlpha(1);
                buttonStatus = SubmitButtonStatus.SUBMIT;
                rating = finalI + 1;
                onceRateView.setVisibility(View.VISIBLE);
                int j = 0;
                while (j <= finalI){
                    starArray[j].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_filled));
                    j++;
                }
                while (j < starArray.length){
                    starArray[j].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.star_empty));
                    j++;
                }
                if (finalI == 4){
                    changeReasonText(true, finalI);
                } else {
                    changeReasonText(false, finalI);
                }
                scrollView.fullScroll(View.FOCUS_DOWN);
            });
        }
    }

    void changeReasonText(boolean good, int i){
        String[] rateMessage = {"AUFUL", "BAD", "INDIFFERENT", "COULD BE BETTER", "GREAT"};
        if (good){
            rateMessage1.setText(rateMessage[i]);
            rateMessage2.setText("Great! What did you like the most?");
            reason0.setText("More than better");
            reason1.setText("She/he was nice");
            reason2.setText("Clean house");
            reason3.setText("Good conversation");
        } else {
            rateMessage1.setText(rateMessage[i]);
            rateMessage2.setText("We regret hearing that. What was the issue?");
            reason0.setText("Problem with host");
            reason1.setText("Problem with food");
            reason2.setText("Problem with cleaning");
            reason3.setText("Problem with payment");
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
}
