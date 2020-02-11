package com.comeaqui.eduardorodriguez.comeaqui.order;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments.FoodCommentFragment;
import com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments.MyFoodCommentRecyclerViewAdapter;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.OrderObject;
import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;


import com.comeaqui.eduardorodriguez.comeaqui.review.food_review_look.ReplyReviewOrCommentActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.Server;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.RatingFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.message_fragments.TwoOptionsMessageFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class OrderLookActivity extends AppCompatActivity implements
        TwoOptionsMessageFragment.OnFragmentInteractionListener,
        MyFoodCommentRecyclerViewAdapter.OnListFragmentInteractionListener{

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

    ImageView posterImageView;
    FrameLayout waitingFrame;

    FoodCommentFragment foodCommentFragment;
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
        posterUsername = findViewById(R.id.meal);
        posterDescription = findViewById(R.id.description);
        posterLocationView = findViewById(R.id.posterLocation);
        price = findViewById(R.id.price);
        subtotalView = findViewById(R.id.postSubtotalPrice);
        totalPriceView = findViewById(R.id.totalPrice);
        mealTimeView = findViewById(R.id.time);
        orderStatus = findViewById(R.id.order_status);

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

        setCommentsIfConfirmed();
        orderCard.setOnClickListener(v -> goToPostLook(order.post.id));
    }

    void setCommentsIfConfirmed(){
        if (order.status.equals("CONFIRMED") || order.status.equals("PENDING")){
            foodCommentFragment = FoodCommentFragment.newInstance(order.post.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.foodpost_comments, foodCommentFragment)
                    .commit();
        }
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


    void deleteComment(int commentId){
        tasks.add(new DeletePostAsyncTask(getResources().getString(R.string.server) + "/delete_food_post_comment/" + commentId + "/").execute());
    }

    void deleteCommentVote(int commentId){
        if (!anyTaskRunning()) {
            tasks.add(new DeletePostAsyncTask(getResources().getString(R.string.server) + "/vote_comment/" + commentId + "/").execute());
        }
    }

    class DeletePostAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public DeletePostAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startWaitingFrame(true);
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.delete(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                foodCommentFragment.updateElement(new FoodCommentObject(jo));
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }
    }


    void onCommentVote(int comment_id, boolean is_up_vote){
        if (!anyTaskRunning()){
            tasks.add(new PostVoteAsyncTask(getResources().getString(R.string.server) + "/vote_comment/" + comment_id + "/").execute(
                    new String[]{"is_up_vote", is_up_vote ? "True" : "False"}
            ));
        }
    }

    private class PostVoteAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PostVoteAsyncTask(String uri){
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
                foodCommentFragment.updateElement(new FoodCommentObject(jo));
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }
    }

    boolean anyTaskRunning(){
        for (AsyncTask task: tasks){
            if (task != null && task.getStatus() == AsyncTask.Status.RUNNING){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCommentDelete(FoodCommentObject comment) {
        deleteComment(comment.id);
    }

    @Override
    public void onVoteComment(FoodCommentObject comment, boolean is_up_vote) {
        onCommentVote(comment.id, is_up_vote);
    }

    @Override
    public void onDeleteVoteComment(FoodCommentObject comment) {
        deleteCommentVote(comment.id);
    }

    @Override
    public void onCommentCreate(FoodCommentObject comment) {
        Intent paymentMethod = new Intent(this, ReplyReviewOrCommentActivity.class);
        paymentMethod.putExtra("comment", comment);
        startActivity(paymentMethod);
    }

    @Override
    public void onGoToProfile(User user){
        Intent profile = new Intent(this, ProfileViewActivity.class);
        profile.putExtra("userId", user.id);
        startActivity(profile);
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
