package com.example.eduardorodriguez.comeaqui.utilities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;


public class UpperNotificationFragment extends Fragment {

    TextView title;
    TextView username;
    TextView plateName;
    TextView time;
    TextView time2;
    ImageView posterImage;
    CardView orderCard;
    CardView postCard;
    LinearLayout allCards;

    OrderObject orderObject;
    OrderObject orderPost;
    float initialY, dY;

    public UpperNotificationFragment() {}
    public static UpperNotificationFragment newInstance() {
        return new UpperNotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getConfirmedOrders();
        getConfirmedFoodPosts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upper_notification, container, false);

        title = view.findViewById(R.id.title);
        username = view.findViewById(R.id.poster_username);
        plateName = view.findViewById(R.id.plate_name);
        time = view.findViewById(R.id.time);
        time2 = view.findViewById(R.id.time2);
        posterImage = view.findViewById(R.id.poster_image);
        orderCard = view.findViewById(R.id.order_card);
        postCard = view.findViewById(R.id.post_card);
        allCards = view.findViewById(R.id.all_cards);


        setCardView(orderCard);
        setCardView(postCard);
        return view;
    }

    void setOrderCard(){
        if (orderObject != null){
            orderCard.setVisibility(View.VISIBLE);
            title.setText("Meal with " +  orderObject.poster.first_name + orderObject.poster.last_name);
            username.setText(orderObject.poster.username);
            plateName.setText( orderObject.post.plate_name);
            time.setText(orderObject.post.time);
            if(!orderObject.poster.profile_photo.contains("no-image")) {
                Glide.with(getActivity()).load(orderObject.poster.profile_photo).into(posterImage);
            }
        }
    }

    void setOrderPost(){
        if (orderPost != null){
            postCard.setVisibility(View.VISIBLE);
            time2.setText(orderPost.post.time);
        }
    }
    
    void setCardView(View view){
        orderCard.setOnClickListener(v -> {
            Intent orderLook = new Intent(getContext(), OrderLookActivity.class);
            orderLook.putExtra("orderId", orderObject.id);
            orderLook.putExtra("delete", false);
            getContext().startActivity(orderLook);
        });
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dY = v.getY() - event.getRawY();
                    initialY = v.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getRawY() + dY < 0){
                        v.animate()
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (initialY - v.getY() > v.getHeight() / 2){
                        v.animate()
                                .y(dY)
                                .setDuration(100).withEndAction(() -> {
                                    v.setVisibility(View.GONE);
                                })
                                .start();
                    } else {
                        v.animate()
                                .y(initialY)
                                .setDuration(100)
                                .start();
                    }
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    void getConfirmedOrders(){
        new GetAsyncTask("GET", getResources().getString(R.string.server) +  "/my_next_confirmed_order/", getContext()){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                    if (ja.size() > 0){
                        orderObject = new OrderObject(ja.get(0).getAsJsonObject());
                        setOrderCard();
                    }
                }
                super.onPostExecute(response);
            }
        }.execute();
    }
    void getConfirmedFoodPosts(){
        new GetAsyncTask("GET", getResources().getString(R.string.server) +  "/my_next_confirmed_post/", getContext()){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    JsonArray ja = new JsonParser().parse(response).getAsJsonArray();
                    if (ja.size() > 0){
                        orderPost = new OrderObject(ja.get(0).getAsJsonObject());
                        setOrderPost();
                    }
                }
                super.onPostExecute(response);
            }
        }.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
