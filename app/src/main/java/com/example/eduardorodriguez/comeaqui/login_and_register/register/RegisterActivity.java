package com.example.eduardorodriguez.comeaqui.login_and_register.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.eduardorodriguez.comeaqui.R;

public class RegisterActivity extends AppCompatActivity implements
        NameSurnameFragment.OnFragmentInteractionListener,
        PhoneNumberFragment.OnFragmentInteractionListener,
        PasswordFragment.OnFragmentInteractionListener,
        EmailFragment.OnFragmentInteractionListener{

    String name, surname, phoneNumber, password, email;

    ImageView backArrow;

    Fragment[] fragmentArray;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backArrow = findViewById(R.id.back_arr);

        fragmentArray = new Fragment[]{
                NameSurnameFragment.newInstance(),
                PhoneNumberFragment.newInstance(),
                EmailFragment.newInstance(),
                PasswordFragment.newInstance()
        };

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_frame, fragmentArray[0])
                .commit();

        backArrow.setOnClickListener(v -> transitionBack());
    }

    void submit(){

    }

    @Override
    public void finish() {
        if (i == 0){
            super.finish();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_frame, fragmentArray[i -= 1])
                .commit();
    }

    void transitionBack(){
        if (i == 0){
            finish();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_frame, fragmentArray[i -= 1])
                .commit();
    }

    @Override
    public void onNameSurname(String name, String surname) {
        this.name = name;
        this.surname = surname;
        i = 1;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_frame, fragmentArray[1])
                .commit();
    }

    @Override
    public void onPhoneNumber(String fullNumber, String number) {
        this.phoneNumber = fullNumber;
        i = 2;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_frame, fragmentArray[2])
                .commit();
    }

    @Override
    public void onEmail(String email) {
        this.email = email;
        i = 3;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_frame, fragmentArray[3])
                .commit();
    }

    @Override
    public void onPassword(String password) {
        this.password = password;

    }
}
