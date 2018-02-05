package com.ashishlakhmani.dit_sphere.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.ashishlakhmani.dit_sphere.R;

import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String objectId = intent.getStringExtra("objectId");
        String activityName = intent.getStringExtra("activity_name");
        String heading = intent.getStringExtra("heading");
        String title = intent.getStringExtra("title");
        createNotification(context, heading, title, objectId, activityName);
    }

    private void createNotification(final Context context, String heading, String title, String objectId, String activityName) {

        Random random = new Random();
        int num = random.nextInt(999999999);

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.notification);
        notification.setWhen(System.currentTimeMillis());
        notification.setSubText(title);
        notification.setContentTitle(title);
        notification.setStyle(new NotificationCompat.BigTextStyle(notification).bigText(heading));
        notification.setDefaults(NotificationCompat.DEFAULT_ALL);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.ashishlakhmani.dit_sphere", activityName);
        //Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(num, notification.build());
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(objectId);
        editor.apply();

    }


}

