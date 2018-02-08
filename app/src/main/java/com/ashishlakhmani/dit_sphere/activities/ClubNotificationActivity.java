package com.ashishlakhmani.dit_sphere.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.services.NotificationReceiverClub;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ClubNotificationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    CoordinatorLayout layout;
    ConstraintLayout no_events;
    Toolbar toolbar;
    TextView date, message;
    ImageView imageView;
    FloatingActionButton floating_notify;

    private Calendar myCalendar = Calendar.getInstance();

    private String objectId;
    private String clubName;
    private String headingText;
    private String imageUrl;

    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_notification);

        initialize();

        SharedPreferences sp = getSharedPreferences("notification", Context.MODE_PRIVATE);
        if (sp.contains(objectId)) {
            floating_notify.setImageResource(R.drawable.notify_on);
            isChecked = true;
        } else {
            floating_notify.setImageResource(R.drawable.notify_off);
            isChecked = false;
        }

        loadDetails();
        floatingActionButtonTask();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initialize() {
        objectId = getIntent().getStringExtra("objectId");
        clubName = getIntent().getStringExtra("club_name");
        progressBar = findViewById(R.id.progressBar);
        layout = findViewById(R.id.layout);
        no_events = findViewById(R.id.no_events);
        toolbar = findViewById(R.id.toolbar);
        date = findViewById(R.id.date);
        message = findViewById(R.id.message);
        imageView = findViewById(R.id.image);

        floating_notify = findViewById(R.id.floating_notify);
    }

    private void toolbarTask(String title) {
        setTitle(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadDetails() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ClubNotification");
        query.whereEqualTo("club_id", objectId);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    headingText = object.getString("heading");

                    message.setText(object.getString("message"));
                    date.setText(getDateString(object.getDate("date")));
                    toolbarTask(headingText);
                    layout.setVisibility(View.VISIBLE);

                    ParseFile file = object.getParseFile("image");
                    if (file != null) {
                        imageUrl = file.getUrl();
                        Picasso.with(ClubNotificationActivity.this)
                                .load(file.getUrl())
                                .placeholder(R.drawable.placeholder_album)
                                .into(imageView);
                    } else {
                        layout.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.dit);
                    }
                } else {
                    no_events.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    Toast.makeText(ClubNotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void floatingActionButtonTask() {
        floating_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("notification", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked) {
                    editor.remove(objectId);
                    editor.apply();
                    isChecked = false;
                    floating_notify.setImageResource(R.drawable.notify_off);
                    Toast.makeText(ClubNotificationActivity.this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    isChecked = true;
                    floating_notify.setImageResource(R.drawable.notify_on);
                    dateTimeTask();
                }
            }
        });
    }

    private String getDateString(Date date) {
        String myFormat = "dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        return sdf.format(date);
    }

    private void dateTimeTask() {

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(ClubNotificationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(year, month, dayOfMonth, selectedHour, selectedMinute);
                        String myFormat = "dd MMMM yyyy : HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                        Toast.makeText(ClubNotificationActivity.this, "You will be Notified on : " + sdf.format(myCalendar.getTime()), Toast.LENGTH_LONG).show();
                        createAlarm();
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.setCancelable(false);

                mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        isChecked = false;
                        floating_notify.setImageResource(R.drawable.notify_off);
                    }
                });
                mTimePicker.show();
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.setCancelable(false);
        dialog.setTitle("Set Date");
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isChecked = false;
                floating_notify.setImageResource(R.drawable.notify_off);
            }
        });
        dialog.show();

    }

    private void createAlarm() {

        Random random = new Random();
        int num = random.nextInt(999999999);

        long alarm = myCalendar.getTimeInMillis();
        Intent intent = new Intent(this, NotificationReceiverClub.class);
        intent.putExtra("heading", headingText.trim());
        intent.putExtra("club_name", clubName);
        intent.putExtra("image_url", imageUrl);
        intent.putExtra("objectId", objectId);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, num, intent, 0);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager1 != null) {
            alarmManager1.set(AlarmManager.RTC_WAKEUP, alarm, pendingIntent1);
        }

        SharedPreferences sp = getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(objectId, true);
        editor.apply();
    }

}
