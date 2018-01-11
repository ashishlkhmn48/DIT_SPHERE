package com.ashishlakhmani.dit_sphere.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;

public class SyllabusDownloadAdapter extends RecyclerView.Adapter {

    private Context context;
    private ProgressDialog dialog;
    private String[] branch = {"C.S.E", "Mechanical", "Civil", "E.C.E", "I.T", "Electrical", "Petroleum"};

    public SyllabusDownloadAdapter(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_syllabus_download, parent, false);
        return new SyllabusDownloadAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((MyViewHolder) holder).heading.setText(branch[position]);

        ((MyViewHolder) holder).download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTask(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return branch.length;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        // initialize the item view's

        TextView heading;
        Button download;

        private MyViewHolder(View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.name);
            download = itemView.findViewById(R.id.download);
        }
    }


    //To do the downloading.
    private void downloadTask(final int position) {

        dialog.setCancelable(false);
        dialog.setTitle("Downloads");
        dialog.setMessage("Please wait..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Syllabus");
        query.whereEqualTo("branch", branch[position]);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if (e == null) {
                    dialog.dismiss();
                    downloadPdf(object, "file", "branch");
                } else {
                    dialog.dismiss();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void downloadPdf(final ParseObject object, final String nameKey, final String fileKey) {
        HomeActivity homeActivity = (HomeActivity) context;
        final ProgressDialog dialog = new ProgressDialog(context);

        if (homeActivity.checkAndRequestPermissions()) {

            ParseFile file = object.getParseFile(fileKey);
            if (file != null) {
                dialog.setMessage("Downloading File..");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            try {
                                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DIT-SPHERE");
                                folder.mkdir();
                                File myFile = new File(folder, object.getString(nameKey) + ".pdf");
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
            } else {
                dialog.dismiss();
                alertDialogTask("Status", "File Not Available.");
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
