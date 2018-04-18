package com.ashishlakhmani.dit_sphere.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.ChatActivity;
import com.ashishlakhmani.dit_sphere.activities.ClubActivity;
import com.ashishlakhmani.dit_sphere.activities.FacultyNotificationActivity;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.activities.PlacementCellActivity;
import com.ashishlakhmani.dit_sphere.activities.UpcomingEventsActivity;
import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;
import com.ashishlakhmani.dit_sphere.classes.WordsFilter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;


public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i("ashish", "Received");

        SharedPreferences sharedPreferences = getSharedPreferences("interact_activity", MODE_PRIVATE);
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String type = remoteMessage.getData().get("type");
        Random random = new Random();
        int num = random.nextInt(999999999);

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(getString(R.string.channel_id), getString(R.string.channel_id), NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.channel_id));
        notification.setAutoCancel(true);
        notification.setWhen(System.currentTimeMillis());
        notification.setDefaults(Notification.DEFAULT_ALL);

        try {
            long l = Long.parseLong(sp.getString("id", ""));

            if (type.equals("message")) {

                String object_id = remoteMessage.getData().get("object_id");
                String id = remoteMessage.getData().get("student_id");
                String message = remoteMessage.getData().get("message");
                String heading_message = remoteMessage.getData().get("heading_message");
                String date = remoteMessage.getData().get("date");


                try {
                    WordsFilter wordsFilter = new WordsFilter();
                    String output = wordsFilter.filteredString(message, new InputStreamReader(getAssets().open("bad.txt")));

                    Log.i("Exception", output);

                    LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);
                    MessageObject messageObject = new MessageObject(object_id, id, output, date, heading_message, "");
                    chatDatabase.addUserDetails(messageObject);

                    if (!sharedPreferences.getBoolean("isOpen", false)) {
                        notifyForMessageBackground(notification, num, remoteMessage, channel);
                    } else {
                        notifyForMessageForeground();
                    }
                } catch (IOException exp) {
                    Log.i("Exception", exp.getMessage());
                }

            } else if (type.equals("news")) {

                notifyForNews(notification, num, remoteMessage, channel);

            } else if (type.equals("events")) {

                notifyForEvents(notification, num, remoteMessage, channel);

            } else if (type.equals("club")) {

                notifyForClub(notification, num, remoteMessage, channel);

            } else if (type.equals("placement")) {

                notifyForPlacement(notification, num, remoteMessage, channel);

            } else if (type.equals("faculty")) {

                notifyForFaculty(notification, num, remoteMessage, channel);

            }

        } catch (NumberFormatException e) {

            Log.i("ashish",e.getMessage());

            if (type.equals("message")) {

                String object_id = remoteMessage.getData().get("object_id");
                String id = remoteMessage.getData().get("student_id");
                String message = remoteMessage.getData().get("message");
                String heading_message = remoteMessage.getData().get("heading_message");
                String date = remoteMessage.getData().get("date");


                try {
                    WordsFilter wordsFilter = new WordsFilter();
                    String output = wordsFilter.filteredString(message, new InputStreamReader(getAssets().open("bad.txt")));


                    LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);
                    MessageObject messageObject = new MessageObject(object_id, id, output, date, heading_message, "");
                    chatDatabase.addUserDetails(messageObject);


                    if (!sharedPreferences.getBoolean("isOpen", false)) {
                        notifyForMessageBackground(notification, num, remoteMessage, channel);
                    } else {
                        notifyForMessageForeground();
                    }
                } catch (IOException ex) {
                    Log.i("Exception", ex.getMessage());
                }

            }

        }
    }

    private void notifyForFaculty(NotificationCompat.Builder notification, int num, RemoteMessage remoteMessage, NotificationChannel channel) {

        String heading = remoteMessage.getData().get("heading");
        String message = remoteMessage.getData().get("message");

        notification.setSmallIcon(R.drawable.faculty);
        notification.setSubText("Faculty Notifications");
        notification.setContentTitle(heading);
        notification.setStyle(new NotificationCompat.BigTextStyle(notification).bigText(message));

        Intent intent = new Intent(this, FacultyNotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(channel);
            }
            nm.notify(num, notification.build());
        }

    }


    private void notifyForPlacement(NotificationCompat.Builder notification, int num, RemoteMessage remoteMessage, NotificationChannel channel) {
        String companyName = remoteMessage.getData().get("company_name");
        String dateText = "Coming on : " + remoteMessage.getData().get("date");

        notification.setSmallIcon(R.drawable.placement_cell);
        notification.setContentTitle(companyName);
        notification.setContentText(dateText);
        notification.setSubText("Placement Cell");

        Intent intent = new Intent(this, PlacementCellActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(channel);
            }
            nm.notify(num, notification.build());
        }

    }

    private void notifyForClub(NotificationCompat.Builder notification, int num, RemoteMessage remoteMessage, NotificationChannel channel) {

        String clubName = remoteMessage.getData().get("club_name");
        String heading = remoteMessage.getData().get("heading");
        String message = remoteMessage.getData().get("message");
        String imageUrl = remoteMessage.getData().get("image_url");
        String clubId = remoteMessage.getData().get("club_id");

        Bitmap largeIcon;
        try {
            largeIcon = Picasso.with(FcmMessagingService.this).load(imageUrl).get();
        } catch (Exception e) {
            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.dit);
        }

        notification.setSmallIcon(R.drawable.interact_3);
        notification.setContentTitle(heading);
        notification.setSubText(clubName);
        notification.setContentText(message);
        notification.setStyle(new NotificationCompat.BigPictureStyle(notification).bigPicture(largeIcon));

        Intent intent = new Intent(this, ClubActivity.class);
        intent.putExtra("objectId", clubId);
        intent.putExtra("club_name", clubName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(channel);
            }
            nm.notify(num, notification.build());
        }

    }


    private void notifyForNews(NotificationCompat.Builder notification, int num, RemoteMessage remoteMessage, NotificationChannel channel) {

        String heading_news = remoteMessage.getData().get("heading_news");

        notification.setSmallIcon(R.drawable.news);
        notification.setContentTitle("DIT - News");
        notification.setSubText("News");
        notification.setStyle(new NotificationCompat.BigTextStyle(notification).bigText(heading_news.trim()));

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(channel);
            }
            nm.notify(num, notification.build());
        }

    }

    private void notifyForEvents(NotificationCompat.Builder notification, int num, RemoteMessage remoteMessage, NotificationChannel channel) {

        String heading_event = remoteMessage.getData().get("heading_event");

        notification.setSmallIcon(R.drawable.upcoming);
        notification.setContentTitle("Upcoming Events");
        notification.setSubText("Events");
        notification.setStyle(new NotificationCompat.BigTextStyle(notification).bigText(heading_event.trim()));

        Intent intent = new Intent(this, UpcomingEventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(channel);
            }
            nm.notify(num, notification.build());
        }

    }


    private void notifyForMessageBackground(NotificationCompat.Builder notification, int num, RemoteMessage remoteMessage, NotificationChannel channel) {

        String object_id = remoteMessage.getData().get("object_id");
        String id = remoteMessage.getData().get("student_id");
        String message = remoteMessage.getData().get("message");
        String heading_message = remoteMessage.getData().get("heading_message");

        notification.setSmallIcon(R.drawable.interact_2);
        notification.setSubText(heading_message);
        notification.setContentTitle(id);
        notification.setStyle(new NotificationCompat.BigTextStyle(notification).bigText(message));

        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object_id", object_id);
        intent.putExtra("heading", heading_message);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nm.createNotificationChannel(channel);
            }
            nm.notify(num, notification.build());
        }

    }

    private void notifyForMessageForeground() {

        try {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                }
            }
        } catch (Exception e) {
            Log.i("Ringtone_Exception", e.getMessage());
        }
        Intent i = new Intent("UPDATE_UI");
        sendBroadcast(i);

    }

}
