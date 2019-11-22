package com.g2.runningback.Common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;
import java.util.Calendar;

public class Common {
    public static String URL_SERVER = "http://10.0.2.2:8080/RunningWeb/";

    public static boolean networkConnected(Activity activity) {
        //連網
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, int message) {
        Toast.makeText(context, String.valueOf(message), Toast.LENGTH_LONG).show();
    }

    public static Gson getTimeStampGson(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyyMMddhhmmss");
        gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter());
        Gson gson = gsonBuilder.create();

        return gson;
    }

    public static String getDay(Timestamp timestamp) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        cal.add(Calendar.MONTH, 1);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        String day = "";
        String month = "";
        if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
            day += "0";
        }
        if (cal.get(Calendar.MONTH) < 10) {
            month += "0";
        }
        day += String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        month += cal.get(Calendar.MONTH);
        String dayStr = month + "/" + day;

        return dayStr;
    }


}
