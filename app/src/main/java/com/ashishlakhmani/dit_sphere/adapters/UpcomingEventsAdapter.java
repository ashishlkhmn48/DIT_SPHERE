package com.ashishlakhmani.dit_sphere.adapters;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.UpcomingEventsActivity;
import com.ashishlakhmani.dit_sphere.fragments.news.NewsWebView;
import com.ashishlakhmani.dit_sphere.services.NotificationReceiver;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;

public class UpcomingEventsAdapter extends RecyclerView.Adapter {

    private Context context;

    private Calendar myCalendar = Calendar.getInstance();
    private List<ParseObject> objectList;

    public UpcomingEventsAdapter(Context context, List<ParseObject> objectList) {
        this.context = context;
        this.objectList = objectList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news, parent, false);
        return new UpcomingEventsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final String heading = objectList.get(position).getString("heading").trim();
        String date = objectList.get(position).getString("date").trim();

        ((UpcomingEventsAdapter.MyViewHolder) holder).heading.setText(heading);
        ((UpcomingEventsAdapter.MyViewHolder) holder).date.setText(date);
        ((UpcomingEventsAdapter.MyViewHolder) holder).sno.setText(String.valueOf(position + 1) + ".)");

        ((UpcomingEventsAdapter.MyViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsWebView newsWebView = new NewsWebView();
                Bundle bundle = new Bundle();
                bundle.putParcelable("parse_object", objectList.get(position));
                newsWebView.setArguments(bundle);
                loadFragment(newsWebView, "news_image");
            }
        });

        SharedPreferences sp = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        if (sp.contains(objectList.get(position).getObjectId())) {
            ((UpcomingEventsAdapter.MyViewHolder) holder).notification.setChecked(true);
        } else {
            ((UpcomingEventsAdapter.MyViewHolder) holder).notification.setChecked(false);
        }

        ((UpcomingEventsAdapter.MyViewHolder) holder).notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateTimeTask(heading, objectList.get(position).getObjectId(), ((UpcomingEventsAdapter.MyViewHolder) holder).notification);
                } else {
                    Toast.makeText(context, "Alarm Cancelled.", Toast.LENGTH_SHORT).show();
                    editor.remove(objectList.get(position).getObjectId());
                    editor.apply();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ToggleButton notification;
        TextView sno, heading, date;

        private MyViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            notification = itemView.findViewById(R.id.toggle_notification);
            sno = itemView.findViewById(R.id.sno);
            heading = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.credit);
        }
    }

    //Load fragment
    public void loadFragment(Fragment fragment, String TAG) {
        UpcomingEventsActivity upcomingEventsActivity = (UpcomingEventsActivity) context;
        FragmentManager fragmentManager = upcomingEventsActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.content, fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit(); // save the changes
    }


    private void dateTimeTask(final String heading, final String objectId, final ToggleButton toggleButton) {

        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(year, month, dayOfMonth, selectedHour, selectedMinute);
                        String myFormat = "dd MMMM yyyy : HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                        Toast.makeText(context, "You will be Notified on : " + sdf.format(myCalendar.getTime()), Toast.LENGTH_LONG).show();
                        createAlarm(heading, objectId);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.setCancelable(false);

                mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        toggleButton.setChecked(false);
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
                toggleButton.setChecked(false);
            }
        });
        dialog.show();

    }

    private void createAlarm(String heading, String objectId) {

        Random random = new Random();
        int num = random.nextInt(999999999);

        long alarm = myCalendar.getTimeInMillis();
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("heading", heading.trim());
        intent.putExtra("objectId", objectId);
        intent.putExtra("activity_name", "com.ashishlakhmani.dit_sphere.activities.UpcomingEventsActivity");
        intent.putExtra("title", "Upcoming Events");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, num, intent, 0);
        AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager1 != null) {
            alarmManager1.set(AlarmManager.RTC_WAKEUP, alarm, pendingIntent1);
        }

        SharedPreferences sp = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(objectId, true);
        editor.apply();
    }

}
