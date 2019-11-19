package com.example.eduardorodriguez.comeaqui.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
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
            Date date = convertToDate(dateString);
            DateFormat df = new SimpleDateFormat("h:mm aa");
            df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
            String dateTextString = df.format(date);
            return dateTextString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date startOfToday(String timeZone) throws ParseException {
        long now = System.currentTimeMillis();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        String startTodayString = format.format(new Date(now));
        return format.parse(startTodayString);
    }

    public static String todayYesterdayWeekDay(String dateString){
        try {
            Date date = convertToDate(dateString);

            long now = System.currentTimeMillis();
            long startOfDay = DateFormatting.startOfToday(USER.timeZone).getTime();

            long differenceToDate = now - date.getTime();
            long differenceToStartOrDay = now - startOfDay;

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

    public static String happenedTodayYesterdayWeekDay(String dateString){
        try {
            Date date;
            try {
                date = convertToDate(dateString);
            } catch (ParseException e) {
                try {
                    // https://stackoverflow.com/questions/32113211/saving-model-instance-with-datetimefield-in-django-admin-loses-microsecond-resol
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    date = format.parse(dateString);
                } catch (ParseException e2) {
                    return null;
                }
            }

            long now = System.currentTimeMillis();
            long startOfDay = DateFormatting.startOfToday(USER.timeZone).getTime();

            long differenceToDate = now - date.getTime();
            long differenceToStartOrDay = now - startOfDay;

            if (now < date.getTime()) {
                DateFormat df = new SimpleDateFormat("h:mm aa");
                df.setTimeZone(TimeZone.getTimeZone(USER.timeZone));
                return df.format(date);
            } else if(now < date.getTime() + TimeUnit.HOURS.toMillis(1)){
                return "HAPPENING NOW";
            } else if (differenceToDate <= differenceToStartOrDay) {
                return "HAPPENED TODAY";
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(1)) {
                return "YESTERDAY";
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(7)) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                return WEEK_DAYS[c.get(Calendar.DAY_OF_WEEK) - 1];
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(30)) {
                int n = (int) (differenceToDate / TimeUnit.DAYS.toMillis(7));
                return n + "WEEKS AGO";
            } else if (differenceToDate <= differenceToStartOrDay + TimeUnit.DAYS.toMillis(30 * 12)) {
                int n = (int) (differenceToDate / TimeUnit.DAYS.toMillis(30));
                return n + "MONTHS AGO";
            } else {
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
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
            long now = System.currentTimeMillis();
            long startOfDay = startOfToday(USER.timeZone).getTime();

            long differenceToDate = now - date.getTime();
            long differenceToStartOrDay = now - startOfDay;

            if (differenceToStartOrDay >= differenceToDate) {
                String pattern = "h:mm aa";
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
