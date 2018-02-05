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
                .applicationId("OCbIsKw6KBpwaiL6HsG7AwY2ZJC8AHh4TwhO9x1V")
                .clientKey("OrMlI12f6HDJMx7mzckHCugt9rqpzJ7E0GV4FY5u")
                .server("https://parseapi.back4app.com/")
                .build()
        );

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        off_to_on = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = getSharedPreferences("interact_activity", MODE_PRIVATE);

                if(sharedPreferences.getBoolean("isOpen", true)){
                    Intent i = new Intent("UPDATE_UI");
                    sendBroadcast(i);
                }else {
                    offToOnTask();
                }
            }
        };

        registerReceiver(off_to_on, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void offToOnTask() {

        if (isNetworkAvailable()) {

            SharedPreferences sharedPreferences = getSharedPreferences("wait_table", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            HashSet<String> set = new HashSet<>(sharedPreferences.getStringSet("set", new HashSet<String>()));

            for (String object_id : set) {
                LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);
                List<MessageObject> messageObjectList = chatDatabase.getWaitMessageObjects();

                for (MessageObject messageObject : messageObjectList) {
                    NotificationBackground background = new NotificationBackground(this, messageObject, true);
                    background.execute(messageObject.getHeading());
                }
            }
            editor.putStringSet("set", new HashSet<String>());
            editor.apply();
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