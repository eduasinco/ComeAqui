package com.example.eduardorodriguez.comeaqui.general;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.FoodPostDetail;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.utilities.WaitFragment;
import com.google.gson.JsonParser;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DinnerFragment extends Fragment {

    private static final String FOOD_POST_ID = "food_post_id";
    private int foodPostId;
    private OnListFragmentInteractionListener mListener;


    LinkedHashMap<Integer, OrderObject> data;
    MyDinnerRecyclerViewAdapter dinnerAdapter;

    FoodPostDetail foodPostDetail;
    RecyclerView recyclerView;
    FrameLayout waitingFrame;

    public DinnerFragment() {}

    public static DinnerFragment newInstance(int foodPostId) {
        DinnerFragment fragment = new DinnerFragment();
        Bundle args = new Bundle();
        args.putInt(FOOD_POST_ID, foodPostId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodPostId = getArguments().getInt(FOOD_POST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinner_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        waitingFrame = view.findViewById(R.id.waiting_frame);
        getFoodPostDetailsAndSet(foodPostId);
        return view;
    }

    void getFoodPostDetailsAndSet(int foodPostId){
        try {
            startWaitingFrame(true);
            new GetAsyncTask("GET", getResources().getString(R.string.server) + "/foods/" + foodPostId + "/", getContext()){
                @Override
                protected void onPostExecute(String response) {
                    if (response != null){
                        foodPostDetail = new FoodPostDetail(new JsonParser().parse(response).getAsJsonObject());
                        dinnerAdapter = new MyDinnerRecyclerViewAdapter(foodPostDetail.confirmedOrdersList, mListener);
                        recyclerView.setAdapter(dinnerAdapter);
                        startWaitingFrame(false);
                    }
                    super.onPostExecute(response);
                }
            }.execute().get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            startWaitingFrame(false);
            Toast.makeText(getContext(), "A problem has occurred", Toast.LENGTH_LONG).show();
        } catch (TimeoutException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Not internet connection", Toast.LENGTH_LONG).show();
        }
    }

    void startWaitingFrame(boolean start){
        if (start) {
            waitingFrame.setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction()
                    .replace(R.id.waiting_frame, WaitFragment.newInstance())
                    .commit();
        } else {
            waitingFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(OrderObject item);
        void onChatInteraction(OrderObject item);
    }
}
