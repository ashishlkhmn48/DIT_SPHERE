package com.ashishlakhmani.dit_sphere.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.adapters.NewsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class News extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout headingLayout;

    private Toolbar toolbar;


    public News() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initialization(view);

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

        pullNewsFeeds();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullNewsFeeds();
            }
        });

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager() != null) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        ViewCompat.setElevation(((HomeActivity) getActivity()).bottomNavigationView, 0);

                    } else {
                        ViewCompat.setElevation(((HomeActivity) getActivity()).bottomNavigationView, 100);
                        ((HomeActivity) getActivity()).setToolbarTitle("News");
                    }
                }
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).setToolbarTitle("News");
        super.onResume();
    }


    private void initialization(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.news_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.news_swipe_refresh);
        headingLayout = view.findViewById(R.id.heading_layout);
    }

    private void pullNewsFeeds() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("News");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    NewsAdapter newsAdapter = new NewsAdapter(getContext(), objects);
                    recyclerView.setAdapter(newsAdapter);
                } else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
