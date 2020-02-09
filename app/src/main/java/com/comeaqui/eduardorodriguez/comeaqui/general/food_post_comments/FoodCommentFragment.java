package com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.ReviewObject;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
public class FoodCommentFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String FOODPOST_ID = "column-count";
    // TODO: Customize parameters
    private int foodPostId;
    private MyFoodCommentRecyclerViewAdapter.OnListFragmentInteractionListener mListener;

    private EditText commentEditText;
    private Button commentButton;
    private RecyclerView recyclerView;

    private ArrayList<FoodCommentObject> foodComments;
    private MyFoodCommentRecyclerViewAdapter adapter;
    public FoodCommentFragment() { }

    ArrayList<AsyncTask> tasks = new ArrayList<>();

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FoodCommentFragment newInstance(int foodPostId) {
        FoodCommentFragment fragment = new FoodCommentFragment();
        Bundle args = new Bundle();
        args.putInt(FOODPOST_ID, foodPostId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodPostId = getArguments().getInt(FOODPOST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_foodcomment_list, container, false);
        commentEditText = view.findViewById(R.id.comment_edit_text);
        commentButton = view.findViewById(R.id.comment_button);
        recyclerView = view.findViewById(R.id.list);
        commentButton.setOnClickListener(v -> createAPostComment());
        getFoodPostComment();
        return view;
    }

    void getFoodPostComment(){
        tasks.add(new GetAsyncTask(getResources().getString(R.string.server) + "/food_post_comment/" + foodPostId + "/").execute());
    }
    class GetAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
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
                foodComments = new ArrayList<>();
                for(JsonElement je: new JsonParser().parse(response).getAsJsonArray()){
                    foodComments.add(new FoodCommentObject(je.getAsJsonObject()));
                }
                adapter = new MyFoodCommentRecyclerViewAdapter(foodComments, mListener);
                recyclerView.setAdapter(adapter);
            }
            super.onPostExecute(response);
        }
    }

    void createAPostComment(){
        tasks.add(new PostAsyncTask(getResources().getString(R.string.server) + "/food_post_comment/" + foodPostId + "/").execute(
                new String[]{"post_id", foodPostId + ""},
                new String[]{"comment_id", ""},
                new String[]{"message", commentEditText.getText().toString()}
        ));
    }
    private class PostAsyncTask extends AsyncTask<String[], Void, String> {
        String uri;

        public PostAsyncTask(String uri){
            this.uri = uri;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.upload(getContext(), "POST", this.uri, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
            }
            super.onPostExecute(response);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyFoodCommentRecyclerViewAdapter.OnListFragmentInteractionListener) {
            mListener = (MyFoodCommentRecyclerViewAdapter.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (AsyncTask task: tasks){
            if (task != null) task.cancel(true);
        }
        mListener = null;
    }
}
