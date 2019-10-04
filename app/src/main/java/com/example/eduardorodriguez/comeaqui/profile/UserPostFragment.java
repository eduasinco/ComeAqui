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
import com.example.eduardorodriguez.comeaqui.objects.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class UserPostFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String USER = "user";
    // TODO: Customize parameters
    private User user;

    private static ArrayList<FoodPost> data;
    private static MyUserPostRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserPostFragment() {
    }


    void makeList(JsonArray jsonArray){
        data = new ArrayList<>();
        for (JsonElement pa : jsonArray) {
            JsonObject jo = pa.getAsJsonObject();
            FoodPost foodPost = new FoodPost(jo);
            data.add(foodPost);
        }
        adapter.addNewRow(data);
    }

    public static void appendToList(String jsonString){
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
        JsonObject jo = jsonArray.get(0).getAsJsonObject();
        data.add(0, new FoodPost(jo));
        adapter.addNewRow(data);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getPostFromUser(user);
        recyclerView.setAdapter(this.adapter);

        return view;
    }


    void getPostFromUser(User user){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/user_food_posts/" + user.id + "/");
        try {
            String response = process.execute().get();
            if (response != null)
                makeList(new JsonParser().parse(response).getAsJsonArray());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
