package com.example.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostReview;
import com.example.eduardorodriguez.comeaqui.objects.ReviewReplyObject;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.utilities.ErrorMessageFragment;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FoodPostReviewLookActivity extends AppCompatActivity implements MyFoodReviewRecyclerViewAdapter.OnListFragmentInteractionListener {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView recList;
    private Menu collapseMenu;

    private ArrayList<ReviewObject> reviews;
    private MyFoodReviewRecyclerViewAdapter adapter;
    FoodPostReview foodPostReview;


    public TextView foodNameView;
    public TextView postPrice;
    public TextView posterDescriptionView;
    public TextView postNameView;
    public TextView postRating;
    public View cardButtonView;
    public ImageView header1;

    ImageView vegetarian;
    ImageView vegan;
    ImageView cereal;
    ImageView spicy;
    ImageView fish;
    ImageView meat;
    ImageView dairy;

    private boolean appBarExpanded = true;

    FoodPost fp;

    @Override
    protected void onResume() {
        super.onResume();
        getReviewsFrompFoodPost(fp.id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_post_review_look);
        toolbar = findViewById(R.id.anim_toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.appbar);
        recList = findViewById(R.id.scrollableview);

        foodNameView = findViewById(R.id.plateName);
        postNameView = findViewById(R.id.plate_name);
        postPrice = findViewById(R.id.price);
        posterDescriptionView = findViewById(R.id.description_post_review);
        cardButtonView = findViewById(R.id.cardButton);
        postRating = findViewById(R.id.food_post_review_rating);
        header1 = findViewById(R.id.header1);

        vegetarian = findViewById(R.id.vegetarian);
        vegan = findViewById(R.id.vegan);
        cereal = findViewById(R.id.cereal);
        spicy = findViewById(R.id.spicy);
        fish = findViewById(R.id.fish);
        meat = findViewById(R.id.meat);
        dairy = findViewById(R.id.dairy);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            fp = (FoodPost) b.getSerializable("foodPost");
            getReviewsFrompFoodPost(fp.id);
        }

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

    void getReviewsFrompFoodPost(int foodPostId){
        Context activity = this;
        new GetAsyncTask("GET", getResources().getString(R.string.server) + "/food_reviews/" + foodPostId + "/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    foodPostReview = new FoodPostReview(new JsonParser().parse(response).getAsJsonObject());
                    reviews = foodPostReview.reviews;
                    adapter = new MyFoodReviewRecyclerViewAdapter(reviews, activity, foodPostReview.owner);
                    recList.setAdapter(adapter);
                    setViewFoodPost();
                }
                super.onPostExecute(response);
            }
        }.execute();
    }

    void setViewFoodPost(){
        postNameView.setText(foodPostReview.plate_name);
        posterDescriptionView.setText(foodPostReview.description);
        posterDescriptionView.setVisibility(View.VISIBLE);
        postPrice.setText(foodPostReview.price + "€");

        String rating = "-.-";
        if (foodPostReview.rating != 0){
            rating = String.format("%.01f", foodPostReview.rating);
        }
        postRating.setText(rating);


        if (!foodPostReview.food_photo.contains("no-image")) {
            header1.setVisibility(View.VISIBLE);
            Glide.with(this).load(foodPostReview.food_photo).into(header1);
            header1.setOnClickListener((v) -> {
                Intent imageLook = new Intent(this, ImageLookActivity.class);
                imageLook.putExtra("image_url", foodPostReview.food_photo);
                startActivity(imageLook);
            });
        }
        setTypes(foodPostReview.type);
    }

    void setTypes(String types){
        ImageView[] imageViews = new ImageView[]{
                vegetarian,
                vegan,
                cereal,
                spicy,
                fish,
                meat,
                dairy
        };
        ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
        for (ImageView imageView: imageViews){
            imageView.setVisibility(View.GONE);
            imageViewArrayList.add(imageView);
        }
        int[] resources = new int[]{
                R.drawable.vegetarianfill,
                R.drawable.veganfill,
                R.drawable.cerealfill,
                R.drawable.spicyfill,
                R.drawable.fishfill,
                R.drawable.meatfill,
                R.drawable.dairyfill,
        };
        for (int i = 0; i < types.length(); i++){
            if (types.charAt(i) == '1'){
                imageViewArrayList.get(i).setImageResource(resources[i]);
                imageViewArrayList.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.collapseMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Setting menu clicked!", Toast.LENGTH_SHORT).show();
                break;
        }

        if(item.getTitle() == "Add"){
            Toast.makeText(this, "Add menu clicked!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    void showErrorMessage(String message){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.error_message_frame, ErrorMessageFragment.newInstance(
                        "Error during posting",
                        "Please make sure that you have connection to the internet"))
                .commit();
    }
    void deleteReview(int reviewId){
        Server deleteFoodPost = new Server("DELETE", getResources().getString(R.string.server) + "/delete_review/" + reviewId + "/");
        try {
            deleteFoodPost.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showErrorMessage("Error deleting review");
        }
    }

    void deleteReply(int replyId){
        Server deleteFoodPost = new Server("DELETE", getResources().getString(R.string.server) + "/delete_reply/" + replyId + "/");
        try {
            deleteFoodPost.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            showErrorMessage("Error deleting reply");
        }
    }


    @Override
    public void onReviewDelete(ReviewObject review) {
        deleteReview(review.id);
    }

    @Override
    public void onReplyDelete(ReviewReplyObject reply) {
        deleteReply(reply.id);
    }

    @Override
    public void onReplyCreate(ReviewObject review) {
        Intent paymentMethod = new Intent(this, ReplyReviewActivity.class);
        paymentMethod.putExtra("review", review);
        startActivity(paymentMethod);
    }
}
