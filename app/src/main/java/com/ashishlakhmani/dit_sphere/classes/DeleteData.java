package com.ashishlakhmani.dit_sphere.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.activities.LoginActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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

import static android.content.Context.MODE_PRIVATE;

public class DeleteData extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog progressDialog;

    public DeleteData(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context);

        progressDialog.setCancelable(false);
        progressDialog.setTitle("DIT - SPHERE");
        progressDialog.setMessage("Logging Out..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String call_url = "http://lakhmanianita.000webhostapp.com/dit_sphere/delete_data.php";
        try {
            String id = params[0];
            URL url = new URL(call_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("student_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
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

        if (!s.contains("not")) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        SharedPreferences sp = context.getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();

                        progressDialog.dismiss();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();

                    } else {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, "Internet Error.", Toast.LENGTH_SHORT).show();
        }

    }


}
