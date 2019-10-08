package com.example.eduardorodriguez.comeaqui.utilities.place_autocomplete;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;



public class PlaceAutocompleteFragment extends Fragment {
    private static final String ADDRESS = "address";
    private String address;
    private OnFragmentInteractionListener mListener;


    EditText addressView;
    TextView loadingView;
    RecyclerView recyclerView;
    View wholePlaceACView;

    MyPlacesAutocompleteRecyclerViewAdapter adapter;
    private static ArrayList<String[]> data;

    static long last_text_edit = 0;

    public PlaceAutocompleteFragment() {}

    public static PlaceAutocompleteFragment newInstance(String address) {
        PlaceAutocompleteFragment fragment = new PlaceAutocompleteFragment();
        Bundle args = new Bundle();
        args.putString(ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = getArguments().getString(ADDRESS);
        }
    }

    public void setErrorBackground(boolean error){
        if (error){
            wholePlaceACView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
        } else {
            wholePlaceACView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_autocomplete, container, false);
        addressView = view.findViewById(R.id.address);
        loadingView = view.findViewById(R.id.loading_places);
        recyclerView = view.findViewById(R.id.places_list);
        wholePlaceACView = view.findViewById(R.id.wholePlaceACView);

        loadingView.setVisibility(View.GONE);
        addressView.setText(address);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new MyPlacesAutocompleteRecyclerViewAdapter(data, this, mListener);
        recyclerView.setAdapter(adapter);

        detectTypingAndSetLocationPrediction();
        return view;
    }

    public String[] createStringArray(JsonObject jo){
        String description = jo.get("description").getAsNumber().toString();
        String place_id = jo.get("place_id").getAsNumber().toString();
        String[] add = new String[]{description, place_id};
        return add;
    }

    public void makeList(String jsonString){
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
            adapter.notifyDataSetChanged();

            recyclerView.hasPendingAdapterUpdates();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void showLoading(boolean show){
        if (show){
            loadingView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            loadingView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    void getPlacesListFromGoogle(){
        String uri = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + addressView.getText().toString() +
                "&types=geocode&language=en&key=" + getResources().getString(R.string.google_key);
        try {
            String jsonString = new Server("GET", uri){
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    showLoading(false);
                }
            }.execute().get(15, TimeUnit.SECONDS);
            if (jsonString != null) {
                makeList(jsonString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showLoading(false);
        }
    }

    private void detectTypingAndSetLocationPrediction(){

        final long delay = 1000;
        final Handler handler = new Handler();
        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                getPlacesListFromGoogle();
            }
        };

        addressView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count,
                                           int after){
            }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before,
                                        int count){
                //You need to remove this to run only once
                mListener.onPlacesAutocompleteChangeText();
                handler.removeCallbacks(input_finish_checker);
                showLoading(true);
            }
            @Override
            public void afterTextChanged ( final Editable s){
                //avoid triggering event when text is empty
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                } else {

                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void setAddress(String text, String id){
        addressView.setText(text);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onPlacesAutocomplete(String address, double lat, double lng);
        void onPlacesAutocompleteChangeText();
    }

}
