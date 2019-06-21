package com.example.eduardorodriguez.comeaqui.profile.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.*;
import com.example.eduardorodriguez.comeaqui.profile.EditAccountActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.GoogleAPIAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {

    private static EditText addressView;
    private static String place_id;
    public static void setAddress(String text, String id){
        addressView.setText(text);
        addressView.setFocusable(false);
        place_id = id;
    }

    static String email;
    static String firstName;
    static String lastName;
    static String phoneCode;
    static String phoneNumber;
    static String location;
    static String profilePhoto;

    private static SeekBar deliverRadiousSeekbarView;
    private static int delivery_radious;
    static long last_text_edit = 0;


    public static void setProfile(JsonObject jo){
        firstName = jo.get("first_name").getAsString();
        lastName = jo.get("last_name").getAsString();
        phoneCode = jo.get("phone_code").getAsString();
        phoneNumber = jo.get("phone_number").getAsString();
        location = jo.get("location").getAsString();
        profilePhoto = jo.get("profile_photo").getAsString();
        delivery_radious = jo.get("deliver_radius").getAsInt();

        deliverRadiousSeekbarView.setProgress(delivery_radious);
        addressView.setText(location);
    }

    public static void getData(){
        GetAsyncTask profileInfo = new GetAsyncTask("my_profile/");
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

        Button editAccountView = findViewById(R.id.editAccount);
        Button saveButtonView = findViewById(R.id.saveButton);
        deliverRadiousSeekbarView = findViewById(R.id.deliverRadiousSeekbar);
        final TextView metersTextView = findViewById(R.id.metersText);


        editAccountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editAccount = new Intent(SettingsActivity.this, EditAccountActivity.class);
                editAccount.putExtra("firstName", firstName);
                editAccount.putExtra("lastName", lastName);
                editAccount.putExtra("phoneCode", phoneCode);
                editAccount.putExtra("phoneNumber", phoneNumber);
                editAccount.putExtra("profilePhoto", profilePhoto);
                startActivity(editAccount);
            }
        });

        deliverRadiousSeekbarView.setProgress(delivery_radious);
        deliverRadiousSeekbarView.setMax(1000);
        deliverRadiousSeekbarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                delivery_radious = progress;
                metersTextView.setText(delivery_radious + "m");
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PlacesAutocompleteFragment())
                .commit();

        saveButtonView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PatchAsyncTask putTast = new PatchAsyncTask();
                putTast.execute("location", addressView.getText().toString());
                PatchAsyncTask putTast2 = new PatchAsyncTask();
                putTast2.execute("deliver_radius", Integer.toString(delivery_radious));

                if (place_id != null) {
                    GoogleAPIAsyncTask gAPI2 = new GoogleAPIAsyncTask(
                            "https://maps.googleapis.com/maps/api/place/details/json?placeid=",
                            place_id,
                            "&fields=geometry&", 1);
                    gAPI2.execute();
                } else {
                    GoogleAPIAsyncTask gAPI2 = new GoogleAPIAsyncTask(
                            "https://maps.googleapis.com/maps/api/place/textsearch/json?query=",
                            addressView.getText().toString(),
                            "&", 2);
                    gAPI2.execute();
                }

                Intent k = new Intent(SettingsActivity.this, MainActivity.class);
                k.putExtra("profile", true);
                startActivity(k);
            }
        });

        TextView signOutView = findViewById(R.id.signOut);
        signOutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("activity_executed", false);
                edt.commit();

                Intent bactToLogin = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(bactToLogin);
            }
        });

        addressView = findViewById(R.id.address);
        final long delay = 1000; // 1 seconds after user stops typing
        final Handler handler = new Handler();
        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    GoogleAPIAsyncTask gAPI = new GoogleAPIAsyncTask(
                            "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                            ,addressView.getText().toString(),
                            "&types=geocode&language=en&", 0);
                    gAPI.execute();
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
            }

        );
        getData();
    }
}
