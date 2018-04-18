package com.ashishlakhmani.dit_sphere.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.PlacementViewActivity;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlacementCellAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ParseObject> objectList;
    private ConstraintLayout no_companies;

    public PlacementCellAdapter(Context context, List<ParseObject> objectList, ConstraintLayout no_companies) {
        this.context = context;
        this.objectList = objectList;
        this.no_companies = no_companies;

        if(objectList.isEmpty()){
            no_companies.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_placement_cell, parent, false);
        return new PlacementCellAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ParseObject parseObject = objectList.get(position);

        ((MyViewHolder) holder).company_name.setText(parseObject.getString("company_name"));
        ((MyViewHolder) holder).ctc.setText(parseObject.getString("ctc"));
        ((MyViewHolder) holder).post.setText(parseObject.getString("post"));
        ((MyViewHolder) holder).date.setText(getDateString(parseObject.getDate("date")));

        ((MyViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlacementViewActivity.class);
                intent.putExtra("objectId", parseObject.getObjectId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView company_name, ctc, date, post;
        CardView card;

        private MyViewHolder(View itemView) {
            super(itemView);
            company_name = itemView.findViewById(R.id.company_name);
            ctc = itemView.findViewById(R.id.ctc);
            date = itemView.findViewById(R.id.company_date);
            card = itemView.findViewById(R.id.card);
            post = itemView.findViewById(R.id.post);
        }
    }

    private String getDateString(Date date) {
        String myFormat = "dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        return sdf.format(date);
    }
}
