package com.example.eduardorodriguez.comeaqui.map.add_food;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.objects.User;
import com.example.eduardorodriguez.comeaqui.utilities.DateFormatting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class FoodTimePickerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    Button nowButton;
    Button scheduleButton;
    TimePicker timePicker;
    TextView timeTextView;
    View buttonTimeArray;

    boolean isNow = false;
    String postTimeString;
    int MINUTES = 30;

    public FoodTimePickerFragment() {}
    public static FoodTimePickerFragment newInstance() {
        return new FoodTimePickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setErrorBackground(){
        nowButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
        scheduleButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape_error));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_time_picker, container, false);
        nowButton = view.findViewById(R.id.anytime_button);
        scheduleButton = view.findViewById(R.id.schedule_button);
        timePicker = view.findViewById(R.id.timePicker);
        timeTextView = view.findViewById(R.id.time_text);
        buttonTimeArray = view.findViewById(R.id.button_array_time);

        setButtonsLogic();
        setTimePickerLogic();
        return view;
    }


    void setButtonsLogic(){
        nowButton.setOnClickListener(v -> {
            Date postTimeDate = new Date(System.currentTimeMillis() + MINUTES * 60 * 1000);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            postTimeString = format.format(postTimeDate);
            mListener.onFragmentInteraction(postTimeString);

            isNow = true;
            timeTextView.setText("Now (the post will be visible during " + MINUTES + " minutes from now)");
            nowButton.setBackgroundColor(Color.TRANSPARENT);
            scheduleButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
            showTimePicker(false);
        });

        scheduleButton.setOnClickListener(v -> {
            mListener.onFragmentInteraction("");
            timeTextView.setText("-- --");
            nowButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
            scheduleButton.setBackgroundColor(Color.TRANSPARENT);
            showTimePicker(true);
        });
    }

    void setTimePickerLogic(){
        timePicker.setOnTimeChangedListener((arg0, arg1, arg2) -> {
            scheduleButton.setBackgroundColor(Color.TRANSPARENT);
            nowButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));


            try {
                Date now = new Date(System.currentTimeMillis());
                Date todayDate = DateFormatting.startOfToday(USER.timeZone);

                Date postTimeDate = new Date(todayDate.getTime() + (arg0.getHour()*60 + arg0.getMinute())*60*1000);
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date now_plus_time_picked = new Date(now.getTime() + MINUTES *60*1000);
                DateFormat formatter = new SimpleDateFormat("h:mm a");
                if (now_plus_time_picked.getTime() > postTimeDate.getTime()){
                    formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                    String dateFormatted = formatter.format(now_plus_time_picked);
                    timeTextView.setText("Please pick a time greater than " + dateFormatted);
                } else {
                    formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                    String dateFormatted = formatter.format(postTimeDate);
                    timeTextView.setText("Today at: " + dateFormatted);
                    postTimeString = format.format(postTimeDate);
                    mListener.onFragmentInteraction(postTimeString);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    void showTimePicker(boolean show){
        int duration = 200;
        if (show){
            timePicker.setScaleX(0);
            timePicker.setVisibility(View.VISIBLE);
            timePicker.animate().scaleX(1).setDuration(duration);
        } else {
            timePicker.animate().scaleX(0).setDuration(duration).withEndAction(() -> timePicker.setVisibility(View.GONE));
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String postTimeString);
    }
}
