package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    private static String credentials;
    private static boolean mock = true;

    public static String getCredemtials(){
        return credentials;
    }

    public static String setCredenditals(String cred){
        credentials = cred;
        return credentials;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false)) {
            SharedPreferences sp = getSharedPreferences("Credentials", MODE_PRIVATE);
            credentials = sp.getString("cred", "");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
