package com.comeaqui.eduardorodriguez.comeaqui.login_and_register.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class VerifyEmailActivity extends AppCompatActivity {

    ProgressBar progress;
    Button sendVerificationEmailAgain;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        TextView text = findViewById(R.id.text);
        Button goToLogin = findViewById(R.id.go_to_login);
        sendVerificationEmailAgain = findViewById(R.id.send_verification_email_again);
        progress = findViewById(R.id.progress);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("user") != null) {
            user = (User) b.getSerializable("user");
            text.setText("Thank you " + user.first_name + " for subscribing to ComeAqui!");
        }

        goToLogin.setOnClickListener((v) -> {
            goToLogin();
        });

        sendVerificationEmailAgain.setOnClickListener((v) -> {
            sendVerificationEmailAgain();
        });
    }

    void goToLogin(){
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    void sendVerificationEmailAgain(){
        new GetAsyncTask(getResources().getString(R.string.server) + "/send_verification_email_again/" + user.email + "/").execute();
    }
    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        public Bitmap bitmap;
        String uri;

        public GetAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            sendVerificationEmailAgain.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.getNoCredentials(this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            sendVerificationEmailAgain.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            super.onPostExecute(response);
        }
    }
}
