package com.example.eduardorodriguez.comeaqui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import com.hbb20.CountryCodePicker;

public class EditAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        CountryCodePicker ccp;
        AppCompatEditText edtPhoneNumber;
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

    }
}
