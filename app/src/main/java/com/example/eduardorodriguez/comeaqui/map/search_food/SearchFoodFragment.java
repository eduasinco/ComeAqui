package com.example.eduardorodriguez.comeaqui.map.search_food;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.search_food.filter_fragment.FilterFragment;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.MyLocation;
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
    private LinearLayout notPostFoundView;

    private boolean[] onFilters = new boolean[]{false, false, false, false, false, false};
    int INITIAL_DISTANCE = 500;

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

    void searchOnMyLocation(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                lng = location.getLongitude();
                lat = location.getLatitude();

                getDistanceIntoQuery(distance);
                getFilteredPosts();
                getLocationFromGoogle();
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getContext(), locationResult);
    }

    public void getLocationFromGoogle(){
        LatLng latLng = new LatLng(lat, lng);
        String latLngString = latLng.latitude + "," + latLng.longitude;
        String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLngString + "&key=" + getResources().getString(R.string.google_key);
        tasks.add(new GetPlaceFromGoogle(uri).execute());
    }
    class GetPlaceFromGoogle extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetPlaceFromGoogle(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.getNoCredentials(this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                JsonObject joo = new JsonParser().parse(response).getAsJsonObject();
                JsonArray jsonArray = joo.get("results").getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jo = jsonArray.get(0).getAsJsonObject();
                    JsonArray addss_components = jo.get("address_components").getAsJsonArray();
                    String address = jo.get("formatted_address").getAsString();
                    String place_id = jo.get("place_id").getAsString();
                    JsonObject jsonLocation = jo.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                    Double lat = jsonLocation.get("lat").getAsDouble();
                    Double lng = jsonLocation.get("lng").getAsDouble();

                    HashMap<String, String> address_elements = new HashMap<>();
                    for (JsonElement je: addss_components){
                        address_elements.put(je.getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString(), je.getAsJsonObject().get("long_name").getAsString());
                    }
                    placeAutocompleteFragment.setAddress(address, place_id);
                }
            }
            super.onPostExecute(response);
        }
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
        notPostFoundView = view.findViewById(R.id.no_messages);

        placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance("", true);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.search_box, placeAutocompleteFragment)
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
                filterFragment = FilterFragment.newInstance(distance, finalI);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.filter_frame, filterFragment)
                        .commit();
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
            if (foodPosts.size() > 0){
                notPostFoundView.setVisibility(View.GONE);
            } else{
                notPostFoundView.setVisibility(View.VISIBLE);
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
        filterFragment.showFilter(false);
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

    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onListPlaceChosen(String address, String place_id, Double lat, Double lng, HashMap<String, String> address_elements) {
        this.lat = lat;
        this.lng = lng;

        hideKeyboard();
        getDistanceIntoQuery(distance);
        getFilteredPosts();
    }

    @Override
    public void myLocationButton() {
        searchOnMyLocation();
        hideKeyboard();
    }

    @Override
    public void onPlacesAutocompleteChangeText(boolean isAddressValid) {}

    @Override
    public void closeButtonPressed() {
        showSearchList(false);
        hideKeyboard();
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
