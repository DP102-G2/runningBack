package com.g2.runningback.Common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

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

}
