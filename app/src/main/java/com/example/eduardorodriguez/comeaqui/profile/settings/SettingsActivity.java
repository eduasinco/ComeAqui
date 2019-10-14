package com.example.eduardorodriguez.comeaqui.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.login_and_register.ChangePasswordActivity;
import com.example.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
import com.example.eduardorodriguez.comeaqui.login_and_register.LoginOrRegisterActivity;

public class SettingsActivity extends AppCompatActivity {

    TextView signOut;
    TextView changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        signOut = findViewById(R.id.sign_out);
        changePassword = findViewById(R.id.change_password);

        signOut.setOnClickListener((v) -> signOut());
        changePassword.setOnClickListener((v) -> startActivity(new Intent(this, ChangePasswordActivity.class)));
    }

    private void signOut(){
        SharedPreferences pref = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("signed_in", false);
        edt.remove("email");
        edt.remove("password");
        edt.apply();

        Intent bactToLogin = new Intent(this, LoginOrRegisterActivity.class);
        startActivity(bactToLogin);
    }
}
