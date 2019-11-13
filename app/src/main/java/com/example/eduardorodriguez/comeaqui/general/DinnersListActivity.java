package com.example.eduardorodriguez.comeaqui.general;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.chat.chat_objects.ChatObject;
import com.example.eduardorodriguez.comeaqui.chat.conversation.ConversationActivity;
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DinnersListActivity extends AppCompatActivity implements DinnerFragment.OnListFragmentInteractionListener {

    DinnerFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinners_list);

        Bundle b =  getIntent().getExtras();
        if(b != null && b.get("foodPostId") != null){
            int fpId = b.getInt("foodPostId");

            fragment = DinnerFragment.newInstance(fpId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dinners_list_frame, fragment)
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
        k.putExtra("userId", user.id);
        startActivity(k);
    }

    void goToConversationWithUser(User user){
        try {
            fragment.startWaitingFrame(true);
            new GetAsyncTask("GET", getResources().getString(R.string.server) + "/get_or_create_chat/" + user.id + "/", this){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null) {
                        ChatObject chat = new ChatObject(new JsonParser().parse(response).getAsJsonObject());
                        goToConversationActivity(chat);
                        fragment.startWaitingFrame(false);
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            fragment.startWaitingFrame(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }


    void goToConversationActivity(ChatObject chat){
        Intent k = new Intent(this, ConversationActivity.class);
        k.putExtra("chatId", chat.id + "");
        startActivity(k);
    }
}
