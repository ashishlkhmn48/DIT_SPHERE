package com.ashishlakhmani.dit_sphere.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.FacultyDetailsActivity;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.adapters.CustomExpandableAdapter;
import com.ashishlakhmani.dit_sphere.classes.FacultyData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class FacultyList extends Fragment {

    ArrayList<String> departmentList = new ArrayList<>(Arrays.asList("cse", "mechanical", "ece", "it", "petroleum", "civil", "electrical", "physics", "chemistry", "mathematics", "humanities"));
    HashMap<String, ArrayList<FacultyData>> map = new HashMap<>();

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private ProgressBar progressBar;


    public FacultyList() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).setToolbarTitle("Faculty List");
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faculty_list, container, false);

        initialize(view);
        loadData();

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getContext(), FacultyDetailsActivity.class);
                intent.putExtra("details",  map.get(departmentList.get(groupPosition)).get(childPosition));
                getActivity().startActivity(intent);
                return true;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ImageView imageView = v.findViewById(R.id.image);
                if (!expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.expandGroup(groupPosition);
                    if (!map.get(departmentList.get(groupPosition)).isEmpty()) {
                        imageView.setImageResource(R.drawable.collapse);
                        Toast.makeText(getContext(), map.get(departmentList.get(groupPosition)).size() + " Faculty/Faculties", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Empty List", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    expandableListView.collapseGroup(groupPosition);
                    imageView.setImageResource(R.drawable.expand);
                }

                return true;
            }
        });

        return view;
    }


    private void initialize(View view) {
        expandableListView = view.findViewById(R.id.expandable_list_view);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void loadData() {

        for (String str : departmentList) {
            map.put(str, new ArrayList<FacultyData>());
        }

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Faculty");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : objects) {

                        String key = obj.getString("branch").toLowerCase();

                        String name = obj.getString("name");
                        String contactNum = obj.getString("contact_num");
                        String emailId = obj.getString("email_id");
                        String branch = obj.getString("branch");
                        String location = obj.getString("building") + ", " + obj.getString("floor") + " Floor, " +
                                obj.getString("cabin");
                        String imageUrl = null;
                        List<String> specialization = obj.getList("specialization");

                        ParseFile file = obj.getParseFile("picture");
                        if (file != null) {
                            imageUrl = file.getUrl();
                        }

                        FacultyData facultyData = new FacultyData(name, contactNum, emailId, branch, location, imageUrl, specialization);
                        ArrayList<FacultyData> list = map.get(key);
                        list.add(facultyData);
                        map.put(key, list);
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    expandableListAdapter = new CustomExpandableAdapter(getContext(), departmentList, map);
                    expandableListView.setAdapter(expandableListAdapter);

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

