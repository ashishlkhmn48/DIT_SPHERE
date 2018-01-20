package com.ashishlakhmani.dit_sphere.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.ashishlakhmani.dit_sphere.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;


public class NotificationBackground extends AsyncTask<String, Void, String> {

    private Context context;
    private ImageView imageView;

    private MessageObject messageObject;

    //To check if all Message objects sent or not.
    private static int count = 0;

    //Used for Network Monitor Offine-to-Online purpose.
    private boolean fromBackground = false;
    private int size;

    public NotificationBackground(Context context, ImageView imageView, MessageObject messageObject) {
        this.context = context;
        this.imageView = imageView;
        this.messageObject = messageObject;
    }

    public NotificationBackground(Context context, MessageObject messageObject, boolean fromBackground, int size) {
        this.context = context;
        this.messageObject = messageObject;
        this.fromBackground = fromBackground;
        this.size = size;
    }

    @Override
    protected String doInBackground(String... params) {

        String login_url = "https://lakhmanianita.000webhostapp.com/notification.php";
        try {
            String object_id = messageObject.getObject_id();
            String student_id = messageObject.getStudent_id();
            String message = messageObject.getMessage();
            String date = messageObject.getDate();
            String head = params[0];
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("object_id", "UTF-8") + "=" + URLEncoder.encode(object_id, "UTF-8") + "&" +
                    URLEncoder.encode("student_id", "UTF-8") + "=" + URLEncoder.encode(student_id, "UTF-8") + "&" +
                    URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8") + "&" +
                    URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&" +
                    URLEncoder.encode("head", "UTF-8") + "=" + URLEncoder.encode(head, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
            StringBuilder sb = new StringBuilder("");
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return sb.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        LocalChatDatabase chatDatabase = new LocalChatDatabase(context, messageObject.getObject_id());

        if (!fromBackground) {
            whileInAppTask(result, chatDatabase);
        } else {
            networkChangeTask(result, chatDatabase);
        }

    }

    private void networkChangeTask(String result, LocalChatDatabase chatDatabase) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("wait_table", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (result.equals("success")) {
            count++;
            messageObject.setSendStatus("success");
            String date = messageObject.getDate();
            chatDatabase.updateSendStatus(date, messageObject);
            context.sendBroadcast(new Intent("UPDATE_UI_ONLINE_OFFLINE"));

            if (count == size) {
                HashSet<String> set = new HashSet<>(sharedPreferences.getStringSet("set", new HashSet<String>()));
                set.remove(messageObject.getObject_id());
                editor.putStringSet("set", set);
                editor.apply();
                count = 0;
            }

            Log.i("Status", "Message Sent to all from background.");
        } else {
            HashSet<String> set = new HashSet<>(sharedPreferences.getStringSet("set", new HashSet<String>()));
            set.add(messageObject.getObject_id());
            editor.putStringSet("set", set);
            editor.apply();
        }
    }

    private void whileInAppTask(String result, LocalChatDatabase chatDatabase) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("wait_table", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (result.equals("success")) {
            messageObject.setSendStatus("success");
            String date = messageObject.getDate();
            chatDatabase.updateSendStatus(date, messageObject);
            imageView.setImageResource(R.drawable.success);
            context.sendBroadcast(new Intent("UPDATE_UI_ONLINE_OFFLINE"));
            Log.i("Status", "Message Sent to all.");
        } else {
            HashSet<String> set = new HashSet<>(sharedPreferences.getStringSet("set", new HashSet<String>()));
            set.add(messageObject.getObject_id());
            editor.putStringSet("set", set);
            editor.apply();
        }
    }


}
