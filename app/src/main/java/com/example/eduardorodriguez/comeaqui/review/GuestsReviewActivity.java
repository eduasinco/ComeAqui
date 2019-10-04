package com.example.eduardorodriguez.comeaqui.review;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class GuestsReviewActivity extends AppCompatActivity implements StarReasonFragment.OnFragmentInteractionListener{


    Button submitButton;
    ScrollView scrollView;

    OrderObject orderObject;

    int rating;
    boolean[] reasonB;
    String review = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guests_review);

        ImageView guestImage = findViewById(R.id.guest_image);
        TextView guestName = findViewById(R.id.guest_name);
        TextView rating = findViewById(R.id.rating);
        submitButton = findViewById(R.id.submitButton);
        scrollView = findViewById(R.id.scrollv);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("order") != null) {
            orderObject = (OrderObject) b.get("order");

            if(!orderObject.owner.profile_photo.contains("no-image")) {
                Glide.with(this).load(orderObject.poster.profile_photo).into(guestImage);
            }
            guestName.setText(orderObject.owner.first_name);
            rating.setText(orderObject.owner.rating + "");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.star_rating_fr, StarReasonFragment.newInstance())
                    .commit();
        }

        setSubmitButton();
    }

    void submit(){
        PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/rate_user/");
        try {
            String response = createOrder.execute(
                    new String[]{"order_id", "" + orderObject.id},
                    new String[]{"review", review},
                    new String[]{"rating", "" + rating},
                    new String[]{"star_reason", ""}
            ).get();
            JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
            OrderObject orderObject = new OrderObject(jo);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        finish();
    }


    void setSubmitButton(){
        submitButton.setAlpha(0.5f);
        submitButton.setOnClickListener(v -> {
            this.submit();
        });
    }

    @Override
    public void onFragmentInteraction(int rating, boolean[] reasonB, String review) {
        submitButton.setAlpha(1);
        this.rating = rating;
        this.reasonB = reasonB;
        this.review = review;
    }

    @Override
    public void onHasToScrollIfNeeded() {
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
