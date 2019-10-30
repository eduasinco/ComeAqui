package com.example.eduardorodriguez.comeaqui.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.example.eduardorodriguez.comeaqui.App.USER;

public class DateFormatting {
    static String[] WEEK_DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

    private static Date convertToDate(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(dateString);
    }

    public static String h(String dateString){
        try {
            // https://stackoverflow.com/questions/32113211/saving-model-instance-with-datetimefield-in-django-admin-loses-microsecond-resol
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = format.parse(dateString);

            DateFormat df = new SimpleDateFormat("h:mm a");
            df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateTextString = df.format(date);
            return dateTextString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date startOfTodayUTC() throws ParseException {
        long now_in_UTC = System.currentTimeMillis();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startTodayString = format.format(new Date(now_in_UTC));
        return format.parse(startTodayString);
    }

    public static Date startOfToday(String timeZone) throws ParseException {
        long now_in_UTC = System.currentTimeMillis();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        String startTodayString = format.format(new Date(now_in_UTC));
        return format.parse(startTodayString);
    }

    public static String todayYesterdayWeekDay(String dateString){
        try {
            Date date = convertToDate(dateString);

            long now_in_UTC = System.currentTimeMillis();
            long startOfDay_UTC = startOfTodayUTC().getTime();

            long differenceToDate = now_in_UTC - date.getTime();
            long differenceToStartOrDay = now_in_UTC - startOfDay_UTC;

            if (differenceToStartOrDay >= differenceToDate) {
                return "TODAY";
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(1) >= differenceToDate && differenceToDate > differenceToStartOrDay) {
                return "YESTERDAY";
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(7) >= differenceToDate && differenceToDate > differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                return WEEK_DAYS[c.get(Calendar.DAY_OF_WEEK) - 1];
            } else{
                String pattern = "MM/dd/yyyy";
                DateFormat df = new SimpleDateFormat(pattern);
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return df.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hYesterdayWeekDay(String dateString){
        try {
            Date date = convertToDate(dateString);
            long now_in_UTC = System.currentTimeMillis();
            long startOfDay = startOfTodayUTC().getTime();

            long differenceToDate = now_in_UTC - date.getTime();
            long differenceToStartOrDay = now_in_UTC - startOfDay;

            if (differenceToStartOrDay >= differenceToDate) {
                String pattern = "h:mm a";
                DateFormat df = new SimpleDateFormat(pattern);
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return df.format(date);
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(1) >= differenceToDate && differenceToDate > differenceToStartOrDay) {
                return "YESTERDAY";
            } else if (differenceToStartOrDay + TimeUnit.DAYS.toMillis(7) >= differenceToDate && differenceToDate > differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                return WEEK_DAYS[c.get(Calendar.DAY_OF_WEEK) - 1];
            } else{
                String pattern = "MM/dd/yyyy";
                DateFormat df = new SimpleDateFormat(pattern);
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return df.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
