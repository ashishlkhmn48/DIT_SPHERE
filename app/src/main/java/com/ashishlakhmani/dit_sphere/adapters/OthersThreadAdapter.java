package com.ashishlakhmani.dit_sphere.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class OthersThreadAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ParseObject> objectList;
    ConstraintLayout no_others;

    public OthersThreadAdapter(Context context, List<ParseObject> objectList, ConstraintLayout no_others) {
        this.context = context;
        this.objectList = objectList;
        this.no_others = no_others;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_others_thread, parent, false);
        return new OthersThreadAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String heading = objectList.get(position).getString("heading");
        final String id = objectList.get(position).getString("from_id");
        Date d = objectList.get(position).getDate("date");
        String date = getDateFormat(d);

        ((MyViewHolder) holder).heading.setText(heading);
        ((MyViewHolder) holder).id.setText(id);
        ((MyViewHolder) holder).date.setText(date);

        ((MyViewHolder) holder).id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long student_id = Long.parseLong(((OthersThreadAdapter.MyViewHolder) holder).id.getText().toString());
                    fetchStudentDetails(id);
                } catch (NumberFormatException e) {
                    fetchFacultyDetails(id);
                }
            }
        });


        ((MyViewHolder) holder).join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.notification);
                builder.setCancelable(false);
                builder.setTitle("Join Conversation.");
                builder.setMessage("You will get the Notifications.\n\nDo you still want to Continue ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        joinTask(objectList.get(position));
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }


    @Override
    public int getItemCount() {
        return objectList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView heading, id, date;
        ImageView join;

        private MyViewHolder(View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.heading);
            id = itemView.findViewById(R.id.id);
            date = itemView.findViewById(R.id.date);
            join = itemView.findViewById(R.id.join);
        }
    }

    //To join in a group.
    private void joinTask(final ParseObject object) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Join Conversation");
        progressDialog.setMessage("Please Wait..\nAdding to Conversation..");
        progressDialog.show();

        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        object.addUnique("connected_id", sharedPreferences.getString("id", ""));
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    objectList.remove(object);
                    notifyDataSetChanged();

                    Intent intent = new Intent("UPDATE_FOLLOWED");
                    context.sendBroadcast(intent);

                    if (objectList.isEmpty()) {
                        no_others.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getDateFormat(Date date) {
        String myFormat = "dd MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        return sdf.format(date);
    }

    private void fetchStudentDetails(String id) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Loading Profile..");
        progressDialog.show();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");
        query.whereEqualTo("student_id", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    final String id = object.getString("student_id");
                    String name = object.getString("name").toUpperCase();
                    String branch = object.getString("branch").toUpperCase();
                    String year = object.getString("year");

                    if (year.equals("1")) {
                        year += "st Year";
                    } else if (year.equals("2")) {
                        year += "nd Year";
                    } else if (year.equals("3")) {
                        year += "rd Year";
                    } else {
                        year += "th Year";
                    }

                    final View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_profile_alert, null);
                    TextView id_tv, name_tv, branch_tv, year_tv;
                    final ImageView picture;

                    id_tv = view.findViewById(R.id.id);
                    name_tv = view.findViewById(R.id.name);
                    branch_tv = view.findViewById(R.id.branch);
                    year_tv = view.findViewById(R.id.year);
                    picture = view.findViewById(R.id.picture);

                    id_tv.setText(id);
                    name_tv.setText(name);
                    branch_tv.setText(branch);
                    year_tv.setText(year);

                    final ParseFile file = object.getParseFile("picture");
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    picture.setImageBitmap(img);

                                    progressDialog.dismiss();
                                    Dialog alertDialog = new Dialog(context);
                                    alertDialog.setCancelable(true);
                                    alertDialog.setContentView(view);
                                    alertDialog.show();
                                    Window window = alertDialog.getWindow();
                                    window.setLayout(1000, 1100);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        progressDialog.dismiss();
                        Dialog alertDialog = new Dialog(context);
                        alertDialog.setCancelable(true);
                        alertDialog.setContentView(view);
                        alertDialog.show();
                        Window window = alertDialog.getWindow();
                        window.setLayout(1000, 1100);
                    }
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchFacultyDetails(String id) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Loading Profile..");
        progressDialog.show();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Faculty");
        query.whereEqualTo("email_id", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    final String id = object.getString("email_id");
                    String name = object.getString("name").toUpperCase();
                    String branch = object.getString("branch").toUpperCase();
                    String location = object.getString("building").toUpperCase() + ", " + object.getString("floor") + " Floor, " +
                            object.getString("cabin");


                    final View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_profile_alert_faculty, null);
                    TextView id_tv, name_tv, branch_tv, loc_tv;
                    final ImageView picture;

                    id_tv = view.findViewById(R.id.id);
                    name_tv = view.findViewById(R.id.name);
                    branch_tv = view.findViewById(R.id.branch);
                    loc_tv = view.findViewById(R.id.location);
                    picture = view.findViewById(R.id.picture);

                    id_tv.setText(id);
                    name_tv.setText(name);
                    branch_tv.setText(branch);
                    loc_tv.setText(location);

                    final ParseFile file = object.getParseFile("picture");
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    picture.setImageBitmap(img);

                                    progressDialog.dismiss();
                                    Dialog alertDialog = new Dialog(context);
                                    alertDialog.setCancelable(true);
                                    alertDialog.setContentView(view);
                                    alertDialog.show();
                                    Window window = alertDialog.getWindow();
                                    window.setLayout(1000, 1100);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        progressDialog.dismiss();
                        Dialog alertDialog = new Dialog(context);
                        alertDialog.setCancelable(true);
                        alertDialog.setContentView(view);
                        alertDialog.show();
                        Window window = alertDialog.getWindow();
                        window.setLayout(1000, 1100);
                    }
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
