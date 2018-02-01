package com.ashishlakhmani.dit_sphere.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;

import java.io.File;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter {

    private Context context;
    private ConstraintLayout no;


    private List<File> file_list;

    public DownloadsAdapter(Context context, List<File> file_list, ConstraintLayout no) {
        this.context = context;
        this.no = no;
        this.file_list = file_list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_downloads, parent, false);
        return new DownloadsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((MyViewHolder) holder).name.setText(file_list.get(position).getName());
        ((MyViewHolder) holder).path.setText(file_list.get(position).getParent());

        ((MyViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri file_uri = FileProvider.getUriForFile(context, "com.ashishlakhmani.dit_sphere.provider", new File(file_list.get(position).getAbsolutePath()));
                Intent intent = new Intent(Intent.ACTION_VIEW, file_uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });

        ((MyViewHolder) holder).overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(context, v);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                final String filePath = file_list.get(position).getAbsolutePath();
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share:
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                Uri uri = Uri.parse(filePath);
                                if (filePath.endsWith("jpg")) {
                                    sharingIntent.setType("image/jpeg");
                                } else {
                                    sharingIntent.setType("application/pdf");
                                }
                                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                context.startActivity(Intent.createChooser(sharingIntent, "Share File Using"));
                                break;
                            case R.id.delete:
                                final String name = file_list.get(position).getName();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setCancelable(false);
                                builder.setTitle("Download");
                                builder.setMessage("Do you Really want to Delete \"" + name + "\" ?");

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (file_list.get(position).delete()) {
                                            Toast.makeText(context, name + " Deleted.", Toast.LENGTH_SHORT).show();
                                            file_list.remove(file_list.get(position));
                                            if (file_list.size() == 0) {
                                                no.setVisibility(View.VISIBLE);
                                            }
                                            notifyDataSetChanged();
                                        }
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
                        return true;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return file_list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView overflow;
        TextView name, path;
        CardView card;

        private MyViewHolder(View itemView) {
            super(itemView);
            overflow = itemView.findViewById(R.id.overflow);
            name = itemView.findViewById(R.id.name);
            path = itemView.findViewById(R.id.path);
            card = itemView.findViewById(R.id.card);
        }
    }

}
