package com.example.eduardorodriguez.comeaqui.chat;

import android.view.View;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.utilities.SearchFragment;

public class ChatActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setFragment(new ChatFragment(), R.id.chat_frame);

        View backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> finish());
    }

    private void setFragment(Fragment fragment, int id) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String string) {
        System.out.println(string);
    }
}
