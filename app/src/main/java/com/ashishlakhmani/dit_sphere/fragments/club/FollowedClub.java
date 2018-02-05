package com.ashishlakhmani.dit_sphere.fragments.club;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.ashishlakhmani.dit_sphere.activities.PushNotificationActivity;
import com.ashishlakhmani.dit_sphere.adapters.FollowedClubAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class FollowedClub extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ConstraintLayout no_followed;
    FloatingActionButton floatingActionButton;

    BroadcastReceiver broadcastReceiver;
    String objectId;
    String clubName;


    public FollowedClub() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followed_club, container, false);

        initialize(view);
        followedTask();
        floatingActionButtonClickTask();
        floatingActionButtonLongClickTask();

        return view;
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_FOLLOWED_CLUB"));
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private void initialize(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.followed_recycler_view);
        no_followed = view.findViewById(R.id.no_followed);
        progressBar = view.findViewById(R.id.progressBar);
        floatingActionButton = getActivity().findViewById(R.id.floatingActionButton);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                followedTask();
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                swipeRefreshLayout.setRefreshing(true);
                followedTask();
            }
        };
    }


    public void followedTask() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        final String id = sharedPreferences.getString("id", "");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Club");
        query.orderByAscending("name");
        query.whereEqualTo("connected_id", id);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.isEmpty()) {

                        recyclerView.setVisibility(View.INVISIBLE);
                        no_followed.setVisibility(View.VISIBLE);

                    } else {
                        no_followed.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        FollowedClubAdapter adapter = new FollowedClubAdapter(getContext(), objects, no_followed);
                        recyclerView.setAdapter(adapter);
                    }

                    ParseQuery<ParseObject> queryCheck = new ParseQuery<ParseObject>("Club");
                    queryCheck.whereEqualTo("head_id", id);
                    queryCheck.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                objectId = object.getObjectId();
                                clubName = object.getString("name");
                                floatingActionButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.INVISIBLE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });

    }


    private void floatingActionButtonClickTask() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PushNotificationActivity.class);
                intent.putExtra("objectId", objectId);
                intent.putExtra("club_name", clubName);
                startActivity(intent);
            }
        });
    }


    private void floatingActionButtonLongClickTask() {

        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

    }

}
