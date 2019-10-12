package com.example.eduardorodriguez.comeaqui.login_and_register.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.utilities.ImageLookActivity;

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
            text.setText("Thank you " + user.first_name + " for subscribing to ComeAqui! \n Please verify your email and have fun cooking! ;)");
        }

        goToLogin.setOnClickListener((v) -> {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        });
    }
}
