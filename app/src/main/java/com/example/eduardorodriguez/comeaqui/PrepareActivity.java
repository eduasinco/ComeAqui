package com.example.eduardorodriguez.comeaqui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.utilities.MyLocation;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PrepareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);
        initializeUser();
        getUserTimeZone();
    }

    void getUserTimeZone(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                double lng = location.getLongitude();
                double lat = location.getLatitude();

                Server gAPI2 = new Server("GET", "https://maps.googleapis.com/maps/api/timezone/json?location=" +
                        lat + "," + lng + "&timestamp=0&key=" + getResources().getString(R.string.google_key));
                try {
                    String response = gAPI2.execute().get();
                    if (response != null) {
                        String timeZone = new JsonParser().parse(response).getAsJsonObject().get("timeZoneId").getAsString();
                        MainActivity.user.timeZone = timeZone;
                        setUserTimeZone(timeZone);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }

    public User initializeUser(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_profile/");
        try {
            String response = process.execute().get();
            if (response != null)
                MainActivity.user = new User(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // initializeFirebaseUser();
        return MainActivity.user;
    }


    private void setUserTimeZone(String timeZone){
        PatchAsyncTask putTask = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
        try {
            putTask.execute("time_zone", timeZone).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        goToMain();
    }
    void goToMain(){
        Intent k = new Intent(this, MainActivity.class);
        startActivity(k);
    }
}
