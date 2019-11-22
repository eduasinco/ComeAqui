package com.example.eduardorodriguez.comeaqui.utilities.place_autocomplete;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyPlacesAutocompleteRecyclerViewAdapter extends RecyclerView.Adapter<MyPlacesAutocompleteRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String[]> mValues;
    private final PlaceAutocompleteFragment f;
    private PlaceAutocompleteFragment.OnFragmentInteractionListener mListener;

    public MyPlacesAutocompleteRecyclerViewAdapter(ArrayList<String[]> items, PlaceAutocompleteFragment f, PlaceAutocompleteFragment.OnFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.f = f;
    }

    public void updateData(ArrayList<String[]> data){
        this.mValues = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_placesautocomplete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.contentView.setText(holder.mItem[0]);
        holder.mView.setOnClickListener(v -> {
            f.setAddress(holder.mItem[0], holder.mItem[1]);
            JsonObject jsonLocation = getPlacesDetailFromGoogle(holder.mItem[1]);
            if (jsonLocation != null){
                mListener.onListPlaceChosen(holder.mItem[0], jsonLocation.get("lat").getAsDouble(), jsonLocation.get("lng").getAsDouble());
                f.setErrorBackground(false);
                f.placeClicked = true;
            }
        });
    }

    JsonObject getPlacesDetailFromGoogle(String placeId){
        String uri = "https://maps.googleapis.com/maps/api/place/details/json?input=bar&placeid=" + placeId + "&key=" + f.getResources().getString(R.string.google_key);
        Server gAPI = new Server(f.getContext(),"GET", uri);
        try {
            String jsonString = gAPI.execute().get(15, TimeUnit.SECONDS);
            if (jsonString != null) {
                JsonObject joo = new JsonParser().parse(jsonString).getAsJsonObject();
                JsonObject jsonLocation = joo.get("result").getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                return jsonLocation;

            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return (mValues != null) ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView contentView;
        public String[] mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            contentView = view.findViewById(R.id.content);
        }
    }
}
