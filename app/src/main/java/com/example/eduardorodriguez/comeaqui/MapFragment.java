package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
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

    static String id;
    static String owner;
    static String plateName;
    static String price;
    static String foodType;
    static String description;
    static String foodPhoto;

    static String posterFirstName;
    static String posterLastName;
    static String posterEmail;
    static String posterImage;
    static String posterLocation;
    static String posterPhoneNumber;
    static String posterPhoneCode;
    static String posterLat;
    static String posterLng;

    MapView mMapView;
    static View rootView;
    private static GoogleMap googleMap;
    public static ArrayList<String[]> data;

    public static void setMarkers(){
        for (int i = 0; i < data.size(); i++){
            float lat = Float.parseFloat(data.get(i)[14]);
            float lng = Float.parseFloat(data.get(i)[15]);

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
        id = jo.get("id").getAsNumber().toString();
        owner = jo.get("owner").getAsNumber().toString();
        plateName = jo.get("plate_name").getAsString();
        price = jo.get("price").getAsString();
        foodType = jo.get("food_type").getAsString();
        description = jo.get("description").getAsString();
        foodPhoto = jo.get("food_photo").getAsString();

        posterFirstName = jo.get("poster_first_name").getAsString();
        posterLastName = jo.get("poster_last_name").getAsString();
        posterEmail = jo.get("poster_email").getAsString();
        posterImage = jo.get("poster_image").getAsString();
        posterLocation = jo.get("poster_location").getAsString();
        posterPhoneNumber = jo.get("poster_phone_number").getAsString();
        posterPhoneCode = jo.get("poster_phone_code").getAsString();
        posterLat = jo.get("poster_lat").getAsString();
        posterLng = jo.get("poster_lng").getAsString();
        String[] add = new String[]{
                id, owner, plateName, price, foodType, description, foodPhoto,
                posterFirstName, posterLastName, posterEmail, posterImage, posterLocation,
                posterPhoneNumber, posterPhoneCode, posterLat, posterLng
        };
        return add;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = rootView.findViewById(R.id.mapView);



        final FloatingActionButton myFab =  rootView.findViewById(R.id.fab);
        final ConstraintLayout postInfoView = rootView.findViewById(R.id.postInfo);
        final ConstraintLayout bigPostInfoView = rootView.findViewById(R.id.bigPostInfo);
        final TextView bigPosterNameView = rootView.findViewById(R.id.bigPosterName);
        final TextView bigPosterEmailView = rootView.findViewById(R.id.bigPosterEmail);
        final TextView bigPosterPhoneView = rootView.findViewById(R.id.bigPosterPhone);
        final TextView bigPosterLocationView = rootView.findViewById(R.id.bigPosterLocation);
        final TextView bigPostDescriptionView = rootView.findViewById(R.id.bigPostDescription);


        final ImageView bigFoodImageView = rootView.findViewById(R.id.bigFoodImage);
        final ImageView bigPosterImageView = rootView.findViewById(R.id.bigPosterImage);

        final TextView foodNameView = rootView.findViewById(R.id.foodName);
        final TextView foodDescriptionView = rootView.findViewById(R.id.foodDescription);
        final TextView posterEmailView = rootView.findViewById(R.id.priceText);
        final TextView posterNameView = rootView.findViewById(R.id.posterName);
        final ImageView foodImageView = rootView.findViewById(R.id.plateName);
        final ImageView posterImageView = rootView.findViewById(R.id.posterImage);

        final Button confirmButtonView = rootView.findViewById(R.id.confirmButton);


        postInfoView.setVisibility(View.GONE);
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
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(getContext(), locationResult);

                GetAsyncTask getPostLocations = new GetAsyncTask(7, "go_foods/");
                getPostLocations.execute();

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {
                        bigPosterImageView.setVisibility(View.GONE);
                        bigPostInfoView.setVisibility(View.GONE);
                        postInfoView.setVisibility(View.VISIBLE);
                        final int index = (int) (marker.getTag());
                        foodNameView.setText(data.get(index)[2]);
                        foodDescriptionView.setText(data.get(index)[5]);
                        posterNameView.setText(data.get(index)[7] + " " + data.get(index)[8]);
                        posterEmailView.setText(data.get(index)[9]);

                        String profile_photo = "http://127.0.0.1:8000";
                        profile_photo += data.get(index)[6];
                        if(!profile_photo.contains("no-image")) Glide.with(rootView.getContext()).load(profile_photo).into(foodImageView);
                        if(!profile_photo.contains("no-image")) Glide.with(rootView.getContext()).load(profile_photo).into(bigFoodImageView);

                        profile_photo = "http://127.0.0.1:8000/media/";
                        profile_photo += data.get(index)[10];
                        if(!profile_photo.contains("no-image")) Glide.with(rootView.getContext()).load(profile_photo).into(posterImageView);
                        if(!profile_photo.contains("no-image")) Glide.with(rootView.getContext()).load(profile_photo).into(bigPosterImageView);



                        bigPosterNameView.setText(data.get(index)[7] + " " + data.get(index)[8]);
                        bigPosterEmailView.setText(data.get(index)[9]);
                        bigPostDescriptionView.setText(data.get(index)[5]);
                        if (data.get(index)[12] != "") bigPosterPhoneView.setText("+" + data.get(index)[13] + data.get(index)[12]);
                        bigPosterLocationView.setText(data.get(index)[11]);

                        return false;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        postInfoView.setVisibility(View.GONE);
                        bigPostInfoView.setVisibility(View.GONE);
                        bigPosterImageView.setVisibility(View.GONE);
                    }
                });

            }
        });
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addFood = new Intent(getActivity(), AddGoFoodActivity.class);
                getActivity().startActivity(addFood);
            }
        });

        postInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postInfoView.setVisibility(View.GONE);
                bigPostInfoView.setVisibility(View.VISIBLE);
                bigPosterImageView.setVisibility(View.VISIBLE);

            }
        });

        confirmButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAsyncTask emitMessage = new PostAsyncTask("http://127.0.0.1:8000/send_message/");
                emitMessage.execute(
                        new String[]{"owner", posterEmail},
                        new String[]{"post_id", id}
                );
                PostAsyncTask createOrder = new PostAsyncTask("http://127.0.0.1:8000/create_order/");
                createOrder.execute(
                        new String[]{"post_id", id}
                );
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
