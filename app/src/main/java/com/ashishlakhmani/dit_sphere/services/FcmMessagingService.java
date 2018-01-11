package com.ashishlakhmani.dit_sphere.services;

import android.app.ActivityManager;
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
import com.ashishlakhmani.dit_sphere.activities.InteractActivity;
import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Random;


public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        SharedPreferences sharedPreferences = getSharedPreferences("interact_activity", MODE_PRIVATE);

        String id = remoteMessage.getData().get("student_id");
        String message = remoteMessage.getData().get("message");
        String date = remoteMessage.getData().get("date");

        LocalChatDatabase chatDatabase = new LocalChatDatabase(this, null, null, 1);
        MessageObject messageObject = new MessageObject(id, message, date, "");
        chatDatabase.addUserDetails(messageObject);

        if (!sharedPreferences.getBoolean("isOpen", false)) {
            Random random = new Random();
            int num = random.nextInt(999999999);

            if (id != null && message != null) {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
                notification.setAutoCancel(true);
                notification.setSmallIcon(R.drawable.interact_2);
                notification.setDefaults(NotificationCompat.DEFAULT_ALL);
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle(id);
                notification.setContentText(message);

                Intent intent = new Intent(this, InteractActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pi = PendingIntent.getActivity(this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pi);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (nm != null) {
                    nm.notify(num, notification.build());
                }
            }
        } else {
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


    private boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }

        return isInBackground;
    }

}
