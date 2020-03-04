package com.comeaqui.eduardorodriguez.comeaqui.login_and_register.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;

public class VerifyEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        TextView text = findViewById(R.id.text);
        Button goToLogin = findViewById(R.id.go_to_login);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("user") != null) {
            User user = (User) b.getSerializable("user");
            text.setText("Thank you " + user.first_name + " for subscribing to ComeAqui!");
        }

        goToLogin.setOnClickListener((v) -> {
            goToLogin();
        });
    }

    void goToLogin(){
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    @Override
    public void finish() {
        goToLogin();
    }
}
