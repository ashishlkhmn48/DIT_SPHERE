package com.ashishlakhmani.dit_sphere.fragments.news;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.DownloaderHelper;
import com.ashishlakhmani.dit_sphere.classes.TouchImageView;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsImage extends Fragment {

    TouchImageView touchImageView;
    private ParseObject object;
    private ProgressDialog dialog;

    public NewsImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_image, container, false);

        touchImageView = view.findViewById(R.id.news_image);
        dialog = new ProgressDialog(getContext());

        object = getArguments().getParcelable("parse_object");
        if (object != null) {
            String url = object.getParseFile("image").getUrl();
            Picasso.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.placeholder_album)
                    .into(touchImageView);
        }

        touchImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                registerForContextMenu(v);
                getActivity().openContextMenu(v);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Share");
        menu.add(0, v.getId(), 0, "Download");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Download")) {
            DownloaderHelper downloaderHelper = new DownloaderHelper(getContext(), object, "image", "heading");
            downloaderHelper.downloadTaskImage();
        } else if (item.getTitle().equals("Share")) {
            shareTask();
        }
        return true;
    }

    private void shareTask() {
        ParseFile file = (ParseFile) object.get("image");
        String link = file.getUrl();
        String heading = object.getString("heading");
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, heading);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, heading + "\n\n" + link);
        getContext().startActivity(Intent.createChooser(sharingIntent, "Share News Using"));
    }
}
