package com.comeaqui.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comeaqui.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.comeaqui.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.comeaqui.eduardorodriguez.comeaqui.general.FoodLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.general.StaticMapFragment;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;


import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments.TwoOptionsMessageFragment;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class OrderLookActivity extends AppCompatActivity implements TwoOptionsMessageFragment.OnFragmentInteractionListener {

    ImageButton back;
    ImageButton options;
    CardView orderCard;
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
    Button chatWithHostButton;

    ImageView posterImageView;
    FrameLayout waitingFrame;


    TwoOptionsMessageFragment continueCancelFragment;
    OrderObject order;

    Context context;
    ArrayList<AsyncTask> tasks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_look);
        context = getApplicationContext();
        back = findViewById(R.id.back);
        orderCard = findViewById(R.id.orderCard);
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
        chatWithHostButton = findViewById(R.id.chat_with_host_button);

        posterImageView = findViewById(R.id.poster_image);
        waitingFrame = findViewById(R.id.waiting_frame);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_frame, WaitFragment.newInstance())
                .commit();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null){
            int orderId = b.getInt("orderId");
            getOrderDetails(orderId);
        }

        continueCancelFragment = TwoOptionsMessageFragment.newInstance(
                "Canceling the order",
                "Should you cancel after confirmation you would still owe the full fee. Are you sure you want to cancel?",
                "NO",
                "CANCEL",
                true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cancel_message, continueCancelFragment)
                .commit();

        back.setOnClickListener(v -> finish());
        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("Help");

            if (order.status.equals("CONFIRMED") || order.status.equals("PENDING"))
                popupMenu.getMenu().add("Cancel order");

            popupMenu.setOnMenuItemClickListener(item -> {
                setOptionsActions(item.getTitle().toString());
                return true;
            });
            popupMenu.show();
        });

    }

    void setView(){
        plateName.setText(order.post.plate_name);
        posterUsername.setText(order.poster.username);
        posterNameView.setText(order.poster.first_name + " " + order.poster.last_name);
        posterDescription.setText(order.post.description);
        posterLocationView.setText(order.post.formatted_address);
        price.setText(order.price_to_show);
        subtotalView.setText(order.price_to_show);
        totalPriceView.setText(order.price_to_show);
        mealTimeView.setText(order.post.time_to_show + " " + order.post.time_range);
        orderStatus.setText(order.status);

        if (order.status.equals("CONFIRMED")){
            orderStatus.setTextColor(getResources().getColor(R.color.success));
        } else if (order.status.equals("CANCELED") || order.status.equals("REJECTED")){
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

        setChatWithHostButton();
        orderCard.setOnClickListener(v -> goToPostLook(order.post.id));
    }

    void setChatWithHostButton(){
        if (order.status.equals("CONFIRMED") || order.status.equals("PENDING")){
            chatWithHostButton.setVisibility(View.VISIBLE);
        }
        chatWithHostButton.setOnClickListener(v -> {
            goToConversationWithUser(order.poster);
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
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/order_detail/" + orderId + "/").execute());
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
            if (response != null) {
                order = new OrderObject(new JsonParser().parse(response).getAsJsonObject());
                setView();
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

    void goToPostLook(int foodPostId) {
        Intent foodLook = new Intent(this, FoodLookActivity.class);
        foodLook.putExtra("foodPostId", foodPostId);
        startActivity(foodLook);
    }
    void goToProfileView(User user){
        Intent k = new Intent(getApplicationContext(), ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
    }

    void checkIfUserWantsToCancel(){
        continueCancelFragment.show(true);
    }

    void cancelOrder(){
        order.status = "CANCELED";
        tasks.add(new PostAsyncTask(context.getString(R.string.server) + "/set_order_status/").execute(
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
            continueCancelFragment.show(false);
            finish();
            super.onPostExecute(response);
        }
    }

    void goToConversationWithUser(User user){
        new GetOrCreateChatAsyncTask(getResources().getString(R.string.server) + "/get_or_create_chat/" + user.id + "/").execute();
    }
    class GetOrCreateChatAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetOrCreateChatAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
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
            if (response != null) {
                ChatObject chat = new ChatObject(new JsonParser().parse(response).getAsJsonObject());
                goToConversationActivity(chat);
            }
            super.onPostExecute(response);
        }

    }

    void goToConversationActivity(ChatObject chat){
        Intent k = new Intent(this, ConversationActivity.class);
        k.putExtra("chatId", chat.id + "");
        startActivity(k);
    }

    @Override
    public void leftButtonPressed() {

    }

    @Override
    public void rightButtonPressed() {
        cancelOrder();
    }

    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
