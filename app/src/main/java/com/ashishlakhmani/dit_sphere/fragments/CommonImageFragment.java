package com.ashishlakhmani.dit_sphere.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.classes.DownloaderHelper;
import com.ashishlakhmani.dit_sphere.pagers.CommonImagePager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonImageFragment extends Fragment {

    private ParseObject parseObject;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout headingLayout;
    private ConstraintLayout main;


    public CommonImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_common_image, container, false);
        loadingTask(view);

        main = view.findViewById(R.id.main);
        main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), "Sorry.\nCurrently no files Available.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        headingLayout = view.findViewById(R.id.heading_layout);
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                headingLayout.animate()
                        .translationY(0)
                        .alpha(0.0f)
                        .setDuration(700)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                headingLayout.setVisibility(View.GONE);
                            }
                        });
            }
        }.start();

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingTask(view);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        String title = getArguments().getString("title");
        ((HomeActivity) getActivity()).setToolbarTitle(title);
        super.onResume();
    }

    private void loadingTask(View view) {
        String id = getArguments().getString("id");
        final ViewPager viewPager = view.findViewById(R.id.view_pager);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        final ImageView imageView = view.findViewById(R.id.imageView);
        final TextView textView = view.findViewById(R.id.textView);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Images");
        query.whereStartsWith("name", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);

                viewPager.setVisibility(View.VISIBLE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    if (objects.size() > 0) {
                        CommonImagePager commonImagePager = new CommonImagePager(getContext(), getActivity(), CommonImageFragment.this, objects);
                        viewPager.setAdapter(commonImagePager);
                    } else {
                        textView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, Menu.NONE, 0, "Download");
        menu.add(0, Menu.NONE, 0, "Share");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Download") {
            DownloaderHelper downloaderHelper = new DownloaderHelper(getContext(), parseObject, "file", "name");
            downloaderHelper.downloadTaskImage();
        } else {
            String title = getArguments().getString("title");
            ParseFile file = (ParseFile) parseObject.get("file");
            String link = file.getUrl();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title + " :\n\n" + link);
            getContext().startActivity(Intent.createChooser(sharingIntent, "Share Using"));
        }
        return true;
    }


    public void registerForContextMenu(View view, ParseObject parseObject) {
        this.parseObject = parseObject;
        super.registerForContextMenu(view);
    }

}
