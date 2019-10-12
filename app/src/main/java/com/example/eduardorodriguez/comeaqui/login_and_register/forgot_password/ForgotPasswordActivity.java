package com.example.eduardorodriguez.comeaqui.login_and_register.forgot_password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.example.eduardorodriguez.comeaqui.SplashActivity;
import com.example.eduardorodriguez.comeaqui.login_and_register.ChangePasswordActivity;
import com.example.eduardorodriguez.comeaqui.login_and_register.LoginActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView emailValtext;
    EditText emailAdress;
    TextView resendPassword;
    Button sendPassword;
    Button goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailValtext = findViewById(R.id.email_vtext);
        emailAdress = findViewById(R.id.email_address);
        sendPassword = findViewById(R.id.send_code_button);
        resendPassword = findViewById(R.id.send_again);
        goToLogin = findViewById(R.id.go_to_login);

        setEditText(emailAdress, emailValtext);
        sendPassword.setOnClickListener((v) -> sendEmail());
        resendPassword.setOnClickListener((v) -> sendEmail());
        goToLogin.setOnClickListener((v) -> {
            Intent a = new Intent(this, LoginActivity.class);
            startActivity(a);
        });
    }

    void sendEmail(){
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
            public void afterTextChanged(Editable s) {}
        });
    }

    void submit(){
        try {
            String response = new SendNewPassword("GET", getResources().getString(R.string.server) + "/send_new_password/" + emailAdress.getText() + "/").execute().get();
            if (response != null){
                sendPassword.setVisibility(View.GONE);
                goToLogin.setVisibility(View.VISIBLE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class  SendNewPassword extends AsyncTask<String[], Void, String> {
        private String uri;
        public String method;

        public SendNewPassword(String method, String uri){
            this.uri = uri;
            this.method = method;
        }

        @Override
        protected String doInBackground(String[]... params)
        {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();


            HttpGet httpGet = new HttpGet(this.uri);
            httpGet.setHeader("Content-Type", "application/json");
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    String resp = builder.toString();
                    return resp;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}