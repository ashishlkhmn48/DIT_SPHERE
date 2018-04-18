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
import com.ashishlakhmani.dit_sphere.activities.ClubActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class FollowedClubAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ParseObject> objectList;
    private ConstraintLayout no_followed;

    public FollowedClubAdapter(Context context, List<ParseObject> objectList, ConstraintLayout no_followed) {
        this.context = context;
        this.objectList = objectList;
        this.no_followed = no_followed;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_followed_club, parent, false);
        return new FollowedClubAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ParseObject parseObject = objectList.get(position);

        ((MyViewHolder) holder).name.setText(parseObject.getString("name"));

        ((MyViewHolder) holder).exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unFollowTask(parseObject);
            }
        });

        ((MyViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClubActivity.class);
                intent.putExtra("objectId", parseObject.getObjectId());
                intent.putExtra("club_name", parseObject.getString("name"));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView exit;
        CardView card;

        private MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            exit = itemView.findViewById(R.id.exit);
            card = itemView.findViewById(R.id.card);
        }
    }


    private void unFollowTask(final ParseObject object) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Unfollow Club");
        builder.setMessage("You will not get Notifications.\n\nDo you still want to Continue ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Unfollow Club");
                progressDialog.setMessage("Please Wait..\nUnfollowing the Club..");
                progressDialog.show();


                SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                List<String> list = object.getList("connected_id");
                list.remove(sharedPreferences.getString("id", ""));
                object.put("connected_id", list);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            objectList.remove(object);
                            notifyDataSetChanged();

                            Intent intent = new Intent("UPDATE_OTHERS_CLUB");
                            context.sendBroadcast(intent);

                            if (objectList.isEmpty()) {
                                no_followed.setVisibility(View.VISIBLE);
                            }
                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
}
