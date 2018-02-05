package com.ashishlakhmani.dit_sphere.fragments.interact;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.ChatActivity;
import com.ashishlakhmani.dit_sphere.adapters.FollowedThreadAdapter;
import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

import it.sephiroth.android.library.tooltip.Tooltip;


public class FollowedThread extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ConstraintLayout no_followed;
    FloatingActionButton floatingActionButton;
    FloatingActionButton floating_action_delete_button;

    BroadcastReceiver broadcastReceiver;

    //To check user's conversation thread status.
    boolean isThreadCreated = false;
    String objectID;
    String heading;

    public FollowedThread() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followed_thread, container, false);
        initialize(view);

        followedTask();
        floatingActionButtonClickTask();
        floatingActionButtonLongClickTask();

        floatingActionDeleteClickTask();
        floatingActionDeleteLongClickTask();

        return view;
    }


    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_FOLLOWED"));
        super.onResume();
    }

    private void initialize(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.followed_recycler_view);
        no_followed = view.findViewById(R.id.no_followed);
        progressBar = view.findViewById(R.id.progressBar);
        floatingActionButton = getActivity().findViewById(R.id.floatingActionButton);
        floating_action_delete_button = getActivity().findViewById(R.id.floating_action_delete_thread);

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
        final String branch = sharedPreferences.getString("branch", "");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Threads");
        query.whereMatches("branch", branch);
        query.orderByAscending("createdAt");
        query.whereEqualTo("connected_id", id);
        query.whereNotEqualTo("from_id", id);


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.isEmpty()) {

                        ParseQuery<ParseObject> innerQuery = new ParseQuery<ParseObject>("Threads");
                        innerQuery.whereEqualTo("from_id", id);
                        innerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }

                                recyclerView.setVisibility(View.INVISIBLE);
                                no_followed.setVisibility(View.VISIBLE);

                                floatingActionButton.setVisibility(View.VISIBLE);
                                if (e == null) {
                                    if (object != null) {
                                        isThreadCreated = true;
                                        objectID = object.getObjectId();
                                        heading = object.getString("heading").toUpperCase();
                                        floatingActionButton.setImageResource(R.drawable.interact_2);
                                        floating_action_delete_button.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    isThreadCreated = false;
                                    floatingActionButton.setImageResource(R.drawable.add);
                                    floating_action_delete_button.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {

                        ParseQuery<ParseObject> innerQuery = new ParseQuery<ParseObject>("Threads");
                        innerQuery.whereEqualTo("from_id", id);
                        innerQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                recyclerView.setVisibility(View.VISIBLE);

                                FollowedThreadAdapter adapter = new FollowedThreadAdapter(getContext(), objects, no_followed);
                                recyclerView.setAdapter(adapter);

                                no_followed.setVisibility(View.INVISIBLE);
                                floatingActionButton.setVisibility(View.VISIBLE);
                                if (e == null) {
                                    if (object != null) {
                                        isThreadCreated = true;
                                        objectID = object.getObjectId();
                                        heading = object.getString("heading").toUpperCase();
                                        floatingActionButton.setImageResource(R.drawable.interact_2);
                                        floating_action_delete_button.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    isThreadCreated = false;
                                    floatingActionButton.setImageResource(R.drawable.add);
                                    floating_action_delete_button.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void floatingActionButtonClickTask() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isThreadCreated) {

                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("object_id", objectID);
                    intent.putExtra("heading", heading);
                    startActivity(intent);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setIcon(R.drawable.interact);
                    builder.setTitle("Conversation Title");

                    View view = getActivity().getLayoutInflater().inflate(R.layout.layout_dialog_edittext, null);
                    final EditText input = view.findViewById(R.id.editText);

                    builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (!input.getText().toString().trim().isEmpty() && input.getText().toString().trim().length() <= 20) {
                                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("Conversation Thread");
                                progressDialog.setMessage("Please Wait..\nStarting Conversation Thread..");
                                progressDialog.show();

                                SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                                String id = sharedPreferences.getString("id", "");
                                String branch = sharedPreferences.getString("branch", "");

                                final ParseObject object = new ParseObject("Threads");
                                object.put("from_id", id);
                                object.put("heading", input.getText().toString().trim());
                                object.put("branch", branch);
                                object.put("date", new Date());
                                object.addUnique("connected_id", id);

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            isThreadCreated = true;
                                            floatingActionButton.setImageResource(R.drawable.interact_2);
                                            floating_action_delete_button.setVisibility(View.VISIBLE);
                                            heading = input.getText().toString().trim();
                                            objectID = object.getObjectId();
                                            Toast.makeText(getContext(), "Your Conversation Thread Started", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            } else {
                                if (input.getText().toString().trim().length() > 20) {
                                    Toast.makeText(getContext(), "Letter Limit Exceeded.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setView(view);
                    alertDialog.show();

                }
            }
        });
    }


    private void floatingActionButtonLongClickTask() {

        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (heading == null) {
                    heading = "Create New Conversation Thread.";
                }
                Tooltip.make(getContext(),
                        new Tooltip.Builder(101)
                                .anchor(floatingActionButton, Tooltip.Gravity.TOP)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 2500)
                                .activateDelay(800)
                                .showDelay(300)
                                .text(heading)
                                .maxWidth(500)
                                .withArrow(true)
                                .withOverlay(true)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();

                return true;
            }
        });

    }

    private void floatingActionDeleteClickTask() {

        floating_action_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.delete);
                builder.setCancelable(false);
                builder.setTitle("Close Conversation Thread");
                builder.setMessage("All the Messages will be Deleted.\nDo you still want to Delete?");

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Close Conversation Thread.");
                        progressDialog.setMessage("Please Wait.\nCloasing Your Conversation Thread.");
                        progressDialog.show();

                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Threads");
                        query.whereEqualTo("objectId", objectID);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {

                                    object.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            final LocalChatDatabase chatDatabase = new LocalChatDatabase(getContext(), objectID);
                                            chatDatabase.deleteData();
                                            floating_action_delete_button.setVisibility(View.INVISIBLE);
                                            floatingActionButton.setImageResource(R.drawable.add);
                                            isThreadCreated = false;
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Your Thread Successfully Closed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

    }


    private void floatingActionDeleteLongClickTask() {

        floating_action_delete_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Tooltip.make(getContext(),
                        new Tooltip.Builder(101)
                                .anchor(floating_action_delete_button, Tooltip.Gravity.TOP)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(true, false), 2500)
                                .activateDelay(800)
                                .showDelay(300)
                                .text("Close Conversation Thread")
                                .maxWidth(1000)
                                .withArrow(true)
                                .withOverlay(true)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();

                return true;
            }
        });

    }


}
