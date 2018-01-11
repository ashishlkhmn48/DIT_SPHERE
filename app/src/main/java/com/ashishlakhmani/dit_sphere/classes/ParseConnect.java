package com.ashishlakhmani.dit_sphere.classes;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;

public class ParseConnect extends Application {
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
    }
}