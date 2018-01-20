package com.ashishlakhmani.dit_sphere.fragments.interact;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.adapters.OthersThreadAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class OthersThread extends Fragment {


    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ConstraintLayout no_others;

    BroadcastReceiver broadcastReceiver;

    public OthersThread() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_others_group, container, false);
        initialize(view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                otherTask();
            }
        });

        otherTask();

        return view;
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_OTHERS"));
        super.onResume();
    }


    private void initialize(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.others_recycler_view);
        no_others = view.findViewById(R.id.no_others);
        progressBar = view.findViewById(R.id.progressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                swipeRefreshLayout.setRefreshing(true);
                otherTask();
            }
        };
    }

    public void otherTask() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Threads");
        query.orderByDescending("createdAt");
        query.whereNotEqualTo("connected_id", id);
        query.whereNotEqualTo("from_id", id);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    progressBar.setVisibility(View.INVISIBLE);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (objects.isEmpty()) {
                        no_others.setVisibility(View.VISIBLE);
                    } else {
                        OthersThreadAdapter adapter = new OthersThreadAdapter(getContext(), objects, no_others);
                        recyclerView.setAdapter(adapter);
                        no_others.setVisibility(View.INVISIBLE);
                    }

                } else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
