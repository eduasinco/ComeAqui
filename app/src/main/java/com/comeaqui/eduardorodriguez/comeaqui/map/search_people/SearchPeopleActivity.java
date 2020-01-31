package com.comeaqui.eduardorodriguez.comeaqui.map.search_people;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.profile.ProfileViewActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.comeaqui.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class SearchPeopleActivity extends AppCompatActivity {

    ArrayList<User> data;
    UserRecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    FrameLayout waitFrame;
    LinearLayout noPeopleView;
    EditText searchBox;
    ImageButton deleteText;
    ImageView noListImage;
    TextView noListMessage;

    String query = "query=";
    ArrayList<AsyncTask> tasks = new ArrayList<>();


    @Override
    public void onResume() {
        super.onResume();
        getPeopleAndSet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);

        recyclerView = findViewById(R.id.recycler_chat);
        waitFrame = findViewById(R.id.wait_frame);
        noPeopleView = findViewById(R.id.no_messages);
        searchBox = findViewById(R.id.search_box);
        deleteText = findViewById(R.id.delete_text);
        noListImage = findViewById(R.id.no_list_image);
        noListMessage = findViewById(R.id.no_list_message);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.wait_frame, WaitFragment.newInstance())
                .commit();

        setSearchListener();

        deleteText.setOnClickListener(v -> searchBox.setText(""));
        View backView = findViewById(R.id.back);
        backView.setOnClickListener(v -> finish());
    }

    static long last_text_edit = 0;
    void setSearchListener(){
        final long delay = 1000;
        final Handler handler = new Handler();
        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                getPeopleAndSet();
            }
        };
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){}
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before, int count){
                handler.removeCallbacks(input_finish_checker);
            }
            @Override
            public void afterTextChanged ( final Editable s){
                if (s.length() > 0) {
                    deleteText.setVisibility(View.VISIBLE);
                    noListImage.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.not_found));
                    noListMessage.setText("No people found");
                } else {
                    noListImage.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.not_found));
                    noListMessage.setText("No people");
                    deleteText.setVisibility(View.GONE);
                    hideKeyboard();
                }
                query = "query=" + s.toString();
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    public void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                User chat = new User(jo);
                data.add(chat);
            }
            if (data.size() > 0){
                noPeopleView.setVisibility(View.GONE);
            } else{
                noPeopleView.setVisibility(View.VISIBLE);
            }
            adapter = new UserRecyclerViewAdapter(data, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void getPeopleAndSet(){
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_search/" + query + "/").execute());
    }

    private class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            startWaitingFrame(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getApplication(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                makeList(new JsonParser().parse(response).getAsJsonArray());
            }
            startWaitingFrame(false);
            super.onPostExecute(response);
        }

    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitFrame.setVisibility(View.VISIBLE);
        } else {
            waitFrame.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void onFragmentInteraction(User user){
        Intent k = new Intent(this, ProfileViewActivity.class);
        k.putExtra("userId", user.id);
        startActivity(k);
    }


    @Override
    public void onDestroy() {
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        super.onDestroy();
    }
}