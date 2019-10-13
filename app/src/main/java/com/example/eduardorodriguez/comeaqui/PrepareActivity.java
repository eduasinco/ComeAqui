package com.example.eduardorodriguez.comeaqui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.utilities.MyLocation;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;
import static com.yalantis.ucrop.UCropFragment.TAG;

public class PrepareActivity extends AppCompatActivity {

    boolean gotTimezone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);
        initializeUser();
        getFirebaseToken();
        getUserTimeZone();
    }

    private void getFirebaseToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    System.out.println("TOKEEEEEEEEN " + token);

                    postTokenToServer(token);
                });
    }

    private void postTokenToServer(String token){
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        PostAsyncTask postToken = new PostAsyncTask(getResources().getString(R.string.server) + "/fcm/v1/devices/");
        postToken.execute(
                new String[]{"dev_id", androidID},
                new String[]{"reg_id", token},
                new String[]{"name", "" + USER.id}
        );
    }

    void getUserTimeZone(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                double lng = location.getLongitude();
                double lat = location.getLatitude();

                if (!gotTimezone){
                    Server gAPI2 = new Server("GET", "https://maps.googleapis.com/maps/api/timezone/json?location=" +
                            lat + "," + lng + "&timestamp=0&key=" + getResources().getString(R.string.google_key));
                    try {
                        String response = gAPI2.execute().get();
                        if (response != null) {
                            String timeZone = new JsonParser().parse(response).getAsJsonObject().get("timeZoneId").getAsString();
                            USER.timeZone = timeZone;
                            setUserTimeZone(timeZone);
                            gotTimezone = true;
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
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
                USER = new User(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return USER;
    }


    private void setUserTimeZone(String timeZone){
        try {
            new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/"){
                @Override
                protected void onPostExecute(JSONObject response) {
                    goToMain();
                    super.onPostExecute(response);
                }
            }.execute(
                    new String[]{"time_zone", timeZone, ""}
            ).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
        }
    }
    void goToMain(){
        Intent k = new Intent(this, MainActivity.class);
        startActivity(k);
       overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
    }
}
