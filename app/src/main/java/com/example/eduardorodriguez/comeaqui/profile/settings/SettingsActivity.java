package com.example.eduardorodriguez.comeaqui.profile.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.*;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
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

        signOutView.setOnClickListener(v -> signOut());

        editAccountView();
        getData();
    }

    void editAccountView(){
        editAccountView.setOnClickListener(v -> {
        });
    }

    private void signOut(){
        SharedPreferences pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("signed_in", false);
        edt.remove("email");
        edt.remove("password");
        edt.apply();

        Intent bactToLogin = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(bactToLogin);
    }
}
