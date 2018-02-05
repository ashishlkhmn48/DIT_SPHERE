package com.ashishlakhmani.dit_sphere.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.ChatActivity;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;
import com.ashishlakhmani.dit_sphere.classes.NotificationBackground;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<MessageObject> messageObjectList;

    public ChatAdapter(Context context, ArrayList<MessageObject> messageObjectList) {
        this.context = context;
        this.messageObjectList = messageObjectList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_bubble, parent, false);
        return new ChatAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final MessageObject messageObject = messageObjectList.get(position);
        final String student_id = messageObject.getStudent_id();
        String message = messageObject.getMessage();
        final String date = messageObject.getDate();
        String sendStatus = messageObject.getSendStatus();

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ((MyViewHolder) holder).bubble_layout.getLayoutParams();

        if (sendStatus.isEmpty()) {
            layoutParams.gravity = Gravity.LEFT;
            ((MyViewHolder) holder).bubble_layout.setLayoutParams(layoutParams);
            ((MyViewHolder) holder).bubble_layout.setBackgroundResource(R.drawable.chat_style_left);
            ((MyViewHolder) holder).id.setText(student_id);
            ((MyViewHolder) holder).statusImage.setImageDrawable(null);
        } else {

            layoutParams.gravity = Gravity.RIGHT;
            ((MyViewHolder) holder).bubble_layout.setLayoutParams(layoutParams);
            ((MyViewHolder) holder).bubble_layout.setBackgroundResource(R.drawable.chat_style_right);
            ((MyViewHolder) holder).id.setText("You");

            if (sendStatus.equals("wait")) {
                ((MyViewHolder) holder).statusImage.setImageResource(R.drawable.wait);
                NotificationBackground background = new NotificationBackground(context, ((MyViewHolder) holder).statusImage, messageObject);
                background.execute(((ChatActivity)context).getHeading());
            } else {
                ((MyViewHolder) holder).statusImage.setImageResource(R.drawable.success);
            }

        }

        ((MyViewHolder) holder).message.setText(message);
        ((MyViewHolder) holder).date.setText(getDateFormat(date));
        ((MyViewHolder) holder).time.setText(getTimeFormat(date));

        ((MyViewHolder) holder).id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Profile");
                progressDialog.setMessage("Loading Profile..");
                progressDialog.show();

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");
                query.whereEqualTo("student_id", student_id);
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
        });
    }

    @Override
    public int getItemCount() {
        return messageObjectList.size();
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout bubble_layout;
        FrameLayout bubble_layout_parent;
        TextView message, id, date, time;
        ImageView statusImage;

        private MyViewHolder(View itemView) {
            super(itemView);
            bubble_layout = itemView.findViewById(R.id.bubble_layout);
            bubble_layout_parent = itemView.findViewById(R.id.bubble_layout_parent);
            message = itemView.findViewById(R.id.message_text);
            id = itemView.findViewById(R.id.id_text);
            date = itemView.findViewById(R.id.id);
            time = itemView.findViewById(R.id.time);
            statusImage = itemView.findViewById(R.id.status_image);
        }
    }

    private String getDateFormat(String date) {

        Date d = new Date();
        try {
            d = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT+05:30' yyyy", Locale.UK).parse(date);
            String myFormat = "dd MMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
            return sdf.format(d);
        } catch (java.text.ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return d.toString();
    }

    private String getTimeFormat(String date) {

        Date d = new Date();
        try {
            d = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT+05:30' yyyy", Locale.UK).parse(date);
            String myFormat = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
            return sdf.format(d);
        } catch (java.text.ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return d.toString();
    }


}