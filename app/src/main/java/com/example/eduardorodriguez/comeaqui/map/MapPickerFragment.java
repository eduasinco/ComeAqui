package com.example.eduardorodriguez.comeaqui.map;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.Server;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class MapPickerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ImageView shadow;
    ImageView hande;
    ImageView shadowPoint;
    public TextView pickedAdress;
    ConstraintLayout mapPickerPanView;

    public boolean abled;
    String LOADING = "Loading...";

    public MapPickerFragment() {}
    public static MapPickerFragment newInstance() {
        return new MapPickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_picker, container, false);
        mapPickerPanView = view.findViewById(R.id.map_picker_pan);
        shadow = view.findViewById(R.id.shadow);
        hande = view.findViewById(R.id.handle);
        shadowPoint = view.findViewById(R.id.shadow_point);
        pickedAdress = view.findViewById(R.id.pickedAdress);
        return view;
    }

    public void setAddressTextVisible(boolean visible){
        if (visible){

        } else {
            pickedAdress.setVisibility(View.GONE);
        }
    }

    public void getLocationFromGoogle(LatLng latLng){
        String latLngString = latLng.latitude + "," + latLng.longitude;
        String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLngString + "&key=" + getResources().getString(R.string.google_key);
        pickedAdress.setVisibility(View.VISIBLE);
        pickedAdress.setText(LOADING);
        new Server(getContext(),"GET", uri){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    JsonObject joo = new JsonParser().parse(response).getAsJsonObject();
                    JsonArray jsonArray = joo.get("results").getAsJsonArray();
                    if (jsonArray.size() > 0) {
                        JsonObject jo = jsonArray.get(0).getAsJsonObject();
                        JsonArray addss_components = jo.get("address_components").getAsJsonArray();
                        String address = jo.get("formatted_address").getAsString();
                        String place_id = jo.get("place_id").getAsString();
                        JsonObject jsonLocation = jo.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
                        Double lat = jsonLocation.get("lat").getAsDouble();
                        Double lng = jsonLocation.get("lng").getAsDouble();

                        HashMap<String, String> address_elements = new HashMap<>();
                        for (JsonElement je: addss_components){
                            address_elements.put(je.getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString(), je.getAsJsonObject().get("long_name").getAsString());
                        }
                        pickedAdress.setText(address);
                        if (!address.equals(LOADING)){
                            mListener.refreshFragment(
                                    address,
                                    place_id,
                                    lat,
                                    lng,
                                    address_elements
                            );
                        }
                    }
                }
                super.onPostExecute(response);
            }
        }.execute();
    }

    public void apearMapPicker(boolean apear){
        abled = apear;
        shadowPoint.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        mapPickerPanView.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        hande.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        shadow.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
    }

    public void moveMapPicker(boolean up){
        int move = 50;
        int secs = move * 2;
        if (up) {
            shadowPoint.animate().alpha(0.5f).setDuration(secs);
            mapPickerPanView.animate().translationY(-move).setDuration(secs);
            hande.animate().translationY(-move).setDuration(secs);
            shadow.animate().translationY(-move * 2 / 3).setDuration(secs);
            shadow.animate().translationX(move * 1 / 3).setDuration(secs);
        } else {
            shadowPoint.animate().alpha(0).setDuration(secs);
            mapPickerPanView.animate().translationY(0).setDuration(secs);
            hande.animate().translationY(0).setDuration(secs);
            shadow.animate().translationY(0).setDuration(secs);
            shadow.animate().translationX(0).setDuration(secs);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException("The parent fragment must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void refreshFragment(String address, String place_id, Double lat, Double lng, HashMap<String, String> address_elements);
    }
}
