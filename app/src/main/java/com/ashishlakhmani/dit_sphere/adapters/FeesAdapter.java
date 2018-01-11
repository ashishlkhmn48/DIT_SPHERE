package com.ashishlakhmani.dit_sphere.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.TouchImageView;

import java.util.List;

public class FeesAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Bitmap> list;

    public FeesAdapter(Context context, List<Bitmap> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fees, parent, false);
        return new FeesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).img.setImageBitmap(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        // initialize the item view's

        TouchImageView img;

        private MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.image);
        }
    }
}
