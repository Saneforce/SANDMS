package com.saneforce.dms.Utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Vengat on 31-05-2021
 */
public class TimeUtils {

    private static final String TAG = TimeUtils.class.getSimpleName();
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
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


}
