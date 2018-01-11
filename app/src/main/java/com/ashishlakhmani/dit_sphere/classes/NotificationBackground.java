package com.ashishlakhmani.dit_sphere.classes;

import android.content.Context;
import android.content.Intent;
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


public class NotificationBackground extends AsyncTask<String, Void, String> {

    private Context context;
    private ImageView imageView;

    private MessageObject messageObject;
    private boolean fromBackground = false;

    public NotificationBackground(Context context, ImageView imageView, MessageObject messageObject) {
        this.context = context;
        this.imageView = imageView;
        this.messageObject = messageObject;
    }

    public NotificationBackground(Context context, MessageObject messageObject, boolean fromBackground) {
        this.context = context;
        this.messageObject = messageObject;
        this.fromBackground = fromBackground;
    }

    @Override
    protected String doInBackground(String... params) {

        String login_url = "https://lakhmanianita.000webhostapp.com/notification.php";
        try {
            String student_id = params[0];
            String message = params[1];
            String date = params[2];
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("student_id", "UTF-8") + "=" + URLEncoder.encode(student_id, "UTF-8") + "&" +
                    URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8") + "&" +
                    URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");
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
    protected void onPostExecute(String s) {
        LocalChatDatabase chatDatabase = new LocalChatDatabase(context, null, null, 1);
        if (!fromBackground) {
            if (s.equals("success")) {
                messageObject.setSendStatus("success");
                String date = messageObject.getDate();
                chatDatabase.updateSendStatus(date);
                imageView.setImageResource(R.drawable.success);
                Log.i("Status", "Message Sent to all.");
            }
        } else {
            messageObject.setSendStatus("success");
            String date = messageObject.getDate();
            chatDatabase.updateSendStatus(date);
            context.sendBroadcast(new Intent("UPDATE_UI_ONLINE_OFFLINE"));
            Log.i("Status", "Message Sent to all from background.");
        }
    }
}
