package com.example.eduardorodriguez.comeaqui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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
    int tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("tab") != null) {
            tab = b.getInt("tab");
        }

        initializeUser();
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
        try {
            new PostAsyncTask(this,getResources().getString(R.string.server) + "/fcm/v1/devices/").execute(
                    new String[]{"dev_id", androidID},
                    new String[]{"reg_id", token},
                    new String[]{"name", "" + USER.id}
            ).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No internet connections", Toast.LENGTH_LONG).show();
        }
        goToMain();
    }

    public void initializeUser(){
        try {
            new GetAsyncTask(this, "GET", getResources().getString(R.string.server) + "/my_profile/"){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null){
                        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("user", response);
                        editor.apply();
                        USER = new User(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
                        getFirebaseToken();
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No internet connections", Toast.LENGTH_LONG).show();
        }
    }


    void goToMain(){
        Intent k = new Intent(this, MainActivity.class);
        k.putExtra("tab", tab);
        startActivity(k);
        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
    }
}
