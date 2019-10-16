package com.example.eduardorodriguez.comeaqui.general;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class DinnersListActivity extends AppCompatActivity implements DinnerFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinners_list);

        Bundle b =  getIntent().getExtras();
        if(b != null && b.get("foodPostId") != null){
            int fpId = b.getInt("foodPostId");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dinners_list_frame, DinnerFragment.newInstance(fpId))
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(OrderObject item) {
        goToProfileView(item.owner);
    }

    @Override
    public void onChatInteraction(OrderObject item) {
        goToConversationWithUser(item.owner);
    }

    void goToProfileView(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("user", user);
        startActivity(k);
    }

    void goToConversationWithUser(User user){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/get_or_create_chat/" + user.id + "/");
        try {
            String response = process.execute().get();
            if (response != null) {
                ChatObject chat = new ChatObject(new JsonParser().parse(response).getAsJsonObject());
                goToConversationActivity(chat);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void goToConversationActivity(ChatObject chat){
        Intent k = new Intent(this, ConversationActivity.class);
        k.putExtra("chat", chat);
        startActivity(k);
    }
}
