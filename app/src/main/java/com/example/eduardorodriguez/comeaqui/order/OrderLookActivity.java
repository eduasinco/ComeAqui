package com.example.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.general.StaticMapFragment;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.review.food_review_look.MyFoodReviewRecyclerViewAdapter;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.ContinueCancelFragment;
import com.example.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.example.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class OrderLookActivity extends AppCompatActivity implements ContinueCancelFragment.OnFragmentInteractionListener {

    ImageButton back;
    ImageButton options;
    TextView plateName;
    TextView price;
    TextView posterDescription;
    TextView posterLocationView;
    TextView subtotalView;
    TextView totalPriceView;
    TextView mealTimeView;
    TextView posterNameView;
    TextView posterUsername;
    TextView orderStatus;

    ImageView posterImageView;
    FrameLayout waitingFrame;


    ContinueCancelFragment continueCancelFragment;
    OrderObject order;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_look);
        context = getApplicationContext();
        back = findViewById(R.id.back);
        options = findViewById(R.id.options);
        plateName = findViewById(R.id.plate_name);
        plateName = findViewById(R.id.plate_name);
        posterNameView = findViewById(R.id.poster_name);
        posterUsername = findViewById(R.id.poster_username);
        posterDescription = findViewById(R.id.description);
        posterLocationView = findViewById(R.id.posterLocation);
        price = findViewById(R.id.price);
        subtotalView = findViewById(R.id.postSubtotalPrice);
        totalPriceView = findViewById(R.id.totalPrice);
        mealTimeView = findViewById(R.id.time);
        orderStatus = findViewById(R.id.order_status);

        posterImageView = findViewById(R.id.poster_image);
        waitingFrame = findViewById(R.id.waiting_frame);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null){
            int orderId = b.getInt("orderId");
            getOrderDetails(orderId);
        }

        continueCancelFragment = ContinueCancelFragment.newInstance(
                "Your are canceling the order",
                "Should you cancel after confirmation you would still owe the full fee");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cancel_message, continueCancelFragment)
                .commit();

        back.setOnClickListener(v -> finish());
        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("Help");

            if (!order.status.equals("CANCELED"))
                popupMenu.getMenu().add("Cancel order");

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(item.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });
    }

    void setOptionsActions(String title){
        switch (title){
            case "Reply":
                break;
            case "Cancel order":
                checkIfUserWantsToCancel();
                break;
        }
    }

    void getOrderDetails(int orderId){
        try {
            new GetAsyncTask(this,"GET", getResources().getString(R.string.server) + "/order_detail/" + orderId + "/"){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null) {
                        order = new OrderObject(new JsonParser().parse(response).getAsJsonObject());
                        setView();
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.wait_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitingFrame.setVisibility(View.GONE);
        }
    }


    void goToProfileView(User user){
        Intent k = new Intent(getApplicationContext(), ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
    }

    void checkIfUserWantsToCancel(){
        continueCancelFragment.appear();
    }

    void cancelOrder(){
        continueCancelFragment.waiting();
        order.status = "CANCELED";
        try {
            new PostAsyncTask(this,context.getString(R.string.server) + "/set_order_status/"){
                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                }
            }.execute(
                    new String[]{"order_id",  order.id + ""},
                    new String[]{"order_status", order.status}
            ).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            continueCancelFragment.dessapear();
        }
        finish();
    }

    void setView(){
        plateName.setText(order.post.plate_name);
        posterUsername.setText(order.poster.username);
        posterNameView.setText(order.poster.first_name + " " + order.poster.last_name);
        posterDescription.setText(order.post.description);
        posterLocationView.setText(order.post.address);
        price.setText("$" + order.post.price);
        subtotalView.setText("$" + order.post.price);
        totalPriceView.setText("$" + order.post.price);
        mealTimeView.setText(order.post.time);
        orderStatus.setText(order.status);

        if (order.status.equals("CONFIRMED")){
            orderStatus.setTextColor(getResources().getColor(R.color.success));
        } else if (order.status.equals("CANCELED")){
            orderStatus.setTextColor(getResources().getColor(R.color.canceled));
        } else {
            orderStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.static_map_frame, StaticMapFragment.newInstance(order.post.lat, order.post.lng))
                .commit();

        if(!order.poster.profile_photo.contains("no-image")) {
            Glide.with(context).load(order.poster.profile_photo).into(posterImageView);
            posterImageView.setOnClickListener(v -> goToProfileView(order.poster));
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(order.post.id,16, 8, 200,4, 4))
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_rating, RatingFragment.newInstance(order.poster.rating, order.poster.ratingN))
                .commit();
    }

    @Override
    public void onFragmentInteraction(boolean ok) {
        if (ok){
            cancelOrder();
        }
    }
}
