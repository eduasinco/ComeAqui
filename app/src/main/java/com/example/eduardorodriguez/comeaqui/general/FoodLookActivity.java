package com.example.eduardorodriguez.comeaqui.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment.PaymentMethodsActivity;


import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.message_fragments.OneOptionMessageFragment;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.example.eduardorodriguez.comeaqui.utilities.HorizontalImageDisplayFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class FoodLookActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    boolean isCollapsed = true;

    TextView plateNameView;
    TextView descriptionView;
    TextView priceView;
    TextView timeView;
    TextView usernameView;
    TextView posterNameView;
    TextView posterLocationView;
    TextView changePaymentMethod;
    Button placeOrderButton;

    ImageView posterImage;
    LinearLayout paymentMethod;
    LinearLayout profileLook;
    View placeOrderProgress;
    FrameLayout placeOrderErrorMessage;
    LinearLayout dinnersListView;
    ImageView[] dinnerArray;
    FrameLayout waitingFrame;
    Menu collapseMenu;

    FoodPostDetail foodPostDetail;
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_look);


        toolbar = findViewById(R.id.anim_toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.appbar);

        plateNameView = findViewById(R.id.postPlateName);
        descriptionView = findViewById(R.id.post_description);
        priceView = findViewById(R.id.price);
        timeView = findViewById(R.id.time);
        placeOrderButton = findViewById(R.id.placeOrderButton);
        usernameView = findViewById(R.id.username);
        posterNameView = findViewById(R.id.poster_name);
        posterLocationView = findViewById(R.id.posterLocation);

        posterImage = findViewById(R.id.poster_image);
        paymentMethod = findViewById(R.id.payment_method_layout);
        changePaymentMethod = findViewById(R.id.change_payment);
        placeOrderProgress = findViewById(R.id.place_order_progress);
        dinnersListView = findViewById(R.id.dinners_list_view);
        profileLook = findViewById(R.id.profile_look);

        waitingFrame = findViewById(R.id.waiting_frame);
        setToolbar();



        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("foodPostId") != null){
            int fpId = b.getInt("foodPostId");
            getFoodPostDetailsAndSet(fpId);
        }
        changePaymentMethod.setOnClickListener(v -> {
            Intent paymentMethod = new Intent(this, PaymentMethodsActivity.class);
            startActivity(paymentMethod);
        });

    }

    void setToolbar(){
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitle("Food Post");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorPrimary_trans));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.collapseMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        setCollapseLogic();
        return true;
    }

    int vo = 1;
    void setCollapseLogic(){
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(vo)-appBarLayout.getTotalScrollRange() == 0) {
                if (!isCollapsed){
                    isCollapsed = true;
                    final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow_white);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    findViewById(R.id.action_settings).setBackground(ContextCompat.getDrawable(this, R.drawable.collapse_three_dots));
                    findViewById(R.id.other).setBackground(ContextCompat.getDrawable(this, R.drawable.collapse_plus));
                }
            } else {
                if (isCollapsed){
                    isCollapsed = false;
                    final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow_with_background);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    findViewById(R.id.action_settings).setBackground(ContextCompat.getDrawable(this, R.drawable.three_dots_with_background));
                    findViewById(R.id.other).setBackground(ContextCompat.getDrawable(this, R.drawable.plus_with_bacground));
                }
            }
            vo = verticalOffset;
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_settings));
        if (foodPostDetail.owner.id == USER.id){
            popupMenu.getMenu().add("Edit");
            if (foodPostDetail.confirmedOrdersList.size() == 0 || (foodPostDetail.confirmedOrdersList.size() > 0 && foodPostDetail.confirmedOrdersList.get(0).status.equals("FINISHED"))){
                popupMenu.getMenu().add("Delete");
            } else {
                Toast.makeText(this, "You can't delete a confirmed post", Toast.LENGTH_LONG).show();
            }
        } else {
            popupMenu.getMenu().add("Report");
        }

        popupMenu.setOnMenuItemClickListener(item1 -> {
            String title = item1.getTitle().toString();
            switch (title){
                case "Edit":
                    editFoodPost();
                    break;
                case "Delete":
                    deleteOrder();
                    break;
                case "Report":
                    break;
            }
            return false;
        });
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                popupMenu.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void editFoodPost(){
        Intent k = new Intent(this, EditFoodPostActivity.class);
        k.putExtra("foodPostId", foodPostDetail.id);
        startActivity(k);
    }

    void setDinners(){

        dinnerArray = new ImageView[]{
                findViewById(R.id.dinner0),
                findViewById(R.id.dinner1),
                findViewById(R.id.dinner2),
                findViewById(R.id.dinner3),
                findViewById(R.id.dinner4),
                findViewById(R.id.dinner5),
                findViewById(R.id.dinner6),
                findViewById(R.id.dinner7)
        };
        TextView dinnersNumber = findViewById(R.id.dinners_number);

        dinnersListView.setOnClickListener((v) -> {
            if (foodPostDetail.confirmedOrdersList.size() > 0) {
                startActivity(new Intent(this, DinnerListActivity.class).putExtra("foodPostId", foodPostDetail.id));
            }
        });

        if (foodPostDetail.confirmedOrdersList.size() > 0){
            dinnersNumber.setText(foodPostDetail.confirmedOrdersList.size() + "/" + foodPostDetail.max_dinners + " dinners for this meal");
        } else {
            dinnersNumber.setText("No dinners for this meal yet");
        }
        int i = 0;
        while (i < dinnerArray.length && i < foodPostDetail.confirmedOrdersList.size()){
            if(!foodPostDetail.confirmedOrdersList.get(i).owner.profile_photo.contains("no-image")) {
                Glide.with(this).load(foodPostDetail.confirmedOrdersList.get(i).owner.profile_photo).into(dinnerArray[i]);
            }
            dinnerArray[i].setVisibility(View.VISIBLE);
            i++;
        }
    }

    void setDetails(){
        posterNameView.setText(foodPostDetail.owner.first_name + " " + foodPostDetail.owner.last_name);
        usernameView.setText("@" + foodPostDetail.owner.username);
        plateNameView.setText(foodPostDetail.plate_name);
        descriptionView.setText(foodPostDetail.description);
        posterLocationView.setText(foodPostDetail.formatted_address);
        priceView.setText(foodPostDetail.price + "$");
        timeView.setText(foodPostDetail.time_to_show);

        Bundle bundle = new Bundle();
        bundle.putSerializable("type", foodPostDetail.type);
        FoodTypeFragment fragment = new FoodTypeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.types, fragment)
                .commit();


        if(!foodPostDetail.owner.profile_photo.contains("no-image")) {
            Glide.with(this).load(foodPostDetail.owner.profile_photo).into(posterImage);
            profileLook.setOnClickListener(v -> goToProfileView(foodPostDetail.owner));
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.static_map_frame, StaticMapFragment.newInstance(foodPostDetail.lat, foodPostDetail.lng))
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalImageDisplayFragment.newInstance(foodPostDetail.id, 0, 4, 200,0, 0))
                .commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.waiting_frame, WaitFragment.newInstance())
                .commit();

        setPlaceButton();
        setDinners();
    }

    void getFoodPostDetailsAndSet(int foodPostId){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostId + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
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
                foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
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

    void goToProfileView(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
    }

    void showProgress(boolean show){
        if (show){
            placeOrderButton.setVisibility(View.GONE);
            placeOrderProgress.setVisibility(View.VISIBLE);
        } else {
            placeOrderButton.setVisibility(View.VISIBLE);
            placeOrderProgress.setVisibility(View.GONE);
        }
    }

    void setPlaceButton(){
        if (foodPostDetail.owner.id == USER.id){
            if (foodPostDetail.confirmedOrdersList.size() > 0){
                placeOrderButton.setText("Post confirmed");
                placeOrderButton.setBackgroundColor(Color.TRANSPARENT);
                placeOrderButton.setTextColor(ContextCompat.getColor(this, R.color.success));
            } else {
                placeOrderButton.setVisibility(View.INVISIBLE);
            }
            paymentMethod.setVisibility(View.GONE);
        }else{
            placeOrderButton.setOnClickListener(v -> {
                showProgress(true);
                createOrder();
            });
        }
    }

    void deleteOrder(){
        tasks.add(new DeletePostAsyncTask(getResources().getString(R.string.server) + "/foods/" + foodPostDetail.id + "/").execute());
    }

    class DeletePostAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public DeletePostAsyncTask(String uri){
            this.uri = uri;
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
            if (response != null){
                finish();
            }
            super.onPostExecute(response);
        }
    }

    void createOrder(){
        PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_order_and_notification/");
        tasks.add(createOrder.execute(new String[]{"food_post_id", "" + foodPostDetail.id}));
    }
    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
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
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject().get("order").getAsJsonObject();
                OrderObject orderObject = new OrderObject(jo);
                goToOrder(orderObject);
                finish();
            }
            super.onPostExecute(response);
        }
    }

    void goToOrder(OrderObject orderObject){
        try{
            Intent goToOrders = new Intent(this, OrderLookActivity.class);
            goToOrders.putExtra("orderId", orderObject.id);
            startActivity(goToOrders);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void showErrorMessage(){
        placeOrderErrorMessage.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.error_message_frame, OneOptionMessageFragment.newInstance(
                        "Error during posting",
                        "Please make sure that you have connection to the internet"))
                .commit();
    }
    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
