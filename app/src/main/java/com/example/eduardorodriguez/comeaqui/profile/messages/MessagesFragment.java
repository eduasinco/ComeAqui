package com.example.eduardorodriguez.comeaqui.profile.messages;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eduardorodriguez.comeaqui.server.GetAsyncTask;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;
import com.example.eduardorodriguez.comeaqui.server.Server;
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
public class MessagesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    static ArrayList<MessageObject> data;
    static MyMessagesRecyclerViewAdapter adapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessagesFragment() {
    }

    public static void makeList(JsonArray jsonArray){
        try {
            data = new ArrayList<>();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(new MessageObject(jo));
            }
            adapter.updateData(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void appendToList(String jsonString){
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(jsonString).getAsJsonArray();
        JsonObject jo = jsonArray.get(0).getAsJsonObject();
        data.add(0, new MessageObject(jo));
        adapter.updateData(data);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MessagesFragment newInstance(int columnCount) {
        MessagesFragment fragment = new MessagesFragment();
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
        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            Server getMessages = new Server("GET", getResources().getString(R.string.server) + "my_messages/");

            try {
                String response = getMessages.execute().get();
                if (response != null)
                    makeList(new JsonParser().parse(response).getAsJsonArray());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            adapter = new MyMessagesRecyclerViewAdapter(data, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
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

class MessageObject{
    String firstName;
    String lastName;
    String senderEmail;
    String senderImage;
    String creationDate;
    String id;
    String postPlateName;
    String postFoodPhoto;
    String postPrice;
    String postDescription;
    String post;
    String poster;
    public MessageObject(JsonObject jo){
        firstName = jo.get("sender_first_name").getAsString();
        lastName = jo.get("sender_last_name").getAsString();
        senderEmail = jo.get("sender_email").getAsString();
        senderImage = jo.get("sender_image").getAsString();
        creationDate = jo.get("created_at").getAsString();
        id = jo.get("id").getAsNumber().toString();

        postPlateName = jo.get("post_plate_name").getAsString();
        postFoodPhoto = jo.get("post_food_photo").getAsString();
        postPrice = jo.get("post_price").getAsString();
        postDescription = jo.get("post_description").getAsString();
        post = jo.get("post").getAsNumber().toString();
        poster = jo.get("sender").getAsNumber().toString();
    }
}
