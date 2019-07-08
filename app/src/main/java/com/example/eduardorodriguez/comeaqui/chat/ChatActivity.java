package com.example.eduardorodriguez.comeaqui.chat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;

public class ChatActivity extends AppCompatActivity implements ChatFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setFragment(new ChatFragment());
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.chat_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onListFragmentInteraction(ChatObject item) {

    }
}
