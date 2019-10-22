package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UserPostFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String USER = "user";
    // TODO: Customize parameters
    private User user;

    private static ArrayList<FoodPost> data;
    private static MyUserPostRecyclerViewAdapter adapter;


    RecyclerView recyclerView;
    FrameLayout waitFrame;


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

    public static UserPostFragment newInstance(User user) {
        UserPostFragment fragment = new UserPostFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getPostFromUser(user);
        return view;
    }


    void getPostFromUser(User user){
        try {
            startWaitingFrame(true);
            new GetAsyncTask("GET", getResources().getString(R.string.server) + "/user_food_posts/" + user.id + "/"){
                @Override
                protected void onPostExecute(String response) {
                    startWaitingFrame(false);
                    makeList(new JsonParser().parse(response).getAsJsonArray());
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(getContext(), "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(getContext(), "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitFrame.setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction()
                    .replace(R.id.wait_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
