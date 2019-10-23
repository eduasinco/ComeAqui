package com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details;

import android.content.Intent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.MainActivity;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.confirm_email.ConfirmEmailActivity;
import com.example.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment.PaymentMethodsActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class EditAcountDetailsActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private TextView phoneNumber;
    private TextView emailAddress;

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
        TextView save = findViewById(R.id.save);
        LinearLayout paymentMethod = findViewById(R.id.payment_method);

        initializeUser();

        paymentMethod.setOnClickListener(v -> {
            Intent paymentMethodA = new Intent(this, PaymentMethodsActivity.class);
            startActivity(paymentMethodA);
        });

        emailAddress.setOnClickListener(v -> goToChangeEmail());
        save.setOnClickListener(v -> saveData());
    }

    public void initializeUser(){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/my_profile/");
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

    void goToChangeEmail(){
        Intent confirmEmail = new Intent(this, ConfirmEmailActivity.class);
        confirmEmail.putExtra("email", emailAddress.getText().toString());
        startActivity(confirmEmail);
    }

    void setData(){
        firstName.setText(USER.first_name);
        lastName.setText(USER.last_name);
        phoneNumber.setText(USER.phone_number);
        emailAddress.setText(USER.email);
    }

    private void saveData(){
        PatchAsyncTask putTast = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
        try {
            putTast.execute(
                    new String[]{"first_name", firstName.getText().toString(), ""},
                    new String[]{"last_name", lastName.getText().toString(), ""},
                    new String[]{"phone_number", phoneNumber.getText().toString(), ""}
                    ).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        finish();
    }
}
