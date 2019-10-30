package com.example.eduardorodriguez.comeaqui.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class DateFormatting {

    private static Date convertToDate(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(dateString);
    }

    public static String h(String dateString){
        try {
            Date date = convertToDate(dateString);
            DateFormat df = new SimpleDateFormat("h:mm a");
            df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateTextString = df.format(date);
            return dateTextString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String todayYesterdayWeekDay(String dateString){
        try {
            Date date = convertToDate(dateString);
            String[] week_days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
            String dateTextString = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long startOfDay = LocalDateTime.now().with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                long differenceToDate = now - date.getTime();
                long differenceToStartOrDay = now - startOfDay;

                if (differenceToStartOrDay >= differenceToDate) {
                    dateTextString = "TODAY";
                } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(1) >= differenceToDate && differenceToDate > differenceToStartOrDay) {
                    dateTextString = "YESTERDAY";
                } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(7) >= differenceToDate && differenceToDate > differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    dateTextString = week_days[c.get(Calendar.DAY_OF_WEEK) - 1];
                } else{
                    String pattern = "MM/dd/yyyy";
                    DateFormat df = new SimpleDateFormat(pattern);
                    dateTextString = df.format(date);
                }
            }
            return dateTextString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hYesterdayWeekDay(String dateString){
        try {
            Date date = convertToDate(dateString);
            String[] week_days = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
            String dateTextString = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long startOfDay = LocalDateTime.now().with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                long differenceToDate = now - date.getTime();
                long differenceToStartOrDay = now - startOfDay;

                if (differenceToStartOrDay >= differenceToDate) {
                    String pattern = "h:mm a";
                    DateFormat df = new SimpleDateFormat(pattern);
                    dateTextString = df.format(date);
                } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(1) >= differenceToDate && differenceToDate > differenceToStartOrDay) {
                    dateTextString = "YESTERDAY";
                } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(7) >= differenceToDate && differenceToDate > differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    dateTextString = week_days[c.get(Calendar.DAY_OF_WEEK) - 1];
                } else{
                    String pattern = "MM/dd/yyyy";
                    DateFormat df = new SimpleDateFormat(pattern);
                    dateTextString = df.format(date);
                }
            }
            return dateTextString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
