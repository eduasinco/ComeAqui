package com.example.eduardorodriguez.comeaqui.map.search_food.filter_fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodDateTimePickerFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;

import static com.example.eduardorodriguez.comeaqui.R.color.colorPrimaryLight;
import static com.example.eduardorodriguez.comeaqui.R.color.grey_light;
import static com.example.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;

public class FilterFragment extends Fragment implements
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        FoodDateTimePickerFragment.OnFragmentInteractionListener
{
    private static final String FILTER = "filter";
    private int filter;

    private OnFragmentInteractionListener mListener;

    private LinearLayout sortWhole;
    private LinearLayout priceWhole;
    private LinearLayout mealTimeWhole;
    private LinearLayout distanceWhole;
    private LinearLayout dietaryWhole;

    private LinearLayout[] sortOptions;
    private Button[] priceOptions;

    private int sortOption;
    private int priceOption;
    private int distance;
    private String dietary;

    FoodDateTimePickerFragment foodTimePickerFragment;

    public FilterFragment() {}

    public static FilterFragment newInstance(int filter) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putInt(FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter = getArguments().getInt(FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        sortWhole = view.findViewById(R.id.sort_whole);
        priceWhole = view.findViewById(R.id.price_whole);
        mealTimeWhole = view.findViewById(R.id.meal_time_whole);
        distanceWhole = view.findViewById(R.id.distance_whole);
        dietaryWhole = view.findViewById(R.id.dietary_whole);
        sortOptions = new LinearLayout[]{
                view.findViewById(R.id.sort0),
                view.findViewById(R.id.sort1),
                view.findViewById(R.id.sort2)
        };
        priceOptions = new Button[]{
                view.findViewById(R.id.price0),
                view.findViewById(R.id.price1),
                view.findViewById(R.id.price2)
        };
        foodTimePickerFragment = FoodDateTimePickerFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.food_time_picker_frame, foodTimePickerFragment)
                .commit();

        Button applyFilterButton = view.findViewById(R.id.apply_filter_button);
        applyFilterButton.setOnClickListener(v -> sendFilterOptions());
        setSortButtons();
        setPirceButtons();
        return view;
    }

    void sendFilterOptions(){
        switch (filter){
            case 0:
                mListener.onSort(sortOption);
                break;
            case 1:
                mListener.onPrice(priceOption);
                break;
            case 2:
                mListener.onDistance(distance);
                break;
            case 3:
                mListener.onDietary(dietary);
                break;
            case 4:
                mListener.onSort(sortOption);
                mListener.onPrice(priceOption);
                mListener.onDistance(distance);
                mListener.onDietary(dietary);
                break;
        }
    }

    void setSortButtons(){
        for (int i = 0; i < sortOptions.length; i++){
            LinearLayout priceClicked = sortOptions[i];
            final int finalI = i;
            priceClicked.setOnClickListener(v -> {
                sortOption = finalI;
                for (LinearLayout button: sortOptions){
                    button.setBackgroundColor(ContextCompat.getColor(getContext(), colorPrimaryLight));
                }
                for (LinearLayout button: sortOptions){
                    if (button != priceClicked){
                        button.setBackgroundColor(ContextCompat.getColor(getContext(), grey_light));
                    }
                }
            });
        }
    }

    void setPirceButtons(){
        for (int i = 0; i < priceOptions.length; i++){
            Button priceClicked = priceOptions[i];
            final int finalI = i;
            priceClicked.setOnClickListener(v -> {
                priceOption = finalI;
                for (Button button: priceOptions){
                    button.setBackgroundColor(ContextCompat.getColor(getContext(), colorPrimaryLight));
                    button.setTextColor(Color.WHITE);
                }
                for (Button button: priceOptions){
                    if (button != priceClicked){
                        button.setBackgroundColor(ContextCompat.getColor(getContext(), grey_light));
                        button.setTextColor(ContextCompat.getColor(getContext(), secondary_text_default_material_light));
                    }
                }
            });
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
    public void onFragmentInteraction(boolean[] pressed) {
        StringBuilder s = new StringBuilder();
        for (boolean b: pressed){
            s.append(b ? "1" : "0");
        }
        dietary = s.toString();
    }

    @Override
    public void onFragmentInteraction(String startDateTime, String endDateTime) {
        mListener.onFragmentInteraction(startDateTime, endDateTime);
    }

    public interface OnFragmentInteractionListener {
        void onApplyClicked();
        void onSort(int option);
        void onPrice(int option);
        void onDistance(int distance);
        void onDietary(String dietary);
        void onFragmentInteraction(String startDateTime, String endDateTime);
    }
}
