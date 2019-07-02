package com.example.eduardorodriguez.comeaqui.profile.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.*;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {

    private static Button saveButtonView;
    private static TextView signOutView;
    private static Button editAccountView;
    private static TextView metersTextView;


    static String email;
    static String firstName;
    static String lastName;
    static String phoneCode;
    static String phoneNumber;
    static String location;
    static String profilePhoto;


    private static int delivery_radious;

    public static void setProfile(JsonObject jo){
        firstName = jo.get("first_name").getAsString();
        lastName = jo.get("last_name").getAsString();
        phoneCode = jo.get("phone_code").getAsString();
        phoneNumber = jo.get("phone_number").getAsString();
        profilePhoto = jo.get("profile_photo").getAsString();

    }

    void getData(){
        GetAsyncTask profileInfo = new GetAsyncTask("GET",  getResources().getString(R.string.server) + "/my_profile/");

        try {
            String response = profileInfo.execute().get();
            if (response != null)
                setProfile(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveButtonView = findViewById(R.id.saveButton);
        signOutView = findViewById(R.id.signOut);
        editAccountView = findViewById(R.id.editAccount);
        metersTextView = findViewById(R.id.metersText);

        signOutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        editAccountView();
        getData();
        saveSettings();
    }

    void editAccountView(){
        editAccountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    void saveSettings(){
        saveButtonView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatchAsyncTask putTast = new PatchAsyncTask();
                putTast.execute("location", AutocompleteLocationFragment.addressView.getText().toString());
                PatchAsyncTask putTast2 = new PatchAsyncTask();
                putTast2.execute("deliver_radius", Integer.toString(delivery_radious));

                String place_id = AutocompleteLocationFragment.place_id;
                if (place_id != null) {
                    Server gAPI2 = new Server("GET", "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place_id +
                            "&fields=geometry&key=" + getResources().getString(R.string.google_key));

                    gAPI2.execute();
                } else {
                    Server gAPI2 = new Server("GET", "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" +
                            AutocompleteLocationFragment.addressView.getText().toString() +
                            "&key=" + getResources().getString(R.string.google_key));
                    gAPI2.execute();
                }
                Intent k = new Intent(SettingsActivity.this, MainActivity.class);
                k.putExtra("profile", true);
                startActivity(k);
            }
        });
    }


    private void signOut(){
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("activity_executed", false);
        edt.apply();

        Intent bactToLogin = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(bactToLogin);
    }
}
