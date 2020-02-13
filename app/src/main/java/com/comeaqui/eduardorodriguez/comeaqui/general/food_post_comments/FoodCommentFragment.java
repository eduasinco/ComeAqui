package com.comeaqui.eduardorodriguez.comeaqui.general.food_post_comments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.FoodCommentObject;
import com.comeaqui.eduardorodriguez.comeaqui.objects.User;
import com.comeaqui.eduardorodriguez.comeaqui.order.OrderLookActivity;
import com.comeaqui.eduardorodriguez.comeaqui.server.ServerAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FoodCommentFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String FOODPOST_ID = "food_post_id";
    // TODO: Customize parameters
    private int foodPostId;
    private OrderLookActivity mListener;

    private EditText commentEditText;
    private Button commentButton;
    private RecyclerView recyclerView;

    private ProgressBar sendLoadingProgress;

    private List<FoodCommentObject> foodComments;
    private HashMap<Integer, FoodCommentObject> foodCommentObjectHashMap;
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
    public void onResume() {
        super.onResume();
        hideKeyboard();
    }

    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    class RecurseComment {
        MyFoodCommentRecyclerViewAdapter adapter;
        List<FoodCommentObject> replies;
        HashMap<Integer, FoodCommentObject> repliesHashMap;

        RecurseComment(MyFoodCommentRecyclerViewAdapter adapter, List<FoodCommentObject> replies, HashMap<Integer, FoodCommentObject> repliesHashMap){
            this.adapter = adapter;
            this.replies = replies;
            this.repliesHashMap = repliesHashMap;
        }
    }
    private RecurseComment recurseToComment(JsonArray trace){
        MyFoodCommentRecyclerViewAdapter adapter = this.adapter;
        HashMap<Integer, FoodCommentObject> repliesHasMap = this.foodCommentObjectHashMap;
        List<FoodCommentObject> replies = this.foodComments;
        for (JsonElement je: trace){
            adapter = adapter.adapters.get(je.getAsInt());
            FoodCommentObject c = repliesHasMap.get(je.getAsInt());
            repliesHasMap = c.repliesHashMap;
            replies = c.replies;
        }
        return new RecurseComment(adapter, replies, repliesHasMap);
    }

    public void updateElement(FoodCommentObject newComment, JsonArray trace){
        RecurseComment rc = recurseToComment(trace);
        FoodCommentObject commentInList = rc.repliesHashMap.get(newComment.id);
        rc.replies.set(rc.replies.indexOf(commentInList), newComment);
        rc.repliesHashMap.put(newComment.id, newComment);
        rc.adapter.notifyItemChanged(rc.replies.indexOf(newComment));
    }

    public void goToElement(int commentId, JsonArray trace){
        RecurseComment rc = recurseToComment(trace);
        FoodCommentObject commentInList = rc.repliesHashMap.get(commentId);
        rc.adapter.notifyItemChanged(rc.replies.indexOf(commentInList));
    }

    public void deleteElement(FoodCommentObject newComment, JsonArray trace){
        RecurseComment rc = recurseToComment(trace);
        rc.adapter.notifyItemRemoved(rc.replies.indexOf(rc.repliesHashMap.get(newComment.id)));
        rc.replies.remove(rc.repliesHashMap.get(newComment.id));
    }

    public void addElement(FoodCommentObject newComment, JsonArray trace){
        RecurseComment rc = recurseToComment(trace);
        rc.replies.add(0, newComment);
        rc.repliesHashMap.put(newComment.id, newComment);
        rc.adapter.notifyItemInserted(0);
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
        sendLoadingProgress = view.findViewById(R.id.send_loading);
        recyclerView.setNestedScrollingEnabled(false);
        commentButton.setOnClickListener(v -> {
            if (!commentEditText.getText().toString().isEmpty()){
                createAPostComment();
                hideKeyboard();
            } else {
                Toast.makeText(getContext(), "You have to write something", Toast.LENGTH_SHORT).show();
            }
        });
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
                foodComments = new LinkedList<>();
                foodCommentObjectHashMap = new HashMap<>();
                for(JsonElement je: new JsonParser().parse(response).getAsJsonArray()){
                    FoodCommentObject fco = new FoodCommentObject(je.getAsJsonObject());
                    foodCommentObjectHashMap.put(fco.id, fco);
                    foodComments.add(fco);
                }
                adapter = new MyFoodCommentRecyclerViewAdapter(foodComments, mListener);
                recyclerView.setAdapter(adapter);
                mListener.onCommentsLoaded();
            }
            super.onPostExecute(response);
        }
    }

    public void getAndUpdateComment(int commentId){
        tasks.add(new GetCommentAsyncTask(getResources().getString(R.string.server) + "/comment_detail/" + commentId + "/").execute());
    }
    class GetCommentAsyncTask extends AsyncTask<String[], Void, String> {
        private String uri;
        public GetCommentAsyncTask(String uri){
            this.uri = uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String[]... params) {
            try {
                return ServerAPI.get(getActivity(), this.uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            if (null != response){
                JsonObject jo = new JsonParser().parse(response).getAsJsonObject();
                try {
                    updateElement(new FoodCommentObject(jo.get("comment").getAsJsonObject()), jo.get("trace").getAsJsonArray());
                } catch (Exception e){}

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
            sendLoadingProgress.setVisibility(View.VISIBLE);
            commentButton.setVisibility(View.GONE);
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
                addElement(new FoodCommentObject(jo.get("comment").getAsJsonObject()), jo.get("trace").getAsJsonArray());
            }
            commentEditText.setText("");
            sendLoadingProgress.setVisibility(View.GONE);
            commentButton.setVisibility(View.VISIBLE);
            super.onPostExecute(response);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyFoodCommentRecyclerViewAdapter.OnListFragmentInteractionListener) {
            mListener = (OrderLookActivity) context;
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


    public interface OnFragmentInteractionListener {
        void onCommentsLoaded();
    }
}
