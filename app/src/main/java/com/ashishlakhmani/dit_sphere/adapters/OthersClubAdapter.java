package com.ashishlakhmani.dit_sphere.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class OthersClubAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ParseObject> objectList;
    private ConstraintLayout no_others;

    public OthersClubAdapter(Context context, List<ParseObject> objectList, ConstraintLayout no_others) {
        this.context = context;
        this.objectList = objectList;
        this.no_others = no_others;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_others_club, parent, false);
        return new OthersClubAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ParseObject parseObject = objectList.get(position);

        ((MyViewHolder) holder).name.setText(parseObject.getString("name"));

        ((MyViewHolder) holder).join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.notification);
                builder.setCancelable(false);
                builder.setTitle("Follow Club.");
                builder.setMessage("You will get the Notifications.\n\nDo you still want to Continue ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        followTask(objectList.get(position));
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

        ((MyViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.notification);
                builder.setCancelable(false);
                builder.setTitle("Follow Club.");
                builder.setMessage("You will get the Notifications.\n\nDo you still want to Continue ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        followTask(objectList.get(position));
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

        TextView name;
        ImageView join;
        CardView card;

        private MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            join = itemView.findViewById(R.id.join);
            card = itemView.findViewById(R.id.card);
        }
    }

    //To follow club.
    private void followTask(final ParseObject object) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Follow Club");
        progressDialog.setMessage("Please Wait..\nFollowing Club..");
        progressDialog.show();

        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        object.addUnique("connected_id", sharedPreferences.getString("id", ""));
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    objectList.remove(object);
                    notifyDataSetChanged();

                    Intent intent = new Intent("UPDATE_FOLLOWED_CLUB");
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
}
