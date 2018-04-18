package com.ashishlakhmani.dit_sphere.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.activities.FacultyActivity;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;

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

public class InsertToDatabase extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog dialog;

    private String id;
    private String password;
    private String branch;
    private String email;

    public InsertToDatabase(Context context, ProgressDialog dialog, String id, String password, String branch,String email) {
        this.context = context;
        this.dialog = dialog;
        this.id = id;
        this.password = password;
        this.branch = branch;
        this.email = email;
    }

    public InsertToDatabase(Context context, String id, String password, String branch) {
        this.context = context;
        this.id = id;
        this.password = password;
        this.branch = branch;
    }

    @Override
    protected String doInBackground(String... params) {

        String call_url = "http://lakhmanianita.000webhostapp.com/dit_sphere/insert_token.php";
        try {
            String id = params[0];
            String fcm_token = params[1];
            String date = params[2];
            URL url = new URL(call_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("fcm_token", "UTF-8") + "=" + URLEncoder.encode(fcm_token, "UTF-8") + "&" +
                    URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                    URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&" +
                    URLEncoder.encode("branch", "UTF-8") + "=" + URLEncoder.encode(branch, "UTF-8");
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
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        if (!sp.contains("id")) {
            if (result.equals("updated") || result.equals("inserted")) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("interact_activity", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isOpen", false);
                editor.apply();

                saveDetails();
                dialog.dismiss();
                try {
                    Long.parseLong(sp.getString("id",""));
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                }catch (NumberFormatException e){
                    Intent intent = new Intent(context, FacultyActivity.class);
                    context.startActivity(intent);
                }
                ((Activity) context).finish();
            } else {
                dialog.dismiss();
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Save login details
    private void saveDetails() {
        SharedPreferences sp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", id);
        editor.putString("password", password);
        editor.putString("branch", branch);
        editor.putString("email", email);
        editor.apply();
    }

}

