package com.example.eduardorodriguez.comeaqui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eduardorodriguez.comeaqui.dummy.DummyContent.DummyItem;
import com.example.eduardorodriguez.comeaqui.profile.MyPlacesAutocompleteRecyclerViewAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlacesAutocompleteFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static ArrayList<String[]> data;
    private static MyPlacesAutocompleteRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlacesAutocompleteFragment() {
    }

    public static void parseLatLng(String jsonString){
        try {
            JsonParser parser = new JsonParser();
            JsonObject joo = parser.parse(jsonString).getAsJsonObject();
            JsonObject res = joo.get("result").getAsJsonObject();
            saveLatLng(res);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void makeList2(String jsonString){
        try {
            JsonParser parser = new JsonParser();
            JsonObject joo = parser.parse(jsonString).getAsJsonObject();
            JsonArray jsonArray = joo.get("results").getAsJsonArray();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                saveLatLng(jo);
                break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveLatLng(JsonObject jo){
        try {
            JsonObject geo = jo.get("geometry").getAsJsonObject();
            JsonObject loc = geo.get("location").getAsJsonObject();
            Float lat = loc.get("lat").getAsFloat();
            Float lng = loc.get("lng").getAsFloat();

            PatchAsyncTask putTast = new PatchAsyncTask();
            putTast.execute("lat", lat.toString());
            PatchAsyncTask putTast2 = new PatchAsyncTask();
            putTast2.execute("lng", lng.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void makeList(String jsonString){
        try {
            data = new ArrayList<>();
            JsonParser parser = new JsonParser();
            JsonObject joo = parser.parse(jsonString).getAsJsonObject();
            JsonArray jsonArray = joo.get("predictions").getAsJsonArray();
            for (JsonElement pa : jsonArray) {
                JsonObject jo = pa.getAsJsonObject();
                data.add(createStringArray(jo));
            }
            adapter.updateData(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String[] createStringArray(JsonObject jo){
        String description = jo.get("description").getAsNumber().toString();
        String place_id = jo.get("place_id").getAsNumber().toString();
        String[] add = new String[]{description, place_id};
        return add;
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlacesAutocompleteFragment newInstance(int columnCount) {
        PlacesAutocompleteFragment fragment = new PlacesAutocompleteFragment();
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
        View view = inflater.inflate(R.layout.fragment_placesautocomplete_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyPlacesAutocompleteRecyclerViewAdapter(data, mListener);
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
