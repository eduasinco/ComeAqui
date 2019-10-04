package com.example.eduardorodriguez.comeaqui.review;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.OrderObject;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GuestReviewFragment extends Fragment {

    private static final String FOOD_POST_ID = "foodPostId";
    private Integer foodPostId = 1;
    private OnListFragmentInteractionListener mListener;
    MyGuestReviewRecyclerViewAdapter adapter;

    RecyclerView recyclerView;

    public GuestReviewFragment() {}

    public static GuestReviewFragment newInstance(int foodPostId) {
        GuestReviewFragment fragment = new GuestReviewFragment();
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
        View view = inflater.inflate(R.layout.fragment_guestreview_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        getOrdersAndSet(foodPostId);
        return view;
    }

    void getOrdersAndSet(int foodPostId){
        GetAsyncTask process = new GetAsyncTask("GET", getResources().getString(R.string.server) + "/food_post_detail/" + foodPostId + "/");
        try {
            String response = process.execute().get();
            if (response != null){
                try {
                    ArrayList<OrderObject>  orderObjects = new ArrayList<>();
                    for (JsonElement pa : new JsonParser().parse(response).getAsJsonArray()) {
                        orderObjects.add(new OrderObject(pa.getAsJsonObject()));
                    }
                    adapter = new MyGuestReviewRecyclerViewAdapter(orderObjects, mListener);
                    recyclerView.setAdapter(adapter);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
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
    }
}
