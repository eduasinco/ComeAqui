package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private static GoogleMap googleMap;
    public static ArrayList<String[]> data;

    public static void setMarkers(){
        for (String[] posterInfo: data){
            float lat = Float.parseFloat(posterInfo[14]);
            float lng = Float.parseFloat(posterInfo[15]);
            LatLng place2 = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(place2).title(posterInfo[2]).snippet(posterInfo[5]));
        }
    }

    public static void makeList(String jsonString){
        try {
            data = new ArrayList<>();
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(createStringArray(jo));
            }
            setMarkers();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String[] createStringArray(JsonObject jo){
        String id = jo.get("id").getAsNumber().toString();
        String owner = jo.get("owner").getAsNumber().toString();
        String plateName = jo.get("plate_name").getAsString();
        String price = jo.get("price").getAsString();
        String foodType = jo.get("food_type").getAsString();
        String description = jo.get("description").getAsString();
        String foodPhoto = jo.get("food_photo").getAsString();

        String posterFirstName = jo.get("poster_first_name").getAsString();
        String posterLastName = jo.get("poster_last_name").getAsString();
        String posterEmail = jo.get("poster_email").getAsString();
        String posterImage = jo.get("poster_image").getAsString();
        String posterLocation = jo.get("poster_location").getAsString();
        String posterPhoneNumber = jo.get("poster_phone_number").getAsString();
        String posterPhoneCode = jo.get("poster_phone_code").getAsString();
        String posterLat = jo.get("poster_lat").getAsString();
        String posterLng = jo.get("poster_lng").getAsString();
        String[] add = new String[]{
                id, owner, plateName, price, foodType, description, foodPhoto,
                posterFirstName, posterLastName, posterEmail, posterImage, posterLocation, posterPhoneNumber, posterPhoneCode, posterLat, posterLng
        };
        return add;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                    @Override
                    public void gotLocation(Location location){
                        //Got the location!
                        double lng = location.getLongitude();
                        double lat = location.getLatitude();

                        LatLng place = new LatLng(lat, lng);
                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(getContext(), locationResult);

                GetAsyncTask getPostLocations = new GetAsyncTask(7);
                getPostLocations.execute();

            }
        });

        FloatingActionButton myFab =  rootView.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addFood = new Intent(getActivity(), AddFoodActivity.class);
                addFood.putExtra("isGoFood", "true");
                getActivity().startActivity(addFood);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
