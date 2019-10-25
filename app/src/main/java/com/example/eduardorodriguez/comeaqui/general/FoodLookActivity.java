package com.example.eduardorodriguez.comeaqui.general;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.utilities.ErrorMessageFragment;
import com.example.eduardorodriguez.comeaqui.utilities.FoodTypeFragment;
import com.example.eduardorodriguez.comeaqui.utilities.HorizontalFoodPostImageDisplayFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    ImageView staticMapView;
    LinearLayout paymentMethod;
    View placeOrderProgress;
    FrameLayout placeOrderErrorMessage;
    LinearLayout dinnersListView;
    ImageView[] dinnerArray;
    FrameLayout waitingFrame;
    Menu collapseMenu;

    FoodPostDetail foodPostDetail;

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
        staticMapView = findViewById(R.id.static_map);
        paymentMethod = findViewById(R.id.payment_method_layout);
        changePaymentMethod = findViewById(R.id.change_payment);
        placeOrderProgress = findViewById(R.id.place_order_progress);
        dinnersListView = findViewById(R.id.dinners_list_view);
        // editFoodOptions = findViewById(R.id.edit_food_options);

        waitingFrame = findViewById(R.id.waiting_frame);

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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_food);
        Palette.from(bitmap).generate(palette -> {
            Palette.Swatch vibrant = palette.getVibrantSwatch();
            if (vibrant != null) {
                collapsingToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
                collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.collapseMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu);
        setCollapseLogic();
        return true;
    }

    void setCollapseLogic(){
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if(Math.abs(verticalOffset) > 200){
                if (!isCollapsed){
                    final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow_white);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    findViewById(R.id.action_settings).setBackgroundColor(Color.TRANSPARENT);
                    findViewById(R.id.other).setBackgroundColor(Color.TRANSPARENT);
                }
                isCollapsed = true;
            }else{
                if (isCollapsed){
                    final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow_with_background);
                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    findViewById(R.id.action_settings).setBackground(ContextCompat.getDrawable(this, R.drawable.circle_in_toolbar));
                    findViewById(R.id.other).setBackground(ContextCompat.getDrawable(this, R.drawable.circle_in_toolbar));
                }
                isCollapsed = false;
            }
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_settings));
        if (foodPostDetail.owner.id == USER.id){
            popupMenu.getMenu().add("Edit");
            popupMenu.getMenu().add("Delete");
        } else {
            popupMenu.getMenu().add("Report");
        }

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                String title = item.getTitle().toString();
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
                popupMenu.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void editFoodPost(){}

    void setDinners(){
        dinnersListView.setOnClickListener((v) -> {
            startActivity(new Intent(this, DinnersListActivity.class).putExtra("foodPostId", foodPostDetail.id));
        });
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
        dinnersNumber.setText(foodPostDetail.confirmedOrdersList.size() + "/" + foodPostDetail.max_dinners + " dinners for this meal");
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
        usernameView.setText(foodPostDetail.owner.username);
        plateNameView.setText(foodPostDetail.plate_name);
        descriptionView.setText(foodPostDetail.description);
        posterLocationView.setText(foodPostDetail.address);
        priceView.setText(foodPostDetail.price + "$");
        timeView.setText(foodPostDetail.time);

        Bundle bundle = new Bundle();
        bundle.putSerializable("type", foodPostDetail.type);
        FoodTypeFragment fragment = new FoodTypeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.types, fragment)
                .commit();


        if(!foodPostDetail.owner.profile_photo.contains("no-image")) {
            Glide.with(this).load(foodPostDetail.owner.profile_photo).into(posterImage);
            posterImage.setOnClickListener(v -> goToProfileView(foodPostDetail.owner));
        }

        String url = "http://maps.google.com/maps/api/staticmap?center=" + foodPostDetail.lat + "," + foodPostDetail.lng + "&zoom=15&size=" + 300 + "x" + 200 +"&sensor=false&key=" + getResources().getString(R.string.google_key);
        Glide.with(this).load(url).into(staticMapView);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.image_list, HorizontalFoodPostImageDisplayFragment.newInstance(foodPostDetail.id, "MEDIUM"))
                .commit();

        setPlaceButton();
        setDinners();
    }

    void getFoodPostDetailsAndSet(int foodPostId){
        try{
            new GetAsyncTask("GET", getResources().getString(R.string.server) + "/foods/" + foodPostId + "/"){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null){
                        foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
                        setDetails();
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
            startWaitingFrame(false);
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_frame, WaitFragment.newInstance())
                    .commit();
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
                placeOrderButton.setText("Delete Post");
                placeOrderButton.setBackgroundColor(ContextCompat.getColor(this, R.color.canceled));
                placeOrderButton.setOnClickListener(v -> {
                    showProgress(true);
                    deleteOrder();
                });
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
        showProgress(true);
        Server deleteFoodPost = new Server("DELETE", getResources().getString(R.string.server) + "/foods/" + foodPostDetail.id + "/");
        try {
            deleteFoodPost.execute().get();
            finish();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showErrorMessage();
        }
    }

    void createOrder(){
        PostAsyncTask createOrder = new PostAsyncTask(getResources().getString(R.string.server) + "/create_order_and_notification/");
        try {
            String response = createOrder.execute(
                    new String[]{"food_post_id", "" + foodPostDetail.id}
            ).get(5, TimeUnit.SECONDS);
            JsonObject jo = new JsonParser().parse(response).getAsJsonObject().get("order").getAsJsonObject();
            OrderObject orderObject = new OrderObject(jo);
            goToOrder(orderObject);
            finish();
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
            showErrorMessage();
            showProgress(false);
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
                .replace(R.id.error_message_frame, ErrorMessageFragment.newInstance(
                        "Error during posting",
                        "Please make sure that you have connection to the internet"))
                .commit();
    }
}
