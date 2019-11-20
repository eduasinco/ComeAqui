package com.example.eduardorodriguez.comeaqui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.MyLocation;
import com.example.eduardorodriguez.comeaqui.utilities.UpperNotificationFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements MapPickerFragment.OnFragmentInteractionListener {
    MapView mMapView;
    static View view;
    private static GoogleMap googleMap;
    public static HashMap<Integer, FoodPost> foodPostHashMap = new HashMap<>();;
    int fabCount;
    WebSocketClient mWebSocketClient;

    MapPickerFragment mapPickerFragment;
    MapCardFragment mapCardFragment;
    UpperNotificationFragment upperNotificationFragment;

    FloatingActionButton myFab;
    FloatingActionButton centerButton;
    ImageView cancelPostView;

    double lng;
    double lat;
    LatLng pickedLocation;
    String pickedAdress = "";

    static Set<Integer> touchedMarkers = new HashSet<>();
    public static HashMap<Integer, Marker> markerHashMap = new HashMap<>();
    boolean gotTimezone = false;


    void setMarkers(){
        for (int key : foodPostHashMap.keySet()) {
            FoodPost fp = foodPostHashMap.get(key);
            double lat = fp.lat;
            double lng = fp.lng;

            Marker marker =  googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
            marker.setTag(key);

            setMarkerDesign(marker, false);
            markerHashMap.put(fp.id, marker);
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

    void makeList(JsonArray jsonArray, boolean favourite){
        try {
            for (JsonElement pa : jsonArray) {
                JsonObject jo = favourite ? pa.getAsJsonObject().get("post").getAsJsonObject() : pa.getAsJsonObject();
                FoodPost fp = new FoodPost(jo);
                fp.favourite = favourite;
                fp.favouriteId = pa.getAsJsonObject().get("id").getAsInt();
                foodPostHashMap.put(fp.id, fp);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void refreshUpperNotifications(){
        upperNotificationFragment.refreshUpperNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        myFab = view.findViewById(R.id.fab);
        centerButton = view.findViewById(R.id.center_map);
        cancelPostView = view.findViewById(R.id.cancel_post);
        mMapView.onCreate(savedInstanceState);

        mapPickerFragment = MapPickerFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_picker_frame, mapPickerFragment)
                .commit();
        upperNotificationFragment = UpperNotificationFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.upper_notification, upperNotificationFragment)
                .commit();
        mapCardFragment = MapCardFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.container1, mapCardFragment)
                .commit();
        mMapView.onResume();
        fabCount = 0;
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cancelPostView.setOnClickListener(v -> cancelPost());
        myFab.setOnClickListener(v -> fabFunctionality());
        centerButton.setOnClickListener(v -> centerMap());
        mMapView.getMapAsync(mMap -> setMap(mMap));
        setMapMarkers();
        listenToPosts();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        cancelPost();
    }

    public void listenToPosts(){
        try {
            URI uri = new URI(getResources().getString(R.string.server) + "/ws/posts/");
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    // runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Unread Messages!", Toast.LENGTH_LONG).show());
                }
                @Override
                public void onMessage(String s) {
                    getActivity().runOnUiThread(() -> {
                        JsonObject jo = new JsonParser().parse(s).getAsJsonObject().get("message").getAsJsonObject();
                        FoodPost fp = new FoodPost(jo.get("post").getAsJsonObject());
                        if (jo.get("delete").getAsBoolean()){
                            if (markerHashMap.containsKey(fp.id)){
                                Marker marker = markerHashMap.get(fp.id);
                                marker.remove();
                            }
                        } else {
                            foodPostHashMap.put(fp.id, fp);
                            Marker marker =  googleMap.addMarker(new MarkerOptions().position(new LatLng(fp.lat, fp.lng)));
                            marker.setTag(fp.id);
                            setMarkerIcon(marker, fp.favourite ? R.drawable.map_icon_favourite : R.drawable.map_icon);
                            markerHashMap.put(fp.id, marker);
                        }
                    });
                }
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }
                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };
            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        mWebSocketClient.close();
        super.onDetach();
    }

    int c  = 0;
    @SuppressLint("RestrictedApi")
    void setMap(GoogleMap mMap){
        googleMap = mMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        setLocationPicker();
        // For dropping a marker at a point on the Map
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                lng = location.getLongitude();
                lat = location.getLatitude();

                pickedLocation = new LatLng(lat, lng);
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(pickedLocation).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (!gotTimezone){
                    System.out.println(++c);
                    gotTimezone = true;
                    String uri = "https://maps.googleapis.com/maps/api/timezone/json?location=" +
                            lat + "," + lng + "&timestamp=0&key=" + getResources().getString(R.string.google_key);
                    try {
                        new Server(getContext(),"GET", uri){
                            @Override
                            protected void onPostExecute(String response) {
                                if (response != null) {
                                    String timeZone = new JsonParser().parse(response).getAsJsonObject().get("timeZoneId").getAsString();
                                    USER.timeZone = timeZone;
                                    setUserTimeZone(timeZone);
                                }
                            }
                        }.execute().get();
                    } catch (ExecutionException | InterruptedException e) {
                        gotTimezone = false;
                        e.printStackTrace();
                    }
                }
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getContext(), locationResult);


        googleMap.setOnMarkerClickListener(marker -> {
            myFab.setVisibility(View.GONE);
            centerButton.setVisibility(View.GONE);
            cancelPostView.setVisibility(View.VISIBLE);

            final int key = (int) (marker.getTag());
            touchedMarkers.add(key);
            FoodPost foodPost = foodPostHashMap.get(key);
            mapCardFragment.showPost(foodPost);
            mapCardFragment.moveCardUp(true);

            setMarkerBigger(marker);
            return false;
        });
    }

    private void setUserTimeZone(String timeZone){
        try {
            new PatchAsyncTask(getContext(),getResources().getString(R.string.server) + "/edit_profile/"){
                @Override
                protected void onPostExecute(JSONObject response) {
                    super.onPostExecute(response);
                }
            }.execute(
                    new String[]{"time_zone", timeZone, ""}
            ).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
        }
    }

    Marker currentBigMarker;
    void setMarkerBigger(Marker marker){
        if (currentBigMarker != null){
            setMarkerDesign(currentBigMarker, false);
        }
        setMarkerDesign(marker, true);
        currentBigMarker = marker;
    }

    static void setMarkerDesign(Marker marker, boolean big){
        FoodPost fp = foodPostHashMap.get(marker.getTag());
        if (fp == null) return;
        if (big){
            if (fp.favourite){
                setMarkerIcon(marker, R.drawable.map_icon_favourite_big);
            } else if (touchedMarkers.contains(marker.getTag())){
                setMarkerIcon(marker, R.drawable.map_icon_seen_big);
            } else {
                setMarkerIcon(marker, R.drawable.map_icon_big);
            }
        } else {
            if (fp.favourite){
                setMarkerIcon(marker, R.drawable.map_icon_favourite);
            } else if (touchedMarkers.contains(marker.getTag())){
                setMarkerIcon(marker, R.drawable.map_icon_seen);
            } else {
                setMarkerIcon(marker, R.drawable.map_icon);
            }
        }

    }

    void removeAllMarkers(){
        for (Marker marker: markerHashMap.values()){
            marker.remove();
        }
        markerHashMap = new HashMap<>();
    }

    void setMapMarkers(){
        removeAllMarkers();
        new GetMarkersAsyncTask(getResources().getString(R.string.server) + "/foods/").execute();
    }
    class GetMarkersAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetMarkersAsyncTask(String uri){
            this.uri = uri;
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
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray(), false);
            setMapFavouriteMarkers();
            super.onPostExecute(response);
        }
    }

    void setMapFavouriteMarkers(){
        new GetFavouriteMarkersAsyncTask(getResources().getString(R.string.server) + "/my_favourites/").execute();
    }
    class GetFavouriteMarkersAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetFavouriteMarkersAsyncTask(String uri){
            this.uri = uri;
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
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray(), true);
            setMarkers();
            super.onPostExecute(response);
        }
    }

    void setLocationPicker(){
        googleMap.setOnCameraMoveStartedListener(i -> {
            if (mapPickerFragment.abled){
                mapPickerFragment.setAddressTextVisible(false);
                mapPickerFragment.moveMapPicker(true);
            }
        });
        googleMap.setOnCameraIdleListener(() -> {
            if (mapPickerFragment.abled){
                mapPickerFragment.moveMapPicker(false);
                pickedLocation = googleMap.getCameraPosition().target;
                mapPickerFragment.getLocationFromGoogle(pickedLocation);
            }
        });
    }

    void fabFunctionality(){
        mapPickerFragment.apearMapPicker(true);
        mapPickerFragment.getLocationFromGoogle(pickedLocation);
        if (fabCount == 0){
            markersVisibility(false);
            fabCount = 1;
            cancelPostView.setVisibility(View.VISIBLE);
            switchFabImage(true);
        } else if (fabCount == 1) {
            Intent addFood = new Intent(getActivity(), AddFoodActivity.class);
            addFood.putExtra("address" , pickedAdress);
            addFood.putExtra("lat" , pickedLocation.latitude);
            addFood.putExtra("lng" , pickedLocation.longitude);
            getActivity().startActivity(addFood);
        } else {
            fabCount = 2;
            switchFabImage(false);
        }
    }

    @SuppressLint("RestrictedApi")
    void cancelPost(){
        if (currentBigMarker != null){
            setMarkerDesign(currentBigMarker, false);
        }
        markersVisibility(true);
        switchFabImage(false);
        fabCount = 0;
        cancelPostView.setVisibility(View.GONE);
        myFab.setVisibility(View.VISIBLE);
        centerButton.setVisibility(View.VISIBLE);
        mapPickerFragment.apearMapPicker(false);
        mapCardFragment.moveCardUp(false);
    }

    void switchFabImage(boolean toPlus){
        myFab.animate().scaleY(0).setDuration(200).withEndAction(() -> {
            myFab.setImageDrawable(ContextCompat.getDrawable(getActivity(), toPlus ? R.drawable.plus_sign : R.drawable.add_food));
            myFab.animate().scaleY(1).setDuration(200);
        }).start();
    }

    void markersVisibility(boolean visible){
        for (Marker marker: markerHashMap.values()){
            marker.setVisible(visible);
        }
    }

    static void setMarkerIcon(Marker marker, int drawable){
        marker.setIcon(getMarkerIconFromDrawable(ContextCompat.getDrawable(view.getContext(), drawable)));
    }

    void centerMap(){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(15)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    @Override
    public void refreshFragment(String address) {
        this.pickedAdress = address;
    }
}