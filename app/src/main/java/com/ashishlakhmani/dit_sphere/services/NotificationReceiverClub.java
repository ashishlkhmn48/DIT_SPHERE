package com.ashishlakhmani.dit_sphere.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.ClubNotificationActivity;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiverClub extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String heading = intent.getStringExtra("heading");
        String clubName = intent.getStringExtra("club_name");
        String objectId = intent.getStringExtra("objectId");
        String imageUrl = intent.getStringExtra("image_url");

        createNotification(context, heading, clubName, objectId, imageUrl);
    }

    private void createNotification(final Context context, String heading, String clubName, String objectId, String imageUrl) {

        Random random = new Random();
        int num = random.nextInt(999999999);

        Bitmap largeIcon;

        try {
            URL url = new URL(imageUrl);
            largeIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            Log.i("parseimage", e.getMessage());
            largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.dit);
        }


        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.notification);
        notification.setWhen(System.currentTimeMillis());
        notification.setSubText("Club");
        notification.setContentTitle(clubName);
        notification.setContentText(heading);
        notification.setStyle(new NotificationCompat.BigPictureStyle(notification).bigPicture(largeIcon));
        notification.setDefaults(NotificationCompat.DEFAULT_ALL);

        Intent intent = new Intent(context, ClubNotificationActivity.class);
        intent.putExtra("objectId", objectId);
        intent.putExtra("club_name", clubName);
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
