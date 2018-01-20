package com.ashishlakhmani.dit_sphere.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.ChatActivity;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;
import com.ashishlakhmani.dit_sphere.classes.WordsFilter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;


public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        SharedPreferences sharedPreferences = getSharedPreferences("interact_activity", MODE_PRIVATE);

        String object_id = remoteMessage.getData().get("object_id");
        String id = remoteMessage.getData().get("student_id");
        String message = remoteMessage.getData().get("message");
        String date = remoteMessage.getData().get("date");
        String head = remoteMessage.getData().get("head");
        String heading = remoteMessage.getData().get("heading");

        Random random = new Random();
        int num = random.nextInt(999999999);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        notification.setWhen(System.currentTimeMillis());
        notification.setDefaults(NotificationCompat.DEFAULT_ALL);

        if (heading != null) {

            notifyForNews(notification, num, heading);

        } else {

            try {
                WordsFilter wordsFilter = new WordsFilter();
                String output = wordsFilter.filteredString(message, new InputStreamReader(getAssets().open("bad.txt")));

                Log.i("Exception", output);

                LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);
                MessageObject messageObject = new MessageObject(object_id, id, output, date, "");
                chatDatabase.addUserDetails(messageObject);

                Log.i("Exception", "Read Success");

                if (!sharedPreferences.getBoolean("isOpen", false)) {
                    notifyForMessageBackground(notification, num, id, output, object_id, head);
                } else {
                    notifyForMessageForeground();
                }
            } catch (IOException e) {
                Log.i("Exception", e.getMessage());
            }
        }

    }


    private void notifyForNews(NotificationCompat.Builder notification, int num, String heading) {
        notification.setSmallIcon(R.drawable.sphere_2);
        notification.setContentTitle("DIT - News");
        notification.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle(notification).bigText(heading.trim()));

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(num, notification.build());
        }

    }


    private void notifyForMessageBackground(NotificationCompat.Builder notification, int num, String id, String message, String object_id, String head) {

        notification.setSmallIcon(R.drawable.interact_2);
        notification.setSubText(head);
        notification.setContentTitle(id);
        notification.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle(notification).bigText(message));

        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object_id", object_id);
        intent.putExtra("heading", head);
        PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm != null) {
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
