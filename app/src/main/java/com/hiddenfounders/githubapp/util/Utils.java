package com.hiddenfounders.githubapp.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

    public static final int PAGE_SIZE = 30;
    public static final int DEFAULT_STARTING_OFFSET = 0;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr;
        connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        try {
            networkInfo = connMgr.getActiveNetworkInfo();
        } catch (NullPointerException ex) {
            return false;
        }

        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * subtract days to date in java
     * @param date
     * @param days
     * @return
     */
    public static Date subtractDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    public static String formatStars(int stars) {
        Log.e("stars: ", String.valueOf(stars));
        if (stars >= 1000) {
            String result = String.valueOf(stars / 1000);
            int quotient = (stars % 1000) / 100;
            return  (quotient > 0) ? result.concat(".")
                            .concat(String.valueOf(quotient))
                            .concat("k") :
                            result.concat("k");

        } else
            return String.valueOf(stars);
    }
}
