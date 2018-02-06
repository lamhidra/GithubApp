package com.hiddenfounders.githubapp.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

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
}
