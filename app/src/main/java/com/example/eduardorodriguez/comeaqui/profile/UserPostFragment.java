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
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserPostFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private static ArrayList<FoodPost> data;
    private static MyUserPostRecyclerViewAdapter adapter;

    private OnListFragmentInteractionListener mListener;

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
            foodPost.favourite_from_server = true;
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
    public static UserPostFragment newInstance(int columnCount) {
        UserPostFragment fragment = new UserPostFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userpost_list, container, false);
        this.adapter = new MyUserPostRecyclerViewAdapter(data, mListener);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            User user = (User) getArguments().getSerializable("user");
            getPostFromUser(user);

            recyclerView.setAdapter(this.adapter);
        }
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
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
