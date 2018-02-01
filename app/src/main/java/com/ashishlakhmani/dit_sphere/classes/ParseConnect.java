package com.ashishlakhmani.dit_sphere.classes;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;

import java.util.HashSet;
import java.util.List;

public class ParseConnect extends Application {

    private BroadcastReceiver off_to_on;

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("t6lE08nCzEERjCE5SrLAnP73LahAWaYapbDjslZ0")
                .clientKey("CDhAcnlkdmGj7HmvZZdTZNPz3ltma4VGPKTgxFvx")
                .server("https://parseapi.back4app.com/")
                .build()
        );

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "746201752368");
        installation.saveInBackground();

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        off_to_on = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                offToOnTask();
            }
        };

        registerReceiver(off_to_on, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void offToOnTask() {

        if (isNetworkAvailable()) {

            SharedPreferences sharedPreferences = getSharedPreferences("wait_table", Context.MODE_PRIVATE);
            HashSet<String> set = new HashSet<>(sharedPreferences.getStringSet("set", new HashSet<String>()));

            for (String object_id : set) {
                LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);
                List<MessageObject> messageObjectList = chatDatabase.getWaitMessageObjects();

                for (MessageObject messageObject : messageObjectList) {
                    NotificationBackground background = new NotificationBackground(this, messageObject, true);
                    background.execute(messageObject.getHeading());
                }
            }
        }

    }

    //To check if the network is available i.e Mobile n/w or wifi
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (cm.isDefaultNetworkActive() && activeNetwork != null && activeNetwork.isConnected())
                return true;
        }
        return false;
    }


}