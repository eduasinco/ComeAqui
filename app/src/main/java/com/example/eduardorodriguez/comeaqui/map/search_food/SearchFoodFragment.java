package com.example.eduardorodriguez.comeaqui.map.search_food;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        PlaceAutocompleteFragment.OnFragmentInteractionListener,
        FilterFragment.OnFragmentInteractionListener {
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private double lat;
    private double lng;

    private OnListFragmentInteractionListener mListener;

    private ArrayList<FoodPost> foodPosts;
    private MySearchFoodRecyclerViewAdapter adapter;


    RecyclerView recyclerView;
    TextView allButton;
    TextView sortButton;
    TextView mealTimeButton;
    TextView priceButton;
    TextView distanceButton;
    TextView dietaryButton;

    int distance = 5000;

    PlaceAutocompleteFragment placeAutocompleteFragment;
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
        allButton = view.findViewById(R.id.all);
        sortButton = view.findViewById(R.id.sort);
        priceButton = view.findViewById(R.id.price);
        mealTimeButton = view.findViewById(R.id.meal_time);
        distanceButton = view.findViewById(R.id.distance);
        dietaryButton = view.findViewById(R.id.dietary);

        placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance("", true);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.search_box, placeAutocompleteFragment)
                .commit();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MySearchFoodRecyclerViewAdapter(foodPosts, mListener);
        recyclerView.setAdapter(adapter);

        getDistanceIntoQuery(distance);
        getFilteredPosts();
        return view;
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
        query.append("distance=" + right.latitude + "," + top.longitude + "," + left.latitude + "," + down.longitude);
    }

    @Override
    public void onApplyClicked() {
        getFilteredPosts();
    }

    @Override
    public void onSort(int option) {
        query.append("&sort=" + option);
    }

    @Override
    public void onPrice(int option) {
        query.append("&price=" + option);
    }

    @Override
    public void onFragmentInteraction(String startDateTime, String endDateTime) {
        query.append("&start_date=" + startDateTime);
        query.append("&end_date=" + endDateTime);
    }

    @Override
    public void onDistance(int distance) {
        getDistanceIntoQuery(distance);
    }

    @Override
    public void onDietary(String dietary) {
        query.append("&dietary=" + dietary);
    }

    void getFilteredPosts(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/food_query/" + query.toString() + "/").execute());
        query = new StringBuilder();
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
