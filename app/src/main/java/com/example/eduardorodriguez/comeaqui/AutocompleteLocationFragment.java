package com.example.eduardorodriguez.comeaqui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.profile.settings.PlacesAutocompleteFragment;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AutocompleteLocationFragment extends Fragment {

    private View view;
    public static TextView addressView;
    static Context context;


    static long last_text_edit = 0;
    public static  String place_id;

    public AutocompleteLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_autocomplete_location, container, false);
        addressView = view.findViewById(R.id.address);

        context = getContext();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        detectTypingAndSetLocationPrediction();

        addressView.setText(getArguments().getString("address"));

        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction()
                .replace(R.id.container1, new PlacesAutocompleteFragment())
                .commit();
        return view;
    }

    public static void setAddress(String text, String id){
        addressView.setText(text);
        addressView.setFocusable(false);
        place_id = id;
    }

    private void detectTypingAndSetLocationPrediction(){

        final long delay = 1000; // 1 seconds after user stops typing
        final Handler handler = new Handler();
        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    String uri = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + addressView.getText().toString() +
                    "&types=geocode&language=en&key=" + getResources().getString(R.string.google_key);
                    Server gAPI = new Server("GET", uri);
                    try {
                        String jsonString = gAPI.execute().get(15, TimeUnit.SECONDS);
                        if (jsonString != null)
                            PlacesAutocompleteFragment.makeList(jsonString);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        addressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count,
                                           int after){
            }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before,
                                        int count){
                //You need to remove this to run only once
                handler.removeCallbacks(input_finish_checker);

            }
            @Override
            public void afterTextChanged ( final Editable s){
                //avoid triggering event when text is empty
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                } else {

                }
            }
        });
    }

    void setLastLocation(){
        if (fetchLocation())
            mFusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        String locationLatAndLng = location.getLatitude() + "," + location.getLongitude();
                        String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + locationLatAndLng + "&key=" + getResources().getString(R.string.google_key);
                        Server gAPI = new Server("GET", uri);
                        try {
                            String jsonString = gAPI.execute().get(15, TimeUnit.SECONDS);
                            JsonObject joo = new JsonParser().parse(jsonString).getAsJsonObject();
                            JsonArray jsonArray = joo.get("results").getAsJsonArray();
                            addressView.setText(jsonArray.get(0).getAsJsonObject().get("formatted_address").getAsString());
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    public static FusedLocationProviderClient mFusedLocationClient;

    public static boolean fetchLocation() {


        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(context)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions((Activity) context,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            return true;

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //abc
            }else{

            }
        }
    }
}
