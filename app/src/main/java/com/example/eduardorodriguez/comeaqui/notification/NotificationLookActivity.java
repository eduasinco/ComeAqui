package com.example.eduardorodriguez.comeaqui.notification;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.general.StaticMapFragment;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;


import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.example.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class NotificationLookActivity extends AppCompatActivity {

    static Context context;

    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView orderAction;
    TextView timeView;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    TextView statucMessage;
    TextView rating;
    Button confirmCancelButton;

    ImageView dinnerImage;
    ImageView backView;
    View confirmNotificationProgress;
    FrameLayout waitingFrame;

    OrderObject orderObject;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_look);
        context = getApplicationContext();

        plateNameView = findViewById(R.id.postPlateName);
        descriptionView = findViewById(R.id.post_description);
        priceView = findViewById(R.id.price);
        timeView = findViewById(R.id.time);
        confirmCancelButton = findViewById(R.id.placeOrderButton);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.dinner_name);
        posterLocationView = findViewById(R.id.posterLocation);
        statucMessage = findViewById(R.id.status_message);
        rating = findViewById(R.id.rating);
        orderAction = findViewById(R.id.order_action);

        dinnerImage = findViewById(R.id.dinner_image);
        backView = findViewById(R.id.back);
        confirmNotificationProgress = findViewById(R.id.confirm_notification_progress);
        waitingFrame = findViewById(R.id.waiting_frame);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_frame, WaitFragment.newInstance())
                .commit();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b != null && b.get("orderId") != null){
            int orderId = b.getInt("orderId");
            getOrderObject(orderId);
        }
        backView.setOnClickListener(v -> finish());
    }

    void getOrderObject(int orderId){
        tasks.add(new GetAsyncTask(context.getResources().getString(R.string.server) + "/order_detail/" + orderId + "/").execute());
    }
    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            startWaitingFrame(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                orderObject = new OrderObject(new JsonParser().parse(response).getAsJsonObject());
                startWaitingFrame(false);
                setDetails();
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }

    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
        } else {
            waitingFrame.setVisibility(View.GONE);
        }
    }

    void setDetails(){
        posterNameView.setText(orderObject.owner.first_name + " " + orderObject.owner.last_name);
        usernameView.setText(orderObject.owner.username);
        plateNameView.setText(orderObject.post.plate_name);
        descriptionView.setText(orderObject.post.description);
        posterLocationView.setText(orderObject.post.formatted_address);
        priceView.setText("$" + orderObject.post.price);
        timeView.setText(orderObject.post.time_to_show);
        rating.setText(String.format("%.01f", orderObject.poster.rating));

        switch (orderObject.status){
            case "PENDING":
                orderAction.setText("Wants to eat with you!");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimary));
                break;
            case "CONFIRMED":
                orderAction.setText("Meal confirmed!");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.success));
                break;
            case "CANCELED":
                orderAction.setText("Meal canceled");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.canceled));

                if(!orderObject.owner.profile_photo.contains("no-image")){
                    Glide.with(this).load(orderObject.owner.profile_photo).into(dinnerImage);
                }
                break;
            case "FINISHED":
                orderAction.setText("Has eaten with you");
                orderAction.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimary));
                break;
        }

        User userToShow = orderObject.owner.id == USER.id  ? orderObject.poster : orderObject.owner;
        if(!userToShow.profile_photo.contains("no-image")){
            Glide.with(this).load(userToShow.profile_photo).into(dinnerImage);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("type", orderObject.post.type);
        FoodTypeFragment fragment = new FoodTypeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.types, fragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(orderObject.post.id,24, 8, 200,4, 4))
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.static_map_frame, StaticMapFragment.newInstance(orderObject.post.lat, orderObject.post.lng))
                .commit();

        dinnerImage.setOnClickListener(v -> {
            goToProfileView(userToShow.id);
        });
        setConfirmCancelButton();
    }

    void showProgress(boolean show){
        if (show){
            confirmNotificationProgress.setVisibility(View.VISIBLE);
            confirmCancelButton.setVisibility(View.GONE);
        } else {
            confirmNotificationProgress.setVisibility(View.GONE);
            confirmCancelButton.setVisibility(View.VISIBLE);
        }
    }

    void setConfirmCancelButton(){
        if (orderObject.status.equals("PENDING")){
            confirmCancelButton.setOnClickListener(v -> {
                showProgress(true);
                confirmOrder(orderObject, true, this);
                finish();
            });
        } else {
            confirmCancelButton.setVisibility(View.GONE);
            statucMessage.setVisibility(View.VISIBLE);
        }

        if (orderObject.status.equals("CONFIRMED")){
            statucMessage.setText("CONFIRMED");
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.confirm));
        } else if (orderObject.status.equals("CANCELED")){
            statucMessage.setText("CANCELED");
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.canceled));
        } else if (orderObject.status.equals("FINISHED")){
            statucMessage.setText("FINISHED");
            statucMessage.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    void confirmOrder(OrderObject order, boolean confirm, Context context){
        PostAsyncTask orderStatus = new PostAsyncTask(context.getString(R.string.server) + "/set_order_status/");
        order.status = confirm ? "CONFIRMED" : "CANCELED";
        tasks.add(orderStatus.execute(
                new String[]{"order_id",  order.id + ""},
                new String[]{"order_status", order.status}
        ));
    }
    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "POST", this.uri, params);
            } catch (IOException e) {
                showProgress(false);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
        }
    }

    void goToProfileView(int userId){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", userId);
        startActivity(k);
    }
    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
