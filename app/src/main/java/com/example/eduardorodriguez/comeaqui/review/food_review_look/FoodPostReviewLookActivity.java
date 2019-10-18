package com.example.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostReview;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class FoodPostReviewLookActivity extends AppCompatActivity implements FoodReviewFragment.OnListFragmentInteractionListener {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView recList;
    private Menu collapseMenu;

    private ArrayList<ReviewObject> reviews;
    private MyFoodReviewRecyclerViewAdapter adapter;
    FoodPostReview foodPostReview;

    private boolean appBarExpanded = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_post_review_look);
        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        recList = (RecyclerView) findViewById(R.id.scrollableview);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            FoodPost fp = (FoodPost) b.getSerializable("foodPost");
            getReviewsFrompFoodPost(fp.id);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.landscape);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) > 200){
                    appBarExpanded = false;
                }else{
                    appBarExpanded = true;
                }
                invalidateOptionsMenu();
            }
        });

    }

    void getReviewsFrompFoodPost(int foodPostId){
        new GetAsyncTask("GET", getResources().getString(R.string.server) + "/food_reviews/" + foodPostId + "/"){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    foodPostReview = new FoodPostReview(new JsonParser().parse(response).getAsJsonObject());
                    reviews = foodPostReview.reviews;
                    for (int i = 0; i < 3; i++){
                        reviews.addAll(reviews);
                    }
                    adapter = new MyFoodReviewRecyclerViewAdapter(reviews);
                    recList.setAdapter(adapter);
                }
                super.onPostExecute(response);
            }
        }.execute();
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

    @Override
    public void onListFragmentInteraction(ReviewObject item) {

    }
}
