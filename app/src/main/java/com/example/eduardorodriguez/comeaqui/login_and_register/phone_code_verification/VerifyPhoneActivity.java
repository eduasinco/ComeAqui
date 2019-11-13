package com.example.eduardorodriguez.comeaqui.login_and_register.phone_code_verification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VerifyPhoneActivity extends AppCompatActivity {

    TextView emailValtext;
    EditText emailAdress;
    EditText verificationCode;
    TextView verificationValtext;
    TextView codeDidNotArrive;
    Button sendCodeButton;
    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        emailValtext = findViewById(R.id.email_valtext);
        emailAdress = findViewById(R.id.email_address);
        verificationCode = findViewById(R.id.code);
        verificationValtext = findViewById(R.id.code_vtext);
        sendCodeButton = findViewById(R.id.send_code_button);
        codeDidNotArrive = findViewById(R.id.send_again);
        progress = findViewById(R.id.send_code_progress);

        setEditText(emailAdress, emailValtext);
        setEditText(verificationCode, verificationValtext);
        sendCodeButton.setOnClickListener((v) -> sendEmailVerificationCode());
        codeDidNotArrive.setOnClickListener((v) -> sendEmailVerificationCode());
    }

    void sendEmailVerificationCode(){
        if (emailValid()){
            submit();
        }
    }

    boolean emailValid(){
        String target = emailAdress.getText().toString();
        if (!(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())){
            showValtext(emailValtext, "Not a valid email", emailAdress);
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
            public void afterTextChanged(Editable s) { }
        });
    }

    void submit(){
        try {
            sendCodeButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            String response = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/send_code_to_email/" + emailAdress.getText() + "/", this){
                @Override
                protected void onPostExecute(String response) {
                    sendCodeButton.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            sendCodeButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            sendCodeButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void sendCode(){
        try {
            String response = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/is_code_valid/" + verificationCode.getText() + "/", this){
                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                }
            }.execute().get();
            if (response != null){
                if (new JsonParser().parse(response).getAsJsonObject().get("is_valid").getAsBoolean()){

                } else {
                    showValtext(verificationValtext, "Wrong verification code", verificationCode);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
