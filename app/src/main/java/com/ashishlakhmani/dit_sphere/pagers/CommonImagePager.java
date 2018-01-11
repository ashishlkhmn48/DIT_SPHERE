package com.ashishlakhmani.dit_sphere.pagers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.TouchImageView;
import com.ashishlakhmani.dit_sphere.fragments.CommonImageFragment;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommonImagePager extends PagerAdapter {

    private Context context;
    private Activity activity;
    private CommonImageFragment obj;
    private List<ParseObject> objectList;

    public CommonImagePager(Context context, Activity activity, CommonImageFragment obj, List<ParseObject> objectList) {
        this.context = context;
        this.activity = activity;
        this.obj = obj;
        this.objectList = objectList;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_common_image, container, false);

        final ParseObject parseObject = objectList.get(position);
        final TouchImageView imageView = (TouchImageView) view.findViewById(R.id.image);
        Picasso.with(context)
                .load(parseObject.getParseFile("file").getUrl())
                .placeholder(R.drawable.placeholder_album)
                .into(imageView);


        //Image view long click listener
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                obj.registerForContextMenu(v, parseObject);
                activity.openContextMenu(v);
                return true;
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

}