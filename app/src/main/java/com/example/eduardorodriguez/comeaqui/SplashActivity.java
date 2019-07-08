package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private static String credentials;
    public static boolean mock = false;
    public static Context context;

    private FirebaseAuth.AuthStateListener mAuthListener;

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
        context = getBaseContext();

        final boolean[] firebaseSignedIn = {false};
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null){
                firebaseSignedIn[0] = true;
            }
        };

        SharedPreferences pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if (pref.getBoolean("signed_in", false) && firebaseSignedIn[0]) {
            SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
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
