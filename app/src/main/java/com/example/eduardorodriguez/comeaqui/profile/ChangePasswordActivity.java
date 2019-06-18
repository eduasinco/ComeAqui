package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.LoginActivity;
import com.example.eduardorodriguez.comeaqui.PutAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final TextView oldPasswordView = findViewById(R.id.oldPassword);
        final TextView newPasswordView = findViewById(R.id.newPassword);
        final Button resetButtonView = findViewById(R.id.ressetButton);

        resetButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PutAsyncTask resetPassword = new PutAsyncTask("http://127.0.0.1:8000/password_change/");
                resetPassword.execute(
                        new String[]{"old_password", oldPasswordView.getText().toString()},
                        new String[]{"new_password", newPasswordView.getText().toString()}
                );
                Intent k = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(k);
            }
        });
    }
}
