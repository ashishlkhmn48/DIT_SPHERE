package com.ashishlakhmani.dit_sphere.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.adapters.PlacementCellAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class PlacementCellActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ConstraintLayout no_companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_cell);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initialize();
        getPlacementDetails();
    }

    @Override
    public void onResume() {
        setTitle("Placement Cell");
        super.onResume();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initialize() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.placement_recycler_view);
        no_companies = findViewById(R.id.no_companies);
        progressBar = findViewById(R.id.progressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlacementCellActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlacementDetails();
            }
        });

    }


    public void getPlacementDetails() {

        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        final String branch = sharedPreferences.getString("branch", "");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Placement");
        query.orderByDescending("date");
        query.whereEqualTo("branch", branch.toLowerCase());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.isEmpty()) {

                        recyclerView.setVisibility(View.INVISIBLE);
                        no_companies.setVisibility(View.VISIBLE);

                    } else {
                        no_companies.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        PlacementCellAdapter adapter = new PlacementCellAdapter(PlacementCellActivity.this, objects, no_companies);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(PlacementCellActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.INVISIBLE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });

    }
}
