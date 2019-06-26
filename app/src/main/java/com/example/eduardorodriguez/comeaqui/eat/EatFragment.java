package com.example.eduardorodriguez.comeaqui.eat;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.food.AddFoodActivity;
import com.example.eduardorodriguez.comeaqui.profile.orders.OrderObject;
import com.example.eduardorodriguez.comeaqui.profile.settings.PlacesAutocompleteFragment;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class EatFragment extends Fragment{

    MapView mMapView;
    static View rootView;
    private static GoogleMap googleMap;
    public static ArrayList<FoodPost> data;
    int fabCount;

    ConstraintLayout mapPickerPanView;
    ImageView shadow;
    ImageView hande;
    ImageView shadowPoint;
    TextView pickedAdress;
    FloatingActionButton myFab;
    ImageView cancelPostView;

    ArrayList<Marker> markers = new ArrayList<>();
    LatLng latLng;

    void setMarkers(){
        for (int i = 0; i < data.size(); i++){
            float lat = data.get(i).lat;
            float lng = data.get(i).lng;

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
            markers.add(marker);
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

    void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new FoodPost(jo));
            }
            setMarkers();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        fabCount = 0;

        mMapView = rootView.findViewById(R.id.mapView);
        myFab =  rootView.findViewById(R.id.fab);
        mapPickerPanView = rootView.findViewById(R.id.map_picker_pan);
        shadow = rootView.findViewById(R.id.shadow);
        hande = rootView.findViewById(R.id.handle);
        shadowPoint = rootView.findViewById(R.id.shadow_point);
        pickedAdress = rootView.findViewById(R.id.pickedAdress);
        cancelPostView = rootView.findViewById(R.id.cancel_post);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setMap();
        setFabFunctionality();
        setCancelPost();

        return rootView;
    }

    void setMap(){
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.setMyLocationEnabled(true);

            setLocationPicker();
            setMapMarkers();
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

            googleMap.setOnMarkerClickListener(marker -> {
                final int index = (int) (marker.getTag());
                FoodPost foodPost = data.get(index);

                Bundle bundle = new Bundle();
                bundle.putSerializable("object", foodPost);
                MapCardFragment fragment = new MapCardFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
                return false;
            });
        });
    }

    void setMapMarkers(){
        GetAsyncTask getPostLocations = new GetAsyncTask("foods/");
        try {
            String response = getPostLocations.execute().get();
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void setLocationPicker(){
        googleMap.setOnCameraMoveStartedListener(i -> {
            pickedAdress.setVisibility(View.GONE);
            moveMapPicker(40, 200);
        });
        googleMap.setOnCameraIdleListener(() -> {
            moveMapPicker(-40, 200);
            latLng = googleMap.getCameraPosition().target;
            String latLngString = latLng.latitude + "," + latLng.longitude;
            String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLngString + "&";
            Server gAPI = new Server("GET", uri);
            try {
                String jsonString = gAPI.execute().get(15, TimeUnit.SECONDS);
                if (jsonString != null){
                    JsonObject joo = new JsonParser().parse(jsonString).getAsJsonObject();
                    JsonArray jsonArray = joo.get("results").getAsJsonArray();
                    if (jsonArray.size() > 0) {
                        pickedAdress.setVisibility(View.VISIBLE);
                        pickedAdress.setText(jsonArray.get(0).getAsJsonObject().get("formatted_address").getAsString());
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    void setFabFunctionality(){
        myFab.setOnClickListener(v -> {
            apearMapPicker(true, 40);
            if (fabCount == 0){
                makersVisibility(false);
                fabCount = 1;
                cancelPostView.setVisibility(View.VISIBLE);
                switchFabImage(true);
            } else if (fabCount == 1) {
                Intent addFood = new Intent(getActivity(), AddFoodActivity.class);
                addFood.putExtra("address" , pickedAdress.getText().toString());
                addFood.putExtra("lat" , latLng.latitude);
                addFood.putExtra("lng" , latLng.longitude);
                getActivity().startActivity(addFood);
            } else {
                fabCount = 2;
                switchFabImage(false);
            }
        });
    }

    void setCancelPost(){
        cancelPostView.setOnClickListener(v -> {
            makersVisibility(true);
            switchFabImage(false);
            fabCount = 0;
            cancelPostView.setVisibility(View.GONE);
            apearMapPicker(false, -40);
        });
    }

    void switchFabImage(boolean toPlus){
        myFab.animate().scaleY(0).setDuration(200).withEndAction(() -> {
            myFab.setImageDrawable(ContextCompat.getDrawable(getActivity(), toPlus ? R.drawable.plus_sign : R.drawable.eat));
            myFab.animate().scaleY(1).setDuration(200);
        }).start();
    }

    void apearMapPicker(boolean apear, int move){

        mapPickerPanView.setTranslationY(move);
        hande.setTranslationY(-move);
        shadow.setTranslationY(-move * 2 / 3);
        shadow.setTranslationX(move * 1 / 3);

        shadowPoint.setVisibility(apear ? View.VISIBLE: View.GONE);
        mapPickerPanView.setVisibility(apear ? View.VISIBLE: View.GONE);
        hande.setVisibility(apear ? View.VISIBLE: View.GONE);
        shadow.setVisibility(apear ? View.VISIBLE: View.GONE);
        shadow.setVisibility(apear ? View.VISIBLE: View.GONE);
        moveMapPicker(0, 200);
    }

    void moveMapPicker(int move, int secs){
        float a = 0f;
        if (move > 0)
            a = 0.5f;
        shadowPoint.animate().alpha(a).setDuration(secs);
        mapPickerPanView.animate().translationY(-move).setDuration(secs);
        hande.animate().translationY(-move).setDuration(secs);
        shadow.animate().translationY(-move * 2 / 3).setDuration(secs);
        shadow.animate().translationX(move * 1 / 3).setDuration(secs);
    }

    void makersVisibility(boolean visible){
        for (Marker marker: markers){
            marker.setVisible(visible);
        }
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