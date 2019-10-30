package com.example.eduardorodriguez.comeaqui.map.add_food;

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
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.eduardorodriguez.comeaqui.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
    int minutes = 30;

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

        setTimeLogic();
        setTimePickerLogic();
        return view;
    }

    void setTimeLogic(){
        nowButton.setOnClickListener(v -> {
            Date now = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String nowString = sdf.format(now);
            try {
                Date nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(nowString);
                Date postTimeDate = new Date(nowDate.getTime() + minutes*60*1000);
                postTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(postTimeDate);
                mListener.onFragmentInteraction(postTimeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            isNow = true;
            timeTextView.setText("Now (the post will be visible for an hour)");
            nowButton.setBackgroundColor(Color.TRANSPARENT);
            scheduleButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
            showTimePicker(false);
        });

        scheduleButton.setOnClickListener(v -> {
            timeTextView.setText("-- --");
            nowButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
            scheduleButton.setBackgroundColor(Color.TRANSPARENT);
            showTimePicker(true);
        });
    }

    void setTimePickerLogic(){
        timePicker.setOnTimeChangedListener((arg0, arg1, arg2) -> {
            timeTextView.setText("Today at: " + arg0.getHour() + ":" + arg0.getMinute());

            Date now = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String formattedDate = format.format(now);
            try {
                Date todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(formattedDate);
                Date postTimeDate = new Date(todayDate.getTime() + (arg0.getHour()*60 + arg0.getMinute())*60*1000);
                postTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(postTimeDate);
                mListener.onFragmentInteraction(postTimeString);
                if (now.getTime() + minutes*60*1000 > postTimeDate.getTime()){
                    Date date = new Date(now.getTime() + minutes*60*1000);
                    DateFormat formatter = new SimpleDateFormat("HH:mm");
                    formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                    String dateFormatted = formatter.format(date);

                    timeTextView.setText("Please pick a time greater than " + dateFormatted);
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
