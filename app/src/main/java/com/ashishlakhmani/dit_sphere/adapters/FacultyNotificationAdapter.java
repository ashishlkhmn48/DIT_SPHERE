package com.ashishlakhmani.dit_sphere.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ashishlakhmani.dit_sphere.R;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FacultyNotificationAdapter extends RecyclerView.Adapter{
    private Context context;

    private Calendar myCalendar = Calendar.getInstance();
    private List<ParseObject> objectList;

    public FacultyNotificationAdapter(Context context, List<ParseObject> objectList) {
        this.context = context;
        this.objectList = objectList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news, parent, false);
        return new FacultyNotificationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        String myFormat = "dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        final String heading = objectList.get(position).getString("heading").trim();
        String date = sdf.format(objectList.get(position).getDate("date"));

        ((FacultyNotificationAdapter.MyViewHolder) holder).heading.setText(heading);
        ((FacultyNotificationAdapter.MyViewHolder) holder).date.setText(date);
        ((FacultyNotificationAdapter.MyViewHolder) holder).sno.setText((position+1)+ ".)");

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
}
