package com.ashishlakhmani.dit_sphere.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.PlacementViewActivity;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiverPlacement extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String company_name = intent.getStringExtra("company_name");
        String date = intent.getStringExtra("club_name");
        String objectId = intent.getStringExtra("objectId");
        String imageUrl = intent.getStringExtra("image_url");

        createNotification(context, company_name, date, objectId, imageUrl);
    }

    private void createNotification(final Context context, String company_name, String date, String objectId, String imageUrl) {

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

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(context.getString(R.string.channel_id), context.getString(R.string.channel_id), NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context,context.getString(R.string.channel_id));
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.notification);
        notification.setWhen(System.currentTimeMillis());
        notification.setSubText("Placement Cell");
        notification.setContentTitle(date);
        notification.setContentText(company_name);
        notification.setStyle(new NotificationCompat.BigPictureStyle(notification).bigPicture(largeIcon));
        notification.setDefaults(NotificationCompat.DEFAULT_ALL);

        Intent intent = new Intent(context, PlacementViewActivity.class);
        intent.putExtra("objectId", objectId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(channel);
            }
            nm.notify(num, notification.build());
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(objectId);
        editor.apply();
    }

}
