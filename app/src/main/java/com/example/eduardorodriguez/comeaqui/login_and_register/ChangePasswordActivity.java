package com.example.eduardorodriguez.comeaqui.login_and_register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.WebSocketMessage;
import com.example.eduardorodriguez.comeaqui.login_and_register.forgot_password.ForgotPasswordActivity;
import com.example.eduardorodriguez.comeaqui.login_and_register.register.VerifyEmailActivity;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView oldPasswordValtext;
    EditText oldPassword;
    EditText newPassword;
    TextView newPasswordValtext;
    Button setPasswordButton;
    TextView passwordSetText;
    Button goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPasswordValtext = findViewById(R.id.old_password_valtext);
        oldPassword = findViewById(R.id.old_password);
        newPasswordValtext = findViewById(R.id.new_password_valtext);
        newPassword = findViewById(R.id.new_password);
        setPasswordButton = findViewById(R.id.set_password);
        passwordSetText = findViewById(R.id.password_set_text);
        goToLogin = findViewById(R.id.go_to_login);

        setEditText(newPassword, newPasswordValtext);
        setEditText(oldPassword, oldPasswordValtext);
        setPasswordButton.setOnClickListener((v) -> sendEmail());
        goToLogin.setOnClickListener((v) -> {
            Intent a = new Intent(this, LoginActivity.class);
            startActivity(a);
        });
        findViewById(R.id.forgot_pw).setOnClickListener((v) -> {
            Intent a = new Intent(this, ForgotPasswordActivity.class);
            startActivity(a);
        });
    }

    void sendEmail(){
        if (emailValid()){
            submit();
        }
    }

    boolean emailValid(){
        Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$");
        Matcher m = p.matcher(newPassword.getText().toString());
        if (!m.find()){
            String text = "A digit must occur at least once \n" +
                    "A lower case letter must occur at least once \n" +
                    "An upper case letter must occur at least once \n" +
                    "A special character (!?@#$%^&+=) must occur at least once \n" +
                    "No whitespace allowed in the entire string \n";
            showValtext(newPasswordValtext, text, newPassword);
            return false;
        }
        return true;
    }

    void showValtext(TextView tv, String text, EditText et){
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
        et.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape_error));
    }

    void setEditText(EditText editText, TextView valtext){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setBackground(ContextCompat.getDrawable(getApplication(), R.drawable.text_input_shape));
                valtext.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    void submit(){
        try {
            new PatchAsyncTask(getResources().getString(R.string.server) + "/password_change/"){
                @Override
                protected void onPostExecute(JSONObject jo) {
                    passwordSetText.setVisibility(View.VISIBLE);
                    goToLogin.setVisibility(View.VISIBLE);
                    super.onPostExecute(jo);
                }
            }.execute(
                    new String[]{"old_password", oldPassword.getText().toString(), ""},
                    new String[]{"new_password", newPassword.getText().toString(), ""}
                    ).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
