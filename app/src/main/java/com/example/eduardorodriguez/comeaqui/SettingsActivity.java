package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SettingsActivity extends AppCompatActivity {

    private static EditText addressView;
    public static void setAddress(String text){
        addressView.setText(text);
        addressView.setFocusable(false);
    }

    static String email;
    static String firstName;
    static String lastName;
    static String phoneCode;
    static String phoneNumber;
    static String location;
    static String profilePhoto;



    public static void setProfile(String jsonString){
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
        JsonObject jo = jsonArray.get(0).getAsJsonObject();

        firstName = jo.get("first_name").getAsString();
        lastName = jo.get("last_name").getAsString();
        phoneCode = jo.get("phone_code").getAsString();
        phoneNumber = jo.get("phone_number").getAsString();
        location = jo.get("location").getAsString();
        profilePhoto = jo.get("profile_photo").getAsString();

        addressView.setText(location);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        GetAsyncTask profileInfo = new GetAsyncTask(2);
        profileInfo.execute("editAccount");

        Button editAccountView = findViewById(R.id.editAccount);
        Button saveButtonView = findViewById(R.id.saveButton);


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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PlacesAutocompleteFragment())
                .commit();

        saveButtonView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatchAsyncTask putTast = new PatchAsyncTask();
                putTast.execute("location", addressView.getText().toString());
                Intent k = new Intent(SettingsActivity.this, MainActivity.class);
                k.putExtra("profile", "profile");
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
        addressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GoogleAPIAsyncTask gAPI = new GoogleAPIAsyncTask(addressView.getText().toString());
                gAPI.execute();
            }
        });
    }
}
