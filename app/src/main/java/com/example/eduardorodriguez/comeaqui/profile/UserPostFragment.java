package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.ServerAPI;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

public class UserPostFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String USER_ID = "userId";
    // TODO: Customize parameters
    private int userId;

    private static ArrayList<FoodPost> data;
    private static MyUserPostRecyclerViewAdapter adapter;


    RecyclerView recyclerView;
    FrameLayout waitFrame;

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    public UserPostFragment() {
    }


    void makeList(JsonArray jsonArray){
        data = new ArrayList<>();
        for (JsonElement pa : jsonArray) {
            JsonObject jo = pa.getAsJsonObject();
            FoodPost foodPost = new FoodPost(jo);
            data.add(foodPost);
        }
        recyclerView.setAdapter(this.adapter);
        adapter.addNewRow(data);
    }

    public static UserPostFragment newInstance(int userId) {
        UserPostFragment fragment = new UserPostFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        getPostFromUser(userId);
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userpost_list, container, false);
        this.adapter = new MyUserPostRecyclerViewAdapter(data);

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.recycler_user_post);
        waitFrame = view.findViewById(R.id.wait_frame);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.wait_frame, WaitFragment.newInstance())
                .commit();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getPostFromUser(userId);
        return view;
    }


    void getPostFromUser(int userId){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/user_food_posts/" + userId + "/").execute());
    }

    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            startWaitingFrame(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getContext(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null){
                makeList(new JsonParser().parse(response).getAsJsonArray());
                super.onPostExecute(response);
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

    @Override
    public void onDetach() {
        super.onDetach();
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
    }

}
