package com.example.eduardorodriguez.comeaqui.chat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.profile.User;

public class ConversationActivity extends AppCompatActivity {

    User senderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);


        Bundle b = getIntent().getExtras();
        if(b != null && b.get("object") != null) {
            senderUser = (User) b.getSerializable("sender");
        }
    }
}
