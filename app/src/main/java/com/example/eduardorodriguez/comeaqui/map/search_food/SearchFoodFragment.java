package com.example.eduardorodriguez.comeaqui.map.search_food;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private double lat;
    private double lng;

    private OnListFragmentInteractionListener mListener;

    private ArrayList<FoodPost> foodPosts;
    private MySearchFoodRecyclerViewAdapter adapter;


    RecyclerView recyclerView;
    HorizontalScrollView filterScroll;
    TextView allButton;
    TextView sortButton;
    TextView mealTimeButton;
    TextView priceButton;
    TextView distanceButton;
    TextView dietaryButton;
    TextView delleteAllButton;

    int INITIAL_DISTANCE = 5000;
    int distance = INITIAL_DISTANCE;

    PlaceAutocompleteFragment placeAutocompleteFragment;
    FilterFragment filterFragment;
    StringBuilder query = new StringBuilder();

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public SearchFoodFragment() {}

    public static SearchFoodFragment newInstance(double lat, double lng) {
        SearchFoodFragment fragment = new SearchFoodFragment();
        Bundle args = new Bundle();
        args.putDouble(LAT, lat);
        args.putDouble(LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getDouble(LAT);
            lng = getArguments().getDouble(LNG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchfood_list, container, false);

        recyclerView = view.findViewById(R.id.food_search_list);

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

        filterFragment = FilterFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.filter_frame, filterFragment)
                .commit();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MySearchFoodRecyclerViewAdapter(foodPosts, mListener);
        recyclerView.setAdapter(adapter);

        getDistanceIntoQuery(distance);
        showFilterLogic();
        getFilteredPosts();

        delleteAllButton.setOnClickListener(v -> {
            deleteAllFilter();
        });
        return view;
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

    void getDistanceIntoQuery(int distance){
        this.distance = distance;
        LatLng right = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 0);
        LatLng top = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 90);
        LatLng left = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 180);
        LatLng down = SphericalUtil.computeOffset(new LatLng(lat, lng), distance, 270);
        query.insert(0,"distance=" + right.latitude + "," + top.longitude + "," + left.latitude + "," + down.longitude);
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

        getDistanceIntoQuery(INITIAL_DISTANCE);
        getFilteredPosts();
    }

    @Override
    public void onApplyFilterClicked() {
        getDistanceIntoQuery(distance);
        getFilteredPosts();
    }

    @Override
    public void onSort(int option) {
        query.append("&sort=" + option);
        darkenFilterButton(true, sortButton);
    }

    @Override
    public void onPrice(int option) {
        query.append("&price=" + option);
        darkenFilterButton(true, priceButton);
    }

    @Override
    public void onMealTime(String startDateTime, String endDateTime) {
        query.append("&start_date=" + startDateTime);
        query.append("&end_date=" + endDateTime);
        darkenFilterButton(true, mealTimeButton);
    }

    @Override
    public void onDistance(int distance) {
        getDistanceIntoQuery(distance);
        darkenFilterButton(true, distanceButton);
    }

    @Override
    public void onDietary(String dietary) {
        query.append("&dietary=" + dietary);
        darkenFilterButton(true, dietaryButton);
    }

    void getFilteredPosts(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/food_query/" + query.toString() + "/").execute());
        query = new StringBuilder();
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
        mListener.close();
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
        void close();
    }
}
