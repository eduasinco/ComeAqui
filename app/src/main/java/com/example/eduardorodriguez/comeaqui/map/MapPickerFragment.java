package com.example.eduardorodriguez.comeaqui.map;

import android.content.Context;
import android.net.Uri;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MapPickerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ImageView shadow;
    ImageView hande;
    ImageView shadowPoint;
    public TextView pickedAdress;
    ConstraintLayout mapPickerPanView;


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
        pickedAdress.setText("Loading...");
        new Server("GET", uri){
            @Override
            protected void onPostExecute(String response) {
                if (response != null){
                    JsonObject joo = new JsonParser().parse(response).getAsJsonObject();
                    JsonArray jsonArray = joo.get("results").getAsJsonArray();
                    if (jsonArray.size() > 0) {
                        String address = jsonArray.get(0).getAsJsonObject().get("formatted_address").getAsString();
                        pickedAdress.setText(address);
                        mListener.onFragmentInteraction(address);
                    }
                }
                super.onPostExecute(response);
            }
        }.execute();
    }

    public void apearMapPicker(boolean apear){
        shadowPoint.setVisibility(apear ? View.VISIBLE: View.INVISIBLE);
        mapPickerPanView.setVisibility(apear ? View.VISIBLE: View.GONE);
        hande.setVisibility(apear ? View.VISIBLE: View.GONE);
        shadow.setVisibility(apear ? View.VISIBLE: View.GONE);
        shadow.setVisibility(apear ? View.VISIBLE: View.GONE);
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
        void onFragmentInteraction(String address);
    }
}
