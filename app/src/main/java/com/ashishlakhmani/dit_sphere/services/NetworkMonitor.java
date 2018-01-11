package com.ashishlakhmani.dit_sphere.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;
import com.ashishlakhmani.dit_sphere.classes.NotificationBackground;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NetworkMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        if (isNetworkAvailable(context)) {

            LocalChatDatabase chatDatabase = new LocalChatDatabase(context, null, null, 1);
            List<MessageObject> list;
            list = chatDatabase.getWaitMessageObjects();

            if (list != null) {
                if (list.size() > 0) {
                    final SharedPreferences sp = context.getSharedPreferences("login", MODE_PRIVATE);
                    for (final MessageObject messageObject : list) {

                        ParseObject parseObject = new ParseObject(sp.getString("branch", ""));
                        parseObject.put("student_id", messageObject.getStudent_id());
                        parseObject.put("message", messageObject.getMessage());
                        parseObject.put("date", messageObject.getDate());

                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    NotificationBackground background = new NotificationBackground(context, messageObject, true);
                                    background.execute(messageObject.getStudent_id(), messageObject.getMessage(), messageObject.getDate());
                                } else {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }
        }


    }

    //To check if the network is available i.e Mobile n/w or wifi
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
                return true;
        }
        return false;
    }
}
