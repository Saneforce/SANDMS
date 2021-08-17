package com.saneforce.dms.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Vengat on 31-05-2021
 */
public class TimeUtils {

    private static final String TAG = TimeUtils.class.getSimpleName();
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT3 = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT1 = "yyyy-MM-dd";
    public static final String FORMAT2 = "dd/MM/yyyy";

    public static long getTimeStamp(String date, String format){

        Date date2 = null;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            date2 = sdf.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date2.getTime();
    }

    public static String getCurrentTimeStamp(String format){

        String stringDate ="";

        long timestampMilliseconds =System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        stringDate = simpleDateFormat.format(new Date(timestampMilliseconds));

        return stringDate;
    }

    public static String getCurrentTime(String format){
//       2021-01-20 11:33:05
        long timestampMilliseconds =System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String stringDate = simpleDateFormat.format(new Date(timestampMilliseconds));
        Log.d(TAG, "getCurrentTimeZone: => "+ stringDate);
        return stringDate;
    }

    public static String formatdate(String fdate)
    {
        if(fdate == null || fdate.equals(""))
            return "";

        String datetime="";
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat d= new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date convertedDate = inputFormat.parse(fdate);
            datetime = d.format(convertedDate);

        }catch (ParseException e)
        {
            e.printStackTrace();
        }
        return  datetime;


    }


    public static void addLoginDate(Context context){
        Date date = new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String strDate = dateFormat.format(calendar.getTime());
        Shared_Common_Pref shared_common_pref = new Shared_Common_Pref(context);
        shared_common_pref.save(Shared_Common_Pref.LOGIN_DATE, strDate);

    }


    public static int compareCurrentAndLoginDate(String dateTime){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            if(dateTime!=null && !dateTime.equals("")){
                Date date1 = dateFormat.parse(dateTime);
                Date date =  dateFormat.parse(TimeUtils.getCurrentTime(FORMAT1));
                return date1.compareTo(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 1;

    }

    public static int hoursDifference(Date date1, Date date2) {

        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        return (int) (date2.getTime() - date1.getTime()) / MILLI_TO_HOUR;
    }

    public static Date getDate(String format, String dateString) {

        SimpleDateFormat formatter1=new SimpleDateFormat(format, Locale.ENGLISH);
        Date date = null;
        try {
            date = formatter1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String changeFormat(String fromFormat, String toFormat, String dateString) {

        SimpleDateFormat sdffromFormat=new SimpleDateFormat(fromFormat, Locale.ENGLISH);
        SimpleDateFormat sdftoFormat=new SimpleDateFormat(toFormat, Locale.ENGLISH);
        Date date = null;
        try {
            date = sdffromFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdftoFormat.format(date);
    }


}
