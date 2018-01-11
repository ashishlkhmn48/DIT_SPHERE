package com.ashishlakhmani.dit_sphere.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;

public class DownloaderHelper {

    private Context context;
    private ParseObject object;
    private String fileKey, nameKey;

    public DownloaderHelper(Context context, ParseObject object, String fileKey, String nameKey) {
        this.context = context;
        this.object = object;
        this.fileKey = fileKey;
        this.nameKey = nameKey;
    }


    //To do the downloading.
    public void downloadTaskImage() {
        HomeActivity homeActivity = (HomeActivity) context;
        final ProgressDialog dialog = new ProgressDialog(context);

        if (homeActivity.checkAndRequestPermissions()) {
            dialog.setCancelable(false);
            dialog.setTitle("Downloads");
            dialog.setMessage("Please wait..");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();

            ParseFile file = (ParseFile) object.get(fileKey);
            if (file != null) {
                dialog.setMessage("Downloading File..");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            try {
                                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DIT-SPHERE");
                                folder.mkdir();
                                File myFile = new File(folder, object.getString(nameKey) + ".jpg");
                                if (!myFile.exists()) {
                                    FileOutputStream out = new FileOutputStream(myFile);
                                    out.write(data, 0, data.length);
                                    out.flush();
                                    out.close();
                                    dialog.dismiss();
                                    alertDialogTask("Status", "Downloaded at " + myFile.getAbsolutePath());
                                } else {
                                    dialog.dismiss();
                                    alertDialogTask("Status", "File Already Exists at " + myFile.getAbsolutePath());
                                }
                            } catch (Exception e1) {
                                Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        } else {
                            alertDialogTask("Status", e.getMessage());
                            dialog.dismiss();
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        dialog.setProgress(percentDone);
                    }
                });
            }

        } else {
            Toast.makeText(context, "Please Provide Permissions.", Toast.LENGTH_LONG).show();
        }
    }


    private void alertDialogTask(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
