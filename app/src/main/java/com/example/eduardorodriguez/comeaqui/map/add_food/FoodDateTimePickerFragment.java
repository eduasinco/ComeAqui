package com.example.eduardorodriguez.comeaqui.map.add_food;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.map.DatePickerFragment;

import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class FoodDateTimePickerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    Button nowButton;
    Button scheduleButton;
    LinearLayout timePicker;
    TextView timeTextView;
    EditText date;
    EditText startTime;
    EditText endTime;
    View buttonTimeArray;

    Date datePicked;
    Long startMilli;
    Long endMilli;

    boolean isNow = false;
    String startDateString;
    int MINUTES = 30;

    public FoodDateTimePickerFragment() {}
    public static FoodDateTimePickerFragment newInstance() {
        return new FoodDateTimePickerFragment();
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
        date = view.findViewById(R.id.date);
        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);

        setButtonsLogic();
        setTimePickerLogic();
        return view;
    }


    void setButtonsLogic(){
        nowButton.setOnClickListener(v -> {
            Date postTimeDate = new Date(System.currentTimeMillis() + MINUTES * 60 * 1000);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            startDateString = format.format(postTimeDate);
            // mListener.onFragmentInteraction(startDateString);

            isNow = true;
            timeTextView.setText("Now (the post will be visible during " + MINUTES + " minutes from now)");
            nowButton.setBackgroundColor(Color.TRANSPARENT);
            scheduleButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
            showTimePicker(false);
        });

        scheduleButton.setOnClickListener(v -> {
            mListener.onFragmentInteraction("", "");
            timeTextView.setText("-- --");
            nowButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));
            scheduleButton.setBackgroundColor(Color.TRANSPARENT);
            showTimePicker(true);
        });
    }

    void setTimePickerLogic(){
        scheduleButton.setBackgroundColor(Color.TRANSPARENT);
        nowButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.text_input_shape));

        date.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getFragmentManager(), "date picker");
        });

        startTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) ->{
                if (datePicked == null){
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    datePicked = c.getTime();
                    String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(datePicked);
                    date.setText(currentDateString);
                }
                if (endMilli == null){
                    endMilli = TimeUnit.HOURS.toMillis(10) + TimeUnit.MINUTES.toMillis(00);
                    endTime.setText("10:00 PM");
                }
                startMilli = TimeUnit.HOURS.toMillis(selectedHour) + TimeUnit.MINUTES.toMillis(selectedMinute);
                startTime.setText(getTime(selectedHour, selectedMinute));
                checkDateAndSend(
                        new Date(datePicked.getTime() + startMilli),
                        new Date(datePicked.getTime() + endMilli)
                );
            }, hour, minute, false);
            mTimePicker.setTitle("Start start_time");
            mTimePicker.show();
        });

        endTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) ->{
                if (datePicked == null){
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    datePicked = c.getTime();
                    String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(datePicked);
                    date.setText(currentDateString);
                }
                if (startMilli == null){
                    startMilli = TimeUnit.HOURS.toMillis(7) + TimeUnit.MINUTES.toMillis(30);
                    startTime.setText("7:30 PM");
                }
                endMilli = TimeUnit.HOURS.toMillis(selectedHour) + TimeUnit.MINUTES.toMillis(selectedMinute);
                endTime.setText(getTime(selectedHour, selectedMinute));
                checkDateAndSend(
                        new Date(datePicked.getTime() + startMilli),
                        new Date(datePicked.getTime() + endMilli)
                );

            }, hour, minute, false);
            mTimePicker.setTitle("End start_time");
            mTimePicker.show();
        });
    }

    private String getTime(int hr,int min) {
        Time tme = new Time(hr,min,0);
        Format formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        datePicked = c.getTime();
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(datePicked);
        date.setText(currentDateString);

        if (startMilli == null){
            startMilli = TimeUnit.HOURS.toMillis(7) + TimeUnit.MINUTES.toMillis(30);
            startTime.setText("7:30 PM");
        }
        if (endMilli == null){
            endMilli = TimeUnit.HOURS.toMillis(10) + TimeUnit.MINUTES.toMillis(00);
            endTime.setText("10:00 PM");
        }
        checkDateAndSend(
                new Date(datePicked.getTime() + startMilli),
                new Date(datePicked.getTime() + endMilli)
        );
    }

    private void checkDateAndSend(Date startDate, Date endDate){
        Date now = new Date(System.currentTimeMillis());
        Date newEndDate;
        if (endDate.getTime() < startDate.getTime()){
            newEndDate = new Date(endDate.getTime() + TimeUnit.DAYS.toMillis(1));
        } else {
            newEndDate = endDate;
        }
        if (System.currentTimeMillis() > startDate.getTime()){
            DateFormat formatter = new SimpleDateFormat("HH:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateFormatted = formatter.format(now);
            timeTextView.setText("Please pick a start_time greater than today at " + dateFormatted);
        } else {
            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateFormatted = formatter.format(startDate);
            timeTextView.setText(dateFormatted);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            mListener.onFragmentInteraction(format.format(startDate), format.format(newEndDate));
        }
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
        void onFragmentInteraction(String startDateTime, String endDateTime);
    }
}
