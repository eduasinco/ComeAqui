package com.example.eduardorodriguez.comeaqui.map.search_food;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.search_food.filter_fragment.FilterFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.place_autocomplete.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class SearchFoodFragment extends Fragment implements
        View.OnTouchListener,
        PlaceAutocompleteFragment.OnFragmentInteractionListener,
        FilterFragment.OnFragmentInteractionListener
{
    private double lat;
    private double lng;

    private OnListFragmentInteractionListener mListener;

    private ArrayList<FoodPost> foodPosts;
    private MySearchFoodRecyclerViewAdapter adapter;


    private CoordinatorLayout cardView;
    private RecyclerView recyclerView;
    private HorizontalScrollView filterScroll;
    private TextView allButton;
    private TextView sortButton;
    private TextView mealTimeButton;
    private TextView priceButton;
    private TextView distanceButton;
    private TextView dietaryButton;
    private TextView delleteAllButton;

    private boolean[] onFilters = new boolean[]{false, false, false, false, false, false};
    int INITIAL_DISTANCE = 5000;

    private int sortOption;
    private int priceOption;
    private int distance = INITIAL_DISTANCE;
    private String startTime;
    private String endTime;
    private String dietary;

    PlaceAutocompleteFragment placeAutocompleteFragment;
    FilterFragment filterFragment;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public SearchFoodFragment() {}

    public static SearchFoodFragment newInstance() {
        return new SearchFoodFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setLocation(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
        getDistanceIntoQuery(INITIAL_DISTANCE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchfood_list, container, false);

        recyclerView = view.findViewById(R.id.food_search_list);

        cardView = view.findViewById(R.id.card_view);
        filterScroll = view.findViewById(R.id.filter_scroll);
        allButton = view.findViewById(R.id.all);
        sortButton = view.findViewById(R.id.sort);
        priceButton = view.findViewById(R.id.price);
        mealTimeButton = view.findViewById(R.id.meal_time);
        distanceButton = view.findViewById(R.id.distance);
        dietaryButton = view.findViewById(R.id.dietary);
        delleteAllButton = view.findViewById(R.id.dellete_all);

        placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance("", true);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.search_box, placeAutocompleteFragment)
                .commit();

        filterFragment = FilterFragment.newInstance(INITIAL_DISTANCE);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.filter_frame, filterFragment)
                .commit();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MySearchFoodRecyclerViewAdapter(foodPosts, mListener);
        recyclerView.setAdapter(adapter);

        showFilterLogic();

        delleteAllButton.setOnClickListener(v -> {
            deleteAllFilter();
        });
        return view;
    }

    public void showSearchList(boolean show){
        int move = cardView.getMeasuredHeight() + ((ConstraintLayout.LayoutParams) cardView.getLayoutParams()).bottomMargin * 2;
        if (show) {
            cardView.setTranslationY(move);
            cardView.setVisibility(View.VISIBLE);
            cardView.animate().translationY(0).setDuration(move / 4).withEndAction(() -> {
                getFilteredPosts();
            });
        } else {
            cardView.animate().translationY(move).setDuration(move / 4).withEndAction(() -> mListener.closeSearch());
        }
    }

    void showFilterLogic(){
        View[] filters = new View[]{sortButton, priceButton, mealTimeButton, distanceButton, dietaryButton, allButton};
        for (int i = 0; i < filters.length; i++){
            final int finalI = i;
            filters[i].setOnClickListener(v -> {
                filterFragment.showFilter(true, finalI);
            });
        }
    }

    public void makeList(JsonArray jsonArray){
        try {
            foodPosts = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                FoodPost chat = new FoodPost(jo);
                foodPosts.add(chat);
            }
            adapter.addData(foodPosts);
            adapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    String getDistanceIntoQuery(int distance){
        this.distance = distance;
        LatLng right = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 0);
        LatLng top = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 90);
        LatLng left = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 180);
        LatLng down = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 270);
        return "distance=" + right.latitude + "," + top.longitude + "," + left.latitude + "," + down.longitude;
    }

    void darkenFilterButton(boolean darken, TextView button){
        if (darken){
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.food_filters_dark));
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.food_filters_light));
            button.setTextColor(Color.BLACK);
        }

    }

    void deleteAllFilter(){
        filterScroll.fullScroll(ScrollView.FOCUS_UP);

        darkenFilterButton(false, sortButton);
        darkenFilterButton(false, priceButton);
        darkenFilterButton(false, mealTimeButton);
        darkenFilterButton(false, distanceButton);
        darkenFilterButton(false, dietaryButton);

        for (int i = 0; i < onFilters.length; i++){
            onFilters[i] = false;
        }

        this.distance = INITIAL_DISTANCE;
        getDistanceIntoQuery(INITIAL_DISTANCE);
        getFilteredPosts();
    }

    String createQuery(){
        StringBuilder query = new StringBuilder();
        if (onFilters[0]){
            query.append("&sort=").append(sortOption);
        }
        if (onFilters[1]){
            query.append("&price=").append(priceOption);
        }
        if (onFilters[2]){
            query.append("&start_date=").append(startTime);
            query.append("&end_date=").append(endTime);
        }
        if (onFilters[3]){
            query.insert(0, getDistanceIntoQuery(distance));
        } else {
            query.insert(0, getDistanceIntoQuery(INITIAL_DISTANCE));
        }
        if (onFilters[4]){
            query.append("&dietary=").append(dietary);
        }
        if (onFilters[5]){
            query.append("&sort=").append(sortOption);
            query.append("&price=").append(priceOption);
            query.append("&start_date=").append(startTime);
            query.append("&end_date=").append(endTime);
            query.insert(0, getDistanceIntoQuery(distance));
            query.append("&dietary=").append(dietary);
        }

        checkShowDeleteAll();
        return query.toString();
    }

    void checkShowDeleteAll(){
        delleteAllButton.setVisibility(View.GONE);
        for (boolean b: onFilters){
            if (b){
                delleteAllButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onApplyFilterClicked() {
        getDistanceIntoQuery(distance);
        getFilteredPosts();
    }

    @Override
    public void onSort(int option) {
        sortOption = option;
        onFilters[0] = true;
        darkenFilterButton(true, sortButton);
    }

    @Override
    public void onPrice(int option) {
        priceOption = option;
        onFilters[1] = true;
        darkenFilterButton(true, priceButton);
    }

    @Override
    public void onMealTime(String startDateTime, String endDateTime) {
        startTime = startDateTime;
        endTime = endDateTime;
        onFilters[2] = true;

        darkenFilterButton(true, mealTimeButton);
    }

    @Override
    public void onDistance(int distance) {
        this.distance = distance;
        onFilters[3] = true;
        darkenFilterButton(true, distanceButton);
    }

    @Override
    public void onDietary(String dietary) {
        this.dietary = dietary;
        onFilters[4] = true;
        darkenFilterButton(true, dietaryButton);
    }

    void getFilteredPosts(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/food_query/" + createQuery() + "/").execute());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        filterFragment.showFilter(false, 0);
        return false;
    }

    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                makeList(new JsonParser().parse(response).getAsJsonArray());
                super.onPostExecute(response);
            }
            super.onPostExecute(response);
        }

    }


    @Override
    public void onListPlaceChosen(String address, String place_id, Double lat, Double lng, HashMap<String, String> address_elements) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public void searchButtonClicked() {
        getDistanceIntoQuery(distance);
        getFilteredPosts();
    }

    @Override
    public void onPlacesAutocompleteChangeText() {

    }

    @Override
    public void closeButtonPressed() {
        showSearchList(false);
    }


    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(FoodPost item);
        void closeSearch();
    }
}
