package com.ashishlakhmani.dit_sphere.fragments.result;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.ashishlakhmani.dit_sphere.adapters.ResultViewerAdapter;
import com.ashishlakhmani.dit_sphere.classes.CalculatorHelperSgpa;
import com.ashishlakhmani.dit_sphere.classes.ResultHelper;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ResultViewer extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ConstraintLayout layout;
    ProgressBar progressBar;
    ConstraintLayout headingLayout;
    ConstraintLayout main;


    List<ResultHelper> list = new ArrayList<>();
    List<CalculatorHelperSgpa> answerList = new ArrayList<>();

    public ResultViewer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_viewer, container, false);

        initialize(view);

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

        main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), "Results not Published Yet.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        loadResults();

        return view;
    }


    private void initialize(View view) {
        recyclerView = view.findViewById(R.id.result_viewer_recycleview);
        layout = view.findViewById(R.id.sorry);
        progressBar = view.findViewById(R.id.progressBar);
        headingLayout = view.findViewById(R.id.heading_layout);
        main = view.findViewById(R.id.main);
    }


    //Load results from server.
    private void loadResults() {
        final String semester = getArguments().getString("semester");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Results");
        query.whereEqualTo("semester", semester);
        query.whereEqualTo("student_id", id);
        query.orderByAscending("subject_code");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (final ParseObject object : objects) {
                            ParseQuery<ParseObject> inner_query = new ParseQuery<ParseObject>("Subjects");
                            inner_query.whereEqualTo("code", object.getString("subject_code"));
                            inner_query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject obj, ParseException e) {
                                    if (e == null) {
                                        String name = obj.getString("name");
                                        String code = "( " + obj.getString("code") + " )";
                                        String credit = obj.getString("credit");
                                        String grade = object.getString("grade");

                                        list.add(new ResultHelper(name, code, credit, grade));

                                        double d = Double.valueOf(credit);
                                        answerList.add(new CalculatorHelperSgpa(d, grade));

                                        progressBar.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        layout.setVisibility(View.INVISIBLE);

                                        ResultViewerAdapter adapter = new ResultViewerAdapter(getContext(), list, answerList, objects.size(), semester);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        layout.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
