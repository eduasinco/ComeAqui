package com.example.eduardorodriguez.comeaqui.review.food_review_look;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.example.eduardorodriguez.comeaqui.objects.ReviewReplyObject;
import com.example.eduardorodriguez.comeaqui.server.PostAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.ErrorMessageFragment;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReplyReviewActivity extends AppCompatActivity {

    TextView review;
    TextView usernameTime;
    ImageButton close;
    Button post;
    EditText reply;
    FrameLayout errorMessage;
    FrameLayout waitingFrame;

    ReviewObject reviewObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_review);

        review = findViewById(R.id.review);
        usernameTime = findViewById(R.id.username_and_time);
        close = findViewById(R.id.close);
        post = findViewById(R.id.post_reply);
        reply = findViewById(R.id.reply);
        errorMessage = findViewById(R.id.error_message);
        waitingFrame = findViewById(R.id.waiting_frame);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("review") != null){
            reviewObject = (ReviewObject) b.get("review");

            setThings();
        }
    }

    void setThings(){
        review.setText(reviewObject.review);
        usernameTime.setText(reviewObject.owner.username + " " + reviewObject.createdAt);
        post.setOnClickListener(v -> postReviewReply());
        close.setOnClickListener(v -> finish());
    }

    void postReviewReply(){
        try {
            startWaitingFrame(true);
            new PostAsyncTask(this,getResources().getString(R.string.server) + "/create_review_reply/"){
                @Override
                protected void onPostExecute(String response) {
                    JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                    ReviewReplyObject reviewObject = new ReviewReplyObject(jo);
                    finish();
                    super.onPostExecute(response);
                }
            }.execute(
                    new String[]{"reply", reply.getText().toString()},
                    new String[]{"review_id", reviewObject.id + ""}
            ).get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(this, "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.waiting_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitingFrame.setVisibility(View.GONE);
        }
    }

    void showErrorMessage(){
        errorMessage.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.error_message_frame, ErrorMessageFragment.newInstance(
                        "Error during posting",
                        "Please make sure that you have connection to the internet"))
                .commit();
    }
}
