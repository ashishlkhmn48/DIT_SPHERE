package com.ashishlakhmani.dit_sphere.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.classes.ListFiles;

import java.io.File;


public class Downloads extends Fragment {

    ConstraintLayout headingLayout, no_download, no_permission;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private View view;

    private static final int MY_PERMISSIONS_REQUEST = 123;

    public Downloads() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);
        this.view = view;

        headingLayout = view.findViewById(R.id.heading_layout);
        no_download = view.findViewById(R.id.no_download);
        no_permission = view.findViewById(R.id.no_permission);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.downloads_recycler_view);

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        if (!((HomeActivity) getActivity()).checkAndRequestPermissions()) {
            progressBar.setVisibility(View.INVISIBLE);
            no_permission.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).setToolbarTitle("Downloads");

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("permission", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("granted", false)) {
            no_permission.setVisibility(View.INVISIBLE);
            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DIT-SPHERE");
            folder.mkdir();
            ListFiles listFiles = new ListFiles(getContext(), view, recyclerView, folder);
            listFiles.execute();
        }
        super.onResume();
    }
}
