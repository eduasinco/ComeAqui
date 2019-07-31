package com.example.eduardorodriguez.comeaqui.profile;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AddBioActivity extends AppCompatActivity {

    EditText bioEditTextView;
    int MAX_NUMBER_OF_LETTER = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bio);
        bioEditTextView = findViewById(R.id.bio_edit_text);
        TextView textCountView = findViewById(R.id.text_count);
        Button save = findViewById(R.id.save);
        Button discard = findViewById(R.id.discard);

        bioEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int letters = bioEditTextView.getText().toString().trim().length();
                textCountView.setText(letters + "/" + MAX_NUMBER_OF_LETTER);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        discard.setOnClickListener(v -> finish());
        save.setOnClickListener(v -> saveImage());
    }

    private void saveImage(){
        PatchAsyncTask putTask = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
        try {
            putTask.execute("bio", bioEditTextView.getText().toString()).get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        finish();
    }
}
