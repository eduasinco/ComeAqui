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
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class EditAcountDetailsActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText emailAddress;

    private User user;

    @Override
    protected void onResume() {
        super.onResume();
        user = USER;
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

        user = USER;

        paymentMethod.setOnClickListener(v -> {
            Intent paymentMethodA = new Intent(this, PaymentMethodsActivity.class);
            startActivity(paymentMethodA);
        });

        setData();

        emailAddress.setOnClickListener(v -> goToChangeEmail());
        save.setOnClickListener(v -> saveData());
    }

    void goToChangeEmail(){
        Intent confirmEmail = new Intent(this, ConfirmEmailActivity.class);
        confirmEmail.putExtra("email", emailAddress.getText().toString());
        startActivity(confirmEmail);
    }

    void setData(){
        firstName.setText(user.first_name);
        lastName.setText(user.last_name);
        phoneNumber.setText(user.phone_number);
        emailAddress.setText(user.email);
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
