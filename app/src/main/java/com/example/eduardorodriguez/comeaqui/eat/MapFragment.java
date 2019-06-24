package com.example.eduardorodriguez.comeaqui.eat;

import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.food.AddFoodActivity;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    static View rootView;
    private static GoogleMap googleMap;
    public static ArrayList<MapPost> data;
    static boolean entered = false;

    public static OrderObject goOrder;

    public static void setMarkers(){
        for (int i = 0; i < data.size(); i++){
            float lat = Float.parseFloat(data.get(i).poster_lat);
            float lng = Float.parseFloat(data.get(i).poster_lng);

            Marker marker =  googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng)));
            marker.setTag(i);

            Drawable myIcon = rootView.getResources().getDrawable( R.drawable.map_food_icon);
            ColorFilter filter = new LightingColorFilter(
                    ContextCompat.getColor(rootView.getContext(), R.color.colorPrimary),
                    ContextCompat.getColor(rootView.getContext(), R.color.colorPrimary)
            );
            myIcon.setColorFilter(filter);
            marker.setIcon(getMarkerIconFromDrawable(myIcon));
        }
    }

    private static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new MapPost(jo));
            }
            setMarkers();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = rootView.findViewById(R.id.mapView);

        final FloatingActionButton myFab =  rootView.findViewById(R.id.fab);
        final ImageView bigPosterImageView = rootView.findViewById(R.id.bigPosterImage);
        final TextView posterEmailView = rootView.findViewById(R.id.priceText);
        final ImageView foodImageView = rootView.findViewById(R.id.plateName);
        final ImageView posterImageView = rootView.findViewById(R.id.posterImage);

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
                GetAsyncTask getPostLocations = new GetAsyncTask("foods/");
                try {
                    String response = getPostLocations.execute().get();
                    if (response != null)
                        makeList(new JsonParser().parse(response).getAsJsonArray());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

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
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(getContext(), locationResult);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {
                        bigPosterImageView.setVisibility(View.GONE);
                        final int index = (int) (marker.getTag());
                        posterEmailView.setText(data.get(index).poster_email);

                        String profile_photo = "http://127.0.0.1:8000";
                        profile_photo += data.get(index).food_photo;
                        if(!profile_photo.contains("no-image")) Glide.with(rootView.getContext()).load(profile_photo).into(foodImageView);

                        profile_photo = "http://127.0.0.1:8000/media/";
                        profile_photo += data.get(index).poster_image;
                        if(!profile_photo.contains("no-image")) Glide.with(rootView.getContext()).load(profile_photo).into(posterImageView);
                        if(!profile_photo.contains("no-image")) Glide.with(rootView.getContext()).load(profile_photo).into(bigPosterImageView);

                        // eatTimeView.setText(data.get(index).go_food_time.substring(0,5));

                        return false;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        bigPosterImageView.setVisibility(View.GONE);
                    }
                });

            }
        });
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addFood = new Intent(getActivity(), AddFoodActivity.class);
                getActivity().startActivity(addFood);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
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

class MapPost{
    String plate_name;
    String id;
    String owner;
    String price;
    String food_type;
    String description;
    String time;
    String food_photo;
    String poster_first_name;
    String poster_last_name;
    String poster_email;
    String poster_image;
    String poster_location;
    String poster_phone_number;
    String poster_phone_code;
    String poster_lat;
    String poster_lng;

    public MapPost(JsonObject jo) {
        plate_name = jo.get("plate_name").getAsString();
        owner = jo.get("owner").getAsNumber().toString();
        id = jo.get("id").getAsNumber().toString();
        price = jo.get("price").getAsString();
        food_type = jo.get("food_type").getAsString();
        description = jo.get("description").getAsString();
        food_photo = jo.get("food_photo").getAsString();
        time = jo.get("time").getAsString();

        poster_first_name = jo.get("poster_first_name").getAsString();
        poster_last_name = jo.get("poster_last_name").getAsString();
        poster_email = jo.get("poster_email").getAsString();
        poster_image = jo.get("poster_image").getAsString();
        poster_location = jo.get("poster_location").getAsString();
        poster_phone_number = jo.get("poster_phone_number").getAsString();
        poster_phone_code = jo.get("poster_phone_code").getAsString();
        poster_lat = jo.get("poster_lat").isJsonNull() ? "0": jo.get("poster_lat").getAsString();
        poster_lng = jo.get("poster_lng").isJsonNull() ? "0": jo.get("poster_lng").getAsString();
    }
}
