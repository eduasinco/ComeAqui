package com.example.eduardorodriguez.comeaqui.profile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.User;

public class ProfileViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_container, ProfileFragment.newInstance((User) getIntent().getExtras().getSerializable("user")))
                .commit();
    }
}
