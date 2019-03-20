package com.example.eduardorodriguez.comeaqui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private static EditText addressView;

    public static void setAddress(String text){
        addressView.setText(text);
        addressView.setFocusable(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button editAccountView = findViewById(R.id.editAccount);


        editAccountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editAccount = new Intent(SettingsActivity.this, EditAccountActivity.class);
                startActivity(editAccount);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PlacesAutocompleteFragment())
                .commit();

        addressView = findViewById(R.id.address);
        addressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GoogleAPIAsyncTask gAPI = new GoogleAPIAsyncTask(addressView.getText().toString());
                gAPI.execute();
            }
        });
    }
}
