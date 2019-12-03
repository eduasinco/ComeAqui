package com.example.eduardorodriguez.comeaqui.map.search_location;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.utilities.place_autocomplete.PlaceAutocompleteFragment;

import java.util.HashMap;

public class SearchLocationFragment extends Fragment implements PlaceAutocompleteFragment.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FrameLayout searchBox;
    private PlaceAutocompleteFragment placeAutocompleteFragment;

    public SearchLocationFragment() {}

    public static SearchLocationFragment newInstance(String param1, String param2) {
        SearchLocationFragment fragment = new SearchLocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_location, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        searchBox = view.findViewById(R.id.search_box);

        placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance("", true);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.search_box, placeAutocompleteFragment)
                .commit();

        return view;
    }

    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showList(boolean show){
        if (!show){
            hideKeyboard();
        }
        placeAutocompleteFragment.showList(show);
    }

    public void showSearchBox(boolean show){
        if (show){
            searchBox.setVisibility(View.VISIBLE);
        } else {
            hideKeyboard();
            searchBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListPlaceChosen(String address, String place_id, Double lat, Double lng, HashMap<String, String> address_elements) {
        mListener.onListPlaceChosen(address, place_id, lat, lng, address_elements);
    }

    @Override
    public void onPlacesAutocompleteChangeText() {
        mListener.onPlacesAutocompleteChangeText();
    }

    @Override
    public void closeButtonPressed() {
        showSearchBox(false);
        mListener.closeButtonPressed();
    }

    @Override
    public void searchButtonClicked() {
        mListener.searchButtonClicked();
    }

    public interface OnFragmentInteractionListener extends PlaceAutocompleteFragment.OnFragmentInteractionListener {
    }
}
