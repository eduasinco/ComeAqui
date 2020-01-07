package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.login_and_register.ChangePasswordActivity;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.confirm_email.ConfirmEmailActivity;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment.PaymentMethodsActivity;


import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class EditAcountDetailsActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private TextView phoneNumber;
    private TextView emailAddress;
    private Button changePassword;
    private TextView save;
    private ProgressBar progressBar;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        initializeUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_acount_details);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        emailAddress = findViewById(R.id.email_address);
        changePassword = findViewById(R.id.change_password);
        save = findViewById(R.id.save);
        progressBar = findViewById(R.id.progressBar);
        LinearLayout paymentMethod = findViewById(R.id.payment_method);

        initializeUser();

        paymentMethod.setOnClickListener(v -> {
            Intent paymentMethodA = new Intent(this, PaymentMethodsActivity.class);
            paymentMethodA.putExtra("changeMode", false);
            startActivity(paymentMethodA);
        });

        emailAddress.setOnClickListener(v -> goToChangeEmail());
        save.setOnClickListener(v -> saveData());
        changePassword.setOnClickListener((v) -> startActivity(new Intent(this, ChangePasswordActivity.class)));
        findViewById(R.id.back_arrow).setOnClickListener(v -> finish());
    }

    public void initializeUser(){
        GetAsyncTask process = new GetAsyncTask(getResources().getString(R.string.server) + "/my_profile/");
        try {
            String response = process.execute().get();
            if (response != null){
                USER = new User(new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject());
                setData();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplicationContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void goToChangeEmail(){
        Intent confirmEmail = new Intent(this, ConfirmEmailActivity.class);
        confirmEmail.putExtra("email", emailAddress.getText().toString());
        startActivity(confirmEmail);
    }

    void showProgress(boolean show){
        progressBar.setVisibility(show ? View.VISIBLE: View.GONE);
        save.setVisibility(show ? View.GONE: View.VISIBLE);
    }

    void setData(){
        firstName.setText(USER.first_name);
        lastName.setText(USER.last_name);
        phoneNumber.setText(USER.phone_number);
        emailAddress.setText(USER.email);
    }

    private void saveData(){
        PatchAsyncTask putTast = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
        tasks.add(putTast.execute(
                new String[]{"first_name", firstName.getText().toString()},
                new String[]{"last_name", lastName.getText().toString()},
                new String[]{"phone_number", phoneNumber.getText().toString()}
        ));
    }
    private class PatchAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;
        public PatchAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getApplicationContext(), "PATCH", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            showProgress(false);
            finish();
            super.onPostExecute(response);
        }
    }
    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}
