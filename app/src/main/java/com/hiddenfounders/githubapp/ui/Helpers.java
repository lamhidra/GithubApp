package com.hiddenfounders.githubapp.ui;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helpers {

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
}
