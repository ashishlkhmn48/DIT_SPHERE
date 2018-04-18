package com.ashishlakhmani.dit_sphere.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.adapters.FacultyNotificationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class FacultyNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout headingLayout;
    private TextView error_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_notification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initialization();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        pullFacultyNotifications();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullFacultyNotifications();
            }
        });
    }

    @Override
    public void onResume() {
        setTitle("Faculty Notifications");
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initialization() {
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.events_recyclerview);
        swipeRefreshLayout = findViewById(R.id.events_swipe_refresh);
        headingLayout = findViewById(R.id.heading_layout);
        error_msg = findViewById(R.id.error_msg);

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
    }

    private void pullFacultyNotifications() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("FacultyNotificationActivity");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (!objects.isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        FacultyNotificationAdapter facultyNotificationAdapter = new FacultyNotificationAdapter(FacultyNotificationActivity.this, objects);
                        recyclerView.setAdapter(facultyNotificationAdapter);
                    } else {
                        error_msg.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(FacultyNotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
