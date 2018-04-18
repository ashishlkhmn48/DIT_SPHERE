package com.ashishlakhmani.dit_sphere.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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


public class NotificationClub extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog progressDialog;

    public NotificationClub(Context context, ProgressDialog progressDialog) {
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    protected String doInBackground(String... params) {
        String login_url = "https://lakhmanianita.000webhostapp.com/dit_sphere/notification_club.php";
        try {
            String objectId = params[0];
            String clubName = params[1];
            String clubId = params[2];
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("object_id", "UTF-8") + "=" + URLEncoder.encode(objectId, "UTF-8") + "&" +
                    URLEncoder.encode("club_name", "UTF-8") + "=" + URLEncoder.encode(clubName, "UTF-8") + "&" +
                    URLEncoder.encode("club_id", "UTF-8") + "=" + URLEncoder.encode(clubId, "UTF-8");
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
        progressDialog.dismiss();
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}
