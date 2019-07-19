package com.example.eduardorodriguez.comeaqui.profile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.FoodTypeFragment;
import com.example.eduardorodriguez.comeaqui.R;

public class ProfileViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        Bundle bundle = new Bundle();
        bundle.putSerializable("user_email",getIntent().getExtras().getString("user_email"));
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_container, fragment)
                .commit();
    }
}
