package com.ashishlakhmani.dit_sphere.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {


    }

    //To check if the network is available i.e Mobile n/w or wifi
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (cm.isDefaultNetworkActive() && activeNetwork != null && activeNetwork.isConnected())
                return true;
        }
        return false;
    }
}
