package com.example.eduardorodriguez.comeaqui.map.search_food.filter_fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodDateTimePickerFragment;
import com.example.eduardorodriguez.comeaqui.map.add_food.FoodTypeSelectorFragment;
import com.example.eduardorodriguez.comeaqui.map.search_food.SearchFoodFragment;

import static com.example.eduardorodriguez.comeaqui.R.color.colorPrimaryLight;
import static com.example.eduardorodriguez.comeaqui.R.color.grey_light;
import static com.example.eduardorodriguez.comeaqui.R.color.secondary_text_default_material_light;

public class FilterFragment extends Fragment implements
        FoodTypeSelectorFragment.OnFragmentInteractionListener,
        FoodDateTimePickerFragment.OnFragmentInteractionListener,
        DatePickerDialog.OnDateSetListener
{
    private static final String INITIAL_DISTANCE = "distance";
    private int initial_distance;

    private int filter;

    private OnFragmentInteractionListener mListener;

    private ConstraintLayout wholeView;
    private LinearLayout cardView;
    private LinearLayout sortWhole;
    private LinearLayout priceWhole;
    private LinearLayout mealTimeWhole;
    private LinearLayout distanceWhole;
    private LinearLayout dietaryWhole;
    private Button applyFilterButton;

    private TextView distanceText;
    private SeekBar distanceSeekbar;

    private LinearLayout[] sortOptions;
    private Button[] priceOptions;

    private int sortOption;
    private int priceOption;
    private int distance;
    private String startTime;
    private String endTime;
    private String dietary;

    FoodDateTimePickerFragment foodTimePickerFragment;
    FoodTypeSelectorFragment foodTypeSelectorFragment;

    public FilterFragment() {}

    public static FilterFragment newInstance(int initial_distance) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putInt(INITIAL_DISTANCE, initial_distance);
        fragment.setArguments(args);
        return new FilterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initial_distance = getArguments().getInt(INITIAL_DISTANCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        wholeView = view.findViewById(R.id.whole);
        cardView = view.findViewById(R.id.card_view);
        sortWhole = view.findViewById(R.id.sort_whole);
        priceWhole = view.findViewById(R.id.price_whole);
        mealTimeWhole = view.findViewById(R.id.meal_time_whole);
        distanceWhole = view.findViewById(R.id.distance_whole);
        dietaryWhole = view.findViewById(R.id.dietary_whole);
        applyFilterButton = view.findViewById(R.id.apply_filter_button);

        distanceText = view.findViewById(R.id.distance_text);
        distanceText.setText(initial_distance + "m");
        distanceSeekbar = view.findViewById(R.id.distance_seekbar);
        setPriceSeekBar();

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
                .replace(R.id.date_frame, foodTimePickerFragment)
                .commit();

        foodTypeSelectorFragment = FoodTypeSelectorFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.dietary_frame, foodTypeSelectorFragment)
                .commit();

        setSortButtons();
        setPirceButtons();
        wholeView.setOnTouchListener((v, event) -> {
            showFilter(false, 0);
            return false;
        });

        view.findViewById(R.id.close).setOnClickListener(v -> {
            showFilter(false, 0);
        });

        applyFilterButton.setOnClickListener(v -> sendFilterOptions());
        return view;
    }

    public void showFilter(boolean show, int filter){
        this.filter = filter;
        int move = cardView.getMeasuredHeight() + ((ConstraintLayout.LayoutParams) cardView.getLayoutParams()).bottomMargin * 2;
        if (show) {
            wholeView.setBackground(ContextCompat.getDrawable(getContext(), R.color.grey_trans));
            wholeView.setVisibility(View.VISIBLE);
            View[] filters = new View[]{sortWhole, priceWhole, mealTimeWhole, distanceWhole, dietaryWhole};
            for (View f : filters) {
                f.setVisibility(View.GONE);
            }
            if (filter > 4) {
                for (View f : filters) {
                    f.setVisibility(View.VISIBLE);
                }
            } else {
                filters[filter].setVisibility(View.VISIBLE);
            }

            cardView.setVisibility(View.VISIBLE);
            cardView.setTranslationY(move);
            cardView.animate().translationY(0).setDuration(move / 2);
        } else {
            cardView.animate().translationY(move).setDuration(move / 2).withEndAction(() -> {
                wholeView.setBackgroundColor(Color.TRANSPARENT);
                wholeView.setVisibility(View.GONE);
            });
        }
    }

    void setPriceSeekBar(){
        distanceSeekbar.setMax(10000);
        distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                distance = progress;
                String priceText = distance + "m";
                distanceText.setText(priceText);
                seekBar.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_shape));
            }
        });
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
                mListener.onMealTime(startTime, endTime);
                break;
            case 3:
                mListener.onDistance(distance);
                break;
            case 4:
                mListener.onDietary(dietary);
                break;
            case 5:
                mListener.onSort(sortOption);
                mListener.onPrice(priceOption);
                mListener.onDistance(distance);
                mListener.onDietary(dietary);
                break;
        }
        mListener.onApplyFilterClicked();
        wholeView.setVisibility(View.GONE);
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
        this.startTime = startDateTime;
        this.endTime= endDateTime;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        foodTimePickerFragment.onDateSet(view, year, month, dayOfMonth);
    }

    public interface OnFragmentInteractionListener {
        void onApplyFilterClicked();
        void onSort(int option);
        void onPrice(int option);
        void onMealTime(String startDateTime, String endDateTime);
        void onDistance(int distance);
        void onDietary(String dietary);
    }
}
